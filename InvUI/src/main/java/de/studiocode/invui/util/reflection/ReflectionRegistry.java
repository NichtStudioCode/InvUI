package de.studiocode.invui.util.reflection;

import java.lang.reflect.Field;

import static de.studiocode.invui.util.reflection.ReflectionUtils.*;

public class ReflectionRegistry {
    
    public static final int VERSION = getVersion();
    
    public static final String NET_MINECRAFT_SERVER_PACKAGE_PATH = getNMS();
    public static final String CRAFT_BUKKIT_PACKAGE_PATH = getCB();
    public static final String BUKKIT_PACKAGE_PATH = "org.bukkit.";
    
    // Classes
    public static final Class<?> CB_CRAFT_META_SKULL_CLASS = getCBClass("inventory.CraftMetaSkull");
    public static final Class<?> PLUGIN_CLASS_LOADER_CLASS = getBukkitClass("plugin.java.PluginClassLoader");
    
    // Fields
    public static final Field CB_CRAFT_META_SKULL_PROFILE_FIELD = getField(CB_CRAFT_META_SKULL_CLASS, true, "profile");
    public static final Field PLUGIN_CLASS_LOADER_PLUGIN_FIELD = getField(PLUGIN_CLASS_LOADER_CLASS, true, "plugin");
    
}
