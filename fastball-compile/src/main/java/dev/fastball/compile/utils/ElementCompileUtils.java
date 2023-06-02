package dev.fastball.compile.utils;

import dev.fastball.compile.CompileContext;
import dev.fastball.compile.exception.CompilerException;
import dev.fastball.core.annotation.Popup;
import dev.fastball.core.annotation.RefComponent;
import dev.fastball.core.annotation.UIComponent;
import dev.fastball.core.component.Component;
import dev.fastball.core.info.basic.PopupInfo;
import dev.fastball.core.info.basic.RefComponentInfo;
import dev.fastball.core.info.component.ComponentProps;
import dev.fastball.core.info.component.ReferencedComponentInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import java.util.*;

import static dev.fastball.compile.CompileConstants.COMPONENT_IMPORT_PREFIX;
import static dev.fastball.compile.CompileConstants.SELF_PACKAGE;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public class ElementCompileUtils {

    private ElementCompileUtils() {
    }

    public static ReferencedComponentInfo getReferencedComponentInfo(ComponentProps props, AnnotationClassGetter annotationClassGetter) {
        TypeMirror popupComponentClass = ElementCompileUtils.getTypeMirrorFromAnnotationValue(annotationClassGetter);
        if (popupComponentClass == null) {
            throw new CompilerException("can't happened");
        }
        TypeElement componentTypeElement = (TypeElement) ((DeclaredType) popupComponentClass).asElement();
        if (Component.class.getCanonicalName().equals(componentTypeElement.getQualifiedName().toString())) {
            return null;
        }
        return ElementCompileUtils.getReferencedComponentInfo(props, componentTypeElement);
    }

    public static RefComponentInfo getReferencedComponentInfo(ComponentProps props, RefComponent refComponentAnnotation) {
        TypeMirror popupComponentClass = ElementCompileUtils.getTypeMirrorFromAnnotationValue(refComponentAnnotation::value);
        if (popupComponentClass == null) {
            throw new CompilerException("can't happened");
        }
        TypeElement componentTypeElement = (TypeElement) ((DeclaredType) popupComponentClass).asElement();
        if (Component.class.getCanonicalName().equals(componentTypeElement.getQualifiedName().toString())) {
            return null;
        }
        ReferencedComponentInfo componentInfo = ElementCompileUtils.getReferencedComponentInfo(props, componentTypeElement);
        if (componentInfo == null) {
            return null;
        }
        return RefComponentInfo.builder()
                .componentInfo(componentInfo)
                .dataPath(refComponentAnnotation.dataPath()).propsKey(refComponentAnnotation.propsKey())
                .currentFieldInput(refComponentAnnotation.currentFieldInput()).build();
    }

    public static ReferencedComponentInfo getReferencedComponentInfo(ComponentProps props, TypeElement componentTypeElement) {
        if (props.referencedComponentInfoList() == null) {
            props.referencedComponentInfoList(new HashSet<>());
        }
        String componentKey = getComponentKey(componentTypeElement);
        if (componentKey == null || componentKey.equals(props.componentKey())) {
            return null;
        }
        ReferencedComponentInfo refComponentInfo = new ReferencedComponentInfo();
        refComponentInfo.setComponent(COMPONENT_IMPORT_PREFIX + (props.referencedComponentInfoList().size() + 1));
        refComponentInfo.setComponentClass(componentTypeElement.getQualifiedName().toString());
        refComponentInfo.setComponentPackage(SELF_PACKAGE);
        refComponentInfo.setComponentPath(getComponentPath(componentTypeElement));
        props.referencedComponentInfoList().add(refComponentInfo);
        return refComponentInfo;
    }

    public static PopupInfo getPopupInfo(ComponentProps props, Popup popupAnnotation) {
        RefComponentInfo refComponentInfo = ElementCompileUtils.getReferencedComponentInfo(props, popupAnnotation.value());
        if (refComponentInfo == null) {
            return null;
        }
        PopupInfo popupInfo = new PopupInfo();
        popupInfo.setPopupComponent(refComponentInfo);
        popupInfo.setWidth(popupAnnotation.width());
        popupInfo.setTitle(popupAnnotation.title());
        popupInfo.setPopupType(popupAnnotation.popupType());
        popupInfo.setTriggerType(popupAnnotation.triggerType());
        popupInfo.setPlacementType(popupAnnotation.placementType());
        return popupInfo;
    }

    public static List<? extends TypeMirror> getTypeMirrorFromAnnotationValues(AnnotationClassGetter c) {
        try {
            c.execute();
        } catch (MirroredTypesException ex) {
            return ex.getTypeMirrors();
        }
        return Collections.emptyList();
    }

    public static TypeMirror getTypeMirrorFromAnnotationValue(AnnotationClassGetter c) {
        try {
            c.execute();
        } catch (MirroredTypeException ex) {
            return ex.getTypeMirror();
        }
        return null;
    }

    public static Map<String, VariableElement> getFields(TypeElement element, ProcessingEnvironment processingEnv) {
        Map<String, VariableElement> fieldMap = new LinkedHashMap<>();
        loadFields(element, processingEnv, fieldMap);
        return fieldMap;
    }

    public static Map<String, ExecutableElement> getMethods(TypeElement element, ProcessingEnvironment processingEnv) {
        Map<String, ExecutableElement> methodMap = new LinkedHashMap<>();
        loadMethods(element, processingEnv, methodMap);
        return methodMap;
    }

    public static String getComponentKey(TypeElement componentElement) {
        UIComponent frontendComponentAnnotation = componentElement.getAnnotation(UIComponent.class);
        if (frontendComponentAnnotation == null) {
            throw new CompilerException("Component class [" + componentElement.getQualifiedName() + "] not found annotation @UIComponent");
        }
        if (frontendComponentAnnotation.value().isEmpty()) {
            return componentElement.getSimpleName().toString();
        }
        return frontendComponentAnnotation.value();
    }

    public static String getComponentPath(TypeElement componentElement) {
        UIComponent frontendComponentAnnotation = componentElement.getAnnotation(UIComponent.class);
        if (frontendComponentAnnotation == null) {
            throw new CompilerException("Component class [" + componentElement.getQualifiedName() + "] not found annotation @UIComponent");
        }
        if (frontendComponentAnnotation.path().isEmpty()) {
            String packageName = componentElement.getQualifiedName().toString();
            return packageName.replaceAll("\\.", "/");
        }
        return frontendComponentAnnotation.path();
    }

    public static boolean isAssignableFrom(Class<?> clazz, CompileContext compileContext) {
        return isAssignableFrom(clazz, compileContext.getComponentElement(), compileContext.getProcessingEnv());
    }

    public static DeclaredType getDeclaredInterface(Class<?> clazz, TypeElement element, ProcessingEnvironment processingEnv) {
        for (TypeMirror anInterface : element.getInterfaces()) {
            DeclaredType interfaceType = (DeclaredType) anInterface;
            TypeElement superInterface = (TypeElement) interfaceType.asElement();
            if (clazz.getCanonicalName().equals(superInterface.getQualifiedName().toString())) {
                return interfaceType;
            }
            interfaceType = getDeclaredInterface(clazz, superInterface, processingEnv);
            if (interfaceType != null) {
                return interfaceType;
            }
        }
        Element superClassElement = processingEnv.getTypeUtils().asElement(element.getSuperclass());
        if (superClassElement == null || ((TypeElement) superClassElement).getQualifiedName().contentEquals(Object.class.getCanonicalName())) {
            return null;
        }
        return getDeclaredInterface(clazz, (TypeElement) superClassElement, processingEnv);
    }

    public static boolean isAssignableFrom(Class<?> clazz, TypeElement element, ProcessingEnvironment processingEnv) {
        if (clazz.getCanonicalName().equals(element.getQualifiedName().toString())) {
            return true;
        }
        if (element.getSuperclass() != null) {
            TypeElement superType = (TypeElement) processingEnv.getTypeUtils().asElement(element.getSuperclass());
            if (superType != null && isAssignableFrom(clazz, superType, processingEnv)) {
                return true;
            }
        }
        for (TypeMirror anInterface : element.getInterfaces()) {
            TypeElement superInterface = (TypeElement) processingEnv.getTypeUtils().asElement(anInterface);
            if (clazz.getCanonicalName().equals(superInterface.getQualifiedName().toString())) {
                return true;
            }
            if (isAssignableFrom(clazz, superInterface, processingEnv)) {
                return true;
            }
        }
        return false;
    }

    private static void loadFields(TypeElement element, ProcessingEnvironment processingEnv, Map<String, VariableElement> fieldMap) {
        TypeMirror superclass = element.getSuperclass();
        if (!(superclass instanceof NoType)) {
            TypeElement superClass = (TypeElement) processingEnv.getTypeUtils().asElement(superclass);
            loadFields(superClass, processingEnv, fieldMap);
        }
        ElementFilter.fieldsIn(element.getEnclosedElements()).forEach(method -> fieldMap.put(method.getSimpleName().toString(), method));
    }

    private static void loadMethods(TypeElement element, ProcessingEnvironment processingEnv, Map<String, ExecutableElement> methodMap) {
        TypeMirror superclass = element.getSuperclass();
        if (!(superclass instanceof NoType)) {
            TypeElement superClass = (TypeElement) processingEnv.getTypeUtils().asElement(superclass);
            loadMethods(superClass, processingEnv, methodMap);
        }
        List<? extends TypeMirror> interfaces = element.getInterfaces();
        if (interfaces != null && !interfaces.isEmpty()) {
            for (TypeMirror anInterface : interfaces) {
                TypeElement superInterface = (TypeElement) processingEnv.getTypeUtils().asElement(anInterface);
                loadMethods(superInterface, processingEnv, methodMap);
            }
        }
        ElementFilter.methodsIn(element.getEnclosedElements()).forEach(method -> methodMap.put(method.getSimpleName().toString(), method));
    }

    public static VariableElement getFieldByPath(TypeElement typeElement, ProcessingEnvironment processingEnv, String[] fieldPath) {
        TypeElement fieldType = typeElement;
        VariableElement fieldElement = null;
        for (String path : fieldPath) {
            fieldElement = getFields(fieldType, processingEnv).get(path);
            if (fieldElement != null && fieldElement.asType() instanceof DeclaredType) {
                fieldType = (TypeElement) ((DeclaredType) fieldElement.asType()).asElement();
            } else {
                return null;
            }
        }
        return fieldElement;
    }

}
