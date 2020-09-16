package ssho.api.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssho.api.core.domain.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByName(String name);
}
