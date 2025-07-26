package xyz.xenondevs.invui;

import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Main class of InvUI, managing the plugin instance.
 */
public final class InvUI implements Listener {
    
    private static final InvUI INSTANCE = new InvUI();
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private @Nullable Plugin plugin;
    
    private InvUI() {}
    
    /**
     * Returns the singleton instance of InvUI.
     *
     * @return the InvUI instance
     */
    public static InvUI getInstance() {
        return INSTANCE;
    }
    
    /**
     * Gets the plugin instance that InvUI is running under.
     * If possible, the plugin instance is inferred from the class loader.
     * If this is not possible, the plugin instance must be set manually using {@link #setPlugin(Plugin)} beforehand.
     *
     * @return The plugin instance.
     * @throws IllegalStateException If the plugin instance is not set and cannot be inferred.
     */
    public Plugin getPlugin() {
        if (plugin == null) {
            setPlugin(tryFindPlugin());
            
            if (plugin == null)
                throw new IllegalStateException("Plugin is not set. Set it using InvUI.getInstance().setPlugin(plugin);");
        }
        
        return plugin;
    }
    
    @SuppressWarnings({"CallToPrintStackTrace", "UnstableApiUsage"})
    private @Nullable Plugin tryFindPlugin() {
        ClassLoader classLoader = getClass().getClassLoader();
        
        try {
            if (classLoader instanceof ConfiguredPluginClassLoader pluginClassLoader) {
                return pluginClassLoader.getPlugin();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Sets the plugin instance that InvUI is running under.
     * This is used to register event listeners, schedule tasks, etc.
     *
     * @param plugin The plugin instance to set.
     * @throws IllegalStateException If the plugin instance is already set.
     */
    public void setPlugin(@Nullable Plugin plugin) {
        if (this.plugin != null)
            throw new IllegalStateException("Plugin is already set");
        
        if (plugin == null)
            return;
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    
    /**
     * Gets the logger of the configured plugin instance.
     *
     * @return The logger of the plugin instance.
     * @throws IllegalStateException If the plugin instance is not set and cannot be inferred.
     */
    public Logger getLogger() {
        return getPlugin().getLogger();
    }
    
    /**
     * Adds a {@link Runnable} that is executed when the plugin is disabled.
     *
     * @param runnable The runnable to execute on plugin disable.
     */
    public void addDisableHandler(Runnable runnable) {
        disableHandlers.add(runnable);
    }
    
    @EventHandler
    private void handlePluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            disableHandlers.forEach(Runnable::run);
        }
    }
    
}
