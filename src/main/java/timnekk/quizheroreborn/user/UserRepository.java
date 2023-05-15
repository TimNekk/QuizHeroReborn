package timnekk.quizheroreborn.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import timnekk.quizheroreborn.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}