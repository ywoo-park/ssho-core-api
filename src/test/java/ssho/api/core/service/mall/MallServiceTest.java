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
        mall.setName("홀리넘버7");
        mall.setId("0021");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Outer", CategoryCode.OUTER));
        categoryList.add(new Category("Top", CategoryCode.TOP));
        categoryList.add(new Category("Dress", CategoryCode.DRESS));
        categoryList.add(new Category("Bottom", CategoryCode.BOTTOM));
        categoryList.add(new Category("Hat", CategoryCode.HAT));
        categoryList.add(new Category("ACC", CategoryCode.EXTRA));

        mall.setCategoryList(categoryList);


        List<String> tagNameList = new ArrayList<>();
        tagNameList.add("펑크룩");

        List<Tag> tagList = tagNameList.stream().map(tagName -> tagService.getTagByName(tagName)).collect(Collectors.toList());

        mall.setTagList(tagList);

        mallList.add(mall);

        //mallService.save(mallList);
    }

    @Test
    void update() throws IOException {

        Mall mall = mallService.getMallById("0013");

        List<String> tagNameList = new ArrayList<>();
        tagNameList.add("페미닌룩");

        List<Tag> tagList = tagNameList.stream().map(tagName -> tagService.getTagByName(tagName)).collect(Collectors.toList());
        mall.setTagList(tagList);

        //mallService.updateMall(mall);
    }
}

