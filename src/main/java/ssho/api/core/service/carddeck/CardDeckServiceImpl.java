package ssho.api.core.service.carddeck;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;
import ssho.api.core.domain.carddeck.CardDeck;
import ssho.api.core.domain.item.model.Item;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.service.useritemcache.UserItemCacheServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardDeckServiceImpl implements CardDeckService {

    private final UserItemCacheServiceImpl userItemCacheService;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private final String ITEM_RT_INDEX ="item-rt";

    public CardDeckServiceImpl(UserItemCacheServiceImpl userItemCacheService, RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.userItemCacheService = userItemCacheService;
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public CardDeck cardDeckByUserId(int userId) throws IOException {

        UserItemCache userItemCache = userItemCacheService.getUserItemCache(userId);

        List<Item> userItemList = userItemCache.getItemIdList().stream().map(itemId -> {
            try {
                return itemById(itemId, ITEM_RT_INDEX);
            } catch (IOException e) {
                return null;
            }
        }).collect(Collectors.toList());

        CardDeck cardDeck = new CardDeck();
        cardDeck.setItemList(userItemList);
        cardDeck.setUserId(Integer.parseInt(userItemCache.getUserId()));

        return cardDeck;
    }

    private Item itemById(String itemId, String index) throws IOException {
        GetRequest getRequest = new GetRequest(index, itemId);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.readValue(getResponse.getSourceAsString(), Item.class);
    }
}
