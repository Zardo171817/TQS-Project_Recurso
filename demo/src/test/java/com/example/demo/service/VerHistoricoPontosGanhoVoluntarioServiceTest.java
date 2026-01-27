package com.example.demo.service;

import com.example.demo.dto.PointsHistoryResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerHistoricoPontosGanhoVoluntario Service Tests")
class VerHistoricoPontosGanhoVoluntarioServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer volunteer;
    private Promoter promoter;
    private Opportunity opportunity1;
    private Opportunity opportunity2;
    private Opportunity opportunity3;
    private Application application1;
    private Application application2;
    private Application application3;

    @BeforeEach
    void setUp() {
        // Setup Volunteer
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John Doe");
        volunteer.setEmail("john@example.com");
        volunteer.setPhone("123456789");
        volunteer.setSkills("Java, Spring");
        volunteer.setTotalPoints(150);

        // Setup Promoter
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Organization ABC");
        promoter.setEmail("org@example.com");
        promoter.setOrganization("ABC Org");

        // Setup Opportunities
        opportunity1 = createOpportunity(1L, "Community Service", "Community", 50);
        opportunity2 = createOpportunity(2L, "Education Support", "Education", 75);
        opportunity3 = createOpportunity(3L, "Environmental Work", "Environment", 25);

        // Setup Applications
        application1 = createApplication(1L, volunteer, opportunity1, 50, LocalDateTime.now().minusDays(10));
        application2 = createApplication(2L, volunteer, opportunity2, 75, LocalDateTime.now().minusDays(5));
        application3 = createApplication(3L, volunteer, opportunity3, 25, LocalDateTime.now().minusDays(1));
    }

    private Opportunity createOpportunity(Long id, String title, String category, Integer points) {
        Opportunity opp = new Opportunity();
        opp.setId(id);
        opp.setTitle(title);
        opp.setDescription("Description for " + title);
        opp.setSkills("Skills");
        opp.setCategory(category);
        opp.setDuration(4);
        opp.setVacancies(5);
        opp.setPoints(points);
        opp.setStatus(OpportunityStatus.CONCLUDED);
        opp.setPromoter(promoter);
        opp.setCreatedAt(LocalDateTime.now().minusDays(30));
        opp.setConcludedAt(LocalDateTime.now().minusDays(1));
        return opp;
    }

    private Application createApplication(Long id, Volunteer vol, Opportunity opp, Integer pointsAwarded, LocalDateTime confirmedAt) {
        Application app = new Application();
        app.setId(id);
        app.setVolunteer(vol);
        app.setOpportunity(opp);
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setMotivation("I want to help");
        app.setAppliedAt(LocalDateTime.now().minusDays(20));
        app.setParticipationConfirmed(true);
        app.setPointsAwarded(pointsAwarded);
        app.setConfirmedAt(confirmedAt);
        return app;
    }

    @Nested
    @DisplayName("getPointsHistory Tests")
    class GetPointsHistoryTests {

        @Test
        @DisplayName("Should return points history for volunteer with confirmed participations")
        void shouldReturnPointsHistoryForVolunteer() {
            List<Application> applications = Arrays.asList(application1, application2, application3);
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(applications);

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertNotNull(history);
            assertEquals(3, history.size());
            verify(volunteerRepository).existsById(1L);
            verify(applicationRepository).findByVolunteerIdAndParticipationConfirmed(1L, true);
        }

        @Test
        @DisplayName("Should return empty list when volunteer has no confirmed participations")
        void shouldReturnEmptyListWhenNoParticipations() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Collections.emptyList());

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertNotNull(history);
            assertTrue(history.isEmpty());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when volunteer does not exist")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> volunteerService.getPointsHistory(999L)
            );

            assertTrue(exception.getMessage().contains("Volunteer not found"));
            verify(applicationRepository, never()).findByVolunteerIdAndParticipationConfirmed(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("Should filter out applications with zero points")
        void shouldFilterOutZeroPointsApplications() {
            Application zeroPointsApp = createApplication(4L, volunteer, opportunity1, 0, LocalDateTime.now());
            List<Application> applications = Arrays.asList(application1, zeroPointsApp);

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(applications);

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertEquals(1, history.size());
            assertEquals(50, history.get(0).getPointsAwarded());
        }

        @Test
        @DisplayName("Should filter out applications with null points")
        void shouldFilterOutNullPointsApplications() {
            Application nullPointsApp = createApplication(4L, volunteer, opportunity1, null, LocalDateTime.now());
            nullPointsApp.setPointsAwarded(null);
            List<Application> applications = Arrays.asList(application1, nullPointsApp);

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(applications);

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertEquals(1, history.size());
        }

        @Test
        @DisplayName("Should sort history by confirmedAt date in descending order")
        void shouldSortByConfirmedAtDescending() {
            List<Application> applications = Arrays.asList(application1, application2, application3);
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(applications);

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            // Most recent first (application3 is the most recent)
            assertEquals(application3.getPointsAwarded(), history.get(0).getPointsAwarded());
            assertEquals(application2.getPointsAwarded(), history.get(1).getPointsAwarded());
            assertEquals(application1.getPointsAwarded(), history.get(2).getPointsAwarded());
        }

        @Test
        @DisplayName("Should handle applications with null confirmedAt dates")
        void shouldHandleNullConfirmedAtDates() {
            application2.setConfirmedAt(null);
            List<Application> applications = Arrays.asList(application1, application2, application3);

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(applications);

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertNotNull(history);
            assertEquals(3, history.size());
            // Application with null confirmedAt should be at the end
            assertNull(history.get(2).getConfirmedAt());
        }

        @Test
        @DisplayName("Should return correct opportunity details in response")
        void shouldReturnCorrectOpportunityDetails() {
            List<Application> applications = Collections.singletonList(application1);
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(applications);

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertEquals(1, history.size());
            PointsHistoryResponse response = history.get(0);
            assertEquals(opportunity1.getId(), response.getOpportunityId());
            assertEquals(opportunity1.getTitle(), response.getOpportunityTitle());
            assertEquals(opportunity1.getCategory(), response.getOpportunityCategory());
            assertEquals(opportunity1.getDescription(), response.getOpportunityDescription());
        }

        @Test
        @DisplayName("Should correctly verify volunteer existence before fetching history")
        void shouldVerifyVolunteerExistence() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Collections.emptyList());

            volunteerService.getPointsHistory(1L);

            verify(volunteerRepository, times(1)).existsById(1L);
        }
    }

    @Nested
    @DisplayName("Integration with Repository Tests")
    class RepositoryIntegrationTests {

        @Test
        @DisplayName("Should call repository with correct volunteer ID")
        void shouldCallRepositoryWithCorrectId() {
            when(volunteerRepository.existsById(42L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(42L, true))
                    .thenReturn(Collections.emptyList());

            volunteerService.getPointsHistory(42L);

            verify(applicationRepository).findByVolunteerIdAndParticipationConfirmed(42L, true);
        }

        @Test
        @DisplayName("Should only fetch confirmed participations")
        void shouldOnlyFetchConfirmedParticipations() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Collections.emptyList());

            volunteerService.getPointsHistory(1L);

            verify(applicationRepository).findByVolunteerIdAndParticipationConfirmed(eq(1L), eq(true));
        }

        @Test
        @DisplayName("Should not call application repository if volunteer not found")
        void shouldNotCallApplicationRepositoryIfVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> volunteerService.getPointsHistory(999L));

            verify(applicationRepository, never()).findByVolunteerIdAndParticipationConfirmed(anyLong(), anyBoolean());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large number of applications")
        void shouldHandleLargeNumberOfApplications() {
            List<Application> manyApplications = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Application app = createApplication((long) i, volunteer, opportunity1, 10 + i,
                        LocalDateTime.now().minusDays(i));
                manyApplications.add(app);
            }

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(manyApplications);

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertEquals(100, history.size());
        }

        @Test
        @DisplayName("Should handle single application in history")
        void shouldHandleSingleApplication() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Collections.singletonList(application1));

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertEquals(1, history.size());
            assertEquals(application1.getPointsAwarded(), history.get(0).getPointsAwarded());
        }

        @Test
        @DisplayName("Should handle applications with same confirmedAt date")
        void shouldHandleSameConfirmedAtDates() {
            LocalDateTime sameTime = LocalDateTime.now();
            application1.setConfirmedAt(sameTime);
            application2.setConfirmedAt(sameTime);

            List<Application> applications = Arrays.asList(application1, application2);
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(applications);

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertEquals(2, history.size());
        }

        @Test
        @DisplayName("Should handle volunteer ID at boundary values")
        void shouldHandleBoundaryVolunteerIds() {
            when(volunteerRepository.existsById(Long.MAX_VALUE)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(Long.MAX_VALUE, true))
                    .thenReturn(Collections.emptyList());

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(Long.MAX_VALUE);

            assertNotNull(history);
            assertTrue(history.isEmpty());
        }

        @Test
        @DisplayName("Should handle applications with very high points")
        void shouldHandleHighPointValues() {
            application1.setPointsAwarded(Integer.MAX_VALUE);

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Collections.singletonList(application1));

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertEquals(Integer.MAX_VALUE, history.get(0).getPointsAwarded());
        }

        @Test
        @DisplayName("Should maintain data integrity in response mapping")
        void shouldMaintainDataIntegrityInMapping() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Collections.singletonList(application1));

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            PointsHistoryResponse response = history.get(0);
            assertEquals(application1.getId(), response.getApplicationId());
            assertEquals(application1.getOpportunity().getId(), response.getOpportunityId());
            assertEquals(application1.getOpportunity().getTitle(), response.getOpportunityTitle());
            assertEquals(application1.getOpportunity().getCategory(), response.getOpportunityCategory());
            assertEquals(application1.getPointsAwarded(), response.getPointsAwarded());
            assertEquals(application1.getConfirmedAt(), response.getConfirmedAt());
        }
    }

    @Nested
    @DisplayName("Filtering Logic Tests")
    class FilteringLogicTests {

        @Test
        @DisplayName("Should only include applications with positive points")
        void shouldOnlyIncludePositivePoints() {
            Application positiveApp = createApplication(1L, volunteer, opportunity1, 50, LocalDateTime.now());
            Application zeroApp = createApplication(2L, volunteer, opportunity2, 0, LocalDateTime.now());
            Application negativeApp = createApplication(3L, volunteer, opportunity3, -10, LocalDateTime.now());

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Arrays.asList(positiveApp, zeroApp, negativeApp));

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            // Only positive points should be included
            assertEquals(1, history.size());
            assertEquals(50, history.get(0).getPointsAwarded());
        }

        @Test
        @DisplayName("Should handle mixed null and valid points")
        void shouldHandleMixedNullAndValidPoints() {
            Application validApp = createApplication(1L, volunteer, opportunity1, 50, LocalDateTime.now());
            Application nullApp = createApplication(2L, volunteer, opportunity2, null, LocalDateTime.now());
            nullApp.setPointsAwarded(null);

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Arrays.asList(validApp, nullApp));

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertEquals(1, history.size());
            assertEquals(50, history.get(0).getPointsAwarded());
        }
    }
}
