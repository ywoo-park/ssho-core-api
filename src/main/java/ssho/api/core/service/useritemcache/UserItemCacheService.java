package ssho.api.core.service.useritemcache;

import ssho.api.core.domain.useritemcache.model.UserItemCache;

import java.io.IOException;

public interface UserItemCacheService {

    /**
     * 회원 추천 상품 캐시 업데이트
     * @return
     */
    void updateUserItemCache() throws IOException;

    /**
     * 회원 고유 번호로 회원 추천 상품 캐시 조회
     * @param userId
     * @return
     */
    UserItemCache getUserItemCache(int userId) throws IOException;
}
