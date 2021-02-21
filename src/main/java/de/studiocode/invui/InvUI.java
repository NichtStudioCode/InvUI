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
    
    private static InvUI instance;
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private Plugin plugin;
    
    public static InvUI getInstance() {
        return instance == null ? instance = new InvUI() : instance;
    }
    
    public Plugin getPlugin() {
        if (plugin == null) {
            System.out.println("[InvUI] Retrieving plugin from PluginClassLoader... This may cause issues!");
            setPlugin(ReflectionUtils.getFieldValue(PLUGIN_CLASS_LOADER_PLUGIN_FIELD, getClass().getClassLoader()));
        }
        return plugin;
    }
    
    public void setPlugin(Plugin plugin) {
        if (this.plugin != null)
            throw new IllegalStateException("The plugin is already set!");
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
