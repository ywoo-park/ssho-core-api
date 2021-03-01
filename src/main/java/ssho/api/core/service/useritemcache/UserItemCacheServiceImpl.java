package ssho.api.core.service.useritemcache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.mall.model.Mall;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.swipelog.model.res.UserSwipeLogRes;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.useritemcache.model.UserItem;
import ssho.api.core.domain.useritemcache.model.UserMall;
import ssho.api.core.domain.useritemcache.model.req.UserItemCacheReq;
import ssho.api.core.domain.useritemcache.model.UserItemCache;
import ssho.api.core.domain.useritemcache.model.UserItemSimilarity;
import ssho.api.core.domain.userswipe.model.UserSwipeScore;
import ssho.api.core.repository.user.UserRepository;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.mall.MallServiceImpl;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserItemCacheServiceImpl implements UserItemCacheService {

    private final UserRepository userRepository;
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    private final ItemServiceImpl itemService;
    private final MallServiceImpl mallService;

    private WebClient webClient;

    @Value("${item.reco.api.host}")
    private String ITEM_RECO_API_HOST;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    private String USER_ITEM_CACHE_INDEX = "cache-useritem";

    private final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)).build();

    public UserItemCacheServiceImpl(final UserRepository userRepository,
                                    final RestHighLevelClient restHighLevelClient, final ObjectMapper objectMapper, final ItemServiceImpl itemService, final MallServiceImpl mallService) {
        this.userRepository = userRepository;
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
        this.itemService = itemService;
        this.mallService = mallService;

    }

    @Override
    public void updateUserItemCache() throws IOException {

        UserItemCacheReq userItemCacheReq = getUserItemList();

        this.webClient = WebClient.builder().baseUrl(ITEM_RECO_API_HOST).exchangeStrategies(exchangeStrategies).build();

        // 회원 추천 상품 캐시 생성
        List<UserItemCache> userItemCacheList =
                webClient
                        .post()
                        .uri("/reco/mf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(userItemCacheReq)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<UserItemCache>>() {
                        })
                        .block()
                        .stream()
                        .sorted((a, b) -> {
                            if (Integer.parseInt(a.getUserId()) >= Integer.parseInt(b.getUserId())) return 1;
                            return -1;
                        }).collect(Collectors.toList());

        // 회원 캐시 내 몰 가중치 필터링
        // 1. NaN -> 0
        // 2. Normalize
        userItemCacheList.forEach(userItemCache -> {

                    List<Double> mallRateList = userItemCache.getUserMallList().stream().map(UserMall::getRate).collect(Collectors.toList());

                    userItemCache.getUserMallList().forEach(userMall -> {
                        Double rate = userMall.getRate();
                        if (Double.isNaN(rate)) {
                            userMall.setRate(0.0);
                        }
                        userMall.setRate(normalize(rate, mallRateList));
                    });
                }
        );

        // 몰 가중치 내림차순 정렬
        userItemCacheList.forEach(userItemCache -> Collections.sort(userItemCache.getUserMallList()));

        // 몰별 전체 상품 조회
        Map<String, List<Item>> mallItemListMap = mallItemListMap();

        userItemCacheList.forEach(userItemCache -> {
            String userId = userItemCache.getUserId();
            List<Item> recentItemList = recentItemList(Integer.parseInt(userId));

            // 최근 스와이프 상품이 있을시
            if (recentItemList.size() > 0) {

                // 전체 상품 리스트에서 필터링
                // 1. imageVec==null 제외
                // 2. 최근 스와이프 상품 제외
                List<Item> itemList = itemService.getItems().stream().filter(item -> {
                    if (item.getImageVec() == null) {
                        return false;
                    }

                    for (Item recentItem : recentItemList) {
                        if (recentItem.getId().equals(item.getId())) {
                            return false;
                        }
                    }
                    return true;
                }).collect(Collectors.toList());

                UserItemSimilarity userItemSimilarity = new UserItemSimilarity();
                userItemSimilarity.setRecentItemList(recentItemList);
                userItemSimilarity.setUserItemList(itemList.stream().map(item -> {
                    UserItem userItem = new UserItem();
                    userItem.setItem(item);
                    return userItem;
                }).collect(Collectors.toList()));

                this.webClient = WebClient.builder().baseUrl(ITEM_RECO_API_HOST).exchangeStrategies(exchangeStrategies).build();

                userItemSimilarity =
                        webClient
                                .post()
                                .uri("/feature/distance")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .bodyValue(userItemSimilarity)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<UserItemSimilarity>() {
                                })
                                .block();

                Collections.sort(userItemSimilarity.getUserItemList());
                userItemCache.setUserItemList(userItemSimilarity.getUserItemList());
                userItemCache.setRecentItemIdList(userItemSimilarity.getRecentItemList().stream().map(Item::getId).collect(Collectors.toList()));

                List<UserMall> userMallList = userItemCache.getUserMallList();
                List<UserItem> userItemList = userItemCache.getUserItemList();

                // 몰 가중치가 있는 경우
                if (!(userMallList.stream().map(UserMall::getRate).collect(Collectors.toList())).equals(new ArrayList<>(Collections.nCopies(userMallList.size(), 0.0)))) {
                    userItemList.stream().forEach(userItem -> {
                        String mallNo = userItem.getItem().getMallNo();
                        Double mallRate = userMallList
                                .stream()
                                .filter(userMall -> userMall.getMall().getId().equals(mallNo))
                                .map(UserMall::getRate)
                                .findFirst()
                                .orElse(1.0);

                        Double newItemRate = userItem.getRate() * mallRate;
                        userItem.setRate(newItemRate);
                    });

                    Collections.sort(userItemList);
                }

                userItemCache.setItemIdList(userItemCache.getUserItemList().stream().map(userItem -> userItem.getItem().getId()).collect(Collectors.toList()));
            }

            // 최근 스와이프 상품이 없을시
            else {
                List<Item> itemList = new ArrayList<>();
                userItemCache.getUserMallList().forEach(userMall -> itemList.addAll(mallItemListMap.get(userMall.getMall().getId())));
                userItemCache.setItemIdList(itemList.stream().map(Item::getId).collect(Collectors.toList()));
            }
        });

        userItemCacheList.forEach(userItemCache -> {

            userItemCache.setId(Integer.parseInt(userItemCache.getUserId()));

            if (userItemCache.getUserItemList() != null) {
                userItemCache.getUserItemList().forEach(userItem -> {
                    Item item = new Item();
                    item.setId(userItem.getItem().getId());
                    item.setTitle(userItem.getItem().getTitle());
                    item.setMallNm(userItem.getItem().getMallNm());
                    userItem.setItem(item);
                });
            }

            if (userItemCache.getUserMallList() != null) {
                userItemCache.getUserMallList().forEach(userMall -> {
                    Mall mall = new Mall();
                    mall.setId(userMall.getMall().getId());
                    mall.setName(userMall.getMall().getName());
                    userMall.setMall(mall);
                });
            }
        });

        delete(USER_ITEM_CACHE_INDEX);

        for (UserItemCache userItemCache : userItemCacheList) {
            save(userItemCache, USER_ITEM_CACHE_INDEX);
        }
    }

    @Override
    public UserItemCache getUserItemCache(int userId) throws IOException {

        // 추천 상품 캐시 조회
        UserItemCache userItemCache = getUserItemCacheByUserId(userId);

        // 스와이프한 상품 고유 번호 리스트 조회
        List<String> swipedItemIdList = swipedItemIdList(userItemCache.getUserId());

        if (swipedItemIdList.size() > 0) {
            // 스와이프한 상품 필터링
            // 20개 상품
            List<String> filteredItemIdList =
                    userItemCache.getUserItemList()
                            .stream()
                            .map(UserItem::getItem)
                            .map(Item::getId)
                            .filter(itemId -> !swipedItemIdList.contains(itemId))
                            .collect(Collectors.toList())
                            .subList(0, 20);

            userItemCache.setItemIdList(filteredItemIdList);
        }

        userItemCache.setItemIdList(userItemCache.getItemIdList().subList(0, 20));

        return userItemCache;
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

    private UserItemCacheReq getUserItemList() {

        List<Mall> mallList = mallService.getMallList().stream().filter(mall -> mall.getLastSyncTime() != null).collect(Collectors.toList());

        // 회원 전체 스와이프 로그 조회
        List<UserSwipeLogRes> userSwipeList = swipeLogs();

        UserItemCacheReq userItemReq = new UserItemCacheReq();
        List<UserSwipeScore> userSwipeScoreList = new ArrayList<>();

        userSwipeList
                .forEach(userSwipe -> {
                    UserSwipeScore userSwipeScore = new UserSwipeScore();
                    userSwipeScore.setUserId(userSwipe.getUserId());

                    double[] scoreList = new double[mallList.size()];

                    if (userSwipe.getSwipeLogList() != null) {

                        List<SwipeLog> swipeLogList = userSwipe.getSwipeLogList();

                        for (int i = 0; i < mallList.size(); i++) {

                            double score = 0.0;
                            int swipeLogSize = 0;

                            Mall mall = mallList.get(i);

                            for (SwipeLog swipeLog : swipeLogList) {

                                String itemId = swipeLog.getItemId();

                                try {
                                    if (itemService.getItemById(itemId, "item" + "-" + mall.getId() + "-" + "cum") != null) {
                                        Item item = itemService.getItemById(itemId, "item" + "-" + mall.getId() + "-" + "cum");

                                        if (item.equals(new Item())) {
                                            continue;
                                        }

                                        String swipeMallNo = item.getMallNo();

                                        if (swipeMallNo.equals(mall.getId())) {
                                            if(swipeLog.getScore() == 1){
                                                score = score + swipeLog.getScore();
                                            }
                                            swipeLogSize++;
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            scoreList[i] = score != 0 && swipeLogSize != 0 ? score / swipeLogSize : 0.0;
                        }
                    }
                    userSwipeScore.setScoreList(scoreList);
                    userSwipeScoreList.add(userSwipeScore);
                });

        userItemReq.setMallList(mallList);
        userItemReq.setUserSwipeScoreList(userSwipeScoreList);

        return userItemReq;
    }

    public List<UserItemCache> getAllUserCache() {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(USER_ITEM_CACHE_INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(10000);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            return Stream.of(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(src -> {
                        try {
                            return objectMapper.readValue(src, UserItemCache.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
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
        } catch (ElasticsearchStatusException | IOException e) {
        }
    }

    private void save(UserItemCache cache, String index) throws IOException {

        IndexRequest indexRequest =
                new IndexRequest(index)
                        .id(cache.getUserId())
                        .source(objectMapper.writeValueAsString(cache), XContentType.JSON);

        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public UserItemCache getUserItemCacheByUserId(int userId) throws IOException {
        UserItemCache userItemCache = new UserItemCache();

        GetRequest getRequest = new GetRequest(USER_ITEM_CACHE_INDEX, String.valueOf(userId));

        boolean exist = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        if(exist) {
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            userItemCache = objectMapper.readValue(getResponse.getSourceAsString(), UserItemCache.class);

            List<String> swipedItemIdList = swipedItemIdList(userItemCache.getUserId());

            if(swipedItemIdList.size() > 0) {
                List<UserItem> filteredUserItemList = userItemCache.getUserItemList().stream().filter(userItem -> !swipedItemIdList.contains(userItem.getItem().getId())).collect(Collectors.toList());
                userItemCache.setUserItemList(filteredUserItemList);

                List<String> filteredItemIdList = userItemCache.getItemIdList().stream().filter(itemId -> !swipedItemIdList.contains(itemId)).collect(Collectors.toList());
                userItemCache.setItemIdList(filteredItemIdList);
            }
        }

        userItemCache.setId(userId);
        return userItemCache;
    }

    private Double normalize(Double e, List<Double> list) {
        Double max = Collections.max(list);
        Double min = Collections.min(list);

        if (max - min == 0.0) {
            return 0.0;
        }

        return (e - min) / (max - min);
    }

    public List<Item> recentItemList(int userId) {
        List<Item> itemList = new ArrayList<>();

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).exchangeStrategies(exchangeStrategies).build();
        List<SwipeLog> swipeLogList =
                webClient
                        .get()
                        .uri("/log/swipe/user/like/recent?userId=" + userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<SwipeLog>>() {
                        })
                        .block();

        if (swipeLogList.size() == 0) {
            return itemList;
        }

        if (swipeLogList.size() > 5) {
            swipeLogList = swipeLogList.subList(0, 5);
        }

        return swipeLogList
                .stream()
                .map(swipeLog -> itemService.getItemById(swipeLog.getItemId()))
                .collect(Collectors.toList());
    }

    public Map<String, List<Item>> mallItemListMap() throws IOException {
        Map<String, List<Item>> mallItemListMap = new HashMap<>();

        List<Mall> mallList = mallService.getMallList().stream().filter(mall -> mall.getLastSyncTime() != null).collect(Collectors.toList());
        for (Mall mall : mallList) {
            mallItemListMap.put(mall.getId(), itemService.getItemsByMallNo(mall.getId()));
        }
        return mallItemListMap;
    }
}
