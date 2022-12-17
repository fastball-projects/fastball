package dev.fastball.ui.components.form;


import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.ApiActionInfo;
import dev.fastball.ui.common.PopupActionInfo;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
interface CloseableActionInfo extends ActionInfo {
    boolean closeOnSuccess();
}

@AutoValue
interface PopupFormApiActionInfo extends ApiActionInfo, CloseableActionInfo {
}

@AutoValue
interface PopupFormPopupActionInfo extends PopupActionInfo, CloseableActionInfo {
}

