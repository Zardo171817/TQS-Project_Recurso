package com.example.demo.unit.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.PointsHistoryResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.dto.VolunteerResponse;
import com.example.demo.entity.Application;
import com.example.demo.entity.ApplicationStatus;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VolunteerService Unit Tests")
class VolunteerServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer volunteer1;
    private Volunteer volunteer2;

    @BeforeEach
    void setUp() {
        volunteer1 = new Volunteer();
        volunteer1.setId(1L);
        volunteer1.setName("John Doe");
        volunteer1.setEmail("john@example.com");
        volunteer1.setPhone("123456789");
        volunteer1.setSkills("Java, Python");
        volunteer1.setInterests("Environment");
        volunteer1.setTotalPoints(100);

        volunteer2 = new Volunteer();
        volunteer2.setId(2L);
        volunteer2.setName("Jane Smith");
        volunteer2.setEmail("jane@example.com");
        volunteer2.setTotalPoints(200);
    }

    @Nested
    @DisplayName("Get Volunteer Tests")
    class GetVolunteerTests {

        @Test
        @DisplayName("Should get volunteer by ID")
        void shouldGetVolunteerById() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer1));

            VolunteerResponse response = volunteerService.getVolunteerById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("John Doe");
            assertThat(response.getEmail()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Should throw exception for non-existent volunteer ID")
        void shouldThrowExceptionForNonExistentId() {
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> volunteerService.getVolunteerById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Volunteer not found");
        }

        @Test
        @DisplayName("Should get volunteer by email")
        void shouldGetVolunteerByEmail() {
            when(volunteerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(volunteer1));

            VolunteerResponse response = volunteerService.getVolunteerByEmail("john@example.com");

            assertThat(response.getEmail()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Should throw exception for non-existent email")
        void shouldThrowExceptionForNonExistentEmail() {
            when(volunteerRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> volunteerService.getVolunteerByEmail("unknown@example.com"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Volunteer not found");
        }

        @Test
        @DisplayName("Should get all volunteers")
        void shouldGetAllVolunteers() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2));

            List<VolunteerResponse> responses = volunteerService.getAllVolunteers();

            assertThat(responses).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Points and Ranking Tests")
    class PointsAndRankingTests {

        @Test
        @DisplayName("Should get volunteer points")
        void shouldGetVolunteerPoints() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer1));

            VolunteerPointsResponse response = volunteerService.getVolunteerPoints(1L);

            assertThat(response.getTotalPoints()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should get volunteers ranking sorted by points descending")
        void shouldGetVolunteersRanking() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2));

            List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

            assertThat(ranking).hasSize(2);
            assertThat(ranking.get(0).getTotalPoints()).isEqualTo(200); // Jane has more points
            assertThat(ranking.get(1).getTotalPoints()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should get top N volunteers")
        void shouldGetTopVolunteers() {
            Volunteer volunteer3 = new Volunteer();
            volunteer3.setId(3L);
            volunteer3.setName("Bob");
            volunteer3.setEmail("bob@example.com");
            volunteer3.setTotalPoints(50);

            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2, volunteer3));

            List<VolunteerPointsResponse> top2 = volunteerService.getTopVolunteers(2);

            assertThat(top2).hasSize(2);
            assertThat(top2.get(0).getTotalPoints()).isGreaterThanOrEqualTo(top2.get(1).getTotalPoints());
        }
    }

    @Nested
    @DisplayName("Participation History Tests")
    class ParticipationHistoryTests {

        @Test
        @DisplayName("Should get confirmed participations")
        void shouldGetConfirmedParticipations() {
            Application confirmedApp = createApplication(true, 50);
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Collections.singletonList(confirmedApp));

            List<ApplicationResponse> participations = volunteerService.getConfirmedParticipations(1L);

            assertThat(participations).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception for non-existent volunteer when getting participations")
        void shouldThrowExceptionForNonExistentVolunteerParticipations() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> volunteerService.getConfirmedParticipations(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should get points history")
        void shouldGetPointsHistory() {
            Application app1 = createApplication(true, 50);
            app1.setConfirmedAt(LocalDateTime.now().minusDays(1));
            Application app2 = createApplication(true, 100);
            app2.setConfirmedAt(LocalDateTime.now());

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Arrays.asList(app1, app2));

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertThat(history).hasSize(2);
            // Most recent should be first
            assertThat(history.get(0).getPointsAwarded()).isEqualTo(100);
        }

        @Test
        @DisplayName("Should filter out zero points from history")
        void shouldFilterOutZeroPoints() {
            Application app1 = createApplication(true, 50);
            app1.setConfirmedAt(LocalDateTime.now());
            Application app2 = createApplication(true, 0);
            app2.setConfirmedAt(LocalDateTime.now());

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Arrays.asList(app1, app2));

            List<PointsHistoryResponse> history = volunteerService.getPointsHistory(1L);

            assertThat(history).hasSize(1);
            assertThat(history.get(0).getPointsAwarded()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("Existence Check Tests")
    class ExistenceCheckTests {

        @Test
        @DisplayName("Should check if volunteer exists by email")
        void shouldCheckIfVolunteerExistsByEmail() {
            when(volunteerRepository.existsByEmail("john@example.com")).thenReturn(true);
            when(volunteerRepository.existsByEmail("unknown@example.com")).thenReturn(false);

            assertThat(volunteerService.existsByEmail("john@example.com")).isTrue();
            assertThat(volunteerService.existsByEmail("unknown@example.com")).isFalse();
        }
    }

    private Application createApplication(boolean confirmed, int points) {
        Application app = new Application();
        app.setId(1L);
        app.setVolunteer(volunteer1);
        app.setOpportunity(createOpportunity());
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setParticipationConfirmed(confirmed);
        app.setPointsAwarded(points);
        app.setAppliedAt(LocalDateTime.now());
        return app;
    }

    private Opportunity createOpportunity() {
        Opportunity opp = new Opportunity();
        opp.setId(1L);
        opp.setTitle("Test Opportunity");
        opp.setDescription("Test Description");
        opp.setPoints(50);
        return opp;
    }
}
