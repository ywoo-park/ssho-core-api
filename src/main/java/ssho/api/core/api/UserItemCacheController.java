package ssho.api.core.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ssho.api.core.dto.UserItemRes;
import ssho.api.core.service.impl.UserItemCacheServiceImpl;

import java.util.List;

@RequestMapping("/cache/user-item")
@RestController
public class UserItemCacheController {

    private UserItemCacheServiceImpl userItemCacheService;

    public UserItemCacheController(UserItemCacheServiceImpl userItemCacheService){
        this.userItemCacheService = userItemCacheService;
    }

    @GetMapping("")
    public List<UserItemRes> saveUserItemCache(){
        return userItemCacheService.saveUserItemCache();
    }
}
