package dev.fastball.core.compile;

import dev.fastball.core.material.MaterialRegistry;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
public class CompileContext {
    private final ProcessingEnvironment processingEnv;
    private final RoundEnvironment roundEnv;
    private final MaterialRegistry materialRegistry;
    private final TypeElement componentElement;

    public CompileContext(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv, MaterialRegistry materialRegistry, TypeElement componentElement) {
        this.componentElement = componentElement;
        this.roundEnv = roundEnv;
        this.materialRegistry = materialRegistry;
        this.processingEnv = processingEnv;
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
}

