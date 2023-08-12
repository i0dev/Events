package com.i0dev.events.cmd;

import com.i0dev.events.Perm;
import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.object.Team;
import com.i0dev.events.util.Utils;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdPartyLeave extends EventsCommand {

    public CmdPartyLeave() {
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    protected <T extends Enum<T>> T calcPerm() {
        return (T) Perm.PARTY_LEAVE;
    }

    @Override
    public void perform() throws MassiveException {
        ActiveEvent activeEvent = ActiveEvent.getEventByPlayer(me);
        ActiveEvent spectatingEvent = ActiveEvent.getEventBySpectator(me);
        if (activeEvent == null) {
            msg("<b>You are not in an event.");
            return;
        } else if (spectatingEvent != null) {
            spectatingEvent.stopSpectating(me);
            return;
        }
        Team team = activeEvent.getTeamByPlayer(me);
        if (team.getMembers().isEmpty()) {
            activeEvent.getTeams().remove(team);
            msg("<g>You have disbanded your team.");
            return;
        }
        if (team.getLeader().getUuid().equals(me.getUniqueId())) {
            team.transferLeader(null);
            msg("<g>You have left the team and transferred leadership to <h>%s<g>.", team.getLeader().getName());
        }

        team.removeMember(me.getUniqueId());

        activeEvent.createNewTeam(me);
        activeEvent.changed();

        msg("<g>You have left the team.");
        team.getMembers().forEach(member -> {
            Player player = Bukkit.getPlayer(member.getUuid());
            if (player == null) return;
            Utils.msg(player, "&c" + me.getName() + " &ahas left the team.");
        });

    }
}
