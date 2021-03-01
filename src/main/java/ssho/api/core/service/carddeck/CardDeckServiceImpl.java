package ssho.api.core.service.carddeck;

import org.springframework.stereotype.Service;
import ssho.api.core.domain.carddeck.CardDeck;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.mall.model.Mall;
import ssho.api.core.domain.tag.model.Tag;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.mall.MallService;
import ssho.api.core.service.mall.MallServiceImpl;
import ssho.api.core.service.tag.TagServiceImpl;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardDeckServiceImpl implements CardDeckService {

    private final UserItemCacheServiceImpl userItemCacheService;
    private final ItemServiceImpl itemService;
    private final TagServiceImpl tagService;
    private final MallServiceImpl mallService;

    public CardDeckServiceImpl(UserItemCacheServiceImpl userItemCacheService, ItemServiceImpl itemService, TagServiceImpl tagService, MallServiceImpl mallService) {
        this.userItemCacheService = userItemCacheService;
        this.itemService = itemService;
        this.tagService = tagService;
        this.mallService = mallService;
    }

    @Override
    public CardDeck cardDeckByUserId(int userId) throws IOException {

        try {
            UserItemCache userItemCache = userItemCacheService.getUserItemCache(userId);

            List<Item> userItemList = userItemCache.getItemIdList().stream().map(itemService::getItemById).collect(Collectors.toList());

            CardDeck cardDeck = new CardDeck();
            cardDeck.setItemList(userItemList);
            cardDeck.setUserId(Integer.parseInt(userItemCache.getUserId()));

            return cardDeck;
        } catch (Exception e) {
            List<Item> userItemList = itemService.getItems().subList(0, 20);
            CardDeck cardDeck = new CardDeck();
            cardDeck.setItemList(userItemList);
            cardDeck.setUserId(userId);

            return cardDeck;
        }
    }

    @Override
    public CardDeck tutorialCardDeck() {

        CardDeck cardDeck = new CardDeck();

        List<String> tagIdList = tagService.getTagList().stream().map(Tag::getId).collect(Collectors.toList());
        List<Item> cardDeckItemList = new ArrayList<>();

        tagIdList.forEach(tagId -> {

            List<Mall> mallList = mallService.getMallList().stream().filter(mall -> mall.getTagList().stream().filter(tag -> tag.getId().equals(tagId)).count() > 0).collect(Collectors.toList());

            List<Item> itemList = mallList.stream().map(mall -> {
                try {
                    return itemService.getItemsByMallNo(mall.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).flatMap(items -> items != null ? items.stream() : null).collect(Collectors.toList());

            if(itemList.size() > 0){
                cardDeckItemList.add(itemList.get((int)(Math.random() * itemList.size())));
            }
        });

        Collections.shuffle(cardDeckItemList);
        cardDeck.setItemList(cardDeckItemList);

        return cardDeck;
    }
}
