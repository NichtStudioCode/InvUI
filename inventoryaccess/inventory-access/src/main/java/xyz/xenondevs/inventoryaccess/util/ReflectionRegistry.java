package xyz.xenondevs.inventoryaccess.util;

import com.mojang.authlib.GameProfile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static xyz.xenondevs.inventoryaccess.util.ReflectionUtils.*;


public class ReflectionRegistry {
    
    public static final int VERSION = getVersionNumber();
    
    public static final String CRAFT_BUKKIT_PACKAGE_PATH = getCB();
    public static final String BUKKIT_PACKAGE_PATH = "org.bukkit.";
    
    // Classes
    public static final Class<?> PLUGIN_CLASS_LOADER_CLASS = getBukkitClass("plugin.java.PluginClassLoader");
    public static final Class<?> CB_CRAFT_META_SKULL_CLASS = getCBClass("inventory.CraftMetaSkull");
    public static final Class<?> CB_CRAFT_META_ITEM_CLASS = getCBClass("inventory.CraftMetaItem");
    
    // Methods
    public static final Method CB_CRAFT_META_SKULL_SET_PROFILE_METHOD = getMethod(CB_CRAFT_META_SKULL_CLASS, true, "setProfile", GameProfile.class);
    
    // Fields
    public static final Field PLUGIN_CLASS_LOADER_PLUGIN_FIELD = getField(PLUGIN_CLASS_LOADER_CLASS, true, "plugin");
    public static final Field CB_CRAFT_META_ITEM_DISPLAY_NAME_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "displayName");
    public static final Field CB_CRAFT_META_ITEM_LORE_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "lore");
    
}
