package ssho.api.core.api.landing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.landing.res.LandingRes;
import ssho.api.core.service.landing.LandingServiceImpl;
import ssho.api.core.util.auth.Auth;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LandingController {

    private LandingServiceImpl landingService;

    public LandingController(LandingServiceImpl landingService) {
        this.landingService = landingService;
    }

    /**
     * 회원별 쇼핑백 조회
     * @return List<Item>
     */
    @Auth
    @GetMapping("")
    public LandingRes getLanding(final HttpServletRequest httpServletRequest){
        final String userId = String.valueOf(httpServletRequest.getAttribute("userId"));
        return landingService.getLandingByUserId(Integer.parseInt(userId));
    }
}
