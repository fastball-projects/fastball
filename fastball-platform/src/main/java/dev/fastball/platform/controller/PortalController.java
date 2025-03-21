package dev.fastball.platform.controller;

import dev.fastball.core.Result;
import dev.fastball.core.component.DataResult;
import dev.fastball.platform.context.PortalContext;
import dev.fastball.platform.message.Message;
import dev.fastball.platform.message.MessageAccessor;
import dev.fastball.platform.model.ChangePasswordModel;
import dev.fastball.platform.service.PlatformUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portal")
public class PortalController {

    private final PlatformUserService portalService;
    private final MessageAccessor messageAccessor;

    @PostMapping("/changePassword")
    public Result<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel) {
        portalService.changePassword(PortalContext.currentUser().getId(), changePasswordModel.getPassword(), changePasswordModel.getNewPassword());
        return Result.success();
    }

    @GetMapping("/hasUnreadMessage")
    public Result<Boolean> hasUnreadMessage() {
        return Result.success(messageAccessor.hasUnreadMessage(PortalContext.currentUser()));
    }

    @GetMapping("/loadMessage")
    public Result<DataResult<Message>> loadMessage(@RequestParam Long current) {
        return Result.success(messageAccessor.loadMessages(PortalContext.currentUser(), current));
    }

    @PostMapping("/readMessage/{messageId}")
    public Result<Boolean> hasUnreadMessage(@PathVariable String messageId) {
        return Result.success(messageAccessor.readMessage(PortalContext.currentUser(), messageId));
    }

}
