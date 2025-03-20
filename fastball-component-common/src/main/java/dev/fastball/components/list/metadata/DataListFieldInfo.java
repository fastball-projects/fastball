package dev.fastball.components.list.metadata;

import dev.fastball.components.list.config.DataListFieldPosition;
import dev.fastball.meta.basic.FieldInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataListFieldInfo extends FieldInfo {

    private DataListFieldPosition position;

    private boolean showLabel;

    private boolean showIfNull;

    private String nullText;
}
