package dev.fastball.platform.controller;

import dev.fastball.core.Result;
import dev.fastball.platform.context.PortalContext;
import dev.fastball.platform.model.ChangePasswordModel;
import dev.fastball.platform.service.PlatformUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portal")
public class PortalController {

    private final PlatformUserService portalService;

    @PostMapping("/changePassword")
    public Result<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel) {
        portalService.changePassword(PortalContext.currentUser().getId(), changePasswordModel.getPassword(), changePasswordModel.getNewPassword());
        return Result.success();
    }
}
