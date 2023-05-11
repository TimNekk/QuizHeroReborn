package timnekk.quizheroreborn;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User getUser() {
        User user = new User();
        user.setUsername("timnekk");
        user.setPassword("password");
        return user;
    }
}