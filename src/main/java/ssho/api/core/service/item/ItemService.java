package ssho.api.core.service.item;

import ssho.api.core.domain.item.Item;

import java.io.IOException;
import java.util.List;

public interface ItemService {
    List<Item> getItems();
    Item getItemById(String itemId);
    Item getItemCumById(String itemId);
    List<Item> getItemsByMallNo(String mallNo) throws IOException;
    List<Item> getItemsByTagId(String tagId);
    Item getItemById(String itemId, String index) throws IOException;
}
