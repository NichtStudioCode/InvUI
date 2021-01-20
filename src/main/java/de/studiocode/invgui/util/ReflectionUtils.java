package de.studiocode.invgui.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    
    public static void setValueOfDeclaredField(Object obj, String fieldName, Object value) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
}
