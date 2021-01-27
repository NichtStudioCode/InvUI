package de.studiocode.invgui.util.reflection;

import java.lang.reflect.Field;

import static de.studiocode.invgui.util.reflection.ReflectionUtils.getCBClass;
import static de.studiocode.invgui.util.reflection.ReflectionUtils.getField;

public class ReflectionRegistry {
    
    public static final String NET_MINECRAFT_SERVER_PACKAGE_PATH = ReflectionUtils.getNMS();
    public static final String CRAFT_BUKKIT_PACKAGE_PATH = ReflectionUtils.getCB();
    
    // Classes
    public static final Class<?> CB_CRAFT_META_SKULL_CLASS = getCBClass("inventory.CraftMetaSkull");
    
    // Fields
    public static final Field CB_CRAFT_META_SKULL_PROFILE_FIELD = getField(CB_CRAFT_META_SKULL_CLASS, true, "profile");

}
