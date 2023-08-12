package com.i0dev.events.cmd;

import com.i0dev.events.cmd.type.TypeActiveEvent;
import com.i0dev.events.entity.ActiveEvent;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;

public class CmdEventsEnd extends EventsCommand {

    public CmdEventsEnd() {
        this.addParameter(TypeActiveEvent.get(), "join code");
    }


    @Override
    public void perform() throws MassiveException {
        ActiveEvent activeEvent = this.readArg();
        if (activeEvent == null) {
            msg("&cEvent not found.");
            return;
        }
        activeEvent.endEvent(false);
    }
}
