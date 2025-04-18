package dev.fastball.platform.controller;

import dev.fastball.core.Result;
import dev.fastball.core.component.DataResult;
import dev.fastball.platform.context.PortalContext;
import dev.fastball.platform.feature.business.context.BusinessContextAccessor;
import dev.fastball.platform.feature.business.context.BusinessContextItem;
import dev.fastball.platform.message.Message;
import dev.fastball.platform.message.MessageAccessor;
import dev.fastball.platform.model.ChangePasswordModel;
import dev.fastball.platform.service.BusinessContextService;
import dev.fastball.platform.service.PlatformUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portal")
public class PortalController {

    private final BusinessContextService businessContextService;
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

    @GetMapping("/business-context/{businessContextKey}")
    public Result<Collection<? extends BusinessContextItem>> loadBusinessContextItems(@PathVariable String businessContextKey) {
        BusinessContextAccessor<?> businessContext = businessContextService.getBusinessContext(businessContextKey);
        if (businessContext != null) {
            return Result.success(businessContext.listBusinessContextItems());
        }
        return Result.success();
    }

}
