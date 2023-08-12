package com.i0dev.events.cmd;

import com.i0dev.events.Perm;
import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.object.Team;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class CmdPartyInfo extends EventsCommand {

    public CmdPartyInfo() {
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    protected <T extends Enum<T>> T calcPerm() {
        return (T) Perm.PARTY_INFO;
    }

    @Override
    public void perform() {
        ActiveEvent activeEvent = ActiveEvent.getEventByPlayer(me);
        if (activeEvent == null) {
            msg("<b>You are not in an event.");
            return;
        }
        Team team = activeEvent.getTeamByPlayer(me);
        msg("<i>Party info for event <h>%s<i>:", activeEvent.getEventType().getId());
        msg("<i>Team: <h>%s<i>.", team.getName());
        msg("<i>Players: <h>%s<i>.", team.getMembers().size());
        msg("<i>Leader: <h>%s<i>.", team.getLeader().getName());
        msg("<i>Members: <h>%s<i>.", team.getMembers().stream().map(Team.Member::getName).toList().toString());
    }
}
