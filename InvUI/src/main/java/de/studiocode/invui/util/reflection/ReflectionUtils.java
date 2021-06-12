package de.studiocode.invui.util.reflection;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static de.studiocode.invui.util.reflection.ReflectionRegistry.*;

public class ReflectionUtils {
    
    protected static String getCB() {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        String version = path.substring(path.lastIndexOf(".") + 1);
        return "org.bukkit.craftbukkit." + version + ".";
    }
    
    protected static String getNMS() {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        String version = path.substring(path.lastIndexOf(".") + 1);
        return "net.minecraft.server." + version + ".";
    }
    
    public static Class<?> getBukkitClass(String path) {
        try {
            return Class.forName(BUKKIT_PACKAGE_PATH + path);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Class<?> getNMSClass(String path) {
        try {
            return Class.forName(NET_MINECRAFT_SERVER_PACKAGE_PATH + path);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Class<?> getCBClass(String path) {
        try {
            return Class.forName(CRAFT_BUKKIT_PACKAGE_PATH + path);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Field getField(Class<?> clazz, boolean declared, String name) {
        try {
            Field field = declared ? clazz.getDeclaredField(name) : clazz.getField(name);
            if (declared) field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Constructor<?> getConstructor(Class<?> clazz, boolean declared, Class<?> parameterTypes) {
        try {
            return declared ? clazz.getDeclaredConstructor(parameterTypes) : clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Method getMethod(Class<?> clazz, boolean declared, String name, Class<?>... parameterTypes) {
        try {
            return declared ? clazz.getDeclaredMethod(name, parameterTypes) : clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static void setFieldValue(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
}
