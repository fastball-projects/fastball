package dev.fastball.compile;

import dev.fastball.compile.utils.ElementCompileUtils;
import dev.fastball.core.material.MaterialRegistry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
public class CompileContext {
    private final ProcessingEnvironment processingEnv;
    private final RoundEnvironment roundEnv;
    private final MaterialRegistry materialRegistry;
    private final TypeElement componentElement;
    private final Map<String, ExecutableElement> methods;

    public CompileContext(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv, MaterialRegistry materialRegistry, TypeElement componentElement) {
        this.componentElement = componentElement;
        this.roundEnv = roundEnv;
        this.materialRegistry = materialRegistry;
        this.processingEnv = processingEnv;
        this.methods = ElementCompileUtils.getMethods(componentElement, processingEnv);
    }

    public ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }

    public RoundEnvironment getRoundEnv() {
        return roundEnv;
    }

    public MaterialRegistry getMaterialRegistry() {
        return materialRegistry;
    }

    public TypeElement getComponentElement() {
        return componentElement;
    }

    public Map<String, ExecutableElement> getMethodMap() {
        return methods;
    }
}

