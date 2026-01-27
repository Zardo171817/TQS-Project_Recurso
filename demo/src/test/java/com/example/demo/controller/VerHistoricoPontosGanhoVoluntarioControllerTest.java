package com.example.demo.controller;

import com.example.demo.dto.PointsHistoryResponse;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerHistoricoPontosGanhoVoluntario Controller Tests")
class VerHistoricoPontosGanhoVoluntarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VolunteerService volunteerService;

    @InjectMocks
    private VolunteerController volunteerController;

    private PointsHistoryResponse historyResponse1;
    private PointsHistoryResponse historyResponse2;
    private PointsHistoryResponse historyResponse3;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(volunteerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Setup test data
        historyResponse1 = new PointsHistoryResponse(
                1L, 10L, "Community Service", "Community", 50,
                LocalDateTime.of(2024, 1, 15, 10, 0), "Help at community center"
        );

        historyResponse2 = new PointsHistoryResponse(
                2L, 20L, "Education Support", "Education", 75,
                LocalDateTime.of(2024, 1, 20, 14, 30), "Tutoring students"
        );

        historyResponse3 = new PointsHistoryResponse(
                3L, 30L, "Environmental Work", "Environment", 25,
                LocalDateTime.of(2024, 1, 25, 9, 0), "Tree planting"
        );
    }

    @Nested
    @DisplayName("GET /api/volunteers/{id}/points-history Tests")
    class GetPointsHistoryTests {

        @Test
        @DisplayName("Should return 200 OK with points history list")
        void shouldReturnPointsHistoryList() throws Exception {
            List<PointsHistoryResponse> history = Arrays.asList(historyResponse1, historyResponse2, historyResponse3);
            when(volunteerService.getPointsHistory(1L)).thenReturn(history);

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].applicationId").value(1))
                    .andExpect(jsonPath("$[0].opportunityTitle").value("Community Service"))
                    .andExpect(jsonPath("$[0].pointsAwarded").value(50));

            verify(volunteerService).getPointsHistory(1L);
        }

        @Test
        @DisplayName("Should return 200 OK with empty list when no history")
        void shouldReturnEmptyListWhenNoHistory() throws Exception {
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(volunteerService).getPointsHistory(1L);
        }

        @Test
        @DisplayName("Should return 404 Not Found when volunteer does not exist")
        void shouldReturnNotFoundWhenVolunteerNotExists() throws Exception {
            when(volunteerService.getPointsHistory(999L))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(get("/api/volunteers/999/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Volunteer not found with id: 999"));

            verify(volunteerService).getPointsHistory(999L);
        }

        @Test
        @DisplayName("Should return correct JSON structure for single history item")
        void shouldReturnCorrectJsonStructure() throws Exception {
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.singletonList(historyResponse1));

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].applicationId").exists())
                    .andExpect(jsonPath("$[0].opportunityId").exists())
                    .andExpect(jsonPath("$[0].opportunityTitle").exists())
                    .andExpect(jsonPath("$[0].opportunityCategory").exists())
                    .andExpect(jsonPath("$[0].pointsAwarded").exists())
                    .andExpect(jsonPath("$[0].confirmedAt").exists())
                    .andExpect(jsonPath("$[0].opportunityDescription").exists());
        }

        @Test
        @DisplayName("Should handle multiple history entries correctly")
        void shouldHandleMultipleHistoryEntries() throws Exception {
            List<PointsHistoryResponse> history = Arrays.asList(historyResponse1, historyResponse2, historyResponse3);
            when(volunteerService.getPointsHistory(1L)).thenReturn(history);

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].opportunityCategory").value("Community"))
                    .andExpect(jsonPath("$[1].opportunityCategory").value("Education"))
                    .andExpect(jsonPath("$[2].opportunityCategory").value("Environment"));
        }

        @Test
        @DisplayName("Should call service with correct volunteer ID")
        void shouldCallServiceWithCorrectId() throws Exception {
            when(volunteerService.getPointsHistory(42L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/42/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(volunteerService, times(1)).getPointsHistory(42L);
        }
    }

    @Nested
    @DisplayName("Response Content Validation Tests")
    class ResponseContentTests {

        @Test
        @DisplayName("Should return correct points values")
        void shouldReturnCorrectPointsValues() throws Exception {
            List<PointsHistoryResponse> history = Arrays.asList(historyResponse1, historyResponse2);
            when(volunteerService.getPointsHistory(1L)).thenReturn(history);

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].pointsAwarded").value(50))
                    .andExpect(jsonPath("$[1].pointsAwarded").value(75));
        }

        @Test
        @DisplayName("Should return correct opportunity details")
        void shouldReturnCorrectOpportunityDetails() throws Exception {
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.singletonList(historyResponse1));

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].opportunityId").value(10))
                    .andExpect(jsonPath("$[0].opportunityTitle").value("Community Service"))
                    .andExpect(jsonPath("$[0].opportunityCategory").value("Community"))
                    .andExpect(jsonPath("$[0].opportunityDescription").value("Help at community center"));
        }

        @Test
        @DisplayName("Should return confirmedAt date in correct format")
        void shouldReturnConfirmedAtInCorrectFormat() throws Exception {
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.singletonList(historyResponse1));

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].confirmedAt").isNotEmpty());
        }

        @Test
        @DisplayName("Should handle null confirmedAt")
        void shouldHandleNullConfirmedAt() throws Exception {
            PointsHistoryResponse responseWithNullDate = new PointsHistoryResponse(
                    1L, 10L, "Test", "Category", 100, null, "Description"
            );
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.singletonList(responseWithNullDate));

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].confirmedAt").isEmpty());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large volunteer ID")
        void shouldHandleLargeVolunteerId() throws Exception {
            when(volunteerService.getPointsHistory(Long.MAX_VALUE)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/" + Long.MAX_VALUE + "/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should handle large number of history entries")
        void shouldHandleLargeNumberOfEntries() throws Exception {
            List<PointsHistoryResponse> largeHistory = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                largeHistory.add(new PointsHistoryResponse(
                        (long) i, (long) i, "Opportunity " + i, "Category", 10 + i, LocalDateTime.now(), "Desc"
                ));
            }
            when(volunteerService.getPointsHistory(1L)).thenReturn(largeHistory);

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(100)));
        }

        @Test
        @DisplayName("Should handle special characters in opportunity title")
        void shouldHandleSpecialCharactersInTitle() throws Exception {
            PointsHistoryResponse responseWithSpecialChars = new PointsHistoryResponse(
                    1L, 10L, "Test & <Special> \"Characters\"", "Category", 50, LocalDateTime.now(), "Desc"
            );
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.singletonList(responseWithSpecialChars));

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].opportunityTitle").value("Test & <Special> \"Characters\""));
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void shouldHandleUnicodeCharacters() throws Exception {
            PointsHistoryResponse responseWithUnicode = new PointsHistoryResponse(
                    1L, 10L, "Oportunidade de Voluntariado", "Educação", 50, LocalDateTime.now(), "Descrição"
            );
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.singletonList(responseWithUnicode));

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].opportunityTitle").value("Oportunidade de Voluntariado"))
                    .andExpect(jsonPath("$[0].opportunityCategory").value("Educação"));
        }

        @Test
        @DisplayName("Should handle zero points")
        void shouldHandleZeroPoints() throws Exception {
            PointsHistoryResponse responseWithZeroPoints = new PointsHistoryResponse(
                    1L, 10L, "Test", "Category", 0, LocalDateTime.now(), "Desc"
            );
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.singletonList(responseWithZeroPoints));

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].pointsAwarded").value(0));
        }

        @Test
        @DisplayName("Should handle maximum points value")
        void shouldHandleMaximumPoints() throws Exception {
            PointsHistoryResponse responseWithMaxPoints = new PointsHistoryResponse(
                    1L, 10L, "Test", "Category", Integer.MAX_VALUE, LocalDateTime.now(), "Desc"
            );
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.singletonList(responseWithMaxPoints));

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].pointsAwarded").value(Integer.MAX_VALUE));
        }
    }

    @Nested
    @DisplayName("Content Type Tests")
    class ContentTypeTests {

        @Test
        @DisplayName("Should return JSON content type")
        void shouldReturnJsonContentType() throws Exception {
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/1/points-history")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Should accept request without content type")
        void shouldAcceptRequestWithoutContentType() throws Exception {
            when(volunteerService.getPointsHistory(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/1/points-history"))
                    .andExpect(status().isOk());
        }
    }
}
