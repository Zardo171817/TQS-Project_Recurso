package com.example.demo.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolunteerPointsServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer testVolunteer;
    private Volunteer secondVolunteer;
    private Volunteer thirdVolunteer;
    private Application testApplication;
    private Opportunity testOpportunity;
    private Promoter testPromoter;

    @BeforeEach
    void setUp() {
        testPromoter = new Promoter();
        testPromoter.setId(1L);
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("promoter@test.com");
        testPromoter.setOrganization("Test Org");

        testVolunteer = new Volunteer();
        testVolunteer.setId(1L);
        testVolunteer.setName("Test Volunteer");
        testVolunteer.setEmail("volunteer@test.com");
        testVolunteer.setTotalPoints(100);

        secondVolunteer = new Volunteer();
        secondVolunteer.setId(2L);
        secondVolunteer.setName("Second Volunteer");
        secondVolunteer.setEmail("second@test.com");
        secondVolunteer.setTotalPoints(200);

        thirdVolunteer = new Volunteer();
        thirdVolunteer.setId(3L);
        thirdVolunteer.setName("Third Volunteer");
        thirdVolunteer.setEmail("third@test.com");
        thirdVolunteer.setTotalPoints(50);

        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Test Opportunity");
        testOpportunity.setDescription("Test Description");
        testOpportunity.setSkills("Java");
        testOpportunity.setCategory("Tech");
        testOpportunity.setDuration(10);
        testOpportunity.setVacancies(5);
        testOpportunity.setPoints(100);
        testOpportunity.setStatus(OpportunityStatus.CONCLUDED);
        testOpportunity.setPromoter(testPromoter);
        testOpportunity.setCreatedAt(LocalDateTime.now());

        testApplication = new Application();
        testApplication.setId(1L);
        testApplication.setVolunteer(testVolunteer);
        testApplication.setOpportunity(testOpportunity);
        testApplication.setStatus(ApplicationStatus.ACCEPTED);
        testApplication.setParticipationConfirmed(true);
        testApplication.setPointsAwarded(100);
        testApplication.setAppliedAt(LocalDateTime.now());
    }

    @Test
    void whenGetVolunteerPoints_thenReturnPoints() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(testVolunteer));

        VolunteerPointsResponse response = volunteerService.getVolunteerPoints(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Volunteer", response.getName());
        assertEquals(100, response.getTotalPoints());
    }

    @Test
    void whenGetVolunteerPointsNotFound_thenThrowException() {
        when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            volunteerService.getVolunteerPoints(999L));
    }

    @Test
    void whenGetVolunteersRanking_thenReturnSortedList() {
        when(volunteerRepository.findAll())
            .thenReturn(Arrays.asList(testVolunteer, secondVolunteer, thirdVolunteer));

        List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

        assertNotNull(ranking);
        assertEquals(3, ranking.size());
        assertEquals(200, ranking.get(0).getTotalPoints());
        assertEquals(100, ranking.get(1).getTotalPoints());
        assertEquals(50, ranking.get(2).getTotalPoints());
    }

    @Test
    void whenGetVolunteersRankingEmpty_thenReturnEmptyList() {
        when(volunteerRepository.findAll()).thenReturn(Collections.emptyList());

        List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

        assertNotNull(ranking);
        assertTrue(ranking.isEmpty());
    }

    @Test
    void whenGetTopVolunteers_thenReturnLimitedList() {
        when(volunteerRepository.findAll())
            .thenReturn(Arrays.asList(testVolunteer, secondVolunteer, thirdVolunteer));

        List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(2);

        assertNotNull(top);
        assertEquals(2, top.size());
        assertEquals(200, top.get(0).getTotalPoints());
        assertEquals(100, top.get(1).getTotalPoints());
    }

    @Test
    void whenGetTopVolunteersMoreThanAvailable_thenReturnAll() {
        when(volunteerRepository.findAll())
            .thenReturn(Arrays.asList(testVolunteer, secondVolunteer));

        List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(10);

        assertNotNull(top);
        assertEquals(2, top.size());
    }

    @Test
    void whenGetTopVolunteersWithZero_thenReturnEmptyList() {
        when(volunteerRepository.findAll())
            .thenReturn(Arrays.asList(testVolunteer, secondVolunteer));

        List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(0);

        assertNotNull(top);
        assertTrue(top.isEmpty());
    }

    @Test
    void whenGetConfirmedParticipations_thenReturnList() {
        when(volunteerRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
            .thenReturn(Arrays.asList(testApplication));

        List<ApplicationResponse> participations = volunteerService.getConfirmedParticipations(1L);

        assertNotNull(participations);
        assertEquals(1, participations.size());
        assertTrue(participations.get(0).getParticipationConfirmed());
    }

    @Test
    void whenGetConfirmedParticipationsNotFound_thenThrowException() {
        when(volunteerRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            volunteerService.getConfirmedParticipations(999L));
    }

    @Test
    void whenGetConfirmedParticipationsEmpty_thenReturnEmptyList() {
        when(volunteerRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
            .thenReturn(Collections.emptyList());

        List<ApplicationResponse> participations = volunteerService.getConfirmedParticipations(1L);

        assertNotNull(participations);
        assertTrue(participations.isEmpty());
    }

    @Test
    void whenGetVolunteerPointsWithZeroPoints_thenReturnZero() {
        testVolunteer.setTotalPoints(0);
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(testVolunteer));

        VolunteerPointsResponse response = volunteerService.getVolunteerPoints(1L);

        assertNotNull(response);
        assertEquals(0, response.getTotalPoints());
    }

    @Test
    void whenGetRankingAllSamePoints_thenReturnAll() {
        testVolunteer.setTotalPoints(100);
        secondVolunteer.setTotalPoints(100);
        thirdVolunteer.setTotalPoints(100);

        when(volunteerRepository.findAll())
            .thenReturn(Arrays.asList(testVolunteer, secondVolunteer, thirdVolunteer));

        List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

        assertNotNull(ranking);
        assertEquals(3, ranking.size());
        assertEquals(100, ranking.get(0).getTotalPoints());
        assertEquals(100, ranking.get(1).getTotalPoints());
        assertEquals(100, ranking.get(2).getTotalPoints());
    }

    @Test
    void whenGetVolunteerPointsResponseFromEntity_thenMapsCorrectly() {
        VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(testVolunteer);

        assertEquals(testVolunteer.getId(), response.getId());
        assertEquals(testVolunteer.getName(), response.getName());
        assertEquals(testVolunteer.getEmail(), response.getEmail());
        assertEquals(testVolunteer.getTotalPoints(), response.getTotalPoints());
    }

    @Test
    void whenVolunteerPointsResponseGettersSetters_thenWork() {
        VolunteerPointsResponse response = new VolunteerPointsResponse();
        response.setId(1L);
        response.setName("Test");
        response.setEmail("test@test.com");
        response.setTotalPoints(500);

        assertEquals(1L, response.getId());
        assertEquals("Test", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals(500, response.getTotalPoints());
    }

    @Test
    void whenVolunteerPointsResponseAllArgsConstructor_thenCreatesCorrectly() {
        VolunteerPointsResponse response = new VolunteerPointsResponse(1L, "Test", "test@test.com", 500);

        assertEquals(1L, response.getId());
        assertEquals("Test", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals(500, response.getTotalPoints());
    }
}
