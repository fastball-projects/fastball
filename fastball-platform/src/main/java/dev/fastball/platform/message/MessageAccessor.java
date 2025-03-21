package dev.fastball.platform.message;

import dev.fastball.core.component.DataResult;
import dev.fastball.platform.entity.User;

public interface MessageAccessor {
    boolean hasUnreadMessage(User currentUser);

    DataResult<Message> loadMessages(User currentUser, Long currentPage);

    boolean readMessage(User currentUser, String messageId);
}
