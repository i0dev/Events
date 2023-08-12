package com.i0dev.events.cmd;

import com.i0dev.events.EventsPlugin;
import com.i0dev.events.cmd.type.TypeEvent;
import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.object.EventSettings;
import com.i0dev.events.entity.object.EventType;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import org.bukkit.Bukkit;

public class CmdEventsStart extends EventsCommand {

    public CmdEventsStart() {
        this.addParameter(TypeEvent.get(), "event id");
        this.addParameter(TypeInteger.get(), "countdown");
    }


    @Override
    public void perform() throws MassiveException {
        EventSettings eventSettings = this.readArg();
        int countdown = this.readArg();

        ActiveEvent event = ActiveEvent.create(EventType.valueOf(eventSettings.getId().toUpperCase()), countdown);
        Bukkit.broadcastMessage("Event " + eventSettings.getId() + " has started with a countdown of " + countdown + " seconds." +
                " Join with /events join " + event.getJoinCode() + ".");

        Bukkit.getScheduler().runTaskLater(EventsPlugin.get(), event::start, countdown * 20L);
    }
}
