package com.i0dev.events.entity;

import com.massivecraft.massivecore.item.DataItemStack;
import com.massivecraft.massivecore.store.SenderEntity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MPlayer extends SenderEntity<MPlayer> {

    public static MPlayer get(Object oid) {
        return MPlayerColl.get().get(oid);
    }

    //   slot  ,  item
    Map<Integer, DataItemStack> savedInventory = new HashMap<>();

    public void saveInventory() {
        savedInventory.clear();
        Player player = getPlayer();
        AtomicInteger slot = new AtomicInteger(0);
        Arrays.stream(player.getInventory().getContents()).forEach(itemStack -> {
            savedInventory.put(slot.get(), new DataItemStack(Objects.requireNonNullElseGet(itemStack, () -> new ItemStack(Material.AIR))));
            slot.getAndIncrement();
        });
        this.changed();
    }

    public void restoreInventory() {
        Player player = getPlayer();
        player.getInventory().clear();
        savedInventory.forEach((slot, item) -> player.getInventory().setItem(slot, item.toBukkit()));
        savedInventory.clear();
        this.changed();
    }
}
