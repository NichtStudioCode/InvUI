package xyz.xenondevs.inventoryaccess.util;

import org.bukkit.Bukkit;
import xyz.xenondevs.inventoryaccess.version.InventoryAccessRevision;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"unchecked", "unused"})
public class ReflectionUtils {
    
    protected static String getCB() {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        String version = path.substring(path.lastIndexOf(".") + 1);
        return "org.bukkit.craftbukkit." + version + ".";
    }
    
    protected static int getVersionNumber() {
        String version = Bukkit.getVersion();
        version = version.substring(version.indexOf("MC: "), version.length() - 1).substring(4);
        return Integer.parseInt(version.split("\\.")[1]);
    }
    
    public static <T> Class<T> getImplClass(String path) {
        try {
            return (Class<T>) Class.forName("xyz.xenondevs.inventoryaccess." + InventoryAccessRevision.REQUIRED_REVISION.getPackageName() + "." + path);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> Class<T> getBukkitClass(String path) {
        return getClass(ReflectionRegistry.BUKKIT_PACKAGE_PATH + path);
    }
    
    public static <T> Class<T> getCBClass(String path) {
        return getClass(ReflectionRegistry.CRAFT_BUKKIT_PACKAGE_PATH + path);
    }
    
    public static <T> Class<T> getClass(String path) {
        try {
            return (Class<T>) Class.forName(path);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> Class<T> getClassOrNull(String path) {
        try {
            return (Class<T>) Class.forName(path);
        } catch (Throwable t) {
            return null;
        }
    }
    
    public static Field getField(Class<?> clazz, boolean declared, String name) {
        try {
            Field field = declared ? clazz.getDeclaredField(name) : clazz.getField(name);
            if (declared) field.setAccessible(true);
            return field;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> Constructor<T> getConstructor(Class<T> clazz, boolean declared, Class<?>... parameterTypes) {
        try {
            Constructor<T> constructor = declared ? clazz.getDeclaredConstructor(parameterTypes) : clazz.getConstructor(parameterTypes);
            if (declared) constructor.setAccessible(true);
            return constructor;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> T constructEmpty(Class<?> clazz) {
        try {
            return (T) getConstructor(clazz, true).newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> T construct(Constructor<T> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static Method getMethod(Class<?> clazz, boolean declared, String name, Class<?>... parameterTypes) {
        try {
            Method method = declared ? clazz.getDeclaredMethod(name, parameterTypes) : clazz.getMethod(name, parameterTypes);
            if (declared) method.setAccessible(true);
            return method;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static Method getMethodOrNull(Class<?> clazz, boolean declared, String name, Class<?>... parameterTypes) {
        try {
            Method method = declared ? clazz.getDeclaredMethod(name, parameterTypes) : clazz.getMethod(name, parameterTypes);
            if (declared) method.setAccessible(true);
            return method;
        } catch (Throwable t) {
            return null;
        }
    }
    
    public static <T> T invokeMethod(Method method, Object obj, Object... args) {
        try {
            return (T) method.invoke(obj, args);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static void setFieldValue(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
}
