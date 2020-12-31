package ssho.api.core.api.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.item.model.Item;
import ssho.api.core.service.item.ItemServiceImpl;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService){
        this.itemService = itemService;
    }

    /**
     * 상품 전체 조회
     * @return
     */
    @GetMapping("")
    public List<Item> getItemList(){
        return itemService.getItems();
    }
}
