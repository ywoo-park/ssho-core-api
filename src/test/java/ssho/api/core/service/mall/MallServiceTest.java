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
        mall.setName("에콘");
        mall.setId("0009");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("DRESS", CategoryCode.DRESS));
        categoryList.add(new Category("OUTER", CategoryCode.OUTER));
        categoryList.add(new Category("TOP", CategoryCode.TOP));
        categoryList.add(new Category("SKIRT", CategoryCode.SKIRT));
        categoryList.add(new Category("PANTS", CategoryCode.BOTTOM));
        categoryList.add(new Category("SUIT", CategoryCode.TOP));
        categoryList.add(new Category("BAG", CategoryCode.EXTRA));
        categoryList.add(new Category("BELT", CategoryCode.EXTRA));
        categoryList.add(new Category("JEWELRY", CategoryCode.EXTRA));
        categoryList.add(new Category("MUFFLER", CategoryCode.EXTRA));
        categoryList.add(new Category("GLOVE", CategoryCode.EXTRA));


        mall.setCategoryList(categoryList);


        List<String> tagNameList = new ArrayList<>();
        tagNameList.add("포멀");

        List<Tag> tagList = tagNameList.stream().map(tagName -> tagService.getTagByName(tagName)).collect(Collectors.toList());

        mall.setTagList(tagList);

        mallList.add(mall);

        mallService.save(mallList);
    }
}

