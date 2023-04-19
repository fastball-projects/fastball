package dev.fastball.compile;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public interface FastballPreCompileGenerator {

    void generate(TypeElement element, ProcessingEnvironment processingEnv);

    Set<String> getSupportedAnnotationTypes();
}
