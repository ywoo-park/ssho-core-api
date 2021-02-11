package ssho.api.core.service.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemServiceTest {

    @Autowired
    ItemServiceImpl itemService;

    @Test
    void addImageVec(){
        itemService.addImageVec();
    }
}
