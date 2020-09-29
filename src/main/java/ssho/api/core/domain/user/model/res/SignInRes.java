package ssho.api.core.domain.user.model.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInRes {
    private String token;
    private String userType;
    private String name;
}
