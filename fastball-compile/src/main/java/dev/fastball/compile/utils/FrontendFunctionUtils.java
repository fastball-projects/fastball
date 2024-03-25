package dev.fastball.compile.utils;

import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;

public class FrontendFunctionUtils {

    private static final Set<TypeMirror> FUNCTION_SET = new HashSet<>();

    private FrontendFunctionUtils() {
    }

    public static void addFunction(TypeMirror functionCLass) {
        FUNCTION_SET.add(functionCLass);
    }

//    public static void buildMainClass(ProcessingEnvironment processingEnv) {
//        if (FUNCTION_SET.isEmpty()) {
//            return;
//        }
//        try {
//            buildMainJavaFile().writeTo(processingEnv.getFiler());
//        } catch (IOException e) {
//            throw new CompilerException(e);
//        }
//    }
//
//    private static JavaFile buildMainJavaFile() {
//        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
//        TypeSpec.Builder typeBuilder = TypeSpec
//                .classBuilder(FRONTEND_MAIN_CLASS_NAME)
//                .addModifiers(Modifier.PUBLIC)
//                .addModifiers(Modifier.FINAL);
//        FUNCTION_SET.forEach(func -> codeBlockBuilder.add("$L(new $T(), $T.class.getCanonicalName());", FRONTEND_REGISTER_METHOD_NAME, func, func));
//        MethodSpec method = MethodSpec.methodBuilder(FRONTEND_MAIN_METHOD_NAME)
//                .addModifiers(Modifier.PUBLIC)
//                .addModifiers(Modifier.STATIC)
//                .returns(TypeName.VOID)
//                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(String[].class), FRONTEND_MAIN_METHOD_ARG_NAME).build())
//                .addCode(codeBlockBuilder.build())
//                .addAnnotation(Deprecated.class)
//                .build();
//        typeBuilder.addMethod(buildFunctionRegister())
//                .addMethod(method)
//                .build();
//        return JavaFile.builder(FRONTEND_MAIN_CLASS_PACKAGE, typeBuilder.build()).build();
//    }
//
//    private static MethodSpec buildFunctionRegister() {
//        AnnotationSpec methodRegisterAnnotation = AnnotationSpec.builder(JSBody.class)
//                .addMember("params", "$L", CodeBlock.of("{\"" + FRONTEND_REGISTER_PARAM_NAME_1 + "\", \"" + FRONTEND_REGISTER_PARAM_NAME_2 + "\"}"))
//                .addMember("script", "$S", FRONTEND_REGISTER_FUNCTION_BODY)
//                .build();
//        return MethodSpec.methodBuilder(FRONTEND_REGISTER_METHOD_NAME)
//                .addModifiers(Modifier.PRIVATE)
//                .addModifiers(Modifier.STATIC)
//                .addModifiers(Modifier.NATIVE)
//                .returns(TypeName.VOID)
//                .addParameter(ParameterSpec.builder(TypeName.get(FrontendFunction.class), FRONTEND_REGISTER_PARAM_NAME_1).build())
//                .addParameter(ParameterSpec.builder(TypeName.get(String.class), FRONTEND_REGISTER_PARAM_NAME_2).build())
//                .addAnnotation(methodRegisterAnnotation)
//                .build();
//    }
}
