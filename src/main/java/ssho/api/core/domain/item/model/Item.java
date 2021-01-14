package ssho.api.core.domain.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ssho.api.core.domain.mall.model.Category;
import ssho.api.core.domain.tag.model.Tag;

import java.util.List;

/**
 * Item(상품) 도메인
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item implements Comparable<Item> {
    private String id;                  // 상품 고유 번호
    private List<Category> category;    // 상품 카테고리
    private String mallNo;              // 쇼핑몰 고유 번호
    private String mallNm;              // 쇼핑몰 이름
    private String title;               // 상품 이름
    private String price;               // 상품 판매가
    private String discPrice;           // 상품 할인가
    private String imageUrl;            // 상품 대표 사진 URL
    private String link;                // 상품 상세 페이지 URL
    private List<Tag> tagList;          // 태그 리스트
    private ProductExtra productExtra;  // 상품 상세 정보
    private String syncTime;            // 상품 크롤링 싱크 시간

    @Override
    public int compareTo(Item item) {

        String a = this.id;
        String b = item.id;

        String mallNoA = a.substring(0,4);
        String mallNoB = b.substring(0,4);

        String uniqueIdA = a.substring(4);
        String uniqueIdB = b.substring(4);

        if(Integer.parseInt(mallNoA) > Integer.parseInt(mallNoB)) {
            return 1;
        }
        else if(Integer.parseInt(mallNoA) < Integer.parseInt(mallNoB)){
            return -1;
        }
        else{
            if(Integer.parseInt(uniqueIdA) > Integer.parseInt(uniqueIdB)){
                return 1;
            }
            else {
                return -1;
            }
        }
    }
}

