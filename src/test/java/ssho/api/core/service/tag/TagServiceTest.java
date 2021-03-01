package ssho.api.core.service.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.tag.model.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TagServiceTest {

    @Autowired
    TagServiceImpl tagService;

    @Test
    void save() throws IOException {
        /*
        String tagName = "히피룩";
        List<String> list = new ArrayList<>();
        list.add(tagName);
        tagService.save(list);

         */

        List<Tag> list = new ArrayList<>();
        Tag tag = tagService.getTagByName("빈티지");
        tag.setName("빈티지룩");
        list.add(tag);
        tagService.save(list);
    }
}
