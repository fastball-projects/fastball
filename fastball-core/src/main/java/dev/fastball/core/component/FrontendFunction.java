package dev.fastball.core.component;

//@JSFunctor
public interface FrontendFunction<T, R> {
    R apply(T t);
}
