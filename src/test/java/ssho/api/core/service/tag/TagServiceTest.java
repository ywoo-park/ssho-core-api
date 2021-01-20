package ssho.api.core.service.tag;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TagServiceTest {

    @Autowired
    TagServiceImpl tagService;

    @Test
    void save() throws IOException {
        String tagName = "스포티룩";
        List<String> list = new ArrayList<>();
        list.add(tagName);
        tagService.save(list);
    }
}
