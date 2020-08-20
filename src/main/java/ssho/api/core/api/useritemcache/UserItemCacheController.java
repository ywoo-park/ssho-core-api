package ssho.api.core.api.useritemcache;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.util.List;

@RequestMapping("/cache/user-item")
@RestController
public class UserItemCacheController {

    private UserItemCacheServiceImpl userItemCacheService;

    public UserItemCacheController(UserItemCacheServiceImpl userItemCacheService){
        this.userItemCacheService = userItemCacheService;
    }

    @GetMapping("/update")
    public List<UserItemCache> updateUserItemCache(){
        return userItemCacheService.updateUserItemCache();
    }

    @GetMapping("")
    public UserItemCache userItemCache(@RequestParam("userId") String userId){
        return userItemCacheService.getUserItemCache(userId);
    }
}
