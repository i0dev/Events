package com.i0dev.events.engine;

import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.MConf;
import com.i0dev.events.entity.MPlayer;
import com.i0dev.events.entity.object.EventSettings;
import com.i0dev.events.entity.object.EventStatus;
import com.i0dev.events.entity.object.bukkitevent.EventSwitchStatus;
import com.i0dev.events.entity.object.bukkitevent.PlayerJoinEventEvent;
import com.i0dev.events.entity.object.bukkitevent.PlayerSpectateEventEvent;
import com.massivecraft.massivecore.Engine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EngineEvent extends Engine {

    public static EngineEvent i = new EngineEvent();

    public static EngineEvent get() {
        return i;
    }

    @Data
    @AllArgsConstructor
    public static class SpawnJoin {
        UUID uuid;
        boolean restoreInventory;
    }

    List<SpawnJoin> spawnOnJoin = new ArrayList<>();

    public SpawnJoin getObjFromUUID(UUID uuid) {
        return spawnOnJoin.stream().filter(spawnJoin -> spawnJoin.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public static void addToSpawnOnJoin(UUID uuid, boolean restoreInventory) {
        EngineEvent.get().spawnOnJoin.add(new SpawnJoin(uuid, restoreInventory));
    }

    public static void removeFromSpawnOnJoin(UUID uuid) {
        EngineEvent.get().spawnOnJoin.removeIf(spawnJoin -> spawnJoin.getUuid().equals(uuid));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        SpawnJoin spawnJoin = getObjFromUUID(e.getPlayer().getUniqueId());
        if (spawnJoin != null) {
            e.getPlayer().teleport(MConf.get().getSpawnLocationPS().asBukkitLocation());
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            if (spawnJoin.isRestoreInventory()) {
                MPlayer mPlayer = MPlayer.get(e.getPlayer());
                mPlayer.restoreInventory();
            }
            removeFromSpawnOnJoin(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ActiveEvent activeEvent = ActiveEvent.getEventByPlayer(e.getPlayer());
        if (activeEvent == null) {
            activeEvent = ActiveEvent.getEventBySpectator(e.getPlayer());
        }
        if (activeEvent != null && activeEvent.getSettings().isSpawnOnLeave()) {
            addToSpawnOnJoin(e.getPlayer().getUniqueId(), activeEvent.getSettings().isSavePlayerInventoryOnEventJoin());
        }
    }


    @EventHandler
    public void onStatusSwitch(EventSwitchStatus e) {
        EventSettings settings = e.getActiveEvent().getSettings();
        if (settings.getApplyArmorOnStatus() == e.getNewStatus())
            e.getActiveEvent().applyArmorSettings();
        EventStatus teleportPlayersStatus = settings.getTeleportPlayersOnStatus();
        if (teleportPlayersStatus == e.getNewStatus()) {
            e.getActiveEvent().getSettings().teleportPlayers();
            e.getActiveEvent().getSpecatators().forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) return; // TODO to spawn??
                player.teleport(e.getActiveEvent().getSettings().getSpectateLocation());
            });
        }
    }

    @EventHandler
    public void onEventJoin(PlayerJoinEventEvent e) {
        if (e.getActiveEvent().getStatus() == e.getActiveEvent().getSettings().getTeleportPlayersOnStatus())
            e.getActiveEvent().getSettings().teleportPlayers();
    }

    @EventHandler
    public void onSpectate(PlayerSpectateEventEvent e) {
        if (e.getActiveEvent().getStatus() == e.getActiveEvent().getSettings().getTeleportPlayersOnStatus())
            e.getPlayer().teleport(e.getActiveEvent().getSettings().getSpectateLocation());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent e) {
        // ensure they move a full block
        if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;

        Player player = e.getPlayer();
        ActiveEvent activeEvent = ActiveEvent.getEventByPlayer(player);
        if (activeEvent == null) return;
        activeEvent.getSettings().onPlayerMove(e);
    }


}
