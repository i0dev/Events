package com.i0dev.events;

import com.i0dev.events.entity.*;
import com.i0dev.events.integration.PlaceholderAPI;
import com.massivecraft.massivecore.MassivePlugin;
import com.massivecraft.massivecore.collections.MassiveList;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class EventsPlugin extends MassivePlugin {

    private static EventsPlugin i;

    public EventsPlugin() {
        EventsPlugin.i = this;
    }

    public static EventsPlugin get() {
        return i;
    }

    @Override
    public void onEnableInner() {
        this.activateAuto();
    }


    @Override
    public void onEnable() {
        super.onEnable();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(this).register();
        } else {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
        }
        ActiveEventColl.get().getAll().forEach(ActiveEvent::initialize);
    }

    @Override
    public List<Class<?>> getClassesActiveColls() {
        return new MassiveList<>(
                MConfColl.class,
                MLangColl.class,
                MPlayerColl.class,
                ActiveEventColl.class
        );
    }

    public WorldEditPlugin getWorldEdit() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        return p instanceof WorldEditPlugin ? (WorldEditPlugin) p : null;
    }

}