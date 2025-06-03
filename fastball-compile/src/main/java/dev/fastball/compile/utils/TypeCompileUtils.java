package dev.fastball.compile.utils;

import com.google.common.collect.Maps;
import dev.fastball.compile.exception.CompilerException;
import dev.fastball.core.DefaultValues;
import dev.fastball.core.annotation.*;
import dev.fastball.core.field.Range;
import dev.fastball.meta.basic.*;
import dev.fastball.meta.component.ComponentProps;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.fastball.compile.CompileConstants.SIMPLE_FORM_LIST_VALUE_FIELD;
import static dev.fastball.compile.utils.LookupCompileUtils.compileLookup;
import static dev.fastball.compile.utils.LookupCompileUtils.compileTreeLookup;

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

    public static <T extends FieldInfo> List<T> compileSelectorTypeFields(TypeElement typeElement, ProcessingEnvironment processingEnv, ComponentProps props, Supplier<T> fieldBuilder, Set<String> tableSelectorFields) {
        Map<String, VariableElement> fieldMap = ElementCompileUtils.getFields(typeElement, processingEnv);
        return fieldMap.values().stream()
                .filter(fieldElement -> tableSelectorFields.contains(fieldElement.getSimpleName().toString()))
                .map(fieldElement -> compileField(fieldElement, processingEnv, props, fieldBuilder, null, new HashSet<>()))
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
            fieldInfo.setPlaceholder(fieldAnnotation.placeholder());
            fieldInfo.setReadonly(fieldAnnotation.readonly());
            fieldInfo.setOrder(fieldAnnotation.order());
            fieldInfo.setEntireRow(fieldAnnotation.entireRow());
            if (!fieldAnnotation.defaultValue().isEmpty()) {
                fieldInfo.setDefaultValue(fieldAnnotation.defaultValue());
            }
        } else {
            fieldInfo.setDisplay(DisplayType.Show);
            fieldInfo.setTitle(fieldElement.getSimpleName().toString());
        }
        if (fieldInfo.getDisplay() != DisplayType.Disabled) {
            compileType(fieldInfo, fieldElement, processingEnv, props, fieldBuilder, afterBuild, compiledTypes);
            fieldInfo.setValidationRules(compileFieldJsr303(fieldElement));
            fieldInfo.setExpression(compileCalculatedField(fieldElement));
            if (afterBuild != null) {
                afterBuild.accept(fieldElement, fieldInfo);
            }
        }
        return fieldInfo;
    }

    private static ExpressionInfo compileExpression(VariableElement fieldElement) {
        Expression expressionAnnotation = fieldElement.getAnnotation(Expression.class);
        if (expressionAnnotation == null) {
            return null;
        }
        return new ExpressionInfo(expressionAnnotation.fields(), ExpressionType.Expression, expressionAnnotation.expression());
    }


    private static ExpressionInfo compileCalculatedField(VariableElement fieldElement) {
        CalculatedField calculatedFieldAnnotation = fieldElement.getAnnotation(CalculatedField.class);
        if (calculatedFieldAnnotation == null) {
            return compileExpression(fieldElement);
        }
        TypeMirror functionClass = ElementCompileUtils.getTypeMirrorFromAnnotationValue(calculatedFieldAnnotation::function);
        if (functionClass == null) {
            throw new CompilerException("can't happened");
        }
        FrontendFunctionUtils.addFunction(functionClass);
        TypeElement componentTypeElement = (TypeElement) ((DeclaredType) functionClass).asElement();
        String functionClassName = componentTypeElement.getQualifiedName().toString();
        return new ExpressionInfo(calculatedFieldAnnotation.fields(), ExpressionType.Function, functionClassName);
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
        } else if (fieldElement.getAnnotation(DynamicPopup.class) != null) {
            compileDynamicPopup(fieldInfo, fieldElement, props);
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
            compileLookup(fieldInfo, fieldElement, props, processingEnv);
            valueType = ValueType.LOOKUP;
        } else if (fieldElement.getAnnotation(TreeLookup.class) != null) {
            compileTreeLookup(fieldInfo, fieldElement, props, processingEnv);
            valueType = ValueType.TREE_LOOKUP;
        } else if (fieldElement.getAnnotation(AutoComplete.class) != null) {
            compileAutoComplete(fieldInfo, fieldElement, processingEnv);
            valueType = ValueType.AUTO_COMPLETE;
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
        TypeElement typeElement = null;
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
            typeElement = (TypeElement) ((DeclaredType) componentType).asElement();
        } else if (componentType.getKind() == TypeKind.WILDCARD) {
            typeElement = (TypeElement) ((DeclaredType) ((WildcardType) componentType).getExtendsBound()).asElement();
        }
        if (typeElement != null) {
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
                    if (type == ValueType.ATTACHMENT) {
                        return ValueType.MULTI_ATTACHMENT;
                    }
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
                    if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.NO_ADD_SUB_TABLE) {
                        fieldInfo.setFieldProps(Maps.immutableEntry("noAdd", true));
                        return ValueType.NO_ADD_SUB_TABLE;
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
        Map<String, EnumItem> enumValues = new LinkedHashMap<>();
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
                            .checkedColor(booleanDisplayAnnotation.trueColor())
                            .unCheckedChildren(booleanDisplayAnnotation.falseLabel())
                            .unCheckedColor(booleanDisplayAnnotation.falseColor())
                            .build()
            );
        } else {
            fieldInfo.setFieldProps(
                    BooleanDisplayInfo.builder()
                            .checkedChildren(DefaultValues.DEFAULT_BOOLEAN_TRUE_LABEL)
                            .checkedColor(DefaultValues.DEFAULT_BOOLEAN_TRUE_COLOR)
                            .unCheckedChildren(DefaultValues.DEFAULT_BOOLEAN_FALSE_LABEL)
                            .unCheckedColor(DefaultValues.DEFAULT_BOOLEAN_FALSE_COLOR)
                            .build()
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
                Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
                if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.RATE) {
                    return ValueType.RATE;
                }
                DigitField digitFieldAnnotation = fieldElement.getAnnotation(DigitField.class);
                if (digitFieldAnnotation != null) {
                    fieldInfo.setDigitPrecision(digitFieldAnnotation.precision());
                }
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
                    if (fieldAnnotation.type() == ValueType.RICH_TEXT) {
                        return ValueType.RICH_TEXT;
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
                    if (fieldAnnotation.type() == ValueType.AVATAR) {
                        return ValueType.AVATAR;
                    }
                }
                return ValueType.TEXT;
            case "java.time.LocalTime":
                return ValueType.TIME;
            case "java.time.LocalDate":
                compileDateField(fieldInfo, fieldElement);
                return ValueType.DATE;
            case "java.util.Date":
                compileDateField(fieldInfo, fieldElement);
                if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.DATE_TIME) {
                    return ValueType.DATE_TIME;
                }
                return ValueType.DATE;
            case "java.time.LocalDateTime":
                compileDateField(fieldInfo, fieldElement);
                return ValueType.DATE_TIME;
            case "dev.fastball.core.field.Attachment":
                return ValueType.ATTACHMENT;
            case "dev.fastball.core.field.Address":
                return ValueType.ADDRESS;
            default:
                if (ElementCompileUtils.isAssignableFrom(Number.class, typeElement, processingEnv)) {
                    if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.MONEY) {
                        return ValueType.MONEY;
                    }
                    if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.SECOND) {
                        return ValueType.SECOND;
                    }
                    if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.PERCENT) {
                        return ValueType.PERCENT;
                    }
                    if (fieldAnnotation != null && fieldAnnotation.type() == ValueType.RATE) {
                        return ValueType.RATE;
                    }
                    DigitField digitFieldAnnotation = fieldElement.getAnnotation(DigitField.class);
                    if (digitFieldAnnotation != null) {
                        fieldInfo.setDigitPrecision(digitFieldAnnotation.precision());
                    }
                    return ValueType.DIGIT;
                }
                return null;
        }
    }

    private static <T extends FieldInfo> void compileDateField(T fieldInfo, VariableElement fieldElement) {
        DateFieldDefaultValue dateFieldDefaultValueAnnotation = fieldElement.getAnnotation(DateFieldDefaultValue.class);
        if (dateFieldDefaultValueAnnotation != null) {
            DateFieldDefaultValueInfo dateFieldDefaultValueInfo = new DateFieldDefaultValueInfo();
            dateFieldDefaultValueInfo.setDefaultValue(dateFieldDefaultValueAnnotation.defaultValue());
            dateFieldDefaultValueInfo.setOffset(dateFieldDefaultValueAnnotation.offset());
            dateFieldDefaultValueInfo.setOffsetUnit(dateFieldDefaultValueAnnotation.offsetUnit());
            fieldInfo.setDateDefaultValue(dateFieldDefaultValueInfo);
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

    private static <T extends FieldInfo> void compileDynamicPopup(T fieldInfo, VariableElement fieldElement, ComponentProps props) {
        DynamicPopup popupAnnotation = fieldElement.getAnnotation(DynamicPopup.class);
        fieldInfo.setPopupInfo(ElementCompileUtils.getDynamicPopupInfo(props, popupAnnotation));
        fieldInfo.setFieldType(FieldType.POPUP.getType());
    }

    private static <T extends FieldInfo> void compileAutoComplete(T field, VariableElement fieldElement, ProcessingEnvironment processingEnv) {
        AutoComplete autoCompleteAnnotation = fieldElement.getAnnotation(AutoComplete.class);
        if (autoCompleteAnnotation == null) {
            return;
        }
        Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
        if (fieldAnnotation != null && fieldAnnotation.type() != ValueType.AUTO && fieldAnnotation.type() != ValueType.AUTO_COMPLETE) {
            String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
            throw new CompilerException("Field [" + fieldReferenceName + "] has annotation @AutoComplete, but @Field.type is not AUTO_COMPLETE, try set @Field.type to FieldType.AUTO or FieldType.AUTO_COMPLETE");
        }
        AutoCompleteInfo_AutoValue autoCompleteInfo = new AutoCompleteInfo_AutoValue();
        TypeMirror lookupActionType = ElementCompileUtils.getTypeMirrorFromAnnotationValue(autoCompleteAnnotation::value);
        if (lookupActionType == null) {
            String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
            throw new CompilerException("Field [" + fieldReferenceName + "] annotation @AutoComplete.value type not found, check dependency.");
        }
        TypeElement autoCompleteActionElement = (TypeElement) processingEnv.getTypeUtils().asElement(lookupActionType);
        if (autoCompleteActionElement == null) {
            throw new CompilerException("can't happened");
        }
        autoCompleteInfo.autoCompleteKey(ElementCompileUtils.getComponentKey(autoCompleteActionElement));
        autoCompleteInfo.inputType(autoCompleteAnnotation.inputType());
        autoCompleteInfo.valueField(autoCompleteAnnotation.valueField());
        autoCompleteInfo.dependencyFields(autoCompleteAnnotation.dependencyFields());
        autoCompleteInfo.fields(
                Arrays.stream(autoCompleteAnnotation.displayFields()).map(displayField -> DisplayFieldInfo.builder()
                        .name(displayField.name())
                        .title(displayField.title())
                        .build()
                ).collect(Collectors.toList())
        );
        field.setAutoComplete(autoCompleteInfo);
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
