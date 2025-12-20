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
import java.util.function.BiConsumer;

/**
 * Main class of InvUI, managing the plugin instance and global settings.
 */
public final class InvUI implements Listener {
    
    private static final InvUI INSTANCE = new InvUI();
    private static final @Nullable Boolean FIRE_BUKKIT_INVENTORY_EVENTS_OVERRIDE;
    
    static {
        String property = System.getProperty("invui.fireBukkitInventoryEvents");
        if (property != null) {
            FIRE_BUKKIT_INVENTORY_EVENTS_OVERRIDE = Boolean.parseBoolean(property);
        } else {
            FIRE_BUKKIT_INVENTORY_EVENTS_OVERRIDE = null;
        }
    }
    
    private final List<Runnable> disableHandlers = new ArrayList<>();
    private @Nullable Plugin plugin;
    private BiConsumer<? super String, ? super Throwable> exceptionHandler = (msg, e) -> getPlugin().getComponentLogger().error(msg, e);
    private boolean fireBukkitInventoryEvents = true;
    
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
     * Sets a handler for exceptions that were thrown in user-provided code but suppressed by InvUI,
     * such as when handling inventory events or similar.
     *
     * @param exceptionHandler The new exception handler.
     */
    public void setExceptionHandler(BiConsumer<? super String, ? super Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    /**
     * Handles an exception using the configured
     * {@link #setExceptionHandler(BiConsumer) exception handler}.
     *
     * @param msg An additional message that provides more context.
     * @param t   The exception that was thrown.
     */
    public void handleException(String msg, Throwable t) {
        exceptionHandler.accept(msg, t);
    }
    
    /**
     * Whether Bukkit's {@link org.bukkit.event.inventory.InventoryClickEvent} and
     * {@link org.bukkit.event.inventory.InventoryDragEvent} should be called for interactions with InvUI inventories.
     * <p>
     * By default, this is {@code true}. It can be changed using {@link #setFireBukkitInventoryEvents(boolean)} or
     * with the system property {@code invui.fireBukkitClickEvents}. If the system property is present,
     * it overrides the value set using {@link #setFireBukkitInventoryEvents(boolean)}.
     *
     * @return Whether Bukkit's inventory events should be fired for interactions with InvUI inventories.
     */
    public boolean isFireBukkitInventoryEvents() {
        if (FIRE_BUKKIT_INVENTORY_EVENTS_OVERRIDE != null)
            return FIRE_BUKKIT_INVENTORY_EVENTS_OVERRIDE;
        return fireBukkitInventoryEvents;
    }
    
    /**
     * Sets whether Bukkit's {@link org.bukkit.event.inventory.InventoryClickEvent} and
     * {@link org.bukkit.event.inventory.InventoryDragEvent} should be called for interactions with InvUI inventories.
     * <p>
     * By default, this is {@code true}. It can be changed using this method or
     * with the system property {@code invui.fireBukkitInventoryEvents}. If the system property is present,
     * it overrides the value set using this method.
     *
     * @param fireBukkitInventoryEvents Whether Bukkit's {@link org.bukkit.event.inventory.InventoryClickEvent} and
     *                                  {@link org.bukkit.event.inventory.InventoryDragEvent}
     *                                  should be called for interactions with InvUI inventories.
     */
    public void setFireBukkitInventoryEvents(boolean fireBukkitInventoryEvents) {
        this.fireBukkitInventoryEvents = fireBukkitInventoryEvents;
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
