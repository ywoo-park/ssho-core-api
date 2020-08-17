package ssho.api.core.domain.useritem.model.res;

import lombok.Data;
import ssho.api.core.domain.item.model.Item;

import java.util.List;

@Data
public class UserItemRes {
    private String userId;
    private List<Item> itemList;
}
