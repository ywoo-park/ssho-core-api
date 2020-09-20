package ssho.api.core.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.user.model.User;
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
     * @param name
     * @param httpServletResponse
     * @return
     */
    @GetMapping("/signin")
    public String signin(@RequestParam("name") String name, HttpServletResponse httpServletResponse) {
        String userId = userService.findUserIdByName(name);
        httpServletResponse.addHeader("User-Type", userService.checkTutorial(userId) == true ? "pass" : "initial");
        return userId;
    }
}
