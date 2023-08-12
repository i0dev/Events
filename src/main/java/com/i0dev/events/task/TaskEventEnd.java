package com.i0dev.events.task;

import com.i0dev.events.entity.ActiveEventColl;
import com.massivecraft.massivecore.ModuloRepeatTask;

public class TaskEventEnd extends ModuloRepeatTask {

    public static TaskEventEnd i = new TaskEventEnd();

    public static TaskEventEnd get() {
        return i;
    }

    @Override
    public long getDelayMillis() {
        return 1000L; // 1 seconds
    }

    @Override
    public void invoke(long l) {
        ActiveEventColl.get().getAll().forEach(activeEvent -> {
            long maxRunTime = activeEvent.getSettings().getMaxRunTimeMillis();
            long startTime = activeEvent.getStartTime();
            if (startTime == 0) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= maxRunTime) {
                activeEvent.endEvent(true);
            }
        });
    }
}
