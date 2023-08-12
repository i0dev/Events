package com.i0dev.events.cmd;

import com.i0dev.events.Perm;
import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.object.Team;
import com.i0dev.events.util.Utils;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdPartyRename extends EventsCommand {

    public CmdPartyRename() {
        this.addParameter(TypeString.get(), "new name", true);
        this.addRequirements(RequirementIsPlayer.get());
    }

    @Override
    protected <T extends Enum<T>> T calcPerm() {
        return (T) Perm.PARTY_RENAME;
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
            msg("<b>You are not the leader of your team. only the leader can rename the team.");
            return;
        }
        String newName = this.readArg();

        // make sure the newName is alphanumeric with spaces, underscores, and dashes
        if (!newName.matches("^[a-zA-Z0-9_ -]*$")) {
            msg("<b>Team names can only contain letters, numbers, spaces, underscores, and dashes.");
            return;
        }
        team.rename(newName);
        activeEvent.changed();

        team.getMembers().forEach(member -> {
            Player player = Bukkit.getPlayer(member.getUuid());
            if (player == null) return;
            Utils.msg(player, "&aYour team has been renamed to &c" + newName + "&a.");
        });
    }
}
