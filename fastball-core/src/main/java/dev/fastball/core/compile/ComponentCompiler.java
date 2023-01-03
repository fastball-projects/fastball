package dev.fastball.core.compile;

import dev.fastball.core.component.ComponentProps;
import dev.fastball.core.component.ComponentInfo;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public interface ComponentCompiler<P extends ComponentProps> {
    ComponentInfo<P> compile(CompileContext componentContext);

    boolean support(CompileContext componentContext);
}
