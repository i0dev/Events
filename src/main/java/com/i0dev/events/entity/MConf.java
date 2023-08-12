package com.i0dev.events.entity;

import com.i0dev.events.entity.object.*;
import com.i0dev.events.entity.object.events.ParkourEvent;
import com.i0dev.events.util.Cuboid;
import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.item.DataItemStack;
import com.massivecraft.massivecore.item.DataPotionEffect;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@Getter
@EditorName("config")
public class MConf extends Entity<MConf> {

    protected static transient MConf i;

    public static MConf get() {
        return i;
    }

    public List<String> aliasesEvents = MUtil.list("events");

    public EventSettings getEventById(EventType eventType) {
        switch (eventType) {
            case PARKOUR:
                return parkourEvent;
            default:
                return null;
        }
    }

    public PS spawnLocationPS = PS.valueOf(new Location(Bukkit.getWorld("mobtest"), 0, 120, 0));

    ParkourEvent parkourEvent = new ParkourEvent(
            10,
            10, //TODO
            true,
            true,
            new Location(Bukkit.getWorld("mobtest"), -185, 120, 55),
            EventStatus.COUNTDOWN,
            new Location(Bukkit.getWorld("mobtest"), -185, 110, 55),
            MUtil.list(new DataPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 120, 0, false, false))),
            MUtil.list(EventStatus.ACTIVE),
            ArmorSettings.builder()
                    .boots(new DataItemStack(new ItemStack(Material.DIAMOND_BOOTS)))
                    .build(),
            EventStatus.ACTIVE,
            false,
            true,
            new Cuboid(new Location(Bukkit.getWorld("mobtest"), -195, 118, 53), new Location(Bukkit.getWorld("mobtest"), -175, 110, 53)).serialize(),
            900000,
            MUtil.list(EventStatus.COUNTDOWN, EventStatus.ACTIVE),
            MUtil.list(30L, 15L, 10L, 5L, 4L, 3L, 2L, 1L, 0L),
            new Cuboid(new Location(Bukkit.getWorld("mobtest"), -175, 115, -87), new Location(Bukkit.getWorld("mobtest"), -195, 109, -91)).serialize(),
            3,
            MUtil.list(
                    new PlacedReward(1, MUtil.list("give %player% diamond 3")),
                    new PlacedReward(2, MUtil.list("give %player% diamond 2")),
                    new PlacedReward(3, MUtil.list("give %player% diamond 1"))
            )
    );


}
