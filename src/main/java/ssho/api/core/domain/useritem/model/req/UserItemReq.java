package ssho.api.core.domain.useritem.model.req;

import lombok.Data;
import ssho.api.core.domain.userswipe.model.UserSwipeScore;

import java.util.List;

@Data
public class UserItemReq {
    List<String> mallNoList;
    List<UserSwipeScore> userSwipeScoreList;
}
