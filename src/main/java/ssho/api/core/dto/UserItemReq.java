package ssho.api.core.dto;

import lombok.Data;
import ssho.api.core.domain.item.Item;
import ssho.api.core.model.UserSwipeScore;

import java.util.List;

@Data
public class UserItemReq {
    List<Item> itemList;
    List<UserSwipeScore> userSwipeScoreList;
}
