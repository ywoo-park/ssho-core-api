package ssho.api.core.api.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.item.model.Item;
import ssho.api.core.service.item.ItemServiceImpl;
import ssho.api.core.service.mall.MallServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemServiceImpl itemService;
    private final MallServiceImpl mallService;

    public ItemController(ItemServiceImpl itemService, MallServiceImpl mallService){
        this.itemService = itemService;
        this.mallService = mallService;
    }

    /**
     * 상품 전체 조회
     * @return
     */
    @GetMapping("")
    public List<Item> getItemList(){
        return itemService.getItems();
    }

    @GetMapping("/{mallNo}")
    public List<Item> getMallItemList(@PathVariable String mallNo) throws IOException {
        return itemService.getItemsByMallNo(mallNo);
    }

    @GetMapping("imageVec/test")
    public List<Item> getItemListImageVecN(){
        return itemService.getItems().stream().filter(item -> item.getImageVec() == null).collect(Collectors.toList());
    }
}
