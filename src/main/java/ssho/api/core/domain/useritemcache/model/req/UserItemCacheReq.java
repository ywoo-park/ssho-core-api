package ssho.api.core.domain.useritemcache.model.req;

import lombok.Data;
import ssho.api.core.domain.mall.model.Mall;
import ssho.api.core.domain.userswipe.model.UserSwipeScore;

import java.util.List;

@Data
public class UserItemCacheReq {
    List<Mall> mallList;
    List<UserSwipeScore> userSwipeScoreList;
}
