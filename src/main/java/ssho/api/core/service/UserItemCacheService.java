package ssho.api.core.service;

import ssho.api.core.dto.UserItemRes;
import ssho.api.core.model.UserSwipe;

import java.util.List;

public interface UserItemCacheService {

    List<UserSwipe> swipeLogs();

    List<UserItemRes> saveUserItemCache();
}
