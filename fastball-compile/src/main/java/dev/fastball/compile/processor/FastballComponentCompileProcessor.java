package dev.fastball.compile.processor;

import dev.fastball.compile.CompileContext;
import dev.fastball.compile.FastballCompileGenerator;
import dev.fastball.compile.FastballPreCompileGenerator;
import dev.fastball.core.annotation.UIComponent;
import dev.fastball.core.material.MaterialRegistry;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FastballComponentCompileProcessor extends AbstractProcessor {

    MaterialRegistry materialRegistry = new MaterialRegistry(FastballComponentCompileProcessor.class.getClassLoader());

    private boolean preCompileDone = false;

    private final Set<Element> firstElement = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!preCompileDone) {
            if (loadGeneratorStream(FastballPreCompileGenerator.class).findAny().isPresent()) {
                processPreCompile(roundEnv);
                preCompileDone = true;
                firstElement.addAll(roundEnv.getRootElements());
            } else {
                processCompile(roundEnv);
            }
            return false;
        } else {
            processCompile(roundEnv);
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = loadGeneratorStream(FastballPreCompileGenerator.class)
                .flatMap(generator -> generator.getSupportedAnnotationTypes().stream())
                .collect(Collectors.toSet());
        loadGeneratorStream(FastballCompileGenerator.class).flatMap(generator -> generator.getSupportedAnnotationTypes().stream())
                .forEach(supportedAnnotationTypes::add);
        return supportedAnnotationTypes;
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
        if (!firstElement.isEmpty()) {
            loadGeneratorStream(FastballCompileGenerator.class).forEach(generator ->
                    loadElements(firstElement, generator.getSupportedAnnotationTypes()).stream()
                            .map(element -> new CompileContext(processingEnv, roundEnv, materialRegistry, element))
                            .forEach(generator::generate)
            );
            firstElement.clear();
        }
    }

    private <T> Stream<T> loadGeneratorStream(Class<T> clazz) {
        return StreamSupport.stream(ServiceLoader.load(clazz, clazz.getClassLoader()).spliterator(), false);
    }

    private List<TypeElement> loadElements(RoundEnvironment roundEnv, Set<String> supportedAnnotationTypes) {
        return supportedAnnotationTypes.stream().map(annotationName -> processingEnv.getElementUtils().getTypeElement(annotationName)).filter(Objects::nonNull).flatMap(annotationType -> roundEnv.getElementsAnnotatedWith(annotationType).stream()).map(TypeElement.class::cast).collect(Collectors.toList());
    }

    private List<TypeElement> loadElements(Set<? extends Element> elements, Set<String> supportedAnnotationTypes) {
        return supportedAnnotationTypes.stream().map(annotationName -> processingEnv.getElementUtils().getTypeElement(annotationName)).filter(Objects::nonNull).flatMap(annotationType -> elements.stream().filter(element ->
                processingEnv.getElementUtils().getAllAnnotationMirrors(element).stream().anyMatch(a -> annotationType.equals(a.getAnnotationType().asElement()))
        )).map(TypeElement.class::cast).collect(Collectors.toList());
    }
}
