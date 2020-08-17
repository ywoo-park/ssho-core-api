package ssho.api.core.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.item.Item;
import ssho.api.core.domain.log.SwipeLog;
import ssho.api.core.domain.user.User;
import ssho.api.core.dto.UserItemReq;
import ssho.api.core.dto.UserItemRes;
import ssho.api.core.model.UserSwipe;
import ssho.api.core.model.UserSwipeScore;
import ssho.api.core.repository.UserRepository;
import ssho.api.core.service.UserItemCacheService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserItemCacheServiceImpl implements UserItemCacheService {

    private UserRepository userRepository;
    private WebClient webClient;

    @Value("${item.reco.api.host}")
    private String ITEM_RECO_API_HOST;

    private final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)).build();

    public UserItemCacheServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserItemReq getUserItemList() {

        List<Item> itemList = items();
        List<UserSwipe> userSwipeList = swipeLogs();

        List<UserSwipeScore> userSwipeScoreList = new ArrayList<>();

        userSwipeList
                .stream()
                .forEach(userSwipe -> {
                    UserSwipeScore userSwipeScore = new UserSwipeScore();
                    int[] scoreList = new int[itemList.size()];

                    List<SwipeLog> swipeLogList = userSwipe.getSwipeLogList();
                    String userId = userSwipe.getUserId();
                    userSwipeScore.setUserId(userId);

                    for (int i = 0; i < itemList.size(); i++) {

                        String itemId = itemList.get(i).getId();

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

        UserItemReq userItemReq = new UserItemReq();
        userItemReq.setItemList(itemList);
        userItemReq.setUserSwipeScoreList(userSwipeScoreList);

        return userItemReq;
    }

    @Override
    public List<UserSwipe> swipeLogs() {

        this.webClient = WebClient.builder().baseUrl("http://13.124.59.2:8082").exchangeStrategies(exchangeStrategies).build();
        //this.webClient = WebClient.builder().baseUrl("http://localhost:8082").exchangeStrategies(exchangeStrategies).build();

        List<User> userList = userRepository.findAll();

        return webClient
                .post()
                .uri("/log/swipe/user/grouped")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userList)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserSwipe>>() {
                })
                .block();
    }

    public List<Item> items() {

        this.webClient = WebClient.builder().baseUrl("http://13.124.59.2:8081").exchangeStrategies(exchangeStrategies).build();

        return webClient
                .get()
                .uri("/item")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Item>>() {
                })
                .block();
    }

    @Override
    public List<UserItemRes> saveUserItemCache() {
        // TODO: 추천 모델 서버로 부터 추천 상품 요청
        // TODO: Redis에 저장

        UserItemReq userItemReq = getUserItemList();

        this.webClient = WebClient.builder().baseUrl(ITEM_RECO_API_HOST).exchangeStrategies(exchangeStrategies).build();

        return webClient
                .post()
                .uri("/reco/mf")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userItemReq)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserItemRes>>() {
                })
                .block()
                .stream().sorted((a, b) -> {
                    if (Integer.parseInt(a.getUserId()) >= Integer.parseInt(b.getUserId())) return 1;
                    return -1;
                }).collect(Collectors.toList());
    }
}
