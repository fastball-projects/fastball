package dev.fastball.compile.processor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fastball.compile.CompileContext;
import dev.fastball.compile.ComponentCompiler;
import dev.fastball.compile.ComponentCompilerLoader;
import dev.fastball.compile.exception.CompilerException;
import dev.fastball.core.component.ComponentInfo;
import dev.fastball.core.component.ComponentInfo_AutoValue;
import dev.fastball.core.material.MaterialRegistry;
import dev.fastball.core.utils.JsonUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.fastball.core.Constants.FASTBALL_RESOURCE_PREFIX;
import static dev.fastball.core.Constants.FASTBALL_VIEW_SUFFIX;

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

                String viewFilePath = componentElement.getQualifiedName().toString().replaceAll("\\.", "/");
                String relativeName = FASTBALL_RESOURCE_PREFIX + viewFilePath + FASTBALL_VIEW_SUFFIX;
                try {
                    FileObject file = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", relativeName);
                    if (file != null) {
                        try (InputStream inputStream = file.openInputStream()) {
                            ComponentInfo<?> existedComponentInfo = JsonUtils.fromJson(inputStream, ComponentInfo_AutoValue.class);
                            // if view file is customized, skip generate
                            if (existedComponentInfo.customized() == Boolean.TRUE) {
                                continue;
                            }
                        }
                    }
                    // if file not found
                } catch (IOException ignore) {
                }
                try {
                    FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", relativeName);
                    try (OutputStream out = file.openOutputStream()) {
                        objectMapper.writerWithDefaultPrettyPrinter().writeValue(out, componentInfo);
                    }
                } catch (IOException e) {
                    throw new CompilerException(e);
                }
            }
        }
        return false;
    }

    protected List<TypeElement> loadElements(RoundEnvironment roundEnv) {
        return getSupportedAnnotationTypes().stream().map(annotationName -> processingEnv.getElementUtils().getTypeElement(annotationName)).filter(Objects::nonNull).flatMap(annotationType -> roundEnv.getElementsAnnotatedWith(annotationType).stream()).map(TypeElement.class::cast).collect(Collectors.toList());
    }
}
