package ssho.api.core.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.user.model.req.SignInReq;
import ssho.api.core.domain.user.model.res.SignInRes;
import ssho.api.core.repository.user.UserRepository;
import ssho.api.core.service.user.UserServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {

    private UserRepository userRepository;
    private UserServiceImpl userService;

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
        if (userRepository.findAll() == null) {
            return new ArrayList<>();
        }
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

        String userType = userService.checkTutorial(userId) == true ? "pass" : "initial";

        SignInRes signInRes = SignInRes.builder().token(token).userType(userType).name(name).build();

        return signInRes;
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
