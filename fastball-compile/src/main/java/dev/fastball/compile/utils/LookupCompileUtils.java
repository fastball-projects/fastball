package dev.fastball.compile.utils;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.core.annotation.Field;
import dev.fastball.core.annotation.Lookup;
import dev.fastball.core.annotation.LookupSelector;
import dev.fastball.core.annotation.TreeLookup;
import dev.fastball.core.component.LookupAction;
import dev.fastball.meta.basic.*;
import dev.fastball.meta.basic.DependencyParamInfo;
import dev.fastball.meta.basic.FieldInfo;
import dev.fastball.meta.basic.LookupFillFieldInfo;
import dev.fastball.meta.basic.ValueType;
import dev.fastball.meta.component.ComponentProps;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LookupCompileUtils {

    private static final ConcurrentHashMap<TypeElement, List<FieldInfo>> LOOKUP_FIELD_INFO_CACHE = new ConcurrentHashMap<>();

    public static <T extends FieldInfo> void compileLookup(T field, VariableElement fieldElement, ComponentProps props, ProcessingEnvironment processingEnv) {
        Lookup lookupAnnotation = fieldElement.getAnnotation(Lookup.class);
        if (lookupAnnotation != null) {
            Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
            if (fieldAnnotation != null && fieldAnnotation.type() != ValueType.AUTO && fieldAnnotation.type() != ValueType.LOOKUP && fieldAnnotation.type() != ValueType.SELECT) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] has annotation @Lookup, but @Field.type is not LOOKUP, try set @Field.type to FieldType.AUTO or FieldType.LOOKUP");
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
            lookupActionInfo.showSearch(lookupAnnotation.showSearch());
            lookupActionInfo.selectedFirst(lookupAnnotation.selectedFirst());
            lookupActionInfo.dependencyParams(Arrays.stream(lookupAnnotation.dependencyParams()).map(dependencyParam -> DependencyParamInfo.builder()
                    .paramKey(dependencyParam.paramKey())
                    .paramPath(dependencyParam.paramPath())
                    .rootValue(dependencyParam.rootValue())
                    .build()
            ).collect(Collectors.toList()));
            lookupActionInfo.extraFillFields(
                    Arrays.stream(lookupAnnotation.extraFillFields()).map(fillField -> LookupFillFieldInfo.builder()
                            .fromField(fillField.fromField())
                            .targetField(fillField.targetField())
                            .onlyEmpty(fillField.onlyEmpty())
                            .build()
                    ).collect(Collectors.toList())
            );

            // FIXME ???
            List<TypeElement> genericTypes = ElementCompileUtils.getGenericTypes(LookupAction.class, lookupActionElement, processingEnv);
            LookupSelector lookupSelectorAnnotation = lookupActionElement.getAnnotation(LookupSelector.class);
            Set<String> selectorTableFields = Arrays.stream(lookupAnnotation.selectorTableFields()).collect(Collectors.toSet());
            if (selectorTableFields.isEmpty() && lookupSelectorAnnotation != null) {
                selectorTableFields = Arrays.stream(lookupSelectorAnnotation.selectorTableFields()).collect(Collectors.toSet());
            }
            if (!selectorTableFields.isEmpty() && !genericTypes.isEmpty()) {
                lookupActionInfo.columns(TypeCompileUtils.compileSelectorTypeFields(genericTypes.get(0), processingEnv, props, FieldInfo::new, selectorTableFields));
            }

            field.setLookup(lookupActionInfo);
        }
    }

    public static <T extends FieldInfo> void compileTreeLookup(T field, VariableElement fieldElement, ComponentProps props, ProcessingEnvironment processingEnv) {
        TreeLookup lookupAnnotation = fieldElement.getAnnotation(TreeLookup.class);
        if (lookupAnnotation != null) {
            Field fieldAnnotation = fieldElement.getAnnotation(Field.class);
            if (fieldAnnotation != null && fieldAnnotation.type() != ValueType.AUTO && fieldAnnotation.type() != ValueType.TREE_SELECT && fieldAnnotation.type() != ValueType.TREE_LOOKUP) {
                String fieldReferenceName = ((TypeElement) fieldElement.getEnclosingElement()).getQualifiedName() + ":" + fieldElement.getSimpleName();
                throw new CompilerException("Field [" + fieldReferenceName + "] has annotation @Lookup, but @Field.type is not TREE_LOOKUP, try set @Field.type to FieldType.AUTO or FieldType.TREE_LOOKUP");
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
            lookupActionInfo.showSearch(lookupAnnotation.showSearch());
            lookupActionInfo.childrenField(lookupAnnotation.childrenField());

            lookupActionInfo.extraFillFields(
                    Arrays.stream(lookupAnnotation.extraFillFields()).map(fillField -> LookupFillFieldInfo.builder()
                            .fromField(fillField.fromField())
                            .targetField(fillField.targetField())
                            .onlyEmpty(fillField.onlyEmpty())
                            .build()
                    ).collect(Collectors.toList())
            );

            // FIXME ???
            List<TypeElement> genericTypes = ElementCompileUtils.getGenericTypes(LookupAction.class, lookupActionElement, processingEnv);
            LookupSelector lookupSelectorAnnotation = lookupActionElement.getAnnotation(LookupSelector.class);
            Set<String> selectorTableFields = Arrays.stream(lookupAnnotation.selectorTableFields()).collect(Collectors.toSet());
            if (selectorTableFields.isEmpty() && lookupSelectorAnnotation != null) {
                selectorTableFields = Arrays.stream(lookupSelectorAnnotation.selectorTableFields()).collect(Collectors.toSet());
            }
            if (!selectorTableFields.isEmpty() && !genericTypes.isEmpty()) {
                lookupActionInfo.columns(TypeCompileUtils.compileSelectorTypeFields(genericTypes.get(0), processingEnv, props, FieldInfo::new, selectorTableFields));
            }

            field.setLookup(lookupActionInfo);
        }
    }


}
