package ssho.api.core.service.user;

import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.user.model.req.SignInReq;
import ssho.api.core.domain.user.model.res.SignInRes;

import java.util.List;

public interface UserService {

    String saveUser(final User user);

    SignInRes authUser(final SignInReq signInReq);

    boolean checkEmailRegistered(final String email);

    List<User> userList();
}
