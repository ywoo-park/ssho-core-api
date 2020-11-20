package ssho.api.core.service.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import ssho.api.core.domain.tag.model.Tag;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    private final String TAG_INDEX = "tag";

    public TagServiceImpl(final RestHighLevelClient restHighLevelClient,
                          final ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(List<String> tagNameList) throws IOException {

        for (String name : tagNameList) {
            Tag tag = new Tag();

            String id = UUID.randomUUID().toString().replace("-", "");
            tag.setId(id);
            tag.setName(name);

            IndexRequest indexRequest = new IndexRequest(TAG_INDEX).source(objectMapper.writeValueAsString(tag), XContentType.JSON);
            indexRequest.id(id);

            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        }
    }

    @Override
    public List<Tag> allList() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(TAG_INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(1000);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            return Stream.of(searchResponse.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(src -> {
                        try {
                            return objectMapper.readValue(src, Tag.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }
}

