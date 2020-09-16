package ssho.api.core.service.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.tag.model.ExpTag;
import ssho.api.core.domain.tag.model.RealTag;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final RestHighLevelClient restHighLevelClient;
    private final WebClient webClient;

    public TagServiceImpl(final RestHighLevelClient restHighLevelClient,
                      final WebClient.Builder webClientBuilder) {
        this.restHighLevelClient = restHighLevelClient;
        this.webClient = webClientBuilder.baseUrl("http://13.124.59.2:8082").build();
    }

    @Override
    public void saveRealTag(List<RealTag> tagList, String index) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        for (RealTag tag : tagList) {
            ObjectMapper mapper = new ObjectMapper();

            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

            IndexRequest indexRequest = new IndexRequest(index).source(mapper.writeValueAsString(tag), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    @Override
    public void saveExpTag(List<ExpTag> tagList, String index) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        for (ExpTag tag : tagList) {

            ObjectMapper mapper = new ObjectMapper();

            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

            IndexRequest indexRequest = new IndexRequest(index).source(mapper.writeValueAsString(tag), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    @Override
    public List<RealTag> findAllRealTags(final String index) {

        //인자로 주어진 index 에서 검
        SearchRequest searchRequest = new SearchRequest(index);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // search query 최대 크기 set
        sourceBuilder.size(1000);

        searchRequest.source(sourceBuilder);

        // ES로 부터 데이터 받기
        SearchResponse searchResponse = null;

        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchHit[] searchHits = searchResponse.getHits().getHits();

        List<RealTag> results = Arrays.stream(searchHits).map(hit -> {
            try {
                return new ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .readValue(hit.getSourceAsString(), RealTag.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        return results;
    }

    @Override
    public RealTag findRealTagByRealTagName(final String tagName) {

        SearchRequest searchRequest = new SearchRequest("real-tag");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name", tagName));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;

        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchHit[] searchHits = searchResponse.getHits().getHits();

        List<RealTag> results = Arrays.stream(searchHits).map(hit -> {
            try {
                return new ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .readValue(hit.getSourceAsString(), RealTag.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        return results.get(0);
    }

    @Override
    public ExpTag findExpTagByRealTagName(final String tagId) {

        SearchRequest searchRequest = new SearchRequest("exp-tag");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("id", tagId));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;

        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchHit[] searchHits = searchResponse.getHits().getHits();

        List<ExpTag> results = Arrays.stream(searchHits).map(hit -> {
            try {
                return new ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .readValue(hit.getSourceAsString(), ExpTag.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        return results.get(0);
    }

    @Override
    public void deleteAllRealTags(final String index) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(QueryBuilders.matchAllQuery());
        try {
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllExpTags(final String index) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(QueryBuilders.matchAllQuery());
        try {
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ExpTag> getExpTagListOrderedByTagCountByUserId(final String userId) {
        List<ExpTag> expTagList =
                webClient
                        .get().uri("/log/swipe/user/tag?userId=" + userId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<ExpTag>>() {
                        })
                        .block();

        return expTagList;
    }

    @Override
    public List<ExpTag> getExpTagListOrderedBySearchScoreByKeyword(final String keyword) {

        SearchRequest searchRequest = new SearchRequest("exp-tag");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("name", keyword));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;

        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchHit[] searchHits = searchResponse.getHits().getHits();

        List<ExpTag> searchedExpTagList = Arrays.stream(searchHits).map(hit -> {
            try {
                return new ObjectMapper()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                        .readValue(hit.getSourceAsString(), ExpTag.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        return searchedExpTagList;
    }
}

