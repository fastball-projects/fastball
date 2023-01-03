package dev.fastball.apt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fastball.core.material.MaterialRegistry;
import dev.fastball.core.compile.ComponentCompiler;
import dev.fastball.core.compile.ComponentCompilerLoader;
import dev.fastball.core.compile.CompileContext;
import dev.fastball.core.component.ComponentInfo;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@SupportedAnnotationTypes("dev.fastball.core.annotation.UIComponent")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FastballComponentCompileProcessor extends AbstractProcessor {

    ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    MaterialRegistry materialRegistry = new MaterialRegistry(FastballComponentCompileProcessor.class.getClassLoader());

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement componentElement : loadElements(roundEnv)) {
            for (ComponentCompiler<?> compiler : ComponentCompilerLoader.getLoaders()) {
                CompileContext compileContext = new CompileContext(processingEnv, roundEnv, materialRegistry, componentElement);
                if (!compiler.support(compileContext)) {
                    continue;
                }
                ComponentInfo<?> componentInfo = compiler.compile(compileContext);
                try {
                    FileObject file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "FASTBALL-INF/" +componentElement.getQualifiedName().toString().replaceAll("\\.", "/") + ".fbv.json");
                    try (OutputStream out = file.openOutputStream()) {
                        objectMapper.writerWithDefaultPrettyPrinter().writeValue(out, componentInfo);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

    protected List<TypeElement> loadElements(RoundEnvironment roundEnv) {
        return getSupportedAnnotationTypes().stream()
                .map(annotationName -> processingEnv.getElementUtils().getTypeElement(annotationName))
                .filter(Objects::nonNull)
                .flatMap(annotationType -> roundEnv.getElementsAnnotatedWith(annotationType).stream())
                .map(TypeElement.class::cast).collect(Collectors.toList());
    }
}
