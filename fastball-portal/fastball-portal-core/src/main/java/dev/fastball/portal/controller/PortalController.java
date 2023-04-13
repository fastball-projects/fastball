package dev.fastball.portal.controller;

import dev.fastball.portal.model.context.Menu;
import dev.fastball.portal.model.context.User;
import dev.fastball.portal.service.FastballPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static dev.fastball.portal.context.PortalContext.currentUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portal")
public class PortalController {

    private final FastballPortalService portalService;

    @GetMapping("/user")
    public User getCurrentUser() {
        return currentUser();
    }

    @GetMapping("/menu")
    public List<Menu> getMenus() {
        return portalService.getUserMenu(currentUser().getId());
    }

}
