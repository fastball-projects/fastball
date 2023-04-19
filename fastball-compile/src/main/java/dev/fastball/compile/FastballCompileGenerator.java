package dev.fastball.compile;

import java.util.Set;

public interface FastballCompileGenerator {

    void generate(CompileContext compileContext);

    Set<String> getSupportedAnnotationTypes();
}
