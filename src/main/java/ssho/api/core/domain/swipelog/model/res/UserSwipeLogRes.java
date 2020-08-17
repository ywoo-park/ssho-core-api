package ssho.api.core.domain.swipelog.model.res;

import lombok.Data;
import ssho.api.core.domain.swipelog.model.SwipeLog;
import java.util.List;

@Data
public class UserSwipeLogRes {
    private String userId;
    private List<SwipeLog> swipeLogList;
}
