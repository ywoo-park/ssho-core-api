package ssho.api.core.domain.useritemcache.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import ssho.api.core.domain.item.Item;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@RedisHash("userItemCache")
public class UserItemCache {
    private int id;
    private String userId;

    private List<String> recentItemIdList;
    private List<Item> recentItemList;

    private List<UserMall> userMallList;
    private List<UserItem> userItemList;

    private List<String> itemIdList;
}
