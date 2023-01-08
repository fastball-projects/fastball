package dev.fastball.compile;

import dev.fastball.core.info.component.ComponentInfo;
import dev.fastball.core.info.component.ComponentProps;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public interface ComponentCompiler<P extends ComponentProps> {
    ComponentInfo<P> compile(CompileContext componentContext);

    boolean support(CompileContext componentContext);
}
