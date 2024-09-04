package xyz.xenondevs.invui.scheduler;

import org.bukkit.scheduler.BukkitTask;

public class BukkitUniversalTask implements UniversalTask {

    private final BukkitTask bukkitTask;

    public BukkitUniversalTask(BukkitTask bukkitTask) {
       this.bukkitTask = bukkitTask;
    }

    @Override
    public void cancel() {
        this.bukkitTask.cancel();
    }
}
