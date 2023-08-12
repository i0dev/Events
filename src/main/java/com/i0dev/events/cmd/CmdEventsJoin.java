package com.i0dev.events.cmd;

import com.i0dev.events.cmd.type.TypeActiveEvent;
import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.object.bukkitevent.PlayerJoinEventEvent;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import org.bukkit.Bukkit;

public class CmdEventsJoin extends EventsCommand {

    public CmdEventsJoin() {
        this.addParameter(TypeActiveEvent.get(), "join code");
        this.addRequirements(RequirementIsPlayer.get());
    }


    @Override
    public void perform() throws MassiveException {
        ActiveEvent activeEvent = this.readArg();
        if (activeEvent == null) {
            msg("&cEvent not found.");
            return;
        }
        activeEvent.joinEvent(me);
    }
}
