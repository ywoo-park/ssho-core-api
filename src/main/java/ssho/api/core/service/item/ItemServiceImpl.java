package ssho.api.core.service.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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

    public ItemServiceImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
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
}
