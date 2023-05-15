package timnekk.quizheroreborn.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import timnekk.quizheroreborn.auth.JwtService;
import timnekk.quizheroreborn.leaderboard.models.LeaderboardEntry;
import timnekk.quizheroreborn.leaderboard.models.LeaderboardRequest;
import timnekk.quizheroreborn.leaderboard.models.LeaderboardResponse;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LeaderboardController.class)
@WithMockUser
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private LeaderboardService leaderboardService;

    private LeaderboardRequest request;
    private LeaderboardResponse response;

    @BeforeEach
    public void setUp() {
        request = new LeaderboardRequest();
        request.setPage(1);
        request.setPageSize(5);

        LeaderboardEntry entry1 = new LeaderboardEntry("user1", 100);
        LeaderboardEntry entry2 = new LeaderboardEntry("user2", 90);
        LeaderboardEntry entry3 = new LeaderboardEntry("user3", 80);
        LeaderboardEntry entry4 = new LeaderboardEntry("user4", 70);
        LeaderboardEntry entry5 = new LeaderboardEntry("user5", 60);

        List<LeaderboardEntry> entries = Arrays.asList(entry1, entry2, entry3, entry4, entry5);
        Sort sort = Sort.by(Sort.Direction.DESC, "points");
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

        Page<LeaderboardEntry> leaderboardPage = new PageImpl<>(entries, pageable, entries.size());

        response = new LeaderboardResponse();
        response.setLeaderboard(leaderboardPage);
    }

    @Test
    void getLeaderboard_WhenPageSizeIsNegative_ReturnsBadRequest() throws Exception {
        request.setPageSize(-1);

        mockMvc.perform(get("/api/v1/leaderboard")
                        .param("page", String.valueOf(request.getPage()))
                        .param("pageSize", String.valueOf(request.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLeaderboard_WhenPageSizeIsGreaterThan100_ReturnsBadRequest() throws Exception {
        request.setPageSize(101);

        mockMvc.perform(get("/api/v1/leaderboard")
                        .param("page", String.valueOf(request.getPage()))
                        .param("pageSize", String.valueOf(request.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLeaderboard_WhenPageSizeIsZero_ReturnsBadRequest() throws Exception {
        request.setPage(0);

        mockMvc.perform(get("/api/v1/leaderboard")
                        .param("page", String.valueOf(request.getPage()))
                        .param("pageSize", String.valueOf(request.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLeaderboard_WhenLeaderboardIsEmpty_ReturnsOk() throws Exception {
        when(leaderboardService.getLeaderboard(any(LeaderboardRequest.class))).thenReturn(new LeaderboardResponse());

        mockMvc.perform(get("/api/v1/leaderboard")
                        .param("page", String.valueOf(request.getPage()))
                        .param("pageSize", String.valueOf(request.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leaderboard").isEmpty());
    }

    @Test
    void getLeaderboard_WhenRequestIsValid_Returns200AndCorrectResponseBody() throws Exception {
        when(leaderboardService.getLeaderboard(any(LeaderboardRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/leaderboard")
                        .param("page", String.valueOf(request.getPage()))
                        .param("pageSize", String.valueOf(request.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leaderboard.totalElements", is(5)))
                .andExpect(jsonPath("$.leaderboard.content", hasSize(5)))
                .andExpect(jsonPath("$.leaderboard.content[0].username", is("user1")))
                .andExpect(jsonPath("$.leaderboard.content[0].points", is(100)))
                .andExpect(jsonPath("$.leaderboard.content[1].username", is("user2")))
                .andExpect(jsonPath("$.leaderboard.content[1].points", is(90)))
                .andExpect(jsonPath("$.leaderboard.content[2].username", is("user3")))
                .andExpect(jsonPath("$.leaderboard.content[2].points", is(80)))
                .andExpect(jsonPath("$.leaderboard.content[3].username", is("user4")))
                .andExpect(jsonPath("$.leaderboard.content[3].points", is(70)))
                .andExpect(jsonPath("$.leaderboard.content[4].username", is("user5")))
                .andExpect(jsonPath("$.leaderboard.content[4].points", is(60)));
    }

}
