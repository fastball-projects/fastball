package dev.fastball.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface GenericTypeGetter<T> {
    default Class<T> getGenericTypeClass() {
        Class<T> clazz = getGenericTypeClass(this.getClass());
        if (clazz != null) {
            return clazz;
        }
        throw new RuntimeException("can't happened");
    }

    default Class<T> getGenericTypeClass(Type type) {
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
            Class<T> clazz = getGenericTypeClass(((Class) type).getGenericSuperclass());
            if (clazz != null) {
                return clazz;
            }
        }
        for (Type genericInterface : ((Class) type).getGenericInterfaces()) {
            Class<T> clazz = getGenericTypeClass(genericInterface);
            if (clazz != null) {
                return clazz;
            }
        }
        return null;
    }
}
