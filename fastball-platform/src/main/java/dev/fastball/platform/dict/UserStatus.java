package dev.fastball.platform.dict;

import dev.fastball.core.annotation.DictionaryItem;

public enum UserStatus {

    @DictionaryItem(label = "启用")
    Enabled,

    @DictionaryItem(label = "禁用")
    Disabled
}
