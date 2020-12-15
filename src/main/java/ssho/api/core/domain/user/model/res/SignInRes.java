package ssho.api.core.domain.user.model.res;

import lombok.Builder;
import lombok.Data;

@Data
public class SignInRes {
    private String token;
    private String name;
    private boolean admin;
}
