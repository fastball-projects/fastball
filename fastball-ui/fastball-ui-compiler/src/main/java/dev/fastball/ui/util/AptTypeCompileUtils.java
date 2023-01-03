package dev.fastball.ui.util;

import dev.fastball.core.compile.CompileUtils;
import dev.fastball.ui.FieldType;
import dev.fastball.ui.annotation.Field;
import dev.fastball.ui.annotation.Lookup;
import dev.fastball.ui.common.FieldInfo;
import dev.fastball.ui.common.LookupActionInfo;
import dev.fastball.ui.common.ValidationRule;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.validation.constraints.*;
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
public class AptTypeCompileUtils {

    private AptTypeCompileUtils() {
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
        return compileTypeFields(typeElement, processingEnv, FieldInfo::new, null);
    }


    public static List<FieldInfo> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, BiConsumer<VariableElement, FieldInfo> afterBuild) {
        return compileTypeFields(typeElement, processingEnv, FieldInfo::new, afterBuild);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, Supplier<T> fieldBuilder) {
        return compileTypeFields(typeElement, processingEnv, fieldBuilder, null);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild) {
        Map<String, VariableElement> fieldMap = CompileUtils.getFields(typeElement, processingEnv);
        List<T> fields = new ArrayList<>(fieldMap.size());
        for (VariableElement fieldElement : fieldMap.values()) {
            T field = fieldBuilder.get();
            fields.add(field);
            field.setColProps(Collections.singletonMap("span", 12));
            field.setDataIndex(fieldElement.getSimpleName().toString());
            field.setValueType(compileType(fieldElement.asType()).getType());
            Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
            if (fieldAnnotation != null) {
                field.setTitle(fieldAnnotation.title());
                field.setTooltip(fieldAnnotation.tips());
                field.setDisplay(fieldAnnotation.display());
            } else {
                field.setDisplay(true);
                field.setTitle(field.getDataIndex());
            }
            Lookup lookupAnnotation = fieldElement.getAnnotation(Lookup.class);
            if (lookupAnnotation != null) {
                // need abs private method
                LookupActionInfo lookupActionInfo = new LookupActionInfo();
                lookupActionInfo.setLookupKey(lookupAnnotation.value().getSimpleName());
                field.setLookupAction(lookupActionInfo);
            }
            if (afterBuild != null) {
                afterBuild.accept(fieldElement, field);
            }
            field.setValidationRules(compileFieldJsr303(fieldElement));
        }
        return fields;
    }

    private static List<ValidationRule> compileFieldJsr303(VariableElement field) {
        List<ValidationRule> validationRules = new ArrayList<>();
        Min min = field.getAnnotation(Min.class);
        if (min != null) {
            validationRules.add(ValidationRule.builder().type("number").min(min.value()).message(min.message()).build());
        }
        Max max = field.getAnnotation(Max.class);
        if (max != null) {
            validationRules.add(ValidationRule.builder().type("number").max(max.value()).message(max.message()).build());
        }
        Size size = field.getAnnotation(Size.class);
        if (size != null) {
            validationRules.add(ValidationRule.builder().type("string").min(size.min()).max(size.max()).message(size.message()).build());
        }
        NotNull notNull = field.getAnnotation(NotNull.class);
        if (notNull != null) {
            validationRules.add(ValidationRule.builder().required(true).message(notNull.message()).build());
        }
        Pattern pattern = field.getAnnotation(Pattern.class);
        if (pattern != null) {
            validationRules.add(ValidationRule.builder().pattern(pattern.regexp()).message(pattern.message()).build());
        }
        return validationRules;
    }

}
