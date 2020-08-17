package ssho.api.core.model;

import lombok.Data;
import ssho.api.core.domain.log.SwipeLog;

import java.util.List;

@Data
public class UserSwipe {
    private String userId;
    private List<SwipeLog> swipeLogList;
}
