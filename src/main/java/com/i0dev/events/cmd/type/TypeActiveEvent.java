package com.i0dev.events.cmd.type;

import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.ActiveEventColl;
import com.massivecraft.massivecore.command.type.TypeAbstractChoice;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class TypeActiveEvent extends TypeAbstractChoice<ActiveEvent> {

    private static final TypeActiveEvent i = new TypeActiveEvent();

    public static TypeActiveEvent get() {
        return i;
    }

    public TypeActiveEvent() {
        super(ActiveEvent.class);
    }

    public String getName() {
        return "text";
    }

    public ActiveEvent read(String arg, CommandSender sender) {
        return ActiveEvent.getEventByJoinCode(arg);
    }

    public Collection<String> getTabList(CommandSender sender, String arg) {
        return ActiveEventColl.get().getAll().stream().map(ActiveEvent::getJoinCode).toList();
    }
}

