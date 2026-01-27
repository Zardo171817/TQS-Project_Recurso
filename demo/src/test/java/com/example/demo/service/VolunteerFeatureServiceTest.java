package com.example.demo.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Volunteer Feature Service Tests")
class VolunteerFeatureServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer volunteer;
    private Volunteer volunteer2;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John Doe");
        volunteer.setEmail("john@test.com");
        volunteer.setPhone("987654321");
        volunteer.setSkills("Cleaning");
        volunteer.setTotalPoints(200);

        volunteer2 = new Volunteer();
        volunteer2.setId(2L);
        volunteer2.setName("Jane Doe");
        volunteer2.setEmail("jane@test.com");
        volunteer2.setPhone("123456789");
        volunteer2.setSkills("Organizing");
        volunteer2.setTotalPoints(500);
    }

    @Test
    @DisplayName("Should return volunteer points successfully")
    void getVolunteerPoints_Success() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));

        VolunteerPointsResponse response = volunteerService.getVolunteerPoints(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
        assertThat(response.getTotalPoints()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should throw exception when volunteer not found for points")
    void getVolunteerPoints_NotFound_ThrowsException() {
        when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> volunteerService.getVolunteerPoints(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Volunteer not found");
    }

    @Test
    @DisplayName("Should return volunteers ranked by total points descending")
    void getVolunteersRanking_ReturnsSortedByPointsDesc() {
        when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer, volunteer2));

        List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

        assertThat(ranking).hasSize(2);
        assertThat(ranking.get(0).getTotalPoints()).isEqualTo(500);
        assertThat(ranking.get(1).getTotalPoints()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should return top volunteers limited by count")
    void getTopVolunteers_ReturnsLimitedResults() {
        Volunteer volunteer3 = new Volunteer();
        volunteer3.setId(3L);
        volunteer3.setName("Bob");
        volunteer3.setEmail("bob@test.com");
        volunteer3.setTotalPoints(100);

        when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer, volunteer2, volunteer3));

        List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(2);

        assertThat(top).hasSize(2);
        assertThat(top.get(0).getTotalPoints()).isEqualTo(500);
        assertThat(top.get(1).getTotalPoints()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should return confirmed participations for volunteer")
    void getConfirmedParticipations_Success() {
        Promoter promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Promoter");
        promoter.setEmail("promoter@test.com");

        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Beach Cleanup");
        opportunity.setPoints(100);
        opportunity.setPromoter(promoter);

        Application app = new Application();
        app.setId(1L);
        app.setVolunteer(volunteer);
        app.setOpportunity(opportunity);
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setParticipationConfirmed(true);
        app.setPointsAwarded(100);
        app.setConfirmedAt(LocalDateTime.now());
        app.setAppliedAt(LocalDateTime.now());

        when(volunteerRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                .thenReturn(Collections.singletonList(app));

        List<ApplicationResponse> participations = volunteerService.getConfirmedParticipations(1L);

        assertThat(participations).hasSize(1);
        assertThat(participations.get(0).getParticipationConfirmed()).isTrue();
        assertThat(participations.get(0).getPointsAwarded()).isEqualTo(100);
    }
}
