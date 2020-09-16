package ssho.api.core.service.useritemcache;

import ssho.api.core.domain.swipelog.model.res.UserSwipeLogRes;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.domain.userswipe.model.UserSwipe;

import java.util.List;

public interface UserItemCacheService {

    List<UserSwipeLogRes> swipeLogs();

    List<UserItemCache> updateUserItemCache();

    UserItemCache getUserItemCache(String userId);
}
