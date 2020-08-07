package ssho.api.core.domain;

import lombok.Builder;
import lombok.Data;
import ssho.api.core.model.ProductExtra;

import java.util.List;

/**
 * Item(상품) 도메인
 */
@Data
@Builder
public class Item {
    private String id;                  // 상품 고유 번호
    private String category;            // 상품 카테고리
    private String mallNo;              // 쇼핑몰 고유 번호
    private String mallNm;              // 쇼핑몰 이름
    private String title;               // 상품 이름
    private String price;               // 상품 판매가
    private String imageUrl;            // 상품 대표 사진 URL
    private String link;                // 상품 상세 페이지 URL
    private List<RealTag> realTagList;  // 실제 태그 리스트
    private List<ExpTag> expTagList;    // 노출 태그 리스트
    private ProductExtra productExtra;  // 상품 상세 정보
}

