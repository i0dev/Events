package com.i0dev.events.entity.object;

import com.i0dev.events.engine.EngineEvent;
import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.MConf;
import com.massivecraft.massivecore.item.DataPotionEffect;
import com.massivecraft.massivecore.ps.PS;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.List;

@Data
public abstract class EventSettings {

    String id;
    int maxTeams;
    int maxPlayersPerTeam;
    boolean spawnOnLeave;
    boolean allowSpectators;
    PS spectateLocationPS;
    protected EventStatus teleportPlayersOnStatus;
    List<DataPotionEffect> permanentPotionEffects;
    List<EventStatus> applyPotionEffectsOnStatuses;
    ArmorSettings armorSettings;
    EventStatus applyArmorOnStatus;
    boolean requireEmptyInventoryToJoin;
    boolean savePlayerInventoryOnEventJoin;
    long maxRunTimeMillis;
    List<EventStatus> allowJoiningOnStatuses;
    List<Long> secondsUntilStartSendTitleTimes;
    transient ActiveEvent activeEvent = null;

    public EventSettings(String id,
                         int maxTeams,
                         int maxPlayersPerTeam,
                         boolean spawnOnLeave,
                         boolean allowSpectators,
                         Location spectateLocation,
                         EventStatus teleportPlayersOnStatus,
                         List<DataPotionEffect> permanentPotionEffects,
                         List<EventStatus> applyPotionEffectsOnStatuses,
                         ArmorSettings armorSettings,
                         EventStatus applyArmorOnStatus,
                         boolean requireEmptyInventoryToJoin,
                         boolean savePlayerInventoryOnEventJoin,
                         long maxRunTimeMillis,
                         List<EventStatus> allowJoiningOnStatuses,
                         List<Long> secondsUntilStartSendTitleTimes
    ) {
        this.id = id;
        this.maxTeams = maxTeams;
        this.maxPlayersPerTeam = maxPlayersPerTeam;
        this.spawnOnLeave = spawnOnLeave;
        this.allowSpectators = allowSpectators;
        this.spectateLocationPS = PS.valueOf(spectateLocation);
        this.teleportPlayersOnStatus = teleportPlayersOnStatus;
        this.permanentPotionEffects = permanentPotionEffects;
        this.applyPotionEffectsOnStatuses = applyPotionEffectsOnStatuses;
        this.armorSettings = armorSettings;
        this.applyArmorOnStatus = applyArmorOnStatus;
        this.requireEmptyInventoryToJoin = requireEmptyInventoryToJoin;
        this.savePlayerInventoryOnEventJoin = savePlayerInventoryOnEventJoin;
        this.maxRunTimeMillis = maxRunTimeMillis;
        this.allowJoiningOnStatuses = allowJoiningOnStatuses;
        this.secondsUntilStartSendTitleTimes = secondsUntilStartSendTitleTimes;
    }

    public Location getSpectateLocation() {
        return spectateLocationPS.asBukkitLocation(true);
    }

    public abstract void startEvent(ActiveEvent activeEvent);

    public abstract void endEvent();

    public abstract void teleportPlayers();

    public abstract void giveRewards();

    public void initialize() {
    }

    public void teleportPlayersOut() {
        activeEvent.getEveryone().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.teleport(MConf.get().spawnLocationPS.asBukkitLocation());
                player.setGameMode(GameMode.SURVIVAL);
            } else {
                EngineEvent.addToSpawnOnJoin(uuid, activeEvent.getSettings().isSavePlayerInventoryOnEventJoin());
            }
        });
    }


    public void onPlayerMove(PlayerMoveEvent e) {

    }

}
