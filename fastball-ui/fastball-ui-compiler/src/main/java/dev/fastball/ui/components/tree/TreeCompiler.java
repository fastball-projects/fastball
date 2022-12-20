package dev.fastball.ui.components.tree;


import dev.fastball.ui.components.AbstractComponentCompiler;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class TreeCompiler extends AbstractComponentCompiler<Tree<?>, TreeProps> {

    private static final String COMPONENT_TYPE = "FastballTree";

    @Override
    protected TreeProps compileProps(Class<Tree<?>> componentClass) {
        TreeProps_AutoValue props = new TreeProps_AutoValue();
        props.componentKey(getComponentKey(componentClass));
        return props;
    }

    @Override
    protected String getComponentName() {
        return COMPONENT_TYPE;
    }
}
