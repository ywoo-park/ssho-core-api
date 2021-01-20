package ssho.api.core.service.mall;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.mall.model.Category;
import ssho.api.core.domain.mall.model.CategoryCode;
import ssho.api.core.domain.mall.model.Mall;
import ssho.api.core.domain.tag.model.Tag;
import ssho.api.core.service.tag.TagServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class MallServiceTest {
    @Autowired
    MallServiceImpl mallService;

    @Autowired
    TagServiceImpl tagService;

    @Test
    void save() throws IOException {
        List<Mall> mallList = new ArrayList<>();
        Mall mall = new Mall();
        mall.setName("모어댄라이크");
        mall.setId("0013");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("OUTER", CategoryCode.OUTER));
        categoryList.add(new Category("TOP", CategoryCode.TOP));
        categoryList.add(new Category("DRESS", CategoryCode.DRESS));
        categoryList.add(new Category("BOTTOM", CategoryCode.BOTTOM));

        mall.setCategoryList(categoryList);


        List<String> tagNameList = new ArrayList<>();
        tagNameList.add("유니섹스");

        List<Tag> tagList = tagNameList.stream().map(tagName -> tagService.getTagByName(tagName)).collect(Collectors.toList());

        mall.setTagList(tagList);

        mallList.add(mall);

        mallService.save(mallList);
    }
}

