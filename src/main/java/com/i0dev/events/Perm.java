package com.i0dev.events;

import com.massivecraft.massivecore.Identified;
import com.massivecraft.massivecore.util.PermissionUtil;
import org.bukkit.permissions.Permissible;

public enum Perm implements Identified {

    BASECOMMAND("basecommand"),

    END("end"),
    JOIN("join"),
    LEAVE("leave"),
    SPECTATE("spectate"),
    START("start"),

    PARTY("party"),
    PARTY_INFO("party.info"),
    PARTY_INVITE("party.invite"),
    PARTY_RENAME("party.rename"),
    PARTY_JOIN("party.join"),
    PARTY_LEAVE("party.leave"),
    PARTY_KICK("party.kick"),


    VERSION("version");

    private final String id;

    Perm(String id) {
        this.id = "events." + id;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean has(Permissible permissible, boolean verboose) {
        return PermissionUtil.hasPermission(permissible, this, verboose);
    }

    public boolean has(Permissible permissible) {
        return PermissionUtil.hasPermission(permissible, this);
    }

}
