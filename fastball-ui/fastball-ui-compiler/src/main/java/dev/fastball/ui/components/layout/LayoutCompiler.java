package dev.fastball.ui.components.layout;

import dev.fastball.compile.AbstractComponentCompiler;
import dev.fastball.compile.CompileContext;
import dev.fastball.compile.exception.CompilerException;

/**
 * @author gr@fastball.dev
 * @since 2022/12/19
 */
public class LayoutCompiler extends AbstractComponentCompiler<LayoutComponent, LayoutProps> {
    private static final String COMPONENT_TYPE = "FastballLayout";

    @Override
    protected LayoutProps compileProps(CompileContext compileContext) {

        LayoutComponent.LeftAndRight leftAndRight = compileContext.getComponentElement().getAnnotation(LayoutComponent.LeftAndRight.class);
        if (leftAndRight != null) {
            LeftAndRightLayoutProps_AutoValue props = new LeftAndRightLayoutProps_AutoValue();
            props.left(getReferencedComponentInfo(props, leftAndRight::left));
            props.right(getReferencedComponentInfo(props, leftAndRight::right));
            return props;
        }
        LayoutComponent.TopAndBottom topAndBottom = compileContext.getComponentElement().getAnnotation(LayoutComponent.TopAndBottom.class);
        if (topAndBottom != null) {
            TopAndBottomLayoutProps_AutoValue props = new TopAndBottomLayoutProps_AutoValue();
            props.top(getReferencedComponentInfo(props, topAndBottom::top));
            props.bottom(getReferencedComponentInfo(props, topAndBottom::bottom));
            return props;
        }
        LayoutComponent.LeftAndTopBottom leftAndTopBottom = compileContext.getComponentElement().getAnnotation(LayoutComponent.LeftAndTopBottom.class);
        if (leftAndTopBottom != null) {
            LeftAndTopBottomLayoutProps_AutoValue props = new LeftAndTopBottomLayoutProps_AutoValue();
            props.left(getReferencedComponentInfo(props, leftAndTopBottom::left));
            props.top(getReferencedComponentInfo(props, leftAndTopBottom::top));
            props.bottom(getReferencedComponentInfo(props, leftAndTopBottom::bottom));
            return props;
        }
        String message = String.format("LayoutComponent [%s] must add annotation @LeftAndRight or @TopAndBottom or LeftAndTopBottom", compileContext.getComponentElement().getQualifiedName());
        throw new CompilerException(message);
    }

    @Override
    public String getComponentName() {
        return COMPONENT_TYPE;
    }
}
