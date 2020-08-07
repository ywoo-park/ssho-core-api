package ssho.api.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 상품 상세 정보 모델
 */
@Data
@Builder
public class ProductExtra {
    private List<String> extraImageUrlList;     // 상세 이미지 리스트
    private String description;                 // 상품 설명
    private List<String> sizeList;              // 상품 사이즈 리스트
}
