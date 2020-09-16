package ssho.api.core.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.repository.user.UserRepository;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private WebClient webClient;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String findUserIdByName(final String name) {
        return userRepository.findByName(name).getId();
    }

    @Override
    public boolean checkTutorial(final String userId) {
        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).build();

        return webClient
                .get()
                .uri("/log/tutorial?userId=" + userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .blockOptional().orElse(false);
    }
}
