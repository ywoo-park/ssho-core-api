package ssho.api.core.repository.useritemcache;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ssho.api.core.domain.useritemcache.model.UserItemCache;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserItemCacheRepository extends CrudRepository<UserItemCache, String> {
}
