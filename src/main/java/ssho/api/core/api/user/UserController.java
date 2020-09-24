package ssho.api.core.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.user.model.req.SignInReq;
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
     *
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
     *
     * @param signInReq
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/signin")
    public void signin(@RequestBody SignInReq signInReq, HttpServletResponse httpServletResponse) {

        String token = userService.authUser(signInReq);

        int userId = userRepository.findByEmail(signInReq.getEmail()).getId();

        httpServletResponse.addHeader("User-Type", userService.checkTutorial(userId) == true ? "pass" : "initial");
        httpServletResponse.addHeader("Token", token);
    }

    /**
     * 회원 등록
     *
     * @param user
     */
    @PostMapping("/signup")
    public void signup(@RequestBody User user) {
        userService.saveUser(user);
    }
}
