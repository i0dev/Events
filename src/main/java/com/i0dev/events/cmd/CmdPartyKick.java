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

public class CmdPartyKick extends EventsCommand {

    public CmdPartyKick() {
        this.addParameter(TypePlayer.get(), "player");
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    protected <T extends Enum<T>> T calcPerm() {
        return (T) Perm.PARTY_KICK;
    }

    @Override
    public void perform() throws MassiveException {
        Player toKick = this.readArg();
        ActiveEvent event = ActiveEvent.getEventByPlayer(me);
        if (event == null) {
            msg("<b>Your not in an is not in an event.");
            return;
        }
        Team team = event.getTeamByPlayer(me);
        if (!team.getLeader().getUuid().equals(me.getUniqueId())) {
            msg("<b>You are not the leader of your team. Only the leader can kick players.");
            return;
        }
        if (team.getMember(toKick.getUniqueId()) == null) {
            msg("<b>This player is not in your team.");
            return;
        }
        if (team.getLeader().getUuid().equals(toKick.getUniqueId())) {
            msg("<b>You cannot kick yourself.");
            return;
        }

        team.removeMember(toKick);
        event.createNewTeam(toKick);
        event.changed();
        Utils.msg(toKick, "&c" + me.getName() + " &ahas kicked you from your team.");
        team.getMembers().forEach(member -> {
            Player player = Bukkit.getPlayer(member.getUuid());
            if (player == null) return;
            Utils.msg(player, "&c" + me.getName() + " &ahas kicked &c" + toKick.getName() + " &afrom your team.");
        });

    }
}
