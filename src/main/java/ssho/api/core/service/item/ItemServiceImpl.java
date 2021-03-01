package ssho.api.core.service.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
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
import ssho.api.core.domain.tag.model.Tag;
import ssho.api.core.service.mall.MallServiceImpl;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService{

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    private final MallServiceImpl mallService;
    private WebClient webClient;

    @Value("${item.reco.api.host}")
    private String ITEM_RECO_API_HOST;

    private final int SIZE = 10000;

    private final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)).build();

    public ItemServiceImpl(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper, MallServiceImpl mallService) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
        this.mallService = mallService;
    }

    void addImageVec() {

        List<Mall> mallList = mallService.getMallList().stream().filter(mall ->
                mall.getLastSyncTime() != null).collect(Collectors.toList());

        this.webClient = WebClient.builder().baseUrl(ITEM_RECO_API_HOST).exchangeStrategies(exchangeStrategies).build();

        for(Mall mall : mallList) {

            String index = "item" + "-" + mall.getId() + "-" + "rt" + "-" + mall.getLastSyncTime();

            // ES에 요청 보내기
            SearchRequest searchRequest = new SearchRequest(index);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // search query 최대 크기 set
            sourceBuilder.size(SIZE);

            searchRequest.source(sourceBuilder);

            // ES로 부터 데이터 받기
            SearchResponse searchResponse;

            try {
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                continue;
            }

            SearchHit[] searchHits = searchResponse.getHits().getHits();

            List<Item> mallItemList = Arrays.stream(searchHits).map(hit -> {
                try {
                    return new ObjectMapper()
                            .readValue(hit.getSourceAsString(), Item.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());

            mallItemList.forEach(item -> {

                try {
                    if(item.getImageVec() == null) {
                        List<Double> imageVec =
                                webClient
                                        .post()
                                        .uri("/feature/image")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .bodyValue(item)
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<Item>() {
                                        })
                                        .block()
                                        .getImageVec();

                        item.setImageVec(imageVec);

                        UpdateRequest updateRequest = new UpdateRequest(index, item.getId());

                        Map<String, Object> parameters = singletonMap("imageVec", item.getImageVec());

                        Script inline = new Script(ScriptType.INLINE, "painless",
                                "ctx._source.imageVec = params.imageVec", parameters);
                        updateRequest.script(inline);

                        try {
                            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    log.info(e.toString());
                }
            });
        }
    }

    @Override
    public List<Item> getItems(){

        // syncTime이 null이 아닌 mallList 조회
        List<Mall> mallList = mallService.getMallList().stream().filter(mall ->
                mall.getLastSyncTime() != null).collect(Collectors.toList());

        List<Item> itemList = new ArrayList<>();

        for(Mall mall : mallList) {

            String index = "item" + "-" + mall.getId() + "-" + "rt" + "-" + mall.getLastSyncTime();

            // ES에 요청 보내기
            SearchRequest searchRequest = new SearchRequest(index);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // search query 최대 크기 set
            sourceBuilder.size(SIZE);

            searchRequest.source(sourceBuilder);

            // ES로 부터 데이터 받기
            SearchResponse searchResponse;

            try {
                searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                continue;
            }

            SearchHit[] searchHits = searchResponse.getHits().getHits();

            List<Item> mallItemList = Arrays.stream(searchHits).map(hit -> {
                try {
                    return new ObjectMapper()
                            .readValue(hit.getSourceAsString(), Item.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());

            itemList.addAll(mallItemList);
        }

        return itemList;
    }

    @Override
    public Item getItemById(String itemId) {

        List<Mall> mallList = mallService.getMallList().stream().filter(mall ->
                mall.getLastSyncTime() != null).collect(Collectors.toList());

        List<Item> itemList = mallList.stream().map(mall -> {
            String index = "item" + "-" + mall.getId() + "-" + "rt" + "-" + mall.getLastSyncTime();
            try {
                GetRequest getRequest = new GetRequest(index, itemId);
                GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
                return objectMapper.readValue(getResponse.getSourceAsString(), Item.class);
            } catch(Exception e) {
                return null;
            }
        }).collect(Collectors.toList());

        return itemList.stream().filter(Objects::nonNull).collect(Collectors.toList()).get(0);
    }

    @Override
    public Item getItemCumById(String itemId) {

        List<Mall> mallList = mallService.getMallList().stream().filter(mall ->
                mall.getLastSyncTime() != null).collect(Collectors.toList());

        List<Item> itemList = mallList.stream().map(mall -> {
            String index = "item" + "-" + mall.getId() + "-" + "cum";
            try {
                GetRequest getRequest = new GetRequest(index, itemId);
                GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
                return objectMapper.readValue(getResponse.getSourceAsString(), Item.class);
            } catch(Exception e) {
                return null;
            }
        }).collect(Collectors.toList());

        return itemList.stream().filter(Objects::nonNull).collect(Collectors.toList()).get(0);
    }

    @Override
    public List<Item> getItemsByMallNo(String mallNo) throws IOException {

        Mall mall = mallService.getMallById(mallNo);

        String index = "item" + "-" + mall.getId() + "-" + "rt" + "-" + mall.getLastSyncTime();

        // ES에 요청 보내기
        SearchRequest searchRequest = new SearchRequest(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(SIZE);
        searchRequest.source(searchSourceBuilder);

        // ES로 부터 데이터 받기
        SearchResponse searchResponse;

        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        SearchHit[] searchHits = searchResponse.getHits().getHits();

        List<Item> itemList = Arrays.stream(searchHits).map(hit -> {
            try {
                return new ObjectMapper()
                        .readValue(hit.getSourceAsString(), Item.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        return itemList;
    }

    @Override
    public List<Item> getItemsByTagId(String tagId) {

        List<Item> itemList = getItems();

        return itemList.stream().filter(item -> {

            List<Tag> tagList = item.getTagList();
            return tagList.stream().anyMatch(tag -> tag.getId().equals(tagId));

        }).collect(Collectors.toList());
    }

    @Override
    public Item getItemById(String itemId, String index) throws IOException {
        GetRequest getRequest = new GetRequest(index, itemId);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        if(getResponse.isExists()){
            return objectMapper.readValue(getResponse.getSourceAsString(), Item.class);
        } else {
            return null;
        }
    }
}
