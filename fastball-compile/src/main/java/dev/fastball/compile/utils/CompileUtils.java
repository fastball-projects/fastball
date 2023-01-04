package dev.fastball.compile.utils;

import dev.fastball.compile.CompileContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import java.util.*;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public class CompileUtils {

    private CompileUtils() {
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
        Map<String, VariableElement> fieldMap = new TreeMap<>();
        loadFields(element, processingEnv, fieldMap);
        return fieldMap;
    }

    public static Map<String, ExecutableElement> getMethods(TypeElement element, ProcessingEnvironment processingEnv) {
        Map<String, ExecutableElement> methodMap = new TreeMap<>();
        loadMethods(element, processingEnv, methodMap);
        return methodMap;
    }

    public static boolean isAssignableFrom(Class<?> clazz, CompileContext compileContext) {
        return isAssignableFrom(clazz, compileContext.getComponentElement(), compileContext.getProcessingEnv());
    }

    public static DeclaredType getDeclaredInterface(Class<?> clazz, TypeElement element) {
        for (TypeMirror anInterface : element.getInterfaces()) {
            DeclaredType interfaceType = (DeclaredType) anInterface;
            TypeElement superInterface = (TypeElement) interfaceType.asElement();
            if (clazz.getCanonicalName().equals(superInterface.getQualifiedName().toString())) {
                return interfaceType;
            }
            interfaceType = getDeclaredInterface(clazz, superInterface);
            if (interfaceType != null) {
                return interfaceType;
            }
        }
        return null;
    }

    private static boolean isAssignableFrom(Class<?> clazz, TypeElement element, ProcessingEnvironment processingEnv) {
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
}
