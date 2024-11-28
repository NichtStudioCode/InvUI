package xyz.xenondevs.invui.internal.util;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"unchecked", "unused"})
public class ReflectionUtils {
    
    public static <T> Class<T> getCBClass(String path) {
        return getClass(ReflectionRegistry.CRAFT_BUKKIT_PACKAGE_PATH + "." + path);
    }
    
    public static <T> Class<T> getClass(String path) {
        try {
            return (Class<T>) Class.forName(path);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> @Nullable Class<T> getClassOrNull(String path) {
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
    
    public static <T> @Nullable Constructor<T> getConstructorOrNull(@Nullable Class<T> clazz, boolean declared, @Nullable Class<?>... parameterTypes) {
        if (clazz == null)
            return null;
        
        try {
            Constructor<T> constructor = declared ? clazz.getDeclaredConstructor(parameterTypes) : clazz.getConstructor(parameterTypes);
            if (declared) constructor.setAccessible(true);
            return constructor;
        } catch (Throwable t) {
            return null;
        }
    }
    
    public static <T> T constructEmpty(Class<?> clazz) {
        try {
            return (T) getConstructor(clazz, true).newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static <T> T construct(Constructor<T> constructor, @Nullable Object @Nullable ... args) {
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
    
    public static @Nullable Method getMethodOrNull(@Nullable Class<?> clazz, boolean declared, String name, @Nullable Class<?>... parameterTypes) {
        if (clazz == null)
            return null;
        
        try {
            Method method = declared ? clazz.getDeclaredMethod(name, parameterTypes) : clazz.getMethod(name, parameterTypes);
            if (declared) method.setAccessible(true);
            return method;
        } catch (Throwable t) {
            return null;
        }
    }
    
    public static <T> T invokeMethod(Method method, @Nullable Object obj, @Nullable Object @Nullable ... args) {
        try {
            return (T) method.invoke(obj, args);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    public static void setFieldValue(Field field, @Nullable Object obj, @Nullable Object value) {
        try {
            field.set(obj, value);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> @Nullable T getFieldValue(Field field, @Nullable Object obj) {
        try {
            return (T) field.get(obj);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
    
}
