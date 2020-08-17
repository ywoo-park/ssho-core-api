package ssho.api.core.domain.userswipe.model;

import lombok.Data;

@Data
public class UserSwipeScore {
    private String userId;
    private int[] scoreList;
}
