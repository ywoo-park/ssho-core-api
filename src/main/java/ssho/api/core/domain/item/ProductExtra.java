package ssho.api.core.domain.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 상품 상세 정보 모델
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductExtra {
    private List<String> extraImageUrlList;     // 상세 이미지 리스트
    private String description;                 // 상품 설명
    private List<String> sizeList;              // 상품 사이즈 리스트
}
