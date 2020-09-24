package ssho.api.core.domain.user.model.req;

import lombok.Data;

@Data
public class SignInReq {
    private String email;
    private String password;
}
