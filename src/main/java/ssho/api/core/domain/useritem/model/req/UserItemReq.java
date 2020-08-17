package ssho.api.core.domain.useritem.model.req;

import lombok.Data;
import ssho.api.core.domain.item.model.Item;
import ssho.api.core.domain.userswipe.model.UserSwipeScore;

import java.util.List;

@Data
public class UserItemReq {
    List<Item> itemList;
    List<UserSwipeScore> userSwipeScoreList;
}
