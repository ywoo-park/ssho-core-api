package ssho.api.core.domain.usercardset;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "user_card_set")
@Data
public class UserCardSet {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int userId;
    private String tagId;
    private String title;

    private String selectedCat;
    private String startPrice;
    private String endPrice;

    private String createTime;
}
