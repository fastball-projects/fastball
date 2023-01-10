package dev.fastball.compile.utils;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.core.annotation.*;
import dev.fastball.core.info.action.PopupActionInfo;
import dev.fastball.core.info.basic.*;
import dev.fastball.core.info.component.ComponentProps;
import dev.fastball.core.info.component.ReferencedComponentInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class TypeCompileUtils {

    private TypeCompileUtils() {
    }

    public static List<FieldInfo> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props) {
        return compileTypeFields(typeElement, processingEnv, props, FieldInfo_AutoValue::new, null);
    }


    public static List<FieldInfo> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props, BiConsumer<VariableElement, FieldInfo> afterBuild) {
        return compileTypeFields(typeElement, processingEnv, props, FieldInfo_AutoValue::new, afterBuild);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder) {
        return compileTypeFields(typeElement, processingEnv, props, fieldBuilder, null);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild) {
        Map<String, VariableElement> fieldMap = ElementCompileUtils.getFields(typeElement, processingEnv);
        List<T> fields = new ArrayList<>(fieldMap.size());
        for (VariableElement fieldElement : fieldMap.values()) {
            T field = fieldBuilder.get();
            fields.add(field);
            field.colProps(Collections.singletonMap("span", 12));
            field.dataIndex(fieldElement.getSimpleName().toString());
            Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
            if (fieldAnnotation != null) {
                field.title(fieldAnnotation.title());
                field.tooltip(fieldAnnotation.tips());
                field.display(fieldAnnotation.display());
            } else {
                field.display(DisplayType.Show);
                field.title(fieldElement.getSimpleName().toString());
                field.tooltip(field.dataIndex());
            }
            compileType(field, fieldElement, processingEnv, props, fieldAnnotation);
            field.validationRules(compileFieldJsr303(fieldElement));
            if (afterBuild != null) {
                afterBuild.accept(fieldElement, field);
            }
        }
        return fields;
    }

    public static void compileType(FieldInfo fieldInfo, VariableElement fieldElement, ProcessingEnvironment processingEnv, ComponentProps props, Field fieldAnnotation) {
        TypeMirror type = fieldElement.asType();
        FieldType fieldType = null;
        if (type.getKind().isPrimitive()) {
            fieldType = compilePrimitiveType(type);
        } else if (fieldElement.getAnnotation(Popup.class) != null) {
            compilePopup(fieldInfo, fieldElement, props);
            fieldType = FieldType.POPUP;
        } else if (fieldElement.getAnnotation(Lookup.class) != null) {
            compileLookup(fieldInfo, fieldElement, processingEnv, fieldAnnotation);
            fieldType = FieldType.SELECT;
        } else if (fieldElement.getAnnotation(TreeLookup.class) != null) {
            compileTreeLookup(fieldInfo, fieldElement, processingEnv, fieldAnnotation);
            fieldType = FieldType.TREE_SELECT;
        } else if (type.getKind() == TypeKind.ARRAY) {
            fieldType = compileArrayType(fieldElement, processingEnv, fieldInfo);
        } else if (type.getKind() == TypeKind.DECLARED) {
            fieldType = compileDeclaredType(fieldElement, processingEnv, fieldInfo);
        }
        if (fieldType == null) {
            fieldType = FieldType.AUTO;
        }
        fieldInfo.valueType(fieldType.getType());
    }

    private static FieldType compilePrimitiveType(TypeMirror type) {
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
                return null;
        }
    }

    private static FieldType compileCollectionType(VariableElement fieldElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo) {
        return null;
    }

    private static FieldType compileArrayType(VariableElement fieldElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo) {
        return null;
    }

    private static FieldType compileDeclaredType(VariableElement fieldElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo) {
        TypeElement typeElement = (TypeElement) ((DeclaredType) fieldElement.asType()).asElement();
        if (ElementCompileUtils.isAssignableFrom(Iterable.class, typeElement, processingEnv)) {
            return compileCollectionType(fieldElement, processingEnv, fieldInfo);
        } else {
            switch (typeElement.getKind()) {
                case ENUM:
                    return compileEnumType(typeElement, processingEnv, fieldInfo);
                case CLASS:
                    return compileClassType(typeElement, processingEnv, fieldInfo);
                default:
                    return null;
            }
        }
    }

    private static FieldType compileEnumType(TypeElement typeElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo) {
        Map<String, VariableElement> enumFields = ElementCompileUtils.getFields(typeElement, processingEnv);
        Map<String, EnumItem> enumValues = new HashMap<>();
        enumFields.values().stream().filter(f -> f.getKind() == ElementKind.ENUM_CONSTANT).forEach(enumItem -> {
            DictionaryItem dictItemAnno = enumItem.getAnnotation(DictionaryItem.class);
            EnumItem item = new EnumItem();
            String itemKey = enumItem.getSimpleName().toString();
            if (dictItemAnno != null) {
                item.setText(dictItemAnno.label().isEmpty() ? itemKey : dictItemAnno.label());
                if (!dictItemAnno.color().isEmpty()) {
                    item.setColor(dictItemAnno.color());
                }
                if (!dictItemAnno.value().isEmpty()) {
                    itemKey = dictItemAnno.value();
                }
            } else {
                item.setText(itemKey);
            }
            enumValues.put(itemKey, item);
        });
        fieldInfo.valueEnum(enumValues);
        return FieldType.SELECT;
    }

    private static FieldType compileClassType(TypeElement typeElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo) {
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
        return FieldType.AUTO;
    }

    private static <T extends FieldInfo> void compilePopup(T field, VariableElement fieldElement, ComponentProps props) {
        Popup popupAnnotation = fieldElement.getAnnotation(Popup.class);
        ReferencedComponentInfo popupComponentInfo = ElementCompileUtils.getReferencedComponentInfo(props, popupAnnotation::component);
        if (popupComponentInfo == null) {
            return;
        }
        PopupInfo popupInfo = new PopupInfo();
        popupInfo.setPopupComponent(popupComponentInfo);
        popupInfo.setPopupTitle(popupAnnotation.popupTitle());
        popupInfo.setWidth(popupAnnotation.width());
        popupInfo.setPopupType(popupAnnotation.popupType());
        popupInfo.setPlacementType(popupAnnotation.placementType());
        field.popupInfo(popupInfo);
    }

    private static <T extends FieldInfo> void compileLookup(T field, VariableElement fieldElement, ProcessingEnvironment processingEnv, Field fieldAnnotation) {
        Lookup lookupAnnotation = fieldElement.getAnnotation(Lookup.class);
        if (lookupAnnotation != null) {
            if (fieldAnnotation != null && fieldAnnotation.type() != FieldType.AUTO && fieldAnnotation.type() != FieldType.SELECT) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] has annotation @Lookup, but @Field.type is not SELECT, try set @Field.type to FieldType.AUTO or FieldType.SELECT");
            }
            LookupInfo_AutoValue lookupActionInfo = new LookupInfo_AutoValue();
            TypeMirror lookupActionType = ElementCompileUtils.getTypeMirrorFromAnnotationValue(lookupAnnotation::value);
            if (lookupActionType == null) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] annotation @Lookup.value type not found, check dependency.");
            }
            TypeElement lookupActionElement = (TypeElement) processingEnv.getTypeUtils().asElement(lookupActionType);
            if (lookupActionElement == null) {
                throw new CompilerException("can't happened");
            }
            TypeElement fieldTypeElement = (TypeElement) ((DeclaredType) fieldElement.asType()).asElement();
            lookupActionInfo.multiple(ElementCompileUtils.isAssignableFrom(Iterable.class, fieldTypeElement, processingEnv));
            lookupActionInfo.lookupKey(ElementCompileUtils.getComponentKey(lookupActionElement));
            lookupActionInfo.labelField(lookupAnnotation.labelField());
            lookupActionInfo.valueField(lookupAnnotation.valueField());
            field.lookupAction(lookupActionInfo);
        }
    }

    private static <T extends FieldInfo> void compileTreeLookup(T field, VariableElement fieldElement, ProcessingEnvironment processingEnv, Field fieldAnnotation) {
        TreeLookup lookupAnnotation = fieldElement.getAnnotation(TreeLookup.class);
        if (lookupAnnotation != null) {
            if (fieldAnnotation != null && fieldAnnotation.type() != FieldType.AUTO && fieldAnnotation.type() != FieldType.TREE_SELECT) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] has annotation @Lookup, but @Field.type is not TREE_SELECT, try set @Field.type to FieldType.AUTO or FieldType.TREE_SELECT");
            }
            TreeLookupInfo_AutoValue lookupActionInfo = new TreeLookupInfo_AutoValue();
            TypeMirror lookupActionType = ElementCompileUtils.getTypeMirrorFromAnnotationValue(lookupAnnotation::value);
            if (lookupActionType == null) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] annotation @Lookup.value type not found, check dependency.");
            }
            TypeElement lookupActionElement = (TypeElement) processingEnv.getTypeUtils().asElement(lookupActionType);
            if (lookupActionElement == null) {
                throw new CompilerException("can't happened");
            }
            TypeElement fieldTypeElement = (TypeElement) ((DeclaredType) fieldElement.asType()).asElement();
            lookupActionInfo.multiple(ElementCompileUtils.isAssignableFrom(Iterable.class, fieldTypeElement, processingEnv));
            lookupActionInfo.lookupKey(ElementCompileUtils.getComponentKey(lookupActionElement));
            lookupActionInfo.labelField(lookupAnnotation.labelField());
            lookupActionInfo.valueField(lookupAnnotation.valueField());
            lookupActionInfo.childrenField(lookupAnnotation.childrenField());
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
        List<ValidationRuleInfo> jakartaValidationRules = JakartaValidationCompileUtils.compileFieldJsr303(fieldElement);
        List<ValidationRuleInfo> javaxValidationRules = ValidationCompileUtils.compileFieldJsr303(fieldElement);
        List<ValidationRuleInfo> validationRules = new ArrayList<>();
        validationRules.addAll(jakartaValidationRules);
        validationRules.addAll(javaxValidationRules);
        return validationRules;
    }

}
