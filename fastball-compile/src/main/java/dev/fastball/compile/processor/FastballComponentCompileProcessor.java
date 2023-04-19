package dev.fastball.compile.processor;

import dev.fastball.compile.CompileContext;
import dev.fastball.compile.FastballCompileGenerator;
import dev.fastball.compile.FastballPreCompileGenerator;
import dev.fastball.core.material.MaterialRegistry;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@SupportedAnnotationTypes("dev.fastball.core.annotation.UIComponent")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FastballComponentCompileProcessor extends AbstractProcessor {

    MaterialRegistry materialRegistry = new MaterialRegistry(FastballComponentCompileProcessor.class.getClassLoader());

    private boolean preCompileDone = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (preCompileDone) {
            processCompile(roundEnv);
        } else {
            processPreCompile(roundEnv);
            preCompileDone = true;
        }
        return false;
    }

    private void processPreCompile(RoundEnvironment roundEnv) {
        loadGeneratorStream(FastballPreCompileGenerator.class).forEach(generator ->
                loadElements(roundEnv, generator.getSupportedAnnotationTypes())
                        .forEach(element -> generator.generate(element, processingEnv))
        );
    }

    private void processCompile(RoundEnvironment roundEnv) {
        loadGeneratorStream(FastballCompileGenerator.class).forEach(generator ->
                loadElements(roundEnv, generator.getSupportedAnnotationTypes()).stream()
                        .map(element -> new CompileContext(processingEnv, roundEnv, materialRegistry, element))
                        .forEach(generator::generate)
        );
    }

    private <T> Stream<T> loadGeneratorStream(Class<T> clazz) {
        return StreamSupport.stream(ServiceLoader.load(clazz, clazz.getClassLoader()).spliterator(), false);
    }

    private List<TypeElement> loadElements(RoundEnvironment roundEnv, Set<String> supportedAnnotationTypes) {
        return supportedAnnotationTypes.stream().map(annotationName -> processingEnv.getElementUtils().getTypeElement(annotationName)).filter(Objects::nonNull).flatMap(annotationType -> roundEnv.getElementsAnnotatedWith(annotationType).stream()).map(TypeElement.class::cast).collect(Collectors.toList());
    }
}
