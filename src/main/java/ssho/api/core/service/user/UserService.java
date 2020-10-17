package ssho.api.core.service.user;

import ssho.api.core.domain.user.model.User;

import java.util.List;

public interface UserService {

    List<User> userList();

    /**
     * 회원 이름으로 회원 조회
     * @param name
     * @return
     */
    int findUserIdByName(final String name);

    String saveUser(final User user);

    boolean checkEmailRegistered(final String email);

    /**
     * 회원의 튜토리얼 진행 여부 체크
     * @param userId
     * @return
     */
    boolean checkTutorial(final int userId);
}
