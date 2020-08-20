package ssho.api.core.domain.useritemcache.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import ssho.api.core.domain.item.model.Item;

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
    private List<Item> itemList;
}
