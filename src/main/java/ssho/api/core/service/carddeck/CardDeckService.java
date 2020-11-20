package ssho.api.core.service.carddeck;

import ssho.api.core.domain.carddeck.CardDeck;

import java.io.IOException;

public interface CardDeckService {
    CardDeck cardDeckByUserId(int userId) throws IOException;
}
