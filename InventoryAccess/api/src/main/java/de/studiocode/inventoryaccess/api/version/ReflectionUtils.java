package de.studiocode.inventoryaccess.api.version;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {
    
    private static final String VERSION = getVersion();
    
    private static String getVersion() {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        return path.substring(path.lastIndexOf(".") + 1);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(String path) {
        try {
            return (Class<T>) Class.forName("de.studiocode.inventoryaccess." + VERSION + "." + path);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Your version (" + VERSION + ") is not supported by InventoryAccess");
        }
    }
    
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T constructEmpty(Class<?> clazz) {
        try {
            return (T) clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static <T> T construct(Constructor<T> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException e) {
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
    
    @SuppressWarnings("unchecked")
    public static <T> T getValueOfField(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Method getMethod(Class<?> clazz, boolean declared, String name, Class<?>... parameterTypes) {
        try {
            Method method = declared ? clazz.getDeclaredMethod(name, parameterTypes) : clazz.getMethod(name, parameterTypes);
            if (declared) method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object obj, Object... args) {
        try {
            return (T) method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
}
