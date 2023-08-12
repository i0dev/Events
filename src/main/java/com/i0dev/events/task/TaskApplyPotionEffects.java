package com.i0dev.events.task;

import com.i0dev.events.entity.ActiveEventColl;
import com.massivecraft.massivecore.ModuloRepeatTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.nio.Buffer;
import java.util.BitSet;

public class TaskApplyPotionEffects extends ModuloRepeatTask {

    public static TaskApplyPotionEffects i = new TaskApplyPotionEffects();

    public static TaskApplyPotionEffects get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return 5000L; // 5 seconds
    }

    @Override
    public void invoke(long l) {
        // potion effects
        ActiveEventColl.get().getAll().forEach(activeEvent -> {
            activeEvent.getParticipants().forEach(uuid -> {
                if (activeEvent.getSettings().getApplyPotionEffectsOnStatuses().contains(activeEvent.getStatus())) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        activeEvent.getSettings().getPermanentPotionEffects().forEach(potionEffect -> {
                            player.addPotionEffect(potionEffect.toBukkit());
                        });
                    }
                }
            });
        });
    }
}
