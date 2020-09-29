package ssho.api.core.util.auth;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ssho.api.core.service.user.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthAspect {

    private final static String AUTHORIZATION = "Authorization";
    private final HttpServletRequest httpServletRequest;

    private final UserServiceImpl userService;

    public AuthAspect(final HttpServletRequest httpServletRequest, final UserServiceImpl userService) {
        this.httpServletRequest = httpServletRequest;
        this.userService = userService;
    }

    //항상 @annotation 패키지 이름을 실제 사용할 annotation 경로로 맞춰줘야 한다.
    @Around("@annotation(ssho.api.core.util.auth.Auth)")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        final String jwt = httpServletRequest.getHeader(AUTHORIZATION);
        int userId;
        if(jwt.equals("")) userId = -1;
        //if (jwt == null) return UNAUTHORIZED_RES;
        userId = userService.authorization(jwt);
        //if (userIdx == -1) return UNAUTHORIZED_RES;

        httpServletRequest.setAttribute("userId", userId);

        return pjp.proceed(pjp.getArgs());
    }
}