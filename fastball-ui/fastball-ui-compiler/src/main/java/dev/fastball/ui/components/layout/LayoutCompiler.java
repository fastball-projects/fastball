package dev.fastball.ui.components.layout;

import dev.fastball.ui.components.AbstractComponentCompiler;
import dev.fastball.ui.exception.CompilerException;

/**
 * @author gr@fastball.dev
 * @since 2022/12/19
 */
public class LayoutCompiler extends AbstractComponentCompiler<LayoutComponent, LayoutProps> {
    private static final String COMPONENT_TYPE = "FastballLayout";

    @Override
    protected LayoutProps compileProps(Class<LayoutComponent> componentClass) {
        LayoutComponent.LeftAndRight leftAndRight = componentClass.getDeclaredAnnotation(LayoutComponent.LeftAndRight.class);
        if (leftAndRight != null) {
            LeftAndRightLayoutProps_AutoValue props = new LeftAndRightLayoutProps_AutoValue();
            props.componentKey(getComponentKey(componentClass));
            props.left(getReferencedComponentInfo(leftAndRight.left()));
            props.right(getReferencedComponentInfo(leftAndRight.right()));
            return props;
        }
        LayoutComponent.TopAndBottom topAndBottom = componentClass.getDeclaredAnnotation(LayoutComponent.TopAndBottom.class);
        if (topAndBottom != null) {
            TopAndBottomLayoutProps_AutoValue props = new TopAndBottomLayoutProps_AutoValue();
            props.componentKey(getComponentKey(componentClass));
            props.top(getReferencedComponentInfo(topAndBottom.top()));
            props.bottom(getReferencedComponentInfo(topAndBottom.bottom()));
            return props;
        }
        LayoutComponent.LeftAndTopBottom leftAndTopBottom = componentClass.getDeclaredAnnotation(LayoutComponent.LeftAndTopBottom.class);
        if (leftAndTopBottom != null) {
            LeftAndTopBottomLayoutProps_AutoValue props = new LeftAndTopBottomLayoutProps_AutoValue();
            props.componentKey(getComponentKey(componentClass));
            props.left(getReferencedComponentInfo(leftAndTopBottom.left()));
            props.top(getReferencedComponentInfo(leftAndTopBottom.top()));
            props.bottom(getReferencedComponentInfo(leftAndTopBottom.bottom()));
            return props;
        }
        String message = String.format("LayoutComponent [%s] must add annotation @LeftAndRight or @TopAndBottom or LeftAndTopBottom", componentClass.getCanonicalName());
        throw new CompilerException(message);
    }

    @Override
    protected String getComponentName() {
        return COMPONENT_TYPE;
    }
}
