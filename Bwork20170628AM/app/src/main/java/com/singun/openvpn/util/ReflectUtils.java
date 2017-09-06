package com.singun.openvpn.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ReflectUtils {
    public static <T> T callStaticObjectMethod(Class<?> clazz, Class<T> returnType, String method, Class<?>[] parameterTypes,
                                               Object... values)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = clazz.getDeclaredMethod(method, parameterTypes);
        declaredMethod.setAccessible(true);
        return (T) declaredMethod.invoke(null, values);
    }
}
