package de.studiocode.invui;

import de.studiocode.inventoryaccess.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static de.studiocode.inventoryaccess.util.ReflectionRegistry.PLUGIN_CLASS_LOADER_PLUGIN_FIELD;

public class InvUI implements Listener {
    
    private static InvUI instance;
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private final Plugin plugin;
    
    private InvUI() {
        plugin = ReflectionUtils.getFieldValue(PLUGIN_CLASS_LOADER_PLUGIN_FIELD, getClass().getClassLoader());
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public static InvUI getInstance() {
        return instance == null ? instance = new InvUI() : instance;
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public void addDisableHandler(Runnable runnable) {
        disableHandlers.add(runnable);
    }
    
    @EventHandler
    public void handlePluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(plugin)) {
            disableHandlers.forEach(Runnable::run);
        }
    }
    
}
