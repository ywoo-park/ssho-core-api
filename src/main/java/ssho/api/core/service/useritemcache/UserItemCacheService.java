package ssho.api.core.service.useritemcache;

import ssho.api.core.domain.useritem.model.res.UserItemRes;
import ssho.api.core.domain.userswipe.model.UserSwipe;

import java.util.List;

public interface UserItemCacheService {

    List<UserSwipe> swipeLogs();

    List<UserItemRes> saveUserItemCache();
}
