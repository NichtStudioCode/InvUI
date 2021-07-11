package de.studiocode.invui;

import de.studiocode.invui.util.reflection.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static de.studiocode.invui.util.reflection.ReflectionRegistry.PLUGIN_CLASS_LOADER_PLUGIN_FIELD;

public class InvUI implements Listener {
    
    private static final Object LOCK = new Object();
    private static volatile InvUI instance;
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private final Plugin plugin;
    
    private InvUI() {
        plugin = ReflectionUtils.getFieldValue(PLUGIN_CLASS_LOADER_PLUGIN_FIELD, getClass().getClassLoader());
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public static InvUI getInstance() {
        InvUI localRef = instance;
        
        if (localRef == null) {
            synchronized (LOCK) {
                localRef = instance;
                if (localRef == null) {
                    instance = localRef = new InvUI();
                }
            }
        }
        
        return localRef;
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
