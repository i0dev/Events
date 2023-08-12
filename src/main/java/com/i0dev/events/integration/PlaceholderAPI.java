package com.i0dev.events.integration;

import com.i0dev.events.EventsPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class PlaceholderAPI extends PlaceholderExpansion {

    public PlaceholderAPI(EventsPlugin plugin) {
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "i0dev";
    }

    @Override
    public String getIdentifier() {
        return "events";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    NumberFormat format = NumberFormat.getInstance();

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        return null;
    }

}