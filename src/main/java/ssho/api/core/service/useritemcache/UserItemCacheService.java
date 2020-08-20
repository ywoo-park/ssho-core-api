package ssho.api.core.service.useritemcache;

import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.domain.userswipe.model.UserSwipe;

import java.util.List;

public interface UserItemCacheService {

    List<UserSwipe> swipeLogs();

    List<UserItemCache> updateUserItemCache();

    UserItemCache getUserItemCache(String userId);
}
