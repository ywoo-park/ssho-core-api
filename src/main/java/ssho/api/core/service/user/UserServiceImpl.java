package ssho.api.core.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.user.model.req.SignInReq;
import ssho.api.core.domain.user.model.req.SocialSignInReq;
import ssho.api.core.domain.user.model.res.SignInRes;
import ssho.api.core.repository.user.UserRepository;
import ssho.api.core.service.user.jwt.JwtService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public UserServiceImpl(final UserRepository userRepository,
                           final PasswordEncoder passwordEncoder,
                           final JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public SignInRes authUser(final SignInReq signInReq) {

        SignInRes signInRes = new SignInRes();

        // 이메일 일치
        if (userRepository.findByEmail(signInReq.getEmail()) != null) {

            User user = userRepository.findByEmail(signInReq.getEmail());

            // 비밀번호 일치
            if (passwordEncoder.matches(signInReq.getPassword(), user.getPassword())) {

                // 토큰값 생성
                String token = new JwtService.TokenRes(jwtService.create(user.getId())).getToken();

                signInRes.setName(user.getName());
                signInRes.setToken(token);
                signInRes.setAdmin(user.isAdmin());

                return signInRes;
            }
        }
        return signInRes;
    }

    @Override
    public SignInRes authSocialUser(final SocialSignInReq signInReq) {

        SignInRes signInRes = new SignInRes();

        // 이메일 일치
        if (userRepository.findByEmail(signInReq.getEmail()) != null) {

            User user = userRepository.findByEmail(signInReq.getEmail());

            // 토큰값 생성
            String token = new JwtService.TokenRes(jwtService.create(user.getId())).getToken();

            signInRes.setName(user.getName());
            signInRes.setToken(token);
            signInRes.setAdmin(user.isAdmin());

            return signInRes;
        }
        return signInRes;
    }

    @Override
    public String saveUser(final User user) {

        if(!user.isSocial()){

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }

        int userId = userRepository.save(user).getId();

        return new JwtService.TokenRes(jwtService.create(userId)).getToken();
    }

    @Override
    public boolean checkEmailRegistered(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public List<User> userList() {
        return userRepository.findAll().stream().peek(user -> user.setPassword("")).collect(Collectors.toList());
    }

    public int authorization(final String jwt) {

        final int userIdx = jwtService.decode(jwt).getUser_idx();
        if (userIdx == -1) return -1;

        final Optional<User> user = userRepository.findById(userIdx);
        if (!user.isPresent()) return -1;

        return userIdx;
    }
}
