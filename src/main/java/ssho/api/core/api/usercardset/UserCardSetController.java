package ssho.api.core.api.usercardset;

import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.usercardset.UserCardSet;
import ssho.api.core.service.usercardset.UserCardSetServiceImpl;
import ssho.api.core.util.auth.Auth;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/user-cardset")
@RestController
public class UserCardSetController {

    private UserCardSetServiceImpl userCardSetService;

    public UserCardSetController(UserCardSetServiceImpl userCardSetService) {
        this.userCardSetService = userCardSetService;
    }

    @Auth
    @GetMapping("")
    UserCardSet getRecentUserCardSet(final HttpServletRequest httpServletRequest) {
        final String userId = String.valueOf(httpServletRequest.getAttribute("userId"));
        return userCardSetService.getRecentByUserId(Integer.parseInt(userId));
    }

    @Auth
    @PostMapping("")
    UserCardSet save(@RequestBody UserCardSet userCardSet, final HttpServletRequest httpServletRequest) {
        userCardSet.setUserId(Integer.parseInt(String.valueOf(httpServletRequest.getAttribute("userId"))));
        return userCardSetService.save(userCardSet);
    }
}
