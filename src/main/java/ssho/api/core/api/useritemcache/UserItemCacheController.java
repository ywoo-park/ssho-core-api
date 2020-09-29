package ssho.api.core.api.useritemcache;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;
import ssho.api.core.util.auth.Auth;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/cache/user-item")
@RestController
public class UserItemCacheController {

    private UserItemCacheServiceImpl userItemCacheService;

    public UserItemCacheController(UserItemCacheServiceImpl userItemCacheService) {
        this.userItemCacheService = userItemCacheService;
    }

    @GetMapping("/update")
    public void updateUserItemCache() {
        userItemCacheService.updateUserItemCache();
    }

    @Auth
    @GetMapping("")
    public UserItemCache userItemCache(final HttpServletRequest httpServletRequest) {
        final String userId = String.valueOf(httpServletRequest.getAttribute("userId"));
        return userItemCacheService.getUserItemCache(userId);
    }
}
