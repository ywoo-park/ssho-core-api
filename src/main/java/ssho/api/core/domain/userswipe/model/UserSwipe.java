package ssho.api.core.domain.userswipe.model;

import lombok.Data;
import ssho.api.core.domain.swipelog.model.SwipeLog;

import java.util.List;

@Data
public class UserSwipe {
    private String userId;
    private List<SwipeLog> swipeLogList;
}
