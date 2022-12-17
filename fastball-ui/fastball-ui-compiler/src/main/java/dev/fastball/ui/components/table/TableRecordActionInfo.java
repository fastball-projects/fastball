package dev.fastball.ui.components.table;


import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.ui.common.ApiActionInfo;
import dev.fastball.ui.common.PopupActionInfo;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
interface TableRecordActionInfo {
    boolean refresh();
}

@AutoValue
interface TableRecordApiActionInfo extends ApiActionInfo, TableRecordActionInfo {
}
@AutoValue
interface TableRecordPopupActionInfo extends PopupActionInfo, TableRecordActionInfo {
}