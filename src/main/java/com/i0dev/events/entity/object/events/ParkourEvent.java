package com.i0dev.events.entity.object.events;

import com.i0dev.events.EventsPlugin;
import com.i0dev.events.engine.EngineEvent;
import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.object.ArmorSettings;
import com.i0dev.events.entity.object.EventSettings;
import com.i0dev.events.entity.object.EventStatus;
import com.i0dev.events.entity.object.PlacedReward;
import com.i0dev.events.util.Cuboid;
import com.i0dev.events.util.Utils;
import com.massivecraft.massivecore.item.DataPotionEffect;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class ParkourEvent extends EventSettings {

    PS teleportLocation;
    String barrierCuboid;
    String winCuboid;
    int winnersCount;
    List<PlacedReward> placedRewards;

    public ParkourEvent(int maxTeams,
                        int maxPlayersPerTeam,
                        boolean spawnOnLeave,
                        boolean allowSpectators,
                        Location spectateLocation,
                        EventStatus teleportPlayersOnStatus,
                        Location teleportLocation,
                        List<DataPotionEffect> permanentPotionEffects,
                        List<EventStatus> applyPotionEffectsOnStatuses,
                        ArmorSettings armorSettings,
                        EventStatus applyArmorOnStatus,
                        boolean requireEmptyInventoryToJoin,
                        boolean savePlayerInventoryOnEventJoin,
                        String barrierCuboid,
                        long maxRunTimeMillis,
                        List<EventStatus> allowJoiningOnStatuses,
                        List<Long> secondsUntilStartSendTitleTimes,
                        String winCuboid,
                        int winnersCount,
                        List<PlacedReward> placedRewards
    ) {
        super("parkour",
                maxTeams,
                maxPlayersPerTeam,
                spawnOnLeave,
                allowSpectators,
                spectateLocation,
                teleportPlayersOnStatus,
                permanentPotionEffects,
                applyPotionEffectsOnStatuses,
                armorSettings,
                applyArmorOnStatus,
                requireEmptyInventoryToJoin,
                savePlayerInventoryOnEventJoin,
                maxRunTimeMillis,
                allowJoiningOnStatuses,
                secondsUntilStartSendTitleTimes
        );
        this.teleportLocation = PS.valueOf(teleportLocation);
        this.barrierCuboid = barrierCuboid;
        this.winCuboid = winCuboid;
        this.winnersCount = winnersCount;
        this.placedRewards = placedRewards;
    }

    public Cuboid getBarrierCuboid() {
        return Cuboid.deserialize(EventsPlugin.get(), barrierCuboid);
    }

    public Cuboid getWinCuboid() {
        return Cuboid.deserialize(EventsPlugin.get(), winCuboid);
    }

    private Location getTeleportLocation() {
        return teleportLocation.asBukkitLocation(true);
    }

    @Override
    public void initialize() {
        getActiveEvent().setWinners(new HashMap<>());
        getActiveEvent().changed();
    }

    @Override
    public void startEvent(ActiveEvent activeEvent) {
        setActiveEvent(activeEvent);
        getBarrierCuboid().getAllBlocks(true).forEach(block -> block.setType(Material.AIR));
    }

    @Override
    public void endEvent() {
        getBarrierCuboid().getAllBlocks(false).forEach(block -> block.setType(Material.GLASS));
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!getActiveEvent().getStatus().equals(EventStatus.ACTIVE)) return;
        if (getWinCuboid().contains(e.getTo())) {
            win(e.getPlayer());
        }
    }

    public Map<Integer, UUID> getWinners() {
        if (getActiveEvent().getWinners() == null) {
            getActiveEvent().setWinners(new HashMap<>());
            return new HashMap<>();
        }
        return ((Map<Integer, UUID>) getActiveEvent().getWinners());
    }

    public int getNextPlace() {
        return getWinners().size() + 1;
    }


    private void win(Player player) {
        if (getWinners().containsValue(player.getUniqueId())) return;
        getWinners().put(getNextPlace(), player.getUniqueId());
        Bukkit.broadcastMessage(player.getName() + " won the parkour event, " + getWinners().size() + "/" + winnersCount + " winners");

        if (winnersCount == getWinners().size() || getWinners().size() >= getActiveEvent().getParticipants().size()) {
            fancyWin();
        }
    }

    private void fancyWin() {
        if (getActiveEvent().isEndingPhase()) return;
        getActiveEvent().setEndingPhase(true);
        getActiveEvent().changed();
        getActiveEvent().messageAll("The parkour event has ended teleporting back to spawn in 10 seconds!");
        getActiveEvent().getParticipants().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                if (getActiveEvent().getSettings().isSpawnOnLeave()) {
                    EngineEvent.addToSpawnOnJoin(uuid, getActiveEvent().getSettings().isSavePlayerInventoryOnEventJoin());
                }
            }
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
        });
        Bukkit.getScheduler().runTaskLater(EventsPlugin.get(), () -> {
            this.getActiveEvent().endEvent(false);
        }, 20 * 10);
    }

    @Override
    public void giveRewards() {
        getWinners().forEach((place, uuid) -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) return;
                    placedRewards.forEach(placedReward -> {
                        if (placedReward.getPlace() == place) {
                            Utils.runCommands(placedReward.getCommands(), player);
                        }
                    });
                }
        );
    }

    @Override
    public void teleportPlayers() {
        getActiveEvent().getParticipants().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                if (getActiveEvent().getSettings().isSpawnOnLeave()) {
                    EngineEvent.addToSpawnOnJoin(uuid, getActiveEvent().getSettings().isSavePlayerInventoryOnEventJoin());
                }
                getActiveEvent().removePlayer(uuid);
            }
            player.teleport(getTeleportLocation());
        });
    }

    @Override
    public void teleportPlayersOut() {
        super.teleportPlayersOut();
        getActiveEvent().getEveryone().forEach(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        if (getActiveEvent().getSettings().isSpawnOnLeave()) {
                            EngineEvent.addToSpawnOnJoin(uuid, getActiveEvent().getSettings().isSavePlayerInventoryOnEventJoin());
                        }
                        getActiveEvent().removePlayer(uuid);
                    }
                    player.setGameMode(GameMode.SURVIVAL);
                }
        );
    }
}
