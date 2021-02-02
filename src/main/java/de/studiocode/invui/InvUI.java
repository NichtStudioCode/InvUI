package de.studiocode.invui;

import de.studiocode.invui.util.reflection.ReflectionUtils;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static de.studiocode.invui.util.reflection.ReflectionRegistry.PLUGIN_CLASS_LOADER_PLUGIN_FIELD;

public class InvUI {
    
    private static InvUI instance;
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private final Plugin plugin;
    
    public InvUI() {
        this.plugin = ReflectionUtils.getFieldValue(PLUGIN_CLASS_LOADER_PLUGIN_FIELD, getClass().getClassLoader());
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
    
    public void onDisable() {
        disableHandlers.forEach(Runnable::run);
    }
    
}
