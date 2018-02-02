package org.mposolda.jpamig.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Reflectionss {


    /**
     * Search the class hierarchy for a method with the given name and arguments. Will return the nearest match,
     * starting with the class specified and searching up the hierarchy.
     *
     * @param clazz The class to search
     * @param name The name of the method to search for
     * @param args The arguments of the method to search for
     *
     * @return The method found, or null if no method is found
     */
    public static Method findDeclaredMethod(Class<?> clazz, String name, Class<?>... args) {
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredMethod(name, args);
            } catch (NoSuchMethodException e) {
                // No-op, continue the search
            }
        }
        return null;
    }

    public static <T> T invokeMethod(boolean setAccessible, Method method,
                                     Class<T> expectedReturnType, Object instance, Object... args) {
        if (setAccessible && !method.isAccessible()) {
            method.setAccessible(true);
        }

        try {
            return expectedReturnType.cast(method.invoke(instance, args));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
