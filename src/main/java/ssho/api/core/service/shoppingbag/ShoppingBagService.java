package ssho.api.core.service.shoppingbag;

import ssho.api.core.domain.shoppingbag.ShoppingBagCardSet;

import java.util.List;

public interface ShoppingBagService {

    /**
     * userId로
     *
     * @param userId
     * @return
     */
    List<ShoppingBagCardSet> getShoppingBagCardSetListByUserId(final String userId);
}
