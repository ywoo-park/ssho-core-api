package ssho.api.core.service.usercardset;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssho.api.core.domain.usercardset.UserCardSet;

import java.time.LocalDateTime;

@SpringBootTest
public class UserCardSetServiceImplTest {

    @Autowired
    UserCardSetServiceImpl userCardSetService;

    @Test
    public void save() {
        UserCardSet userCardSet = new UserCardSet();
        userCardSet.setUserId(5);
        userCardSet.setTagId("12345678");
        userCardSet.setSelectedCat("00000000");
        userCardSet.setStartPrice("10000");
        userCardSet.setEndPrice("20000");
        userCardSetService.save(userCardSet);
    }
}
