package dev.fastball.platform.message;

import dev.fastball.core.component.DataResult;
import dev.fastball.platform.entity.User;

public class DefaultMessageAccessor implements MessageAccessor {
    @Override
    public boolean hasUnreadMessage(User currentUser) {
        return false;
    }

    @Override
    public DataResult<Message> loadMessages(User currentUser, Long currentPage) {
        return DataResult.empty();
    }

    @Override
    public boolean readMessage(User currentUser, String messageId) {
        return false;
    }
}
