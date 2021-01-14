package ssho.api.core.service.mall;

import ssho.api.core.domain.mall.model.Mall;

import java.io.IOException;
import java.util.List;

public interface MallService {
    void save(List<Mall> mallList) throws IOException;
    List<Mall> getMallList();
    Mall getMallById(String itemId) throws IOException;
    void updateMall(Mall mall) throws IOException;
}
