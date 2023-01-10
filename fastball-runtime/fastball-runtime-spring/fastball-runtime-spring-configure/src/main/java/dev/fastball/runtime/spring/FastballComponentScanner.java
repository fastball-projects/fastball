package dev.fastball.runtime.spring;

import dev.fastball.core.annotation.UIComponent;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
public class FastballComponentScanner extends ClassPathBeanDefinitionScanner {

    private static final Class<? extends Annotation>[] INCLUDED_ANNOTATION_TYPES = new Class[]{
            UIComponent.class
    };

    public FastballComponentScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
        Arrays.stream(INCLUDED_ANNOTATION_TYPES).map(AnnotationTypeFilter::new).forEach(this::addIncludeFilter);
        setBeanNameGenerator(FullyQualifiedAnnotationBeanNameGenerator.INSTANCE);
    }

    public void scan(ResourceLoader resourceLoader) {
        setResourceLoader(resourceLoader);
        BeanDefinitionRegistry registry = getRegistry();
        assert registry != null;
        Set<String> packages = findBasePackages(registry);
        scan(packages.toArray(new String[]{}));
    }

    public static Set<String> findBasePackages(BeanDefinitionRegistry registry) {
        Set<String> packages = new LinkedHashSet<>();
        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
            BeanDefinition definition = registry.getBeanDefinition(beanDefinitionName);
            packages.addAll(parseDefinition(definition));
        }
        return packages;
    }

    private static Set<String> parseDefinition(BeanDefinition definition) {
        if (definition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) definition;
            return parseComponentScan(annotatedBeanDefinition.getMetadata());
        }
        return Collections.emptySet();
    }

    private static Set<String> parseComponentScan(AnnotationMetadata metadata) {
        Set<String> packages = new LinkedHashSet<>();

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(ComponentScan.class.getName(), true));
        if (null == attributes) {
            return packages;
        }

        packages.addAll(Arrays.asList(attributes.getStringArray("value")));
        packages.addAll(Arrays.asList(attributes.getStringArray("basePackages")));
        packages.addAll(classToPackage(attributes.getStringArray("basePackageClasses")));
        if (packages.isEmpty()) {
            packages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packages;
    }

    private static Collection<String> classToPackage(String[] values) {
        if (values == null || values.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(values)
                .map(ClassUtils::getPackageName)
                .collect(Collectors.toList());
    }
}
