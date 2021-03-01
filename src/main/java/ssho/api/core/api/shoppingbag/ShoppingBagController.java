package ssho.api.core.api.shoppingbag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.shoppingbag.ShoppingBagCardSet;
import ssho.api.core.service.shoppingbag.ShoppingBagServiceImpl;
import ssho.api.core.util.auth.Auth;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/shopping-bag")
@RestController
public class ShoppingBagController {

    private final ShoppingBagServiceImpl shoppingBagService;

    public ShoppingBagController(ShoppingBagServiceImpl shoppingBagService) {
        this.shoppingBagService = shoppingBagService;
    }

    /**
     * 회원별 쇼핑백 조회
     * @return List<Item>
     */
    @Auth
    @GetMapping("")
    public List<ShoppingBagCardSet> getShoppingBagByUserId(final HttpServletRequest httpServletRequest){
        final String userId = String.valueOf(httpServletRequest.getAttribute("userId"));
        return shoppingBagService.getShoppingBagCardSetListByUserId(userId);
    }
}
