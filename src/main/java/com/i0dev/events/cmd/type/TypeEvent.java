package com.i0dev.events.cmd.type;

import com.i0dev.events.entity.MConf;
import com.i0dev.events.entity.object.EventSettings;
import com.i0dev.events.entity.object.EventType;
import com.massivecraft.massivecore.command.type.TypeAbstractChoice;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

public class TypeEvent extends TypeAbstractChoice<EventSettings> {

    private static final TypeEvent i = new TypeEvent();

    public static TypeEvent get() {
        return i;
    }

    public TypeEvent() {
        super(EventSettings.class);
    }

    public String getName() {
        return "text";
    }

    public EventSettings read(String arg, CommandSender sender) {
        return MConf.get().getEventById(EventType.valueOf(arg.toUpperCase()));
    }

    public Collection<String> getTabList(CommandSender sender, String arg) {
        return EnumSet.allOf(EventType.class).stream().map(EventType::getId).collect(Collectors.toList());
    }
}

