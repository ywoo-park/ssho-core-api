package ssho.api.core.service.item;

import ssho.api.core.domain.item.model.Item;

import java.io.IOException;
import java.util.List;

public interface ItemService {
    List<Item> getItems();
    Item getItemCumById(String itemId);
    List<Item> getItemsByMallNo(String mallNo) throws IOException;
    Item getItemById(String itemId, String index) throws IOException;
}
