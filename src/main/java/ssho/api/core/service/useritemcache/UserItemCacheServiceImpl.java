package ssho.api.core.service.useritemcache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.item.model.Item;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.swipelog.model.res.UserSwipeLogRes;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.useritem.model.req.UserItemReq;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.domain.userswipe.model.UserSwipeScore;
import ssho.api.core.repository.user.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserItemCacheServiceImpl implements UserItemCacheService {

    private final UserRepository userRepository;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private WebClient webClient;

    @Value("${item.reco.api.host}")
    private String ITEM_RECO_API_HOST;

    @Value("${item.api.host}")
    private String ITEM_API_HOST;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    private String USER_ITEM_CACHE_INDEX = "cache-useritem";

    private final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)).build();

    public UserItemCacheServiceImpl(final UserRepository userRepository,
                                    final RestHighLevelClient restHighLevelClient, final ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void updateUserItemCache() throws IOException {

        UserItemReq userItemReq = getUserItemList();

        this.webClient = WebClient.builder().baseUrl(ITEM_RECO_API_HOST).exchangeStrategies(exchangeStrategies).build();

        // 회원 고유 번호 오름차순으로 추천 상품 캐시 생성
        List<UserItemCache> userItemCacheList =
                webClient
                        .post()
                        .uri("/reco/mf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(userItemReq)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<UserItemCache>>() {
                        })
                        .block()
                        .stream()
                        .sorted((a, b) -> {
                            if (Integer.parseInt(a.getUserId()) >= Integer.parseInt(b.getUserId())) return 1;
                            return -1;
                        }).collect(Collectors.toList());

        userItemCacheList.forEach(userItemCache -> userItemCache.setId(Integer.parseInt(userItemCache.getUserId())));

        delete(USER_ITEM_CACHE_INDEX);
        save(userItemCacheList, USER_ITEM_CACHE_INDEX);
    }

    @Override
    public UserItemCache getUserItemCache(int userId) throws IOException {

        // 추천 상품 캐시 조회
        UserItemCache userItemCache = getByUserId(userId, USER_ITEM_CACHE_INDEX);

        // 스와이프한 상품 고유 번호 리스트 조회
        List<String> swipedItemIdList = swipedItemIdList(userItemCache.getUserId());

        if(swipedItemIdList.size() > 0) {
            // 스와이프한 상품 필터링
            // 20개 상품
            List<String> filteredItemIdList =
                    userItemCache.getItemIdList()
                            .stream()
                            .filter(itemId -> !swipedItemIdList.contains(itemId))
                            .collect(Collectors.toList())
                            .subList(0, 20);

            userItemCache.setItemIdList(filteredItemIdList);
        }

        userItemCache.setItemIdList(userItemCache.getItemIdList().subList(0, 20));

        return userItemCache;
    }

    private List<Item> items() {

        this.webClient = WebClient.builder().baseUrl(ITEM_API_HOST).exchangeStrategies(exchangeStrategies).build();

        return webClient
                .get()
                .uri("/item")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Item>>() {
                })
                .block();
    }

    private List<String> swipedItemIdList(String userId) {

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).exchangeStrategies(exchangeStrategies).build();

        List<String> swipedItemIdList =
                webClient
                        .get()
                        .uri("/log/swipe/user?userId=" + userId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<SwipeLog>>() {
                        })
                        .block()
                        .stream()
                        .map(SwipeLog::getItemId)
                        .collect(Collectors.toList());
        return swipedItemIdList;
    }

    private UserItemReq getUserItemList() {

        // 전체 등록 상품 조회
        List<String> itemIdList = items().stream().map(Item::getId).collect(Collectors.toList());

        // 회원 전체 스와이프 로그 조회
        List<UserSwipeLogRes> userSwipeList = swipeLogs();

        UserItemReq userItemReq = new UserItemReq();
        List<UserSwipeScore> userSwipeScoreList = new ArrayList<>();

        userSwipeList
                .forEach(userSwipe -> {
                    UserSwipeScore userSwipeScore = new UserSwipeScore();
                    int[] scoreList = new int[itemIdList.size()];

                    List<SwipeLog> swipeLogList = userSwipe.getSwipeLogList();

                    String userId = userSwipe.getUserId();
                    userSwipeScore.setUserId(userId);

                    for (int i = 0; i < itemIdList.size(); i++) {

                        String itemId = itemIdList.get(i);

                        if(swipeLogList == null || swipeLogList.size() == 0) {
                            continue;
                        }

                        for (int j = 0; j < swipeLogList.size(); j++) {

                            if (swipeLogList.get(j).getItemId().equals(itemId)) {
                                scoreList[i] = swipeLogList.get(j).getScore();
                                break;
                            }
                        }
                    }

                    userSwipeScore.setScoreList(scoreList);
                    userSwipeScoreList.add(userSwipeScore);
                });

        userItemReq.setItemIdList(itemIdList);
        userItemReq.setUserSwipeScoreList(userSwipeScoreList);

        return userItemReq;
    }

    private List<UserSwipeLogRes> swipeLogs() {

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).exchangeStrategies(exchangeStrategies).build();

        List<User> userList = userRepository.findAll();

        return webClient
                .post()
                .uri("/log/swipe/user/grouped")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userList)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserSwipeLogRes>>() {
                })
                .block();
    }

    private void delete(String index) {
        try {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
            restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        }
        catch (ElasticsearchStatusException | IOException e) { }
    }

    private void save(List<UserItemCache> cacheList, String index) throws IOException {

        final BulkRequest bulkRequest = new BulkRequest();

        for (UserItemCache cache : cacheList) {

            IndexRequest indexRequest =
                    new IndexRequest(index)
                            .id(cache.getUserId())
                            .source(objectMapper.writeValueAsString(cache), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    private UserItemCache getByUserId(int userId, String index) throws IOException {
        GetRequest getRequest = new GetRequest(index, String.valueOf(userId));
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.readValue(getResponse.getSourceAsString(), UserItemCache.class);
    }
}
