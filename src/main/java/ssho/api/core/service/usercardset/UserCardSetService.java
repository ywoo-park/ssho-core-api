package ssho.api.core.service.usercardset;

import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.usercardset.UserCardSet;

public interface UserCardSetService {
    UserCardSet save(UserCardSet userCardSet);
    UserCardSet getRecentByUserId(Integer userId);
    UserCardSet getById(Integer id);
}
