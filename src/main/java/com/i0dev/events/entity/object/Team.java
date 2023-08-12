package com.i0dev.events.entity.object;

import com.i0dev.events.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Team {

    public Team(Player leader) {
        this.leader = new Member(leader.getUniqueId(), leader.getName());
        this.name = leader.getName() + "'s Team";
        this.members.add(new Member(leader.getUniqueId(), leader.getName()));
    }

    String name;
    Member leader;
    List<Member> members = new ArrayList<>();
    List<UUID> invites = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class Member {
        UUID uuid;
        String name;
    }

    public Member getMember(UUID uuid) {
        return getMembers()
                .stream()
                .filter(member -> member.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }


    public void addMember(Player player) {
        UUID uuid = player.getUniqueId();
        Member member = getMember(uuid);
        if (member != null) return;
        members.add(new Member(uuid, player.getName()));
    }

    public void removeMember(Player player) {
        UUID uuid = player.getUniqueId();
        Member member = getMember(uuid);
        if (member == null) return;
        members.remove(member);
    }

    public void removeMember(UUID uuid) {
        Member member = getMember(uuid);
        if (member == null) return;
        members.remove(member);
    }

    public void transferLeader(Player newLeader) {
        if (newLeader == null) {
            leader = members.stream().filter(member -> !member.getUuid().equals(leader.getUuid())).findFirst().orElse(null);
            return;
        }

        UUID uuid = newLeader.getUniqueId();
        leader = new Member(uuid, newLeader.getName());
    }

    public void invitePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (invites.contains(uuid)) return;
        invites.add(uuid);
        Utils.msg(player, "&aYou have been invited to join &e" + name + "&a!, type &e/team join " + getLeader().getName() + "&a to join!");
    }

    public void rename(String newName) {
        if (name.equals(newName)) return;
        name = newName;
    }



}
