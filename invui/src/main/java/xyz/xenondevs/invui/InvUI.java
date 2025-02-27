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

public class InvUI implements Listener {
    
    private static final InvUI INSTANCE = new InvUI();
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private @Nullable Plugin plugin;
    
    private InvUI() {
    }
    
    public static InvUI getInstance() {
        return INSTANCE;
    }
    
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
    
    public void setPlugin(@Nullable Plugin plugin) {
        if (this.plugin != null)
            throw new IllegalStateException("Plugin is already set");
        
        if (plugin == null)
            return;
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    
    public Logger getLogger() {
        return getPlugin().getLogger();
    }
    
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
