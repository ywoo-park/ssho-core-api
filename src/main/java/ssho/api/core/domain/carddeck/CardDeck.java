package ssho.api.core.domain.carddeck;

import lombok.Data;
import ssho.api.core.domain.item.Item;

import java.util.List;

@Data
public class CardDeck {
    private int userId;
    private List<Item> itemList;
}
