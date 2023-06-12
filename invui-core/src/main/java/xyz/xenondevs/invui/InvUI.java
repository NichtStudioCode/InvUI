package xyz.xenondevs.invui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.util.ReflectionRegistry;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static xyz.xenondevs.inventoryaccess.util.ReflectionRegistry.PAPER_PLUGIN_CLASS_LOADER_GET_LOADED_JAVA_PLUGIN_METHOD;
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
            plugin = tryFindPlugin();
            
            if (plugin == null)
                throw new IllegalStateException("Plugin is not set. Set it using InvUI.getInstance().setPlugin(plugin);");
        }
        
        return plugin;
    }
    
    private @Nullable Plugin tryFindPlugin() {
        ClassLoader loader = getClass().getClassLoader();
        
        if (ReflectionRegistry.PLUGIN_CLASS_LOADER_CLASS.isInstance(loader)) {
            return ReflectionUtils.getFieldValue(PLUGIN_CLASS_LOADER_PLUGIN_FIELD, loader);
        } else if (ReflectionRegistry.PAPER_PLUGIN_CLASS_LOADER_CLASS.isInstance(loader)) {
            return ReflectionUtils.invokeMethod(PAPER_PLUGIN_CLASS_LOADER_GET_LOADED_JAVA_PLUGIN_METHOD, loader);
        }
        
        return null;
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
