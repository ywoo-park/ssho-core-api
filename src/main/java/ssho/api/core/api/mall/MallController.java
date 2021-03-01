package ssho.api.core.api.mall;

import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.mall.model.Mall;
import ssho.api.core.service.mall.MallServiceImpl;

import java.io.IOException;
import java.util.List;

@RequestMapping("/mall")
@RestController
public class MallController {

    private MallServiceImpl mallService;

    public MallController(MallServiceImpl mallService) {
        this.mallService = mallService;
    }

    @GetMapping("/list")
    public List<Mall> getMallList() {
        return mallService.getMallList();
    }

    @GetMapping("")
    public Mall getMallById(@RequestParam String mallNo) throws IOException {
        return mallService.getMallById(mallNo);
    }

    @PostMapping("/list")
    public void saveMallList(@RequestBody List<Mall> mallList) throws IOException {
        mallService.saveAll(mallList);
    }

    @PostMapping("")
    public void saveMall(@RequestBody Mall mall) throws IOException {
        mallService.save(mall);
    }
}
