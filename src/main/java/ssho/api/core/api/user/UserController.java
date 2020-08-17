package ssho.api.core.api.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssho.api.core.domain.user.model.User;
import ssho.api.core.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {

    private UserRepository userRepository;

    public UserController(final UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public List<User> getUsers(){
        if(userRepository.findAll() == null) {
            return new ArrayList<>();
        }
        return userRepository.findAll();
    }
}
