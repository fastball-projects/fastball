package dev.fastball.compile.utils;

import javax.lang.model.type.MirroredTypesException;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@FunctionalInterface
public interface AnnotationClassGetter {
    Class<?> execute() throws MirroredTypesException;
}
