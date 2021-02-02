package de.studiocode.invgui;

import de.studiocode.invgui.util.reflection.ReflectionUtils;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static de.studiocode.invgui.util.reflection.ReflectionRegistry.PLUGIN_CLASS_LOADER_PLUGIN_FIELD;

public class InvGui {
    
    private static InvGui instance;
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private final Plugin plugin;
    
    public InvGui() {
        this.plugin = ReflectionUtils.getFieldValue(PLUGIN_CLASS_LOADER_PLUGIN_FIELD, getClass().getClassLoader());
    }
    
    public static InvGui getInstance() {
        return instance == null ? instance = new InvGui() : instance;
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public void addDisableHandler(Runnable runnable) {
        disableHandlers.add(runnable);
    }
    
    public void onDisable() {
        disableHandlers.forEach(Runnable::run);
    }
    
}
