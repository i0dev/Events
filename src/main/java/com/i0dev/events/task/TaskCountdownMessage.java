package com.i0dev.events.task;

import com.i0dev.events.entity.ActiveEventColl;
import com.i0dev.events.entity.object.EventStatus;
import com.i0dev.events.util.Utils;
import com.massivecraft.massivecore.ModuloRepeatTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TaskCountdownMessage extends ModuloRepeatTask {

    public static TaskCountdownMessage i = new TaskCountdownMessage();

    public static TaskCountdownMessage get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return 1000L; // 1 seconds
    }

    @Override
    public boolean isSync() {
        return false;
    }

    @Override
    public void invoke(long l) {
        ActiveEventColl.get().getAll().forEach(activeEvent -> {
            if (activeEvent.getSecondsUntilStartSendTitleTimes().isEmpty()) return;
            long countdownSeconds = activeEvent.getCountdown();
            long countdownStartedAtTime = activeEvent.getStartedCountdown();
            long secondsUntilStart = countdownSeconds - ((System.currentTimeMillis() - countdownStartedAtTime) / 1000);
            List<Long> toRemove = new ArrayList<>();
            String name = activeEvent.getEventType().getId().substring(0, 1).toUpperCase() + activeEvent.getEventType().getId().substring(1);
            activeEvent.getSecondsUntilStartSendTitleTimes().forEach(secondsRemaining -> {
                if (secondsRemaining >= secondsUntilStart) {
                    activeEvent.getEveryone().forEach(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) return;
                        if (secondsRemaining <= 0)
                            player.sendTitle(Utils.c("&a&lThe event has started!"), Utils.c("&e&lGood luck!"), 10, 40, 10);
                        else
                            player.sendTitle(Utils.color("&a" + name + " event starting in..."), Utils.color("&c" + secondsRemaining + "&7 seconds"), 10, 20, 10);
                        toRemove.add(secondsRemaining);
                    });
                }
            });
            int removed = toRemove.size();
            toRemove.forEach(secondsRemaining -> activeEvent.getSecondsUntilStartSendTitleTimes().remove(secondsRemaining));
            if (removed > 0)
                activeEvent.changed();
        });
    }
}
