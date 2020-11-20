package ssho.api.core.api.tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.tag.model.Tag;
import ssho.api.core.service.tag.TagServiceImpl;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagServiceImpl tagService;

    public TagController(final TagServiceImpl tagService) {
        this.tagService = tagService;
    }

    /**
     * 태그 저장
     * @param tagNameList
     * @throws IOException
     */
    @PostMapping("")
    public void save(@RequestBody List<String> tagNameList) throws IOException {
        tagService.save(tagNameList);
    }

    /**
     * 태그 전체 조회
     * @return
     */
    @GetMapping("")
    public List<Tag> getAllTagList() {
        return tagService.allList();
    }
}

