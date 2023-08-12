package com.i0dev.events.entity.object.bukkitevent;

import com.i0dev.events.entity.ActiveEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerSpectateEventEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public ActiveEvent activeEvent;

    public PlayerSpectateEventEvent(@NotNull Player who, ActiveEvent activeEvent) {
        super(who);
        this.activeEvent = activeEvent;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
