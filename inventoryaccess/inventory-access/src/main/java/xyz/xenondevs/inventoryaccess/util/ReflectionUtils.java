package xyz.xenondevs.inventoryaccess.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.version.InventoryAccessRevision;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"unchecked", "unused"})
public class ReflectionUtils {
    
    public static <T> @NotNull Class<T> getImplClass(@NotNull String path) {
        try {
            return (Class<T>) Class.forName("xyz.xenondevs.inventoryaccess." + InventoryAccessRevision.REQUIRED_REVISION.getPackageName() + "." + path);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> @NotNull Class<T> getCBClass(@NotNull String path) {
        return getClass(ReflectionRegistry.CRAFT_BUKKIT_PACKAGE_PATH + "." + path);
    }
    
    public static <T> @NotNull Class<T> getClass(@NotNull String path) {
        try {
            return (Class<T>) Class.forName(path);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> @Nullable Class<T> getClassOrNull(@NotNull String path) {
        try {
            return (Class<T>) Class.forName(path);
        } catch (Throwable t) {
            return null;
        }
    }
    
    public static @NotNull Field getField(@NotNull Class<?> clazz, boolean declared, @NotNull String name) {
        try {
            Field field = declared ? clazz.getDeclaredField(name) : clazz.getField(name);
            if (declared) field.setAccessible(true);
            return field;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> @NotNull Constructor<T> getConstructor(@NotNull Class<T> clazz, boolean declared, @NotNull Class<?> @NotNull... parameterTypes) {
        try {
            Constructor<T> constructor = declared ? clazz.getDeclaredConstructor(parameterTypes) : clazz.getConstructor(parameterTypes);
            if (declared) constructor.setAccessible(true);
            return constructor;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> @NotNull T constructEmpty(@NotNull Class<?> clazz) {
        try {
            return (T) getConstructor(clazz, true).newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> @NotNull T construct(@NotNull Constructor<T> constructor, @Nullable Object @Nullable... args) {
        try {
            return constructor.newInstance(args);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static @NotNull Method getMethod(@NotNull Class<?> clazz, boolean declared, @NotNull String name, @NotNull Class<?>@NotNull ... parameterTypes) {
        try {
            Method method = declared ? clazz.getDeclaredMethod(name, parameterTypes) : clazz.getMethod(name, parameterTypes);
            if (declared) method.setAccessible(true);
            return method;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static @Nullable Method getMethodOrNull(@NotNull Class<?> clazz, boolean declared, @NotNull String name, @NotNull Class<?> @NotNull... parameterTypes) {
        try {
            Method method = declared ? clazz.getDeclaredMethod(name, parameterTypes) : clazz.getMethod(name, parameterTypes);
            if (declared) method.setAccessible(true);
            return method;
        } catch (Throwable t) {
            return null;
        }
    }
    
    public static <T> T invokeMethod(@NotNull Method method, @Nullable Object obj, @Nullable Object @Nullable... args) {
        try {
            return (T) method.invoke(obj, args);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static void setFieldValue(@NotNull Field field, @Nullable Object obj, @Nullable Object value) {
        try {
            field.set(obj, value);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> @Nullable T getFieldValue(@NotNull Field field, @Nullable Object obj) {
        try {
            return (T) field.get(obj);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
}
