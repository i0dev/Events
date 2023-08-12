package com.i0dev.events.entity;

import com.massivecraft.massivecore.store.Coll;

public class ActiveEventColl extends Coll<ActiveEvent> {

    private static final ActiveEventColl i = new ActiveEventColl();

    public static ActiveEventColl get() {
        return i;
    }

    @Override
    public void onTick() {
        super.onTick();
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }
}