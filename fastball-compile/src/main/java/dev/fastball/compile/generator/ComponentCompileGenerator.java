package dev.fastball.compile.generator;

import com.google.auto.service.AutoService;
import dev.fastball.compile.CompileContext;
import dev.fastball.compile.ComponentCompiler;
import dev.fastball.compile.ComponentCompilerLoader;
import dev.fastball.compile.FastballCompileGenerator;
import dev.fastball.compile.exception.CompilerException;
import dev.fastball.core.annotation.UIComponent;
import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.meta.component.ComponentInfo_AutoValue;
import dev.fastball.meta.utils.JsonUtils;

import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

import static dev.fastball.core.Constants.FASTBALL_RESOURCE_PREFIX;
import static dev.fastball.core.Constants.FASTBALL_VIEW_SUFFIX;

@AutoService(FastballCompileGenerator.class)
public class ComponentCompileGenerator implements FastballCompileGenerator {
    @Override
    public void generate(CompileContext compileContext) {
        for (ComponentCompiler<?> compiler : ComponentCompilerLoader.getLoaders()) {
            if (!compiler.support(compileContext)) {
                continue;
            }
            ComponentInfo<?> componentInfo = compiler.compile(compileContext);
            if(componentInfo == null) {
                continue;
            }

            String viewFilePath = compileContext.getComponentElement().getQualifiedName().toString().replaceAll("\\.", "/");
            String relativeName = FASTBALL_RESOURCE_PREFIX + componentInfo.material().getPlatform() + "/" + viewFilePath + FASTBALL_VIEW_SUFFIX;
            try {
                FileObject file = compileContext.getProcessingEnv().getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", relativeName);
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
                FileObject file = compileContext.getProcessingEnv().getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", relativeName);
                try (OutputStream out = file.openOutputStream()) {
                    JsonUtils.writeJson(out, componentInfo);
                }
            } catch (IOException e) {
                throw new CompilerException(e);
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(UIComponent.class.getCanonicalName());
    }
}
