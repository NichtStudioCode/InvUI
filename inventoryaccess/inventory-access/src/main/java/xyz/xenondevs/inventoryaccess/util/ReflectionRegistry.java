package xyz.xenondevs.inventoryaccess.util;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static xyz.xenondevs.inventoryaccess.util.ReflectionUtils.*;

public class ReflectionRegistry {
    
    public static final String CRAFT_BUKKIT_PACKAGE_PATH = Bukkit.getServer().getClass().getPackage().getName();
    
    // Classes
    public static final Class<?> PLUGIN_CLASS_LOADER_CLASS = ReflectionUtils.getClass("org.bukkit.plugin.java.PluginClassLoader");
    public static final Class<?> PAPER_PLUGIN_CLASS_LOADER_CLASS = getClassOrNull("io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader");
    public static final Class<?> CB_CRAFT_META_SKULL_CLASS = getCBClass("inventory.CraftMetaSkull");
    public static final Class<?> CB_CRAFT_META_ITEM_CLASS = getCBClass("inventory.CraftMetaItem");
    
    // Methods
    public static final Method PAPER_PLUGIN_CLASS_LOADER_GET_LOADED_JAVA_PLUGIN_METHOD;
    public static final Method CB_CRAFT_META_SKULL_SET_PROFILE_METHOD = getMethod(CB_CRAFT_META_SKULL_CLASS, true, "setProfile", GameProfile.class);
    
    // Fields
    public static final Field PLUGIN_CLASS_LOADER_PLUGIN_FIELD = getField(PLUGIN_CLASS_LOADER_CLASS, true, "plugin");
    public static final Field CB_CRAFT_META_ITEM_DISPLAY_NAME_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "displayName");
    public static final Field CB_CRAFT_META_ITEM_LORE_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "lore");
    
    static {
        Method getPlugin = getMethodOrNull(PAPER_PLUGIN_CLASS_LOADER_CLASS, false, "getLoadedJavaPlugin");
        if (getPlugin == null) getPlugin = getMethodOrNull(PAPER_PLUGIN_CLASS_LOADER_CLASS, false, "getPlugin");
        PAPER_PLUGIN_CLASS_LOADER_GET_LOADED_JAVA_PLUGIN_METHOD = getPlugin;
    }
    
}
