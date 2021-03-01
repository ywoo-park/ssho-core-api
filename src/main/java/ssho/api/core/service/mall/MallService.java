package ssho.api.core.service.mall;

import ssho.api.core.domain.mall.model.Mall;

import java.io.IOException;
import java.util.List;

public interface MallService {

    /**
     * 전체 몰 조회
     *
     * @return
     */
    List<Mall> getMallList();

    /**
     * 몰 조회
     *
     * @param itemId
     * @return
     * @throws IOException
     */
    Mall getMallById(String itemId) throws IOException;

    /**
     * 몰 전체 저장
     *
     * @param mallList
     * @throws IOException
     */
    void saveAll(List<Mall> mallList) throws IOException;

    /**
     * 몰 저장
     *
     * @param mall
     * @throws IOException
     */
    void save(Mall mall) throws IOException;
}
