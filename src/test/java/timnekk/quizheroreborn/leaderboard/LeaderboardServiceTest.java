package timnekk.quizheroreborn.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import timnekk.quizheroreborn.leaderboard.models.LeaderboardRequest;
import timnekk.quizheroreborn.leaderboard.models.LeaderboardResponse;
import timnekk.quizheroreborn.user.UserRepository;
import timnekk.quizheroreborn.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private LeaderboardRequest request;
    private User testUser;

    @BeforeEach
    public void setUp() {
        request = new LeaderboardRequest();
        request.setPage(1);
        request.setPageSize(5);

        testUser = new User();
        testUser.setUsername("user1");
        testUser.setPoints(100);
    }

    @Test
    void getLeaderboard_ReturnsLeaderboard_WhenUsersExist() {
        // Arrange
        List<User> users = Collections.singletonList(testUser);
        Page<User> userPage = new PageImpl<>(users);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        LeaderboardResponse result = leaderboardService.getLeaderboard(request);

        // Assert
        assertEquals(1, result.getLeaderboard().getTotalElements());
        assertEquals(testUser.getUsername(), result.getLeaderboard().getContent().get(0).getUsername());
        assertEquals(testUser.getPoints(), result.getLeaderboard().getContent().get(0).getPoints());
    }

    @Test
    void getLeaderboard_ReturnsEmptyLeaderboard_WhenNoUsersExist() {
        // Arrange
        Page<User> userPage = Page.empty();
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        LeaderboardResponse result = leaderboardService.getLeaderboard(request);

        // Assert
        assertEquals(0, result.getLeaderboard().getTotalElements());
    }
}
