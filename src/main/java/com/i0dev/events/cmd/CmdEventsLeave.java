package com.i0dev.events.cmd;

import com.i0dev.events.entity.ActiveEvent;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;

public class CmdEventsLeave extends EventsCommand {

    public CmdEventsLeave() {
        this.addRequirements(RequirementIsPlayer.get());
    }


    @Override
    public void perform() {
        ActiveEvent event = ActiveEvent.getEventByPlayer(me);
        if (event == null) {
            event = ActiveEvent.getEventBySpectator(me);
            if (event == null) {
                msg("&cYou are not in an event.");
                return;
            }
            return;
        }
        event.leaveEvent(me);
    }
}
