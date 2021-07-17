package de.studiocode.inventoryaccess.util;

import java.lang.reflect.Field;

import static de.studiocode.inventoryaccess.util.ReflectionUtils.*;


public class ReflectionRegistry {
    
    public static final int VERSION = getVersionNumber();
    public static final String INV_ACCESS_VERSION = getInventoryAccessVersion();
    
    public static final String CRAFT_BUKKIT_PACKAGE_PATH = getCB();
    public static final String BUKKIT_PACKAGE_PATH = "org.bukkit.";
    
    // Classes
    public static final Class<?> CB_CRAFT_META_SKULL_CLASS = getCBClass("inventory.CraftMetaSkull");
    public static final Class<?> PLUGIN_CLASS_LOADER_CLASS = getBukkitClass("plugin.java.PluginClassLoader");
    public static final Class<?> CB_CRAFT_META_ITEM_CLASS = getCBClass("inventory.CraftMetaItem");
    public static final Class<?> CB_CRAFT_ITEM_STACK_CLASS = getCBClass("inventory.CraftItemStack");
    
    // Fields
    public static final Field CB_CRAFT_META_SKULL_PROFILE_FIELD = getField(CB_CRAFT_META_SKULL_CLASS, true, "profile");
    public static final Field PLUGIN_CLASS_LOADER_PLUGIN_FIELD = getField(PLUGIN_CLASS_LOADER_CLASS, true, "plugin");
    public static final Field CB_CRAFT_META_ITEM_INTERNAL_TAG_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "internalTag");
    public static final Field CB_CRAFT_META_ITEM_DISPLAY_NAME_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "displayName");
    public static final Field CB_CRAFT_META_ITEM_LORE_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "lore");
    public static final Field CB_CRAFT_ITEM_STACK_HANDLE_FIELD = ReflectionUtils.getField(CB_CRAFT_ITEM_STACK_CLASS, true, "handle");
    
}
