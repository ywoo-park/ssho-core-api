package ssho.api.core.service.user;

public interface UserService {

    /**
     * 회원 이름으로 회원 조회
     * @param name
     * @return
     */
    String findUserIdByName(final String name);

    /**
     * 회원의 튜토리얼 진행 여부 체크
     * @param userId
     * @return
     */
    boolean checkTutorial(final String userId);
}
