package com.i0dev.events.entity.object;

public enum EventType {

    PARKOUR("parkour");

    final String id;

    EventType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
