package dev.fastball.ui.util;

import dev.fastball.ui.FieldType;
import dev.fastball.ui.annotation.Field;
import dev.fastball.ui.annotation.Lookup;
import dev.fastball.ui.common.FieldInfo;
import dev.fastball.ui.common.LookupActionInfo;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class TypeCompileUtils {

    public static FieldType compileType(Type type) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isPrimitive()) {
                if (Long.TYPE == clazz || Integer.TYPE == clazz || Short.TYPE == clazz || Byte.TYPE == clazz) {
                    return FieldType.DIGIT;
                } else if (Float.TYPE == clazz || Double.TYPE == clazz) {
                    return FieldType.DIGIT;
                } else if (Boolean.TYPE == clazz) {
                    return FieldType.SWITCH;
                }
            } else if (Number.class.isAssignableFrom(clazz)) {
                return FieldType.DIGIT;
            } else if (CharSequence.class.isAssignableFrom(clazz)) {
                return FieldType.TEXT;
            } else if (Boolean.class == clazz) {
                return FieldType.SWITCH;
            } else if (Date.class.isAssignableFrom(clazz)) {
                return FieldType.DATE_TIME;
            } else if (LocalDate.class.isAssignableFrom(clazz)) {
                return FieldType.DATE;
            } else if (LocalTime.class.isAssignableFrom(clazz)) {
                return FieldType.TIME;
            } else if (LocalDateTime.class.isAssignableFrom(clazz)) {
                return FieldType.DATE_TIME;
            }
        }
        return FieldType.TEXT;
    }

    public static List<FieldInfo> compileTypeFields(Type type) {
        return compileTypeFields(type, FieldInfo::new, null);
    }


    public static List<FieldInfo> compileTypeFields(Type type, BiConsumer<java.lang.reflect.Field, FieldInfo> afterBuild) {
        return compileTypeFields(type, FieldInfo::new, afterBuild);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(Type type, Supplier<T> fieldBuilder) {
        return compileTypeFields(type, fieldBuilder, null);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(Type type, Supplier<T> fieldBuilder, BiConsumer<java.lang.reflect.Field, T> afterBuild) {
        if (type == Void.class) {
            throw new RuntimeException("can not compile void type");
        }
        if (type instanceof Class) {
            java.lang.reflect.Field[] declaredFields = ((Class<?>) type).getDeclaredFields();
            List<T> fields = new ArrayList<>(declaredFields.length);
            for (java.lang.reflect.Field declaredField : declaredFields) {
                T field = fieldBuilder.get();
                fields.add(field);
                field.setColProps(Collections.singletonMap("span", 12));
                field.setDataIndex(declaredField.getName());
                field.setValueType(compileType(declaredField.getGenericType()).getType());
                Field fieldAnnotation = declaredField.getDeclaredAnnotation(Field.class);
                if (fieldAnnotation != null) {
                    field.setTitle(fieldAnnotation.title());
                    field.setTooltip(fieldAnnotation.tips());
                    field.setDisplay(fieldAnnotation.display());
                } else {
                    field.setDisplay(true);
                    field.setTitle(declaredField.getName());
                }
                Lookup lookupAnnotation = declaredField.getDeclaredAnnotation(Lookup.class);
                if (lookupAnnotation != null) {
                    // need abs private method
                    LookupActionInfo lookupActionInfo = new LookupActionInfo();
                    lookupActionInfo.setLookupKey(lookupAnnotation.value().getSimpleName());
                    field.setLookupAction(lookupActionInfo);
                }
                if (afterBuild != null) {
                    afterBuild.accept(declaredField, field);
                }
            }
            return fields;
        }
        throw new RuntimeException("can not compile type[" + type + "]");
    }

}
