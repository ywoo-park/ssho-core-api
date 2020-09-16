package ssho.api.core.api.tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import ssho.api.core.domain.tag.model.ExpTag;
import ssho.api.core.domain.tag.model.RealTag;
import ssho.api.core.domain.tag.model.TagRes;
import ssho.api.core.service.tag.TagServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {

    private TagServiceImpl tagService;
    private WebClient webClient;

    @Value("${item.reco.api.host}")
    private String ITEM_RECO_API_HOST;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    public TagController(final TagServiceImpl tagService) {
        this.tagService = tagService;
        this.webClient = WebClient.builder().baseUrl(ITEM_RECO_API_HOST).build();
    }

    @PostMapping("/real")
    public void saveRealTag(@RequestBody List<RealTag> tagList) throws IOException {
        tagService.saveRealTag(tagList, "real-tag");
    }

    @PostMapping("/exp")
    public void saveExpTag(@RequestBody List<ExpTag> tagList) throws IOException {
        tagService.saveExpTag(tagList, "exp-tag");
    }

    @GetMapping("/real")
    public List<RealTag> findAllRealTags() {
        return tagService.findAllRealTags("real-tag");
    }

    @GetMapping("/exp/{name}")
    public TagRes findExpTagAndRealTagByRealTagName(@PathVariable("name") String tagName) {
        TagRes tagRes = new TagRes();
        tagRes.setRealTag(tagService.findRealTagByRealTagName(tagName));
        tagRes.setExpTag(tagService.findExpTagByRealTagName(tagRes.getRealTag().getExpTagId()));

        return tagRes;
    }

    @DeleteMapping("/real")
    public void deleteAllRealTag() {
        tagService.deleteAllRealTags("real-tag");
    }

    @DeleteMapping("/exp")
    public void deleteAllExpTag() {
        tagService.deleteAllExpTags("exp-tag");
    }

    @GetMapping("/reco")
    public List<ExpTag> getRecoExpTagList(@RequestParam("userId") String userId, HttpServletResponse response) {
        response.addHeader("User-Type", buildUserTypeBySwipeLogCount(userId));
        return tagService.getExpTagListOrderedByTagCountByUserId(userId);
    }

    @GetMapping("/search")
    public List<ExpTag> getSearchExpTagList(@RequestParam("keyword") String keyword, @RequestParam("userId") String userId, HttpServletResponse response) {
        response.addHeader("User-Type", buildUserTypeBySwipeLogCount(userId));
        return tagService.getExpTagListOrderedBySearchScoreByKeyword(keyword);
    }

    @PostMapping("/embedding/real")
    public List<RealTag> getEmbeddingSetRealTagList(@RequestBody List<RealTag> realTagList) {

        List<RealTag> embeddingSetRealTagList =
                new ArrayList<>(Objects.requireNonNull(webClient
                        .post()
                        .uri("/embedding/real")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .bodyValue(realTagList)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<RealTag>>() {
                        })
                        .block()));

        return embeddingSetRealTagList;
    }

    private String buildUserTypeBySwipeLogCount(String userId) {
        return swipeLogCount(userId) < 100 ? "initial" : "pass";
    }

    private int swipeLogCount(String userId) {

        webClient = WebClient.builder().baseUrl(LOG_API_HOST).build();

        return new ArrayList<>(Objects.requireNonNull(webClient
                .get()
                .uri("/log/swipe/user?userId=" + userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SwipeLog>>() {
                })
                .block())).size();
    }
}

