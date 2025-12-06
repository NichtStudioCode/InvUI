package xyz.xenondevs.inventoryaccess.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static xyz.xenondevs.inventoryaccess.util.ReflectionUtils.*;

public class ReflectionRegistry {
    
    public static final String CRAFT_BUKKIT_PACKAGE_PATH = Bukkit.getServer().getClass().getPackage().getName();
    
    // Classes
    public static final Class<?> PLUGIN_CLASS_LOADER_CLASS = ReflectionUtils.getClass("org.bukkit.plugin.java.PluginClassLoader");
    public static final Class<?> PAPER_PLUGIN_CLASS_LOADER_CLASS = getClassOrNull("io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader");
    public static final Class<?> CB_CRAFT_META_SKULL_CLASS = getCBClass("inventory.CraftMetaSkull");
    public static final Class<?> CB_CRAFT_META_ITEM_CLASS = getCBClass("inventory.CraftMetaItem");
    public static final Class<?> RESOLVABLE_PROFILE_CLASS = getClassOrNull("net.minecraft.world.item.component.ResolvableProfile");
    
    // Constructors
    public static final Constructor<?> RESOLVABLE_PROFILE_FROM_GAME_PROFILE_CONSTRUCTOR = getConstructorOrNull(RESOLVABLE_PROFILE_CLASS, false, GameProfile.class);
    
    // Methods
    public static final Method PAPER_PLUGIN_CLASS_LOADER_GET_LOADED_JAVA_PLUGIN_METHOD;
    public static final Method CB_CRAFT_META_SKULL_SET_PROFILE_METHOD = getMethodOrNull(CB_CRAFT_META_SKULL_CLASS, true, "setProfile", GameProfile.class); // since spigot 1.14.4 or paper 1.15.1
    public static final Method CB_CRAFT_META_SKULL_SET_RESOLVABLE_PROFILE_METHOD = getMethodOrNull(CB_CRAFT_META_SKULL_CLASS, true, "setProfile", RESOLVABLE_PROFILE_CLASS);
    
    // Fields
    public static final Field PLUGIN_CLASS_LOADER_PLUGIN_FIELD = getField(PLUGIN_CLASS_LOADER_CLASS, true, "plugin");
    public static final Field CB_CRAFT_META_ITEM_DISPLAY_NAME_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "displayName");
    public static final Field CB_CRAFT_META_ITEM_LORE_FIELD = getField(CB_CRAFT_META_ITEM_CLASS, true, "lore");
    public static final Field CB_CRAFT_META_SKULL_PROFILE_FIELD = getField(CB_CRAFT_META_SKULL_CLASS, true, "profile");
    
    static {
        Method getPlugin = getMethodOrNull(PAPER_PLUGIN_CLASS_LOADER_CLASS, false, "getLoadedJavaPlugin");
        if (getPlugin == null) getPlugin = getMethodOrNull(PAPER_PLUGIN_CLASS_LOADER_CLASS, false, "getPlugin");
        PAPER_PLUGIN_CLASS_LOADER_GET_LOADED_JAVA_PLUGIN_METHOD = getPlugin;
    }
    
}
