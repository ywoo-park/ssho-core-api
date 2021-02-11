package ssho.api.core.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.domain.user.model.req.SignInReq;
import ssho.api.core.domain.user.model.req.SocialSignInReq;
import ssho.api.core.domain.user.model.res.SignInRes;
import ssho.api.core.service.user.UserServiceImpl;

import java.util.List;

@Slf4j
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserServiceImpl userService;

    public UserController(final UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * 로그인
     * @param signInReq
     * @return
     */
    @PostMapping("/signin")
    public SignInRes signin(@RequestBody SignInReq signInReq) {
        return userService.authUser(signInReq);
    }

    /**
     * 로그인(소셜)
     * @param signInReq
     * @return
     */
    @PostMapping("/signin/social")
    public SignInRes socialSignin(@RequestBody SocialSignInReq signInReq) {
        return userService.authSocialUser(signInReq);
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

    /**
     * 회원 전체 조회
     * @return
     */
    @GetMapping("")
    public List<User> getUsers() {
        return userService.userList();
    }
}
