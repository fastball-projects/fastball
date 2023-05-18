package dev.fastball.compile.utils;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.core.annotation.*;
import dev.fastball.core.component.Range;
import dev.fastball.core.info.basic.*;
import dev.fastball.core.info.component.ComponentProps;

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
 * TODO 字段类型编译, 这里需要优化一下, 各类型抽成独立注册的
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
        fieldInfo.dataIndex(fieldElement.getSimpleName().toString());
        Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
        if (fieldAnnotation != null) {
            fieldInfo.setDisplay(fieldAnnotation.display());
            fieldInfo.setTitle(fieldAnnotation.title());
            fieldInfo.setTooltip(fieldAnnotation.tips());
            fieldInfo.setReadonly(fieldAnnotation.readonly());
            fieldInfo.setEntireRow(fieldAnnotation.entireRow());
        } else {
            fieldInfo.setDisplay(DisplayType.Show);
            fieldInfo.setTitle(fieldElement.getSimpleName().toString());
        }
        compileType(fieldInfo, fieldElement, processingEnv, props, fieldBuilder, afterBuild, compiledTypes);
        fieldInfo.setValidationRules(compileFieldJsr303(fieldElement));
        fieldInfo.setExpression(compileExpression(fieldElement));
        if (afterBuild != null) {
            afterBuild.accept(fieldElement, fieldInfo);
        }
        return fieldInfo;
    }

    private static ExpressionInfo compileExpression(VariableElement fieldElement) {
        Expression expressionAnnotation = fieldElement.getAnnotation(Expression.class);
        if(expressionAnnotation == null) {
            return null;
        }
        return new ExpressionInfo(expressionAnnotation.fields(), expressionAnnotation.expression());
    }

    // FIXME 这样设计有问题, 某些场景主动声明 fieldType 会不生效, 回头要改一下
    public static <T extends FieldInfo> ValueType compileType(T fieldInfo, VariableElement fieldElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes) {
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
                EditComponent editComponentAnnotation = fieldElement.getAnnotation(EditComponent.class);
                fieldInfo.setEditModeComponent(ElementCompileUtils.getReferencedComponentInfo(props, editComponentAnnotation.value()));
                fieldInfo.setFieldType(FieldType.COMPONENT.getType());
            }
            if (fieldElement.getAnnotation(DisplayComponent.class) != null) {
                DisplayComponent displayComponentAnnotation = fieldElement.getAnnotation(DisplayComponent.class);
                fieldInfo.setDisplayModeComponent(ElementCompileUtils.getReferencedComponentInfo(props, displayComponentAnnotation.value()));
                fieldInfo.setFieldType(FieldType.COMPONENT.getType());
            }
        }
        if (fieldElement.getAnnotation(Lookup.class) != null) {
            compileLookup(fieldInfo, fieldElement, processingEnv);
            valueType = ValueType.SELECT;
        } else if (fieldElement.getAnnotation(TreeLookup.class) != null) {
            compileTreeLookup(fieldInfo, fieldElement, processingEnv);
            valueType = ValueType.TREE_SELECT;
        } else if (fieldElement.getAnnotation(ShowField.class) != null) {
            compileShowField(fieldInfo, fieldElement, processingEnv, props, fieldBuilder, afterBuild, compiledTypes);
            valueType = ValueType.TREE_SELECT;
        } else if (type.getKind() == TypeKind.ARRAY) {
            valueType = compileArray((ArrayType) type, processingEnv, fieldInfo, props, fieldBuilder, afterBuild, compiledTypes, fieldElement);
        }
        if (valueType == null) {
            if (type.getKind().isPrimitive()) {
                valueType = compilePrimitiveType(type, fieldElement, fieldInfo);
            } else if (type.getKind() == TypeKind.DECLARED) {
                valueType = compileDeclaredType(fieldElement, processingEnv, fieldInfo, props, fieldBuilder, afterBuild, compiledTypes);
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

    private static ValueType compileRange(DeclaredType fieldType, VariableElement fieldElement, ProcessingEnvironment processingEnv) {
        TypeMirror typeMirror = fieldType.getTypeArguments().get(0);
        Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
        TypeElement typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
        switch (typeElement.getQualifiedName().toString()) {
            case "java.lang.CharSequence":
            case "java.lang.String":
                if (fieldAnnotation != null) {
                    if (fieldAnnotation.type() == ValueType.TIME_RANGE) {
                        return ValueType.TIME_RANGE;
                    }
                    if (fieldAnnotation.type() == ValueType.DATE_WEEK_RANGE) {
                        return ValueType.DATE_WEEK_RANGE;
                    }
                    if (fieldAnnotation.type() == ValueType.DATE_MONTH_RANGE) {
                        return ValueType.DATE_MONTH_RANGE;
                    }
                    if (fieldAnnotation.type() == ValueType.DATE_QUARTER_RANGE) {
                        return ValueType.DATE_QUARTER_RANGE;
                    }
                    if (fieldAnnotation.type() == ValueType.DATE_YEAR_RANGE) {
                        return ValueType.DATE_YEAR_RANGE;
                    }
                }
                return null;
            case "java.time.LocalTime":
                return ValueType.TIME_RANGE;
            case "java.time.LocalDate":
                return ValueType.DATE_RANGE;
            case "java.time.LocalDateTime":
                return ValueType.DATE_TIME_RANGE;
            case "java.util.Date":
                if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.DATE_TIME_RANGE) {
                    return ValueType.DATE_TIME_RANGE;
                }
                return ValueType.DATE_RANGE;
            default:
                if (ElementCompileUtils.isAssignableFrom(Number.class, typeElement, processingEnv)) {
                    return ValueType.DIGIT_RANGE;
                }
                return null;
        }
    }

    private static <T extends FieldInfo> ValueType compileCollection(DeclaredType fieldType, ProcessingEnvironment processingEnv, T fieldInfo, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes, VariableElement fieldElement) {
        return compileCollectionType(fieldType.getTypeArguments().get(0), processingEnv, fieldInfo, props, fieldBuilder, afterBuild, compiledTypes, fieldElement);
    }

    private static <T extends FieldInfo> ValueType compileArray(ArrayType arrayType, ProcessingEnvironment processingEnv, T fieldInfo, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes, VariableElement fieldElement) {
        TypeMirror componentType = arrayType.getComponentType();
        return compileCollectionType(componentType, processingEnv, fieldInfo, props, fieldBuilder, afterBuild, compiledTypes, fieldElement);
    }

    private static <T extends FieldInfo> ValueType compileCollectionType(TypeMirror componentType, ProcessingEnvironment processingEnv, T fieldInfo, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes, VariableElement fieldElement) {
        if (componentType.getKind().isPrimitive()) {
            String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
            throw new CompilerException("Field [" + fieldReferenceName + "] Collection primitive type [" + componentType + "] not supported, if you want multiple select, try use @Lookup");
        } else if (componentType.getKind() == TypeKind.ARRAY) {
            T valueField = fieldBuilder.get();
            valueField.dataIndex(SIMPLE_FORM_LIST_VALUE_FIELD);
            ValueType valueType = compileArray((ArrayType) componentType, processingEnv, valueField, props, fieldBuilder, afterBuild, compiledTypes, fieldElement);
            valueField.setValueType(valueType.getType());
            fieldInfo.setSubFields(Collections.singletonList(valueField));
            return ValueType.SIMPLE_ARRAY;
        } else if (componentType.getKind() == TypeKind.DECLARED) {
            TypeElement typeElement = (TypeElement) ((DeclaredType) componentType).asElement();
            if (ElementCompileUtils.isAssignableFrom(Iterable.class, typeElement, processingEnv)) {
                T valueField = fieldBuilder.get();
                valueField.dataIndex(SIMPLE_FORM_LIST_VALUE_FIELD);
                ValueType valueType = compileCollectionType(componentType, processingEnv, valueField, props, fieldBuilder, afterBuild, compiledTypes, fieldElement);
                valueField.setValueType(valueType.getType());
                fieldInfo.setSubFields(Collections.singletonList(valueField));
                return ValueType.SIMPLE_ARRAY;
            } else if (typeElement.getKind() == ElementKind.ENUM) {
                ValueType valueType = compileEnumType(typeElement, processingEnv, fieldInfo);
                fieldInfo.setValueType(valueType.getType());
                return ValueType.MULTI_SELECT;
            } else if (typeElement.getKind() == ElementKind.CLASS) {
                ValueType type = compileBasicClassType(typeElement, fieldElement, fieldInfo, processingEnv);
                if (type != null) {
                    String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                    throw new CompilerException("Field [" + fieldReferenceName + "] Collection basic type [" + typeElement + "] not supported, if you want multiple select, try use @Lookup");
                } else {
                    fieldInfo.setEntireRow(true);
                    fieldInfo.setFormItemProps(Collections.singletonMap("alwaysShowItemLabel", true));
                    compileSubFields(typeElement, processingEnv, fieldInfo, props, fieldBuilder, afterBuild, compiledTypes);
                    Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
                    if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.ARRAY) {
                        return ValueType.ARRAY;
                    }
                    return ValueType.SUB_TABLE;
                }
            }
        }
        String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
        throw new CompilerException("Field [" + fieldReferenceName + "] Collection type [" + componentType + "] not supported");
    }

    private static <T extends FieldInfo> ValueType compileDeclaredType(VariableElement fieldElement, ProcessingEnvironment processingEnv, T fieldInfo, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes) {
        TypeMirror fieldType = fieldElement.asType();
        TypeElement typeElement = (TypeElement) ((DeclaredType) fieldType).asElement();
        if (ElementCompileUtils.isAssignableFrom(Iterable.class, typeElement, processingEnv)) {
            return compileCollection((DeclaredType) fieldType, processingEnv, fieldInfo, props, fieldBuilder, afterBuild, compiledTypes, fieldElement);
        } else if (ElementCompileUtils.isAssignableFrom(Range.class, typeElement, processingEnv)) {
            return compileRange((DeclaredType) fieldType, fieldElement, processingEnv);
        } else {
            switch (typeElement.getKind()) {
                case ENUM:
                    return compileEnumType(typeElement, processingEnv, fieldInfo);
                case CLASS:
                    ValueType type = compileBasicClassType(typeElement, fieldElement, fieldInfo, processingEnv);
                    if (type != null) {
                        return type;
                    }
                    compileSubFields(typeElement, processingEnv, fieldInfo, props, fieldBuilder, afterBuild, compiledTypes);
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

    private static <T extends FieldInfo> ValueType compileShowField(T fieldInfo, VariableElement fieldElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes) {
        TypeElement typeElement = (TypeElement) ((DeclaredType) fieldElement.asType()).asElement();
        ShowField showFieldAnnotation = fieldElement.getAnnotation(ShowField.class);
        if (showFieldAnnotation != null) {
            VariableElement mainFieldElement = ElementCompileUtils.getFieldByPath(typeElement, processingEnv, showFieldAnnotation.value());
            if (mainFieldElement == null) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] has annotation @ShowField([" + String.join(",", showFieldAnnotation.value()) + "]), but field path not found field");
            }
            ValueType valueType = compileType(fieldInfo, mainFieldElement, processingEnv, props, fieldBuilder, afterBuild, compiledTypes);
            List<String> dataIndex = new ArrayList<>();
            dataIndex.addAll(fieldInfo.getDataIndex());
            dataIndex.addAll(Arrays.asList(showFieldAnnotation.value()));
            fieldInfo.setDataIndex(dataIndex);
            return valueType;
        }
        return null;
    }

    // 挺奇怪的...逻辑上应该先判断类型给 FieldInfo 的子类, 先这样简单处理一下吧
    private static void compileBooleanField(VariableElement fieldElement, FieldInfo fieldInfo) {
        BooleanDisplay booleanDisplayAnnotation = fieldElement.getAnnotation(BooleanDisplay.class);
        if (booleanDisplayAnnotation != null) {
            fieldInfo.setFieldProps(
                    BooleanDisplayInfo.builder()
                            .checkedChildren(booleanDisplayAnnotation.trueLabel())
                            .unCheckedChildren(booleanDisplayAnnotation.falseLabel()).build()
            );
        }
    }

    private static ValueType compilePrimitiveType(TypeMirror type, VariableElement fieldElement, FieldInfo fieldInfo) {
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
                compileBooleanField(fieldElement, fieldInfo);
                return ValueType.BOOLEAN;
            default:
                return ValueType.AUTO;
        }
    }

    private static ValueType compileBasicClassType(TypeElement typeElement, VariableElement fieldElement, FieldInfo fieldInfo, ProcessingEnvironment processingEnv) {
        Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
        switch (typeElement.getQualifiedName().toString()) {
            case "java.lang.Boolean":
                compileBooleanField(fieldElement, fieldInfo);
                return ValueType.BOOLEAN;
            case "java.lang.CharSequence":
            case "java.lang.String":
                if (fieldAnnotation != null) {
                    if (fieldAnnotation.type() == ValueType.TEXTAREA) {
                        return ValueType.TEXTAREA;
                    }
                    if (fieldAnnotation.type() == ValueType.DATE_WEEK) {
                        return ValueType.DATE_WEEK;
                    }
                    if (fieldAnnotation.type() == ValueType.DATE_MONTH) {
                        return ValueType.DATE_MONTH;
                    }
                    if (fieldAnnotation.type() == ValueType.DATE_QUARTER) {
                        return ValueType.DATE_QUARTER;
                    }
                    if (fieldAnnotation.type() == ValueType.DATE_YEAR) {
                        return ValueType.DATE_YEAR;
                    }
                    if (fieldAnnotation.type() == ValueType.IMAGE) {
                        return ValueType.IMAGE;
                    }
                    if (fieldAnnotation.type() == ValueType.AVATAR) {
                        return ValueType.AVATAR;
                    }
                }
                return ValueType.TEXT;
            case "java.time.LocalTime":
                return ValueType.TIME;
            case "java.time.LocalDate":
                return ValueType.DATE;
            case "java.util.Date":
                if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.DATE_TIME) {
                    return ValueType.DATE_TIME;
                }
                return ValueType.DATE;
            case "java.time.LocalDateTime":
                return ValueType.DATE_TIME;
            default:
                if (ElementCompileUtils.isAssignableFrom(Number.class, typeElement, processingEnv)) {
                    if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.MONEY) {
                        return ValueType.MONEY;
                    }
                    return ValueType.DIGIT;
                }
                return null;
        }
    }

    private static <T extends FieldInfo> void compileSubFields(TypeElement typeElement, ProcessingEnvironment processingEnv, T fieldInfo, ComponentProps props, Supplier<T> fieldBuilder, BiConsumer<VariableElement, T> afterBuild, Set<TypeMirror> compiledTypes) {
        List<T> subFields = compileTypeFields(typeElement, processingEnv, props, fieldBuilder, afterBuild, compiledTypes);
        fieldInfo.setSubFields(subFields);
        fieldInfo.setEntireRow(true);
    }

    private static <T extends FieldInfo> void compilePopup(T fieldInfo, VariableElement fieldElement, ComponentProps props) {
        Popup popupAnnotation = fieldElement.getAnnotation(Popup.class);
        fieldInfo.setPopupInfo(ElementCompileUtils.getPopupInfo(props, popupAnnotation));
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
            lookupActionInfo.extraFillFields(
                    Arrays.stream(lookupAnnotation.extraFillFields()).map(fillField -> LookupFillFieldInfo.builder()
                            .fromField(fillField.fromField())
                            .targetField(fillField.targetField())
                            .onlyEmpty(fillField.onlyEmpty())
                            .build()
                    ).collect(Collectors.toList())
            );
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
