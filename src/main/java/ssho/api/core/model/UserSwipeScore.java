package ssho.api.core.model;

import lombok.Data;

@Data
public class UserSwipeScore {
    private String userId;
    private int[] scoreList;
}
