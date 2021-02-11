package ssho.api.core.api.useritemcache;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.io.IOException;
import java.util.List;

@RequestMapping("/cache/user-item")
@RestController
public class UserItemCacheController {

    private final UserItemCacheServiceImpl userItemCacheService;

    public UserItemCacheController(UserItemCacheServiceImpl userItemCacheService) {
        this.userItemCacheService = userItemCacheService;
    }

    /**
     * 회원 추천 상품 캐시 업데이트
     */
    @GetMapping("/update")
    public void updateUserItemCache() throws IOException {
        userItemCacheService.updateUserItemCache();
    }

    /**
     * 회원 추천 상품 캐시 조회
     */
    @GetMapping("")
    public List<UserItemCache> getAllUserItemCache() {
        return userItemCacheService.getAllUserCache();
    }
}
