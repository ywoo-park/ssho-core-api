package ssho.api.core.dto;

import lombok.Data;
import ssho.api.core.domain.log.SwipeLog;
import java.util.List;

@Data
public class UserSwipeLogRes {
    private String userId;
    private List<SwipeLog> swipeLogList;
}
