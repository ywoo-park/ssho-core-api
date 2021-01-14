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
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.service.item.ItemServiceImpl;

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

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    private final String ITEM_RT_INDEX ="item-rt";

    public ShoppingBagServiceImpl(final RestHighLevelClient restHighLevelClient, final ObjectMapper objectMapper, final ItemServiceImpl itemService) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
        this.itemService = itemService;
    }

    @Override
    public List<List<Item>> getLikeItemsByUserId(final String userId){

        List<List<Item>> likedItemsList = new ArrayList<>();

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).build();

        final Map<Integer, List<SwipeLog>> groupedSwipeLogList =
                webClient
                        .get().uri("/log/swipe/user/like/grouped?userId={userId}", userId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<Integer, List<SwipeLog>>>() {})
                        .block();

        // 스와이프 로그 이력이 없을 경우
        if(groupedSwipeLogList.size() == 0) {
            return likedItemsList;
        }

        for(Map.Entry<Integer, List<SwipeLog>> entry : groupedSwipeLogList.entrySet()){

            List<SwipeLog> swipeLogList = entry.getValue();

            List<Item> itemList =
                    swipeLogList
                            .stream()
                            .map(swipeLog ->
                                itemService.getItemCumById(swipeLog.getItemId()))
                            .collect(Collectors.toList());

            likedItemsList.add(itemList);
        }

        return likedItemsList;
    }
}
