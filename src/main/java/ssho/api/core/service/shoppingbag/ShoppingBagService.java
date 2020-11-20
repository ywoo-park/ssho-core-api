package ssho.api.core.service.shoppingbag;

import ssho.api.core.domain.item.model.Item;

import java.util.List;

public interface ShoppingBagService {
    List<List<Item>> getLikeItemsByUserId(final String userId);
}
