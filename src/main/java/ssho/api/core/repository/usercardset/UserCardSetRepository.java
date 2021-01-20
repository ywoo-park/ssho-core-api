package ssho.api.core.repository.usercardset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssho.api.core.domain.usercardset.UserCardSet;

@Repository
public interface UserCardSetRepository extends JpaRepository<UserCardSet, Integer> {
    UserCardSet findFirstByUserIdOrderByIdDesc(Integer userId);
}
