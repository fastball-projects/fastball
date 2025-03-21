package dev.fastball.platform.message;

import dev.fastball.core.annotation.Field;
import dev.fastball.core.component.Component;
import dev.fastball.meta.basic.DisplayType;
import lombok.Data;

@Data
public class Message {
    @Field(title = "id", display = DisplayType.Hidden)
    private String id;
    @Field(title = "已读")
    private boolean alreadyRead;
    @Field(title = "消息标题")
    private String title;
    @Field(title = "消息内容")
    private String content;
    @Field(title = "消息数据")
    private Object data;
    @Field(title = "消息跳转组件", display = DisplayType.Hidden)
    private Class<? extends Component> componentClass;
    @Field(title = "消息跳转组件", display = DisplayType.Hidden)
    private String component;

    public String getComponent() {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName();
    }
}
