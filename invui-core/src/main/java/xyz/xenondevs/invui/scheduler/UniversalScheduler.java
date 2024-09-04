package xyz.xenondevs.invui.scheduler;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.xenondevs.invui.util.FoliaUtils;

@SuppressWarnings("deprecation")
public class UniversalScheduler {

    private final GlobalRegionScheduler globalRegionScheduler;
    private final AsyncScheduler asyncScheduler;
    private final BukkitScheduler bukkitScheduler;

    private final Plugin plugin;

    public UniversalScheduler(Plugin plugin) {
        this.plugin = plugin;

        Server server = this.plugin.getServer();

        this.globalRegionScheduler = server.getGlobalRegionScheduler();
        this.asyncScheduler = server.getAsyncScheduler();
        this.bukkitScheduler = server.getScheduler();
    }

    public UniversalTask runTaskTimer(Runnable runnable, long delay, long period) {
        if (FoliaUtils.isFolia()) {
            return new FoliaUniversalTask(this.globalRegionScheduler.runAtFixedRate(this.plugin, task -> runnable.run(), FoliaUtils.getFoliaDelay(delay), period));
        }
        return new BukkitUniversalTask(this.bukkitScheduler.runTaskTimer(this.plugin, runnable, delay, period));
    }

    public UniversalTask runTaskAsynchronously(Runnable runnable) {
        if (FoliaUtils.isFolia()) {
            return new FoliaUniversalTask(this.asyncScheduler.runNow(this.plugin, task -> runnable.run()));
        }
        return new BukkitUniversalTask(this.bukkitScheduler.runTaskAsynchronously(this.plugin, runnable));
    }

    public UniversalTask runTask(Runnable runnable) {
        if (FoliaUtils.isFolia()) {
            return new FoliaUniversalTask(this.globalRegionScheduler.run(this.plugin, task -> runnable.run()));
        }
        return new BukkitUniversalTask(this.bukkitScheduler.runTask(this.plugin, runnable));
    }

    public UniversalTask runTaskLater(Runnable runnable, long delay) {
        if (FoliaUtils.isFolia()) {
            if (delay <= 0) {
                return this.runTask(runnable);
            }
            return new FoliaUniversalTask(this.globalRegionScheduler.runDelayed(this.plugin, task -> runnable.run(), delay));
        }
        return new BukkitUniversalTask(this.bukkitScheduler.runTaskLater(this.plugin, runnable, delay));
    }
}
