package ssho.api.core.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.user.model.req.SignInReq;
import ssho.api.core.domain.user.model.res.SignInRes;
import ssho.api.core.repository.user.UserRepository;
import ssho.api.core.service.user.UserServiceImpl;

import java.util.List;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    public UserController(final UserRepository userRepository, final UserServiceImpl userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * 회원 전체 조회
     * @return
     */
    @GetMapping("")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * 로그인
     * @param signInReq
     * @return
     */
    @PostMapping("/signin")
    public SignInRes signin(@RequestBody SignInReq signInReq) {

        String token = userService.authUser(signInReq);

        User user = userRepository.findByEmail(signInReq.getEmail());

        final int userId = user.getId();
        final String name = user.getName();
        final boolean admin = user.isAdmin();

        String userType = userService.checkTutorial(userId) ? "pass" : "initial";

        return SignInRes.builder().token(token).userType(userType).name(name).admin(admin).build();
    }

    /**
     * 회원 등록
     * @param user
     */
    @PostMapping("/signup")
    public void signup(@RequestBody User user) {
        userService.saveUser(user);
    }

    /**
     * 이메일 중복 체크
     * @param email
     * @return
     */
    @GetMapping("/check")
    public boolean checkEmailRegistered(@RequestParam("email") String email){
        return userService.checkEmailRegistered(email);
    }
}
