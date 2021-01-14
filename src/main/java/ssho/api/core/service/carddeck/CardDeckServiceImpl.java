package ssho.api.core.service.carddeck;

import org.springframework.stereotype.Service;
import ssho.api.core.domain.carddeck.CardDeck;
import ssho.api.core.domain.item.model.Item;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardDeckServiceImpl implements CardDeckService {

    private final UserItemCacheServiceImpl userItemCacheService;
    private final ItemServiceImpl itemService;

    public CardDeckServiceImpl(UserItemCacheServiceImpl userItemCacheService, ItemServiceImpl itemService) {
        this.userItemCacheService = userItemCacheService;
        this.itemService = itemService;
    }

    @Override
    public CardDeck cardDeckByUserId(int userId) throws IOException {

        UserItemCache userItemCache = userItemCacheService.getUserItemCache(userId);

        List<Item> userItemList = userItemCache.getItemIdList().stream().map(itemService::getItemById).collect(Collectors.toList());

        CardDeck cardDeck = new CardDeck();
        cardDeck.setItemList(userItemList);
        cardDeck.setUserId(Integer.parseInt(userItemCache.getUserId()));

        return cardDeck;
    }
}
