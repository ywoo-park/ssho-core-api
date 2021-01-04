package ssho.api.core.service.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import ssho.api.core.domain.item.model.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService{

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    public ItemServiceImpl(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Item> getItems(){

        // ES에 요청 보내기
        SearchRequest searchRequest = new SearchRequest("item-rt");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // search query 최대 크기 set
        sourceBuilder.size(10000);

        searchRequest.source(sourceBuilder);

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
        Collections.sort(itemList);
        return itemList;
    }

    @Override
    public List<Item> getItemsByMallNo(String mallNo){

        // ES에 요청 보내기
        SearchRequest searchRequest = new SearchRequest("item-rt");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("mallNo", mallNo));
        searchSourceBuilder.size(10000);
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
        Collections.sort(itemList);
        return itemList;
    }

    @Override
    public Item getItemById(String itemId, String index) throws IOException {
        GetRequest getRequest = new GetRequest(index, itemId);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.readValue(getResponse.getSourceAsString(), Item.class);
    }
}
