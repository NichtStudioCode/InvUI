package xyz.xenondevs.invui.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaUniversalTask implements UniversalTask {

    private final ScheduledTask scheduledTask;

    public FoliaUniversalTask(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }

    @Override
    public void cancel() {
        this.scheduledTask.cancel();
    }
}
