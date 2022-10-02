package idv.attendance.controller;

import idv.attendance.configuration.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping("ping")
    public String ping() {
        return "OK";
    }

    @GetMapping("user/info")
    public String userInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails.getCompanyId() + "|" + userDetails.getUserId();
    }
}
