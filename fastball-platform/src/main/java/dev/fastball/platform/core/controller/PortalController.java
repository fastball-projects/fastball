package dev.fastball.platform.core.controller;

import dev.fastball.core.Result;
import dev.fastball.platform.core.context.PortalContext;
import dev.fastball.platform.core.model.ChangePasswordModel;
import dev.fastball.platform.core.service.FastballPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portal")
public class PortalController {

    private final FastballPortalService portalService;

    @PostMapping("/changePassword")
    public Result<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel) {
        portalService.changePassword(PortalContext.currentUser().getId(), changePasswordModel.getPassword(), changePasswordModel.getNewPassword());
        return Result.success();
    }
}
