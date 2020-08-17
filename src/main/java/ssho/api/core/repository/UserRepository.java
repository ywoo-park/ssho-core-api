package ssho.api.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ssho.api.core.domain.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    User findByName(String name);
}
