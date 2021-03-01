package ssho.api.core.service.mall;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;
import ssho.api.core.domain.mall.model.Mall;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MallServiceImpl implements MallService {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private final String MALL_INDEX = "mall";
    private final Integer SEARCH_SIZE = 1000;

    public MallServiceImpl(final RestHighLevelClient restHighLevelClient,
                          final ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Mall> getMallList() {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(MALL_INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(SEARCH_SIZE);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            return Stream.of(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(src -> {
                        try {
                            return objectMapper.readValue(src, Mall.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Mall getMallById(String mallId) throws IOException {
        GetRequest getRequest = new GetRequest(MALL_INDEX, mallId);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return objectMapper.readValue(getResponse.getSourceAsString(), Mall.class);
    }

    @Override
    public void saveAll(List<Mall> mallList) throws IOException {

        for (Mall mall : mallList) {

            IndexRequest indexRequest = new IndexRequest(MALL_INDEX).source(objectMapper.writeValueAsString(mall), XContentType.JSON);
            indexRequest.id(mall.getId());

            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @Override
    public void save(Mall mall) throws IOException {
        saveAll(Collections.singletonList(mall));
    }
}
