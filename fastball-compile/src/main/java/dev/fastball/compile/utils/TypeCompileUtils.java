package dev.fastball.compile.utils;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.core.annotation.*;
import dev.fastball.core.component.MainFieldComponent;
import dev.fastball.core.info.basic.*;
import dev.fastball.core.info.component.ComponentProps;
import dev.fastball.core.info.component.MainFieldComponentInfo;
import dev.fastball.core.info.component.ReferencedComponentInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.fastball.compile.CompileConstants.SIMPLE_FORM_LIST_VALUE_FIELD;

/**
 * TODO 字段类型编译, 这里需要优化一下
 *
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class TypeCompileUtils {

    private TypeCompileUtils() {
    }

    public static List<FieldInfo> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props) {
        return compileTypeFields(typeElement, processingEnv, props, FieldInfo::new, null);
    }


    public static List<FieldInfo> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props, BiConsumer<VariableElement, FieldInfo> afterBuild) {
        return compileTypeFields(typeElement, processingEnv, props, FieldInfo::new, afterBuild);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder) {
        return compileTypeFields(typeElement, processingEnv, props, fieldBuilder, null);
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild) {
        return compileTypeFields(typeElement, processingEnv, props, fieldBuilder, afterBuild, new HashSet<>());
    }

    public static <T extends FieldInfo> List<T> compileTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes) {
        Map<String, VariableElement> fieldMap = ElementCompileUtils.getFields(typeElement, processingEnv);
        return fieldMap.values().stream()
                .map(fieldElement -> compileField(fieldElement, processingEnv, props, fieldBuilder, afterBuild, compiledTypes))
                .collect(Collectors.toList());
    }

    public static <T extends FieldInfo> T compileField(VariableElement fieldElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes) {
        T fieldInfo = fieldBuilder.get();
        compileField(fieldElement, processingEnv, props, fieldInfo, afterBuild, compiledTypes);
        return fieldInfo;
    }

    public static <T extends FieldInfo> void compileField(VariableElement fieldElement, ProcessingEnvironment processingEnv, ComponentProps props, T fieldInfo, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes) {
        fieldInfo.setColProps(Collections.singletonMap("span", 12));
        fieldInfo.dataIndex(fieldElement.getSimpleName().toString());
        Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
        if (fieldAnnotation != null) {
            fieldInfo.setDisplay(fieldAnnotation.display());
            fieldInfo.setTitle(fieldAnnotation.title());
            fieldInfo.setTooltip(fieldAnnotation.tips());
            fieldInfo.setReadonly(fieldAnnotation.readonly());
        } else {
            fieldInfo.setDisplay(DisplayType.Show);
            fieldInfo.setTitle(fieldElement.getSimpleName().toString());
        }
        compileType(fieldInfo, fieldElement, processingEnv, props, compiledTypes);
        fieldInfo.setValidationRules(compileFieldJsr303(fieldElement));
        if (afterBuild != null) {
            afterBuild.accept(fieldElement, fieldInfo);
        }
    }

    public static ValueType compileType(FieldInfo fieldInfo, VariableElement fieldElement, ProcessingEnvironment processingEnv, ComponentProps props, Set<TypeMirror> compiledTypes) {
        TypeMirror type = fieldElement.asType();
        if (compiledTypes.contains(type)) {
            return ValueType.CIRCULAR;
        }
        compiledTypes.add(type);
        ValueType valueType = null;
        if (fieldElement.getAnnotation(Popup.class) != null) {
            compilePopup(fieldInfo, fieldElement, props);
        } else {
            if (fieldElement.getAnnotation(EditComponent.class) != null) {
                compileEditComponent(fieldInfo, fieldElement, props);
            }
            if (fieldElement.getAnnotation(DisplayComponent.class) != null) {
                compileDisplayComponent(fieldInfo, fieldElement, props);
            }
        }
        if (fieldElement.getAnnotation(Lookup.class) != null) {
            compileLookup(fieldInfo, fieldElement, processingEnv);
            valueType = ValueType.SELECT;
        } else if (fieldElement.getAnnotation(TreeLookup.class) != null) {
            compileTreeLookup(fieldInfo, fieldElement, processingEnv);
            valueType = ValueType.TREE_SELECT;
        } else if (fieldElement.getAnnotation(ShowField.class) != null) {
            compileShowField(fieldInfo, fieldElement, processingEnv, props, compiledTypes);
            valueType = ValueType.TREE_SELECT;
        } else if (type.getKind() == TypeKind.ARRAY) {
            valueType = compileArray((ArrayType) type, processingEnv, fieldInfo, props, compiledTypes, fieldElement);
        }
        if (valueType == null) {
            if (type.getKind().isPrimitive()) {
                valueType = compilePrimitiveType(type);
            } else if (type.getKind() == TypeKind.DECLARED) {
                valueType = compileDeclaredType(fieldElement, processingEnv, fieldInfo, props, compiledTypes);
            }
        }
        if (valueType == null) {
            valueType = ValueType.AUTO;
        }
        // TODO 临时避免同级同类型字段消失, 可以优化 cache
        compiledTypes.remove(type);
        fieldInfo.setValueType(valueType.getType());
        return valueType;
    }

    private static ValueType compilePrimitiveType(TypeMirror type) {
        switch (type.getKind()) {
            case LONG:
            case INT:
            case SHORT:
            case BYTE:
            case DOUBLE:
            case FLOAT:
            case CHAR:
                return ValueType.DIGIT;
            case BOOLEAN:
                return ValueType.SWITCH;
            default:
                return ValueType.AUTO;
        }
    }

    private static ValueType compileCollection(DeclaredType fieldType, ProcessingEnvironment processingEnv, FieldInfo fieldInfo, ComponentProps props, Set<TypeMirror> compiledTypes, VariableElement fieldElement) {
        return compileCollectionType(fieldType.getTypeArguments().get(0), processingEnv, fieldInfo, props, compiledTypes, fieldElement);
    }

    private static ValueType compileArray(ArrayType arrayType, ProcessingEnvironment processingEnv, FieldInfo fieldInfo, ComponentProps props, Set<TypeMirror> compiledTypes, VariableElement fieldElement) {
        TypeMirror componentType = arrayType.getComponentType();
        return compileCollectionType(componentType, processingEnv, fieldInfo, props, compiledTypes, fieldElement);
    }

    private static ValueType compileCollectionType(TypeMirror componentType, ProcessingEnvironment processingEnv, FieldInfo fieldInfo, ComponentProps props, Set<TypeMirror> compiledTypes, VariableElement fieldElement) {
        if (componentType.getKind().isPrimitive()) {
            String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
            throw new CompilerException("Field [" + fieldReferenceName + "] Collection primitive type [" + componentType + "] not supported, if you want multiple select, try use @Lookup");
        } else if (componentType.getKind() == TypeKind.ARRAY) {
            FieldInfo valueField = new FieldInfo(SIMPLE_FORM_LIST_VALUE_FIELD);
            ValueType valueType = compileArray((ArrayType) componentType, processingEnv, valueField, props, compiledTypes, fieldElement);
            valueField.setValueType(valueType.getType());
            fieldInfo.setSubFields(Collections.singletonList(valueField));
            return ValueType.SIMPLE_ARRAY;
        } else if (componentType.getKind() == TypeKind.DECLARED) {
            TypeElement typeElement = (TypeElement) ((DeclaredType) componentType).asElement();
            if (ElementCompileUtils.isAssignableFrom(Iterable.class, typeElement, processingEnv)) {
                FieldInfo valueField = new FieldInfo(SIMPLE_FORM_LIST_VALUE_FIELD);
                ValueType valueType = compileCollectionType(componentType, processingEnv, valueField, props, compiledTypes, fieldElement);
                valueField.setValueType(valueType.getType());
                fieldInfo.setSubFields(Collections.singletonList(valueField));
                return ValueType.SIMPLE_ARRAY;
            } else if (typeElement.getKind() == ElementKind.ENUM) {
                ValueType valueType = compileEnumType(typeElement, processingEnv, fieldInfo);
                fieldInfo.setValueType(valueType.getType());
                return ValueType.MULTI_SELECT;
            } else if (typeElement.getKind() == ElementKind.CLASS) {
                ValueType type = compileBasicClassType(typeElement, processingEnv, fieldInfo, props);
                if (type != null) {
                    String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                    throw new CompilerException("Field [" + fieldReferenceName + "] Collection basic type [" + typeElement + "] not supported, if you want multiple select, try use @Lookup");
                } else {
                    fieldInfo.setColProps(Collections.singletonMap("span", 24));
                    fieldInfo.setFormItemProps(Collections.singletonMap("alwaysShowItemLabel", true));
                    compileSubFields(typeElement, processingEnv, fieldInfo, props, compiledTypes);
                    return ValueType.ARRAY;
                }
            }
        }
        String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
        throw new CompilerException("Field [" + fieldReferenceName + "] Collection type [" + componentType + "] not supported");
    }

    private static ValueType compileDeclaredType(VariableElement fieldElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo, ComponentProps props, Set<TypeMirror> compiledTypes) {
        TypeMirror fieldType = fieldElement.asType();
        TypeElement typeElement = (TypeElement) ((DeclaredType) fieldType).asElement();
        if (ElementCompileUtils.isAssignableFrom(Iterable.class, typeElement, processingEnv)) {
            return compileCollection((DeclaredType) fieldType, processingEnv, fieldInfo, props, compiledTypes, fieldElement);
        } else {
            switch (typeElement.getKind()) {
                case ENUM:
                    return compileEnumType(typeElement, processingEnv, fieldInfo);
                case CLASS:
                    ValueType type = compileBasicClassType(typeElement, processingEnv, fieldInfo, props);
                    if (type != null) {
                        return type;
                    }
                    compileSubFields(typeElement, processingEnv, fieldInfo, props, compiledTypes);
                    return ValueType.SUB_FIELDS;
                default:
                    return null;
            }
        }
    }

    private static ValueType compileEnumType(TypeElement typeElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo) {
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
        fieldInfo.setValueEnum(enumValues);
        return ValueType.SELECT;
    }

    private static ValueType compileShowField(FieldInfo fieldInfo, VariableElement fieldElement, ProcessingEnvironment processingEnv, ComponentProps props, Set<TypeMirror> compiledTypes) {
        TypeElement typeElement = (TypeElement) ((DeclaredType) fieldElement.asType()).asElement();
        ShowField showFieldAnnotation = fieldElement.getAnnotation(ShowField.class);
        if (showFieldAnnotation != null) {
            VariableElement mainFieldElement = ElementCompileUtils.getFieldByPath(typeElement, processingEnv, showFieldAnnotation.value());
            if (mainFieldElement == null) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] has annotation @ShowField([" + String.join(",", showFieldAnnotation.value()) + "]), but field path not found field");
            }
            ValueType valueType = compileType(fieldInfo, mainFieldElement, processingEnv, props, compiledTypes);
            List<String> dataIndex = new ArrayList<>();
            dataIndex.addAll(fieldInfo.getDataIndex());
            dataIndex.addAll(Arrays.asList(showFieldAnnotation.value()));
            fieldInfo.setDataIndex(dataIndex);
            return valueType;
        }
        return null;
    }


    private static ValueType compileBasicClassType(TypeElement typeElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo, ComponentProps props) {
        switch (typeElement.getQualifiedName().toString()) {
            case "java.lang.Long":
            case "java.lang.Integer":
            case "java.lang.Short":
            case "java.lang.Byte":
            case "java.lang.Double":
            case "java.lang.Float":
                return ValueType.DIGIT;
            case "java.lang.Boolean":
                return ValueType.SWITCH;
            case "java.lang.CharSequence":
            case "java.lang.String":
                return ValueType.TEXT;
            case "java.time.LocalTime":
                return ValueType.TIME;
            case "java.time.LocalDate":
                return ValueType.DATE;
            case "java.util.Date":
            case "java.time.LocalDateTime":
                return ValueType.DATE_TIME;
            default:
                return null;
        }
    }

    private static void compileSubFields(TypeElement typeElement, ProcessingEnvironment processingEnv, FieldInfo fieldInfo, ComponentProps props, Set<TypeMirror> compiledTypes) {
        List<FieldInfo> subFields = compileTypeFields(typeElement, processingEnv, props, FieldInfo::new, null, compiledTypes);
        fieldInfo.setSubFields(subFields);
        fieldInfo.setColProps(Collections.singletonMap("span", 24));
    }

    private static <T extends FieldInfo> void compileEditComponent(T fieldInfo, VariableElement fieldElement, ComponentProps props) {
        EditComponent useComponentAnnotation = fieldElement.getAnnotation(EditComponent.class);
        TypeMirror componentType = ElementCompileUtils.getTypeMirrorFromAnnotationValue(useComponentAnnotation::value);
        assert componentType != null;
        TypeElement componentTypeElement = (TypeElement) ((DeclaredType) componentType).asElement();
        UseComponentInfo useComponentInfo = UseComponentInfo.builder()
                .valueKey(useComponentAnnotation.valueKey())
                .recordKey(useComponentAnnotation.recordKey())
                .componentInfo(ElementCompileUtils.getReferencedComponentInfo(props, componentTypeElement))
                .build();
        fieldInfo.setEditModeComponent(useComponentInfo);
        fieldInfo.setFieldType(FieldType.COMPONENT.getType());
    }

    private static <T extends FieldInfo> void compileDisplayComponent(T fieldInfo, VariableElement fieldElement, ComponentProps props) {
        DisplayComponent useComponentAnnotation = fieldElement.getAnnotation(DisplayComponent.class);
        TypeMirror componentType = ElementCompileUtils.getTypeMirrorFromAnnotationValue(useComponentAnnotation::value);
        assert componentType != null;
        TypeElement componentTypeElement = (TypeElement) ((DeclaredType) componentType).asElement();
        UseComponentInfo useComponentInfo = UseComponentInfo.builder()
                .valueKey(useComponentAnnotation.valueKey())
                .recordKey(useComponentAnnotation.recordKey())
                .componentInfo(ElementCompileUtils.getReferencedComponentInfo(props, componentTypeElement))
                .build();
        fieldInfo.setDisplayModeComponent(useComponentInfo);
        fieldInfo.setFieldType(FieldType.COMPONENT.getType());
    }

    private static ReferencedComponentInfo compileEditComponent(VariableElement fieldElement, TypeMirror popupComponentClass, ComponentProps props) {
        TypeElement componentTypeElement = (TypeElement) ((DeclaredType) popupComponentClass).asElement();
        if (MainFieldComponent.class.getCanonicalName().equals(componentTypeElement.getQualifiedName().toString())) {
            TypeElement fieldTypeElement = (TypeElement) ((DeclaredType) fieldElement.asType()).asElement();
            MainField mainFieldAnnotation = fieldTypeElement.getAnnotation(MainField.class);
            if (mainFieldAnnotation == null) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] has annotation @UseComponent(MainFieldComponent), but Type [" + fieldTypeElement.getQualifiedName() + "] not found annotation @MainField");
            }
            MainFieldComponentInfo mainFieldComponentInfo = new MainFieldComponentInfo();
            mainFieldComponentInfo.setComponentClass(MainFieldComponent.class.getSimpleName());
            mainFieldComponentInfo.setMainField(mainFieldAnnotation.value());
            return mainFieldComponentInfo;
        }
        return ElementCompileUtils.getReferencedComponentInfo(props, componentTypeElement);
    }

    private static <T extends FieldInfo> void compilePopup(T fieldInfo, VariableElement fieldElement, ComponentProps props) {
        Popup popupAnnotation = fieldElement.getAnnotation(Popup.class);
        ReferencedComponentInfo popupComponentInfo = ElementCompileUtils.getReferencedComponentInfo(props, popupAnnotation::component);
        if (popupComponentInfo == null) {
            return;
        }
        PopupInfo popupInfo = new PopupInfo();
        popupInfo.setPopupComponent(popupComponentInfo);
        popupInfo.setWidth(popupAnnotation.width());
        popupInfo.setDataPath(popupAnnotation.dataPath());
        popupInfo.setPopupTitle(popupAnnotation.popupTitle());
        popupInfo.setPopupType(popupAnnotation.popupType());
        popupInfo.setTriggerType(popupAnnotation.triggerType());
        popupInfo.setPlacementType(popupAnnotation.placementType());
        fieldInfo.setPopup(popupInfo);
        fieldInfo.setFieldType(FieldType.POPUP.getType());
    }

    private static <T extends FieldInfo> void compileLookup(T field, VariableElement fieldElement, ProcessingEnvironment processingEnv) {
        Lookup lookupAnnotation = fieldElement.getAnnotation(Lookup.class);
        if (lookupAnnotation != null) {
            Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
            if (fieldAnnotation != null && fieldAnnotation.type() != ValueType.AUTO && fieldAnnotation.type() != ValueType.SELECT) {
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
            field.setLookup(lookupActionInfo);
        }
    }

    private static <T extends FieldInfo> void compileTreeLookup(T field, VariableElement fieldElement, ProcessingEnvironment processingEnv) {
        TreeLookup lookupAnnotation = fieldElement.getAnnotation(TreeLookup.class);
        if (lookupAnnotation != null) {
            Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
            if (fieldAnnotation != null && fieldAnnotation.type() != ValueType.AUTO && fieldAnnotation.type() != ValueType.TREE_SELECT) {
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
            field.setLookup(lookupActionInfo);
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