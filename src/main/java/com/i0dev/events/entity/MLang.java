package com.i0dev.events.entity;

import com.massivecraft.massivecore.command.editor.annotation.EditorName;
import com.massivecraft.massivecore.store.Entity;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC)
@EditorName("config")
public class MLang extends Entity<MLang> {

    protected static transient MLang i;

    public static MLang get() {
        return i;
    }

    String prefix = "&8[&a&lEvents &8]&7";

}
