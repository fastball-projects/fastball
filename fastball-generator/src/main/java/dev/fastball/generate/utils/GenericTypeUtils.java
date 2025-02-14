package dev.fastball.generate.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericTypeUtils {
     <T> Class<T> getGenericClass() {
        Class<T> clazz = getGenericClass(this.getClass());
        if (clazz != null) {
            return clazz;
        }
        throw new RuntimeException("can't happened");
    }

    <T> Class<T> getGenericClass(Type type) {
        if (type == Object.class) {
            return null;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() instanceof Class) {
                return (Class<T>) parameterizedType.getActualTypeArguments()[0];
            }
        }
        if (((Class) type).getGenericSuperclass() != null) {
            Class<T> clazz = getGenericClass(((Class) type).getGenericSuperclass());
            if (clazz != null) {
                return clazz;
            }
        }
        for (Type genericInterface : ((Class) type).getGenericInterfaces()) {
            Class<T> clazz = getGenericClass(genericInterface);
            if (clazz != null) {
                return clazz;
            }
        }
        return null;
    }
}
