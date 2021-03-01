package ssho.api.core.domain.shoppingbag;

import lombok.Data;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.usercardset.UserCardSet;

import java.util.List;

@Data
public class ShoppingBagCardSet {
    private UserCardSet userCardSet;
    private List<Item> itemList;
}
