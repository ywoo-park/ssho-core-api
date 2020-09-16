package ssho.api.core.service.user;

public interface UserService {
    String findUserIdByName(final String name);

    boolean checkTutorial(final String userId);
}
