package xyz.xenondevs.invui.internal.util;

import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static xyz.xenondevs.invui.internal.util.ReflectionUtils.*;

public class ReflectionRegistry {
    
    public static final String CRAFT_BUKKIT_PACKAGE_PATH = Bukkit.getServer().getClass().getPackage().getName();
    
    // Classes
    public static final Class<?> PLUGIN_CLASS_LOADER_CLASS = ReflectionUtils.getClass("org.bukkit.plugin.java.PluginClassLoader");
    public static final @Nullable Class<?> PAPER_PLUGIN_CLASS_LOADER_CLASS = getClassOrNull("io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader");
    
    // Methods
    public static final Method PAPER_PLUGIN_CLASS_LOADER_GET_LOADED_JAVA_PLUGIN_METHOD;
    public static final Method PLAYER_ADVANCEMENTS_REGISTER_LISTENERS_METHOD = ReflectionUtils.getMethod(PlayerAdvancements.class, true, "registerListeners", ServerAdvancementManager.class);
    
    // Fields
    public static final Field PLUGIN_CLASS_LOADER_PLUGIN_FIELD = getField(PLUGIN_CLASS_LOADER_CLASS, true, "plugin");
    public static final Field SERVER_PLAYER_CONTAINER_LISTENER_FIELD = getField(ServerPlayer.class, true, "containerListener");
    
    static {
        Method getPlugin = getMethodOrNull(PAPER_PLUGIN_CLASS_LOADER_CLASS, false, "getLoadedJavaPlugin");
        if (getPlugin == null) getPlugin = getMethodOrNull(PAPER_PLUGIN_CLASS_LOADER_CLASS, false, "getPlugin");
        PAPER_PLUGIN_CLASS_LOADER_GET_LOADED_JAVA_PLUGIN_METHOD = getPlugin;
    }
    
}
