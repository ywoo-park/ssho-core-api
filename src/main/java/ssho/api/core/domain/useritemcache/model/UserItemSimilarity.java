package ssho.api.core.domain.useritemcache.model;

import lombok.Data;
import ssho.api.core.domain.item.model.Item;

import java.util.List;

@Data
public class UserItemSimilarity {
    private List<Item> recentItemList;
    private List<UserItem> userItemList;
}
