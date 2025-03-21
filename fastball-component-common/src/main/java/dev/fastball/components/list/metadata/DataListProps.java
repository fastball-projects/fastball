package dev.fastball.components.list.metadata;


import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.components.common.sort.SortFieldInfo;
import dev.fastball.meta.action.ActionInfo;
import dev.fastball.meta.basic.FieldInfo;
import dev.fastball.meta.component.ComponentProps;

import java.util.List;

@AutoValue
public interface DataListProps extends ComponentProps {
    List<DataListFieldInfo> fields();

    List<FieldInfo> queryFields();

    List<SortFieldInfo> sortFields();

    List<ActionInfo> selectionActions();
}
