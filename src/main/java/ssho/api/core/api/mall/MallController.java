package ssho.api.core.api.mall;

import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.service.mall.MallServiceImpl;

@RestController
public class MallController {

    private MallServiceImpl mallService;

    public MallController(MallServiceImpl mallService) {
        this.mallService = mallService;
    }


}
