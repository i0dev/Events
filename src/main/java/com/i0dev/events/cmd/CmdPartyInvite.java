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

public class CmdPartyInvite extends EventsCommand {

    public CmdPartyInvite() {
        this.addParameter(TypePlayer.get(), "player");
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    protected <T extends Enum<T>> T calcPerm() {
        return (T) Perm.PARTY_INVITE;
    }

    @Override
    public void perform() throws MassiveException {
        ActiveEvent activeEvent = ActiveEvent.getEventByPlayer(me);
        if (activeEvent == null) {
            msg("<b>You are not in an event.");
            return;
        }
        Team team = activeEvent.getTeamByPlayer(me);
        if (!team.getLeader().getUuid().equals(me.getUniqueId())) {
            msg("<b>You are not the leader of your team. Only the leader can invite players.");
            return;
        }
        Player invitee = this.readArg();
        if (team.getInvites().contains(invitee.getUniqueId())) {
            msg("<b>You have already invited this player.");
            return;
        }
        team.invitePlayer(invitee);
        activeEvent.changed();

        team.getMembers().forEach(member -> {
            Player player = Bukkit.getPlayer(member.getUuid());
            if (player == null) return;
            Utils.msg(player, "&c" + invitee.getName() + " &ahas been invited to your team.");
        });
    }
}
