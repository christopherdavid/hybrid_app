package com.neatorobotics.android.slide.framework.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskUtils {

    private static final int MAX_THREAD_POOL_SIZE = 10;

    public static final void scheduleTask(Runnable task, long delayInMilliSeconds) {
        ScheduledExecutorService schedulerService = Executors.newScheduledThreadPool(MAX_THREAD_POOL_SIZE);
        schedulerService.schedule(task, delayInMilliSeconds, TimeUnit.MILLISECONDS);
    }

    public static void sleep(long timeInMs) {
        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            // ignore
        }
    }

}
