package ssho.api.core.service.useritemcache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.item.model.Item;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class UserItemCacheTest {

    @Autowired
    UserItemCacheServiceImpl userItemCacheService;

    @Test
    void updateUserItemCache() throws IOException {
        userItemCacheService.updateUserItemCache();
    }

    @Test
    void recentItemList() {
        List<Item> itemList = userItemCacheService.recentItemList(5);
        int size = itemList.size();
    }
}
