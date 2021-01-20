package ssho.api.core.service.shoppingbag;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.item.model.Item;
import ssho.api.core.domain.shoppingbag.ShoppingBagCardSet;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.usercardset.UserCardSet;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.usercardset.UserCardSetServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingBagServiceImpl implements ShoppingBagService {

    private  WebClient webClient;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    private ItemServiceImpl itemService;
    private UserCardSetServiceImpl userCardSetService;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    private final String ITEM_RT_INDEX ="item-rt";

    public ShoppingBagServiceImpl(final RestHighLevelClient restHighLevelClient, final ObjectMapper objectMapper, final ItemServiceImpl itemService, final UserCardSetServiceImpl userCardSetService) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
        this.itemService = itemService;
        this.userCardSetService = userCardSetService;
    }

    @Override
    public List<ShoppingBagCardSet> getLikeItemsByUserId(final String userId){

        List<ShoppingBagCardSet> shoppingBagCardSetList = new ArrayList<>();

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).build();

        final Map<Integer, List<SwipeLog>> groupedSwipeLogList =
                webClient
                        .get().uri("/log/swipe/user/like/grouped?userId={userId}", userId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<Integer, List<SwipeLog>>>() {})
                        .block();

        // 스와이프 로그 이력이 없을 경우
        if(groupedSwipeLogList.size() == 0) {
            return shoppingBagCardSetList;
        }

        for(Map.Entry<Integer, List<SwipeLog>> entry : groupedSwipeLogList.entrySet()){

            ShoppingBagCardSet shoppingBagCardSet = new ShoppingBagCardSet();
            List<SwipeLog> swipeLogList = entry.getValue();

            if(swipeLogList.size() == 0){
                continue;
            }

            UserCardSet userCardSet = userCardSetService.getById(swipeLogList.get(0).getUserCardSetId());

            if(userCardSet.equals(new UserCardSet()) || userCardSet.getCreateTime() == null){
                continue;
            }

            List<Item> itemList =
                    swipeLogList
                            .stream()
                            .map(swipeLog ->
                                itemService.getItemCumById(swipeLog.getItemId()))
                            .collect(Collectors.toList());

            shoppingBagCardSet.setItemList(itemList);
            shoppingBagCardSet.setUserCardSet(userCardSetService.getById(swipeLogList.get(0).getUserCardSetId()));

            shoppingBagCardSetList.add(shoppingBagCardSet);
        }

        return shoppingBagCardSetList;
    }
}
