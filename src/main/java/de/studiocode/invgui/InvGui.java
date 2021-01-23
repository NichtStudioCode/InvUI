package de.studiocode.invgui;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class InvGui {
    
    private static InvGui instance;
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private Plugin plugin;
    
    public static InvGui getInstance() {
        return instance == null ? instance = new InvGui() : instance;
    }
    
    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public Plugin getPlugin() {
        if (plugin == null)
            throw new IllegalStateException("Please set your plugin using InvGui.getInstance().setPlugin");
        return plugin;
    }
    
    public void addDisableHandler(Runnable runnable) {
        disableHandlers.add(runnable);
    }
    
    public void onDisable() {
        disableHandlers.forEach(Runnable::run);
    }
    
}
