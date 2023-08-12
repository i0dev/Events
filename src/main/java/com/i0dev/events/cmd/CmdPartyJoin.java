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

public class CmdPartyJoin extends EventsCommand {

    public CmdPartyJoin() {
        this.addParameter(TypePlayer.get(), "player");
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    protected <T extends Enum<T>> T calcPerm() {
        return (T) Perm.PARTY_JOIN;
    }

    @Override
    public void perform() throws MassiveException {
        Player teamLeader = this.readArg();
        ActiveEvent leadersEvent = ActiveEvent.getEventByPlayer(teamLeader); //leaders team
        if (leadersEvent == null) {
            msg("<b>That player is not in an event.");
            return;
        }
        if (!leadersEvent.getSettings().getAllowJoiningOnStatuses().contains(leadersEvent.getStatus())) {
            msg("<b>You cannot join this event at this time.");
            return;
        }
        Team leadersTeam = leadersEvent.getTeamByPlayer(teamLeader); //leaders Team
        if (!leadersTeam.getInvites().contains(me.getUniqueId())) {
            msg("<b>You have not been invited to this team.");
            return;
        }
        ActiveEvent joinersEvent = ActiveEvent.getEventByPlayer(me); //joiners event
        if (joinersEvent != null) { //joiners event
            Team joinersTeam = joinersEvent.getTeamByPlayer(me);
            if (joinersTeam.getMembers().size() != 1) {
                msg("You are already on a team, leave it first");
                return;
            }
            joinersEvent.getTeams().remove(leadersTeam);
            joinersEvent.changed();
        } else {
            if (!leadersEvent.joinEvent(me)) return;
        }

        ActiveEvent spectatingEvent = ActiveEvent.getEventBySpectator(me);
        if (spectatingEvent != null) {
            spectatingEvent.stopSpectating(me);
        }

        leadersTeam.getInvites().remove(me.getUniqueId());
        leadersTeam.addMember(me);
        leadersEvent.changed();
        msg("<g>You have joined the team.");
        leadersTeam.getMembers().forEach(member -> {
            Player player = Bukkit.getPlayer(member.getUuid());
            if (player == null) return;
            Utils.msg(player, "&c" + me.getName() + " &ahas joined the team.");
        });

    }
}
