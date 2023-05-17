package dev.fastball.apt;

import com.squareup.javapoet.*;
import dev.fastball.auto.value.annotation.GeneratedFrom;
import dev.fastball.compile.FastballPreCompileGenerator;
import dev.fastball.compile.exception.CompilerException;
import dev.fastball.compile.utils.ElementCompileUtils;
import dev.fastball.core.annotation.SimpleQueryable;
import dev.fastball.core.querymodel.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ClassUtils;

import javax.annotation.processing.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Xyf
 */
public abstract class SimpleQueryModelProcessor implements FastballPreCompileGenerator {
    public static final String QUERY_MODEL_PACKAGE = "query";
    public static final String QUERY_MODEL_PREFIX = "Q";

    protected Map<String, TypeName> queryTypesMap = initQueryTypesMap();

    @Override
    public void generate(TypeElement element, ProcessingEnvironment processingEnv) {
        Map<String, VariableElement> fields = getFieldsMap(element, processingEnv);
        TypeSpec.Builder typeBuilder = typeBuilder(element, fields, processingEnv);
        AnnotationSpec generatedAnnotation = AnnotationSpec.builder(Generated.class).addMember("value", "$S", this.getClass().getName()).addMember("date", "$S", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).build();
        typeBuilder.addAnnotation(generatedAnnotation);
        AnnotationSpec generatedFromAnnotation = AnnotationSpec.builder(GeneratedFrom.class).addMember("value", element.getQualifiedName().toString() + ".class").build();
        typeBuilder.addAnnotation(generatedFromAnnotation);
        typeBuilder.addAnnotation(Getter.class);
        typeBuilder.addAnnotation(Setter.class);
        fields.forEach((name, field) -> {
            TypeName typeName = getQueryFieldTypeName(field, processingEnv);
            if (typeName == null) {
                return;
            }
            FieldSpec fieldSpec = FieldSpec.builder(typeName, name, Modifier.PRIVATE).build();
            typeBuilder.addField(fieldSpec);
        });

        JavaFile queryModelFile = JavaFile.builder(getPackageName(element, processingEnv), typeBuilder.build()).build();
        try {
            queryModelFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            throw new CompilerException(e);
        }
    }

    private Map<String, VariableElement> getFieldsMap(TypeElement element, ProcessingEnvironment processingEnv) {
        Map<String, VariableElement> fields = new HashMap<>();
        ElementCompileUtils.getFields(element, processingEnv).forEach((name, field) -> {
            SimpleQueryable.Ignore ignoreAnno = field.getAnnotation(SimpleQueryable.Ignore.class);
            if (ignoreAnno == null) {
                fields.put(name, field);
            }
        });
        return fields;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(SimpleQueryable.class.getCanonicalName());
    }

    private String getPackageName(TypeElement element, ProcessingEnvironment processingEnv) {
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        if (packageElement != null) {
            return packageElement.getQualifiedName().toString() + "." + QUERY_MODEL_PACKAGE;
        }
        return "";
    }

    protected TypeSpec.Builder typeBuilder(TypeElement element, Map<String, VariableElement> fields, ProcessingEnvironment processingEnv) {
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(buildClassName(element)).addModifiers(Modifier.PUBLIC);
        return typeBuilder(typeBuilder, element, fields, processingEnv);
    }

    protected abstract TypeSpec.Builder typeBuilder(TypeSpec.Builder builder, TypeElement element, Map<String, VariableElement> fields, ProcessingEnvironment processingEnv);

    protected String buildClassName(TypeElement element) {
        return QUERY_MODEL_PREFIX + element.getSimpleName();
    }

    protected Map<String, TypeName> initQueryTypesMap() {
        Map<String, TypeName> types = new HashMap<>();
        putQType(types, "java.math.BigDecimal", QBigDecimal.class);
        putQType(types, "java.util.Date", QDate.class);
        putQType(types, "java.lang.Integer", QInteger.class);
        putQType(types, "java.lang.Long", QLong.class);
        putQType(types, "java.lang.String", QString.class);
        return types;
    }

    private void putQType(Map<String, TypeName> types, String typeName, Class<?> type) {
        // TODO 如果有别的继承方式 这里要改一下
        types.put(typeName, TypeName.get(type));
    }

    protected TypeName getQueryFieldTypeName(VariableElement field, ProcessingEnvironment processingEnv) {
//        Types typeUtils = processingEnv.getTypeUtils();
//        Elements elementUtils = processingEnv.getElementUtils();
        final TypeMirror typeMirror = field.asType();
        String typeName = getTypeAsStr(typeMirror);
        TypeName type = queryTypesMap.get(typeName);
        if (type != null) {
            return type;
        }
        // TODO enum
        // TODO 关联关系
        // TODO json field?
        // TODO list field
        return buildQType(typeName);
    }

    private TypeName buildQType(String typeName) {
        return ParameterizedTypeName.get(
                ClassName.get("dev.fastball.core.querymodel", "QType"),
                ClassName.get(ClassUtils.getPackageName(typeName), ClassUtils.getShortName(typeName)));
    }

    private static String getTypeAsStr(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            return declaredType.asElement().toString();
        } else {
            return typeMirror.toString();
        }
    }
}
