package xyz.xenondevs.invui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static xyz.xenondevs.inventoryaccess.util.ReflectionRegistry.PLUGIN_CLASS_LOADER_PLUGIN_FIELD;

public class InvUI implements Listener {
    
    private static InvUI instance;
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private Plugin plugin;
    
    private InvUI() {
    }
    
    public static @NotNull InvUI getInstance() {
        return instance == null ? instance = new InvUI() : instance;
    }
    
    public @NotNull Plugin getPlugin() {
        if (plugin == null) {
            // get plugin from class loader if it wasn't set manually
            plugin = ReflectionUtils.getFieldValue(PLUGIN_CLASS_LOADER_PLUGIN_FIELD, getClass().getClassLoader());
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
        
        return plugin;
    }
    
    public void setPlugin(@NotNull Plugin plugin) {
        if (this.plugin != null)
            throw new IllegalStateException("Plugin is already set");
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }
    
    public @NotNull Logger getLogger() {
        return getPlugin().getLogger();
    }
    
    public void addDisableHandler(@NotNull Runnable runnable) {
        disableHandlers.add(runnable);
    }
    
    @EventHandler
    private void handlePluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            disableHandlers.forEach(Runnable::run);
        }
    }
    
}
