package ssho.api.core.api.carddeck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.carddeck.CardDeck;
import ssho.api.core.service.carddeck.CardDeckServiceImpl;
import ssho.api.core.util.auth.Auth;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequestMapping("/card-deck")
@RestController
public class CardDeckController {

    private final CardDeckServiceImpl cardDeckService;

    public CardDeckController(CardDeckServiceImpl cardDeckService) {
        this.cardDeckService = cardDeckService;
    }

    /**
     * 회원 카드덱 조회
     * @param httpServletRequest
     * @return
     */
    @Auth
    @GetMapping("")
    public CardDeck getCardDeck(final HttpServletRequest httpServletRequest) throws IOException {
        final String userId = String.valueOf(httpServletRequest.getAttribute("userId"));
        return cardDeckService.cardDeckByUserId(Integer.parseInt(userId));
    }
}
