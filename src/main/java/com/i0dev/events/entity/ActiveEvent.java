package com.i0dev.events.entity;

import com.i0dev.events.entity.object.*;
import com.i0dev.events.entity.object.bukkitevent.EventSwitchStatus;
import com.i0dev.events.entity.object.bukkitevent.PlayerJoinEventEvent;
import com.i0dev.events.entity.object.bukkitevent.PlayerSpectateEventEvent;
import com.i0dev.events.util.Utils;
import com.massivecraft.massivecore.store.Entity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ActiveEvent extends Entity<ActiveEvent> {

    public static ActiveEvent get(Object oid) {
        return ActiveEventColl.get().get(oid);
    }

    public void initialize() {
        MConf.get().getEventById(eventType).setActiveEvent(this);
        getSettings().initialize();
    }


    public static ActiveEvent create(EventType eventType, long countdown) {
        ActiveEvent event = ActiveEventColl.get().create(UUID.randomUUID().toString());
        event.setCountdown(countdown);
        event.setStartedCountdown(System.currentTimeMillis());
        event.setEventType(eventType);
        MConf.get().getEventById(eventType).setActiveEvent(event);
        event.setJoinCode(UUID.randomUUID().toString().substring(0, 5)); // TODO: ensure unique
        event.getSecondsUntilStartSendTitleTimes().addAll(event.getSettings().getSecondsUntilStartSendTitleTimes());
        Bukkit.getServer().getPluginManager().callEvent(new EventSwitchStatus(EventStatus.NOT_STARTED, EventStatus.COUNTDOWN, event));
        event.setStatus(EventStatus.COUNTDOWN);
        event.initialize();
        return event;
    }

    public static ActiveEvent getEventByJoinCode(String joinCode) {
        return ActiveEventColl.get().getAll().stream().filter(event -> event.getJoinCode().equals(joinCode)).findFirst().orElse(null);
    }

    public static ActiveEvent getEventByPlayer(Player player) {
        return ActiveEventColl.get()
                .getAll()
                .stream()
                .filter(event -> event.getParticipants().contains(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    public static ActiveEvent getEventBySpectator(Player player) {
        return ActiveEventColl.get().getAll().stream().filter(event -> event.getSpecatators().contains(player.getUniqueId())).findFirst().orElse(null);
    }

    public EventSettings getSettings() {
        return MConf.get().getEventById(eventType);
    }

    public List<UUID> getEveryone() {
        List<UUID> everyone = new ArrayList<>();
        everyone.addAll(participants);
        everyone.addAll(specatators);
        return everyone;
    }

    // meta
    EventType eventType;
    EventStatus status;
    String joinCode;
    long startTime;

    long startedCountdown;
    List<Long> secondsUntilStartSendTitleTimes = new ArrayList<>();
    long countdown;
    List<UUID> participants = new ArrayList<>();
    List<UUID> specatators = new ArrayList<>();
    List<Team> teams = new ArrayList<>();
    boolean endingPhase = false;
    Object winners;

    public void specateEvent(Player player) {
        UUID uuid = player.getUniqueId();
        if (!getSettings().isAllowSpectators()) {
            Utils.msg(player, "&cThis event does not allow spectators.");
            return;
        }
        if (specatators.contains(uuid)) {
            Utils.msg(player, "&cYou are already spectating this event!");
            return;
        }
        if (participants.contains(uuid)) {
            Utils.msg(player, "&cYou are already in this event!");
            return;
        }
        ActiveEvent currentEvent = getEventByPlayer(player);
        if (currentEvent != null) {
            Utils.msg(player, "&cYou are already in an" + currentEvent.getEventType().getId() + " event!");
            return;
        }
        currentEvent = getEventBySpectator(player);
        if (currentEvent != null) {
            Utils.msg(player, "&cYou are already spectating an" + currentEvent.getEventType().getId() + " event!");
            return;
        }
        specatators.add(uuid);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerSpectateEventEvent(player, this));
        Utils.msg(player, "&aYou started spectating the event &e" + getSettings().getId() + "&a.");
        player.setGameMode(GameMode.SPECTATOR);
        this.changed();
    }

    public void stopSpectating(Player player) {
        UUID uuid = player.getUniqueId();
        if (!specatators.contains(uuid)) return;
        specatators.remove(uuid);
        Utils.msg(player, "&aYou stopped spectating the event &e" + getSettings().getId() + "&a.");
        this.changed();
    }

    public boolean joinEvent(Player player) {
        UUID uuid = player.getUniqueId();
        if (participants.contains(uuid)) {
            Utils.msg(player, "&cYou are already in this event!");
            return false;
        }

        ActiveEvent currentEvent = getEventByPlayer(player);
        if (currentEvent != null) {
            Utils.msg(player, "&cYou are already in an" + currentEvent.getEventType().getId() + " event!");
            return false;
        }

        if (!getSettings().getAllowJoiningOnStatuses().contains(status)) {
            Utils.msg(player, "&cYou cannot join this event at this time!");
            return false;
        }

        if (getSettings().isRequireEmptyInventoryToJoin()) {
            if (!player.getInventory().isEmpty()) {
                Utils.msg(player, "&cYou must empty your inventory to join this event!");
                return false;
            }
        }

        if (getTeamByPlayer(player) == null) {
            if (teams.size() >= getSettings().getMaxPlayersPerTeam()) {
                Utils.msg(player, "&cThis event is full!");
                return false;
            }
            createNewTeam(player);
        } else {
            Utils.msg(player, "&cYou are already in a team!");
            return false;
        }
        participants.add(player.getUniqueId());
        Utils.msg(player, "&aYou have joined the event " + getEventType().getId() + "!");

        if (getSettings().isSavePlayerInventoryOnEventJoin()) {
            MPlayer mPlayer = MPlayer.get(player);
            mPlayer.saveInventory();
        }

        player.getInventory().clear();
        Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinEventEvent(player, this));
        this.changed();
        return true;
    }

    public void leaveEvent(Player player) {
        player.teleport(MConf.get().getSpawnLocationPS().asBukkitLocation());

        if (specatators.contains(player.getUniqueId())) {
            specatators.remove(player.getUniqueId());
            return;
        }

        Team team = getTeamByPlayer(player);
        if (team.getMembers().size() == 1) {
            teams.remove(team);
        } else if (team.getLeader().getUuid().equals(player.getUniqueId())) {
            team.transferLeader(null);
            team.removeMember(player.getUniqueId());
        }

        participants.remove(player.getUniqueId());
        Utils.msg(player, "&cYou have left the event " + getEventType().getId() + "!");
        this.changed();
    }

    public Team getTeamByPlayer(Player player) {
        return this.getTeamByPlayerUUID(player.getUniqueId());
    }

    public Team getTeamByPlayerUUID(UUID uuid) {
        return this.getTeams()
                .stream()
                .filter(team -> team.getMember(uuid) != null)
                .findFirst()
                .orElse(null);
    }

    public void createNewTeam(Player leader) {
        Team team = new Team(leader);
        teams.add(team);
        this.changed();
    }

    public void removePlayer(UUID uuid) {
        Team team = getTeamByPlayerUUID(uuid);
        if (team == null) return;
        team.removeMember(uuid);
        participants.remove(uuid);
        //todo spawn the player if null or whatever, add to list etc.
        this.changed();
    }

    public void start() {
        setStartTime(System.currentTimeMillis());
        this.setStatus(EventStatus.ACTIVE);
        Bukkit.getServer().getPluginManager().callEvent(new EventSwitchStatus(EventStatus.COUNTDOWN, EventStatus.ACTIVE, this));
        Bukkit.broadcastMessage("Event started! (ParkourEvent) players: " + getParticipants().size());
        getSettings().startEvent(this);
        this.changed();
    }

    public void endEvent(boolean reachedMaxRunTime) {
        Bukkit.getServer().getPluginManager().callEvent(new EventSwitchStatus(EventStatus.ACTIVE, EventStatus.ENDED, this));
        this.setStatus(EventStatus.ENDED);
        if (reachedMaxRunTime)
            Bukkit.broadcastMessage("Event " + getSettings().getId() + " has ended due to reaching the max run time!");
        else
            Bukkit.broadcastMessage("Event " + getSettings().getId() + " has ended!");

        if (getSettings().isSavePlayerInventoryOnEventJoin()) {
            participants.forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) return;
                MPlayer mPlayer = MPlayer.get(player);
                mPlayer.restoreInventory();
            });
        }
        getSettings().teleportPlayersOut();
        getSettings().endEvent();
        getSettings().giveRewards();
        this.detach();
    }


    public void applyArmorSettings() {
        participants.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            ArmorSettings set = getSettings().getArmorSettings();
            if (set.getHelmet() != null)
                player.getInventory().setHelmet(set.getHelmet().toBukkit());
            if (set.getChestplate() != null)
                player.getInventory().setChestplate(set.getChestplate().toBukkit());
            if (set.getLeggings() != null)
                player.getInventory().setLeggings(set.getLeggings().toBukkit());
            if (set.getBoots() != null)
                player.getInventory().setBoots(set.getBoots().toBukkit());
        });
    }

    public void messageAllParticipants(String message) {
        participants.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            Utils.msg(player, message);
        });
    }

    public void messageAllSpectators(String message) {
        specatators.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            Utils.msg(player, message);
        });
    }

    public void messageAll(String message) {
        messageAllParticipants(message);
        messageAllSpectators(message);
    }

}
