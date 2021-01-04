package ssho.api.core.service.item;

import ssho.api.core.domain.item.model.Item;

import java.io.IOException;
import java.util.List;

public interface ItemService {
    List<Item> getItems();
    List<Item> getItemsByMallNo(String mallNo);
    Item getItemById(String itemId, String index) throws IOException;
}
