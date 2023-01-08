package dev.fastball.compile.utils;

import dev.fastball.core.annotation.Field;
import dev.fastball.core.annotation.Lookup;
import dev.fastball.core.annotation.LookupComponent;
import dev.fastball.core.info.basic.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class TypeCompileUtils {

    private TypeCompileUtils() {
    }

    public static FieldType compileType(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            switch (type.getKind()) {
                case LONG:
                case INT:
                case SHORT:
                case BYTE:
                case DOUBLE:
                case FLOAT:
                    return FieldType.DIGIT;
                case BOOLEAN:
                    return FieldType.SWITCH;
                default:
                    break;
            }
        } else if (type.getKind() == TypeKind.DECLARED) {
            TypeElement typeElement = (TypeElement) ((DeclaredType) type).asElement();
            switch (typeElement.getQualifiedName().toString()) {
                case "java.lang.Long":
                case "java.lang.Integer":
                case "java.lang.Short":
                case "java.lang.Byte":
                case "java.lang.Double":
                case "java.lang.Float":
                    return FieldType.DIGIT;
                case "java.lang.Boolean":
                    return FieldType.SWITCH;
                case "java.lang.CharSequence":
                case "java.lang.String":
                    return FieldType.TEXT;
                case "java.time.LocalTime":
                    return FieldType.TIME;
                case "java.time.LocalDate":
                    return FieldType.DATE;
                case "java.util.Date":
                case "java.time.LocalDateTime":
                    return FieldType.DATE_TIME;
                default:
                    break;
            }
        }
        return FieldType.AUTO;
    }

    public static List<FieldInfo> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv) {
        return compileTypeFields(typeElement, processingEnv, FieldInfo_AutoValue::new, null);
    }


    public static List<FieldInfo> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, BiConsumer<VariableElement, FieldInfo> afterBuild) {
        return compileTypeFields(typeElement, processingEnv, FieldInfo_AutoValue::new, afterBuild);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, Supplier<T> fieldBuilder) {
        return compileTypeFields(typeElement, processingEnv, fieldBuilder, null);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild) {
        Map<String, VariableElement> fieldMap = ElementCompileUtils.getFields(typeElement, processingEnv);
        List<T> fields = new ArrayList<>(fieldMap.size());
        for (VariableElement fieldElement : fieldMap.values()) {
            T field = fieldBuilder.get();
            fields.add(field);
            field.colProps(Collections.singletonMap("span", 12));
            field.dataIndex(fieldElement.getSimpleName().toString());
            field.valueType(compileType(fieldElement.asType()).getType());
            Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
            if (fieldAnnotation != null) {
                field.title(fieldAnnotation.title());
                field.tooltip(fieldAnnotation.tips());
                field.display(fieldAnnotation.display());
            } else {
                field.display(true);
                field.tooltip(field.dataIndex());
            }
            compileLookup(field, fieldElement, processingEnv, fieldAnnotation);
            if (afterBuild != null) {
                afterBuild.accept(fieldElement, field);
            }
            field.validationRules(compileFieldJsr303(fieldElement));
        }
        return fields;
    }

    private static <T extends FieldInfo> void compileLookup(T field, VariableElement fieldElement, ProcessingEnvironment processingEnv, Field fieldAnnotation) {
        Lookup lookupAnnotation = fieldElement.getAnnotation(Lookup.class);
        if (lookupAnnotation != null) {
            LookupInfo_AutoValue lookupActionInfo = new LookupInfo_AutoValue();
            TypeMirror lookupActionType = ElementCompileUtils.getTypeMirrorFromAnnotationValue(lookupAnnotation::value);
            if (lookupActionType != null) {
                TypeElement lookupActionElement = (TypeElement) processingEnv.getTypeUtils().asElement(lookupActionType);
                LookupComponent lookupComponentAnnotation = lookupActionElement.getAnnotation(LookupComponent.class);
                if (lookupComponentAnnotation != null && !lookupComponentAnnotation.value().isEmpty()) {
                    lookupActionInfo.lookupKey(lookupComponentAnnotation.value());
                } else {
                    lookupActionInfo.lookupKey(lookupActionElement.getSimpleName().toString());
                }
            }
            if (fieldAnnotation == null || fieldAnnotation.type() == FieldType.AUTO) {
                field.valueType(FieldType.SELECT.getType());
            }
            lookupActionInfo.labelField(lookupAnnotation.labelField());
            lookupActionInfo.valueField(lookupAnnotation.valueField());
            field.lookupAction(lookupActionInfo);
        }
    }

    /**
     * 兼容 javax 和 jakarta 的 jsr303 注解, 优先 jakarta
     *
     * @param fieldElement 需处理的字段
     * @return 该字段的校验规则
     */
    private static List<ValidationRuleInfo> compileFieldJsr303(VariableElement fieldElement) {
        List<ValidationRuleInfo> validationRules = JakartaValidationCompileUtils.compileFieldJsr303(fieldElement);
        if (validationRules.isEmpty()) {
            return validationRules;
        }
        return ValidationCompileUtils.compileFieldJsr303(fieldElement);
    }

}
