package com.i0dev.events.entity.object.bukkitevent;

import com.i0dev.events.entity.ActiveEvent;
import com.i0dev.events.entity.object.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class EventSwitchStatus extends Event {

    private static final HandlerList handlers = new HandlerList();

    public EventStatus oldStatus;
    public EventStatus newStatus;
    public ActiveEvent activeEvent;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
