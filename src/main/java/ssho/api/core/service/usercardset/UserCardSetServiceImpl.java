package ssho.api.core.service.usercardset;

import org.springframework.stereotype.Service;
import ssho.api.core.domain.usercardset.UserCardSet;
import ssho.api.core.repository.usercardset.UserCardSetRepository;

@Service
public class UserCardSetServiceImpl implements UserCardSetService {

    private UserCardSetRepository userCardSetRepository;

    public UserCardSetServiceImpl(UserCardSetRepository userCardSetRepository) {
        this.userCardSetRepository = userCardSetRepository;
    }

    @Override
    public UserCardSet save(UserCardSet userCardSet) {
        return userCardSetRepository.saveAndFlush(userCardSet);
    }

    @Override
    public UserCardSet getRecentByUserId(Integer userId) {
        return userCardSetRepository.findFirstByUserIdOrderByIdDesc(userId);
    }

    @Override
    public UserCardSet getById(Integer id) {
        return userCardSetRepository.findById(id).orElseGet(UserCardSet::new);
    }
}
