package ssho.api.core.service.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.user.model.req.SignInReq;
import ssho.api.core.repository.user.UserRepository;
import ssho.api.core.service.jwt.JwtService;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private WebClient webClient;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    @Value("${log.api.host}")
    private String LOG_API_HOST;

    public UserServiceImpl(final UserRepository userRepository,
                           final PasswordEncoder passwordEncoder,
                           final JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public int findUserIdByName(final String name) {
        return userRepository.findByName(name).getId();
    }

    /**
     * 회원 정보 인증
     *
     * @param signInReq 회원 데이터
     * @return DefaultRes
     */
    public String authUser(final SignInReq signInReq) {

        if (userRepository.findByEmail(signInReq.getEmail()) != null) {

            User user = userRepository.findByEmail(signInReq.getEmail());

            if (passwordEncoder.matches(signInReq.getPassword(), user.getPassword())) {
                return new JwtService.TokenRes(jwtService.create(user.getId())).getToken();
            }

            else{
                return "";
            }
        } else {
            return "";
        }
    }

    @Override
    public String saveUser(final User user) {

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        int userId = userRepository.save(user).getId();

        return new JwtService.TokenRes(jwtService.create(userId)).getToken();
    }

    @Override
    public boolean checkTutorial(final int userId) {

        this.webClient = WebClient.builder().baseUrl(LOG_API_HOST).build();

        return webClient
                .get()
                .uri("/log/tutorial?userId=" + userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .blockOptional().orElse(false);
    }

    public int authorization(final String jwt) {

        final int userIdx = jwtService.decode(jwt).getUser_idx();
        if (userIdx == -1) return -1;

        final Optional<User> user = userRepository.findById(userIdx);
        if (!user.isPresent()) return -1;

        return userIdx;

    }
}
