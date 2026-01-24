package com.example.demo.service;

import com.example.demo.dto.OpportunityResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpportunityStatusServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private PromoterRepository promoterRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private OpportunityService opportunityService;

    private Promoter testPromoter;
    private Opportunity openOpportunity;
    private Opportunity concludedOpportunity;

    @BeforeEach
    void setUp() {
        testPromoter = new Promoter();
        testPromoter.setId(1L);
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("promoter@test.com");
        testPromoter.setOrganization("Test Org");

        openOpportunity = new Opportunity();
        openOpportunity.setId(1L);
        openOpportunity.setTitle("Open Opportunity");
        openOpportunity.setDescription("This is open");
        openOpportunity.setSkills("Java");
        openOpportunity.setCategory("Tech");
        openOpportunity.setDuration(10);
        openOpportunity.setVacancies(5);
        openOpportunity.setPoints(100);
        openOpportunity.setStatus(OpportunityStatus.OPEN);
        openOpportunity.setPromoter(testPromoter);
        openOpportunity.setCreatedAt(LocalDateTime.now());

        concludedOpportunity = new Opportunity();
        concludedOpportunity.setId(2L);
        concludedOpportunity.setTitle("Concluded Opportunity");
        concludedOpportunity.setDescription("This is concluded");
        concludedOpportunity.setSkills("Python");
        concludedOpportunity.setCategory("Data");
        concludedOpportunity.setDuration(5);
        concludedOpportunity.setVacancies(3);
        concludedOpportunity.setPoints(150);
        concludedOpportunity.setStatus(OpportunityStatus.CONCLUDED);
        concludedOpportunity.setPromoter(testPromoter);
        concludedOpportunity.setCreatedAt(LocalDateTime.now().minusDays(30));
        concludedOpportunity.setConcludedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    void whenGetOpportunitiesByStatusOpen_thenReturnOnlyOpen() {
        when(opportunityRepository.findByStatus(OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(openOpportunity));

        List<OpportunityResponse> results = opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN);

        assertEquals(1, results.size());
        assertEquals(OpportunityStatus.OPEN, results.get(0).getStatus());
    }

    @Test
    void whenGetOpportunitiesByStatusConcluded_thenReturnOnlyConcluded() {
        when(opportunityRepository.findByStatus(OpportunityStatus.CONCLUDED))
            .thenReturn(Arrays.asList(concludedOpportunity));

        List<OpportunityResponse> results = opportunityService.getOpportunitiesByStatus(OpportunityStatus.CONCLUDED);

        assertEquals(1, results.size());
        assertEquals(OpportunityStatus.CONCLUDED, results.get(0).getStatus());
        assertNotNull(results.get(0).getConcludedAt());
    }

    @Test
    void whenGetOpportunitiesByStatusEmpty_thenReturnEmptyList() {
        when(opportunityRepository.findByStatus(OpportunityStatus.OPEN))
            .thenReturn(Collections.emptyList());

        List<OpportunityResponse> results = opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN);

        assertTrue(results.isEmpty());
    }

    @Test
    void whenGetOpportunitiesByPromoterAndStatusOpen_thenReturnOpen() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterIdAndStatus(1L, OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(openOpportunity));

        List<OpportunityResponse> results =
            opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.OPEN);

        assertEquals(1, results.size());
        assertEquals(OpportunityStatus.OPEN, results.get(0).getStatus());
    }

    @Test
    void whenGetOpportunitiesByPromoterAndStatusConcluded_thenReturnConcluded() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED))
            .thenReturn(Arrays.asList(concludedOpportunity));

        List<OpportunityResponse> results =
            opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.CONCLUDED);

        assertEquals(1, results.size());
        assertEquals(OpportunityStatus.CONCLUDED, results.get(0).getStatus());
    }

    @Test
    void whenGetOpportunitiesByPromoterAndStatusMixed_thenReturnCorrect() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterIdAndStatus(1L, OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(openOpportunity));
        when(opportunityRepository.findByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED))
            .thenReturn(Arrays.asList(concludedOpportunity));

        List<OpportunityResponse> openResults =
            opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.OPEN);
        List<OpportunityResponse> concludedResults =
            opportunityService.getOpportunitiesByPromoterAndStatus(1L, OpportunityStatus.CONCLUDED);

        assertEquals(1, openResults.size());
        assertEquals(1, concludedResults.size());
        assertEquals("Open Opportunity", openResults.get(0).getTitle());
        assertEquals("Concluded Opportunity", concludedResults.get(0).getTitle());
    }

    @Test
    void whenCountConcludedOpportunities_thenReturnCorrectCount() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.countByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED))
            .thenReturn(5L);

        long count = opportunityService.countConcludedOpportunitiesByPromoter(1L);

        assertEquals(5L, count);
    }

    @Test
    void whenCountConcludedOpportunitiesZero_thenReturnZero() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.countByPromoterIdAndStatus(1L, OpportunityStatus.CONCLUDED))
            .thenReturn(0L);

        long count = opportunityService.countConcludedOpportunitiesByPromoter(1L);

        assertEquals(0L, count);
    }

    @Test
    void whenGetOpportunitiesByNonExistentPromoter_thenThrowException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            opportunityService.getOpportunitiesByPromoterAndStatus(999L, OpportunityStatus.OPEN));
    }

    @Test
    void whenCountConcludedByNonExistentPromoter_thenThrowException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
            opportunityService.countConcludedOpportunitiesByPromoter(999L));
    }

    @Test
    void whenOpportunityHasNoConcludedAt_thenFieldIsNull() {
        when(opportunityRepository.findByStatus(OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(openOpportunity));

        List<OpportunityResponse> results = opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN);

        assertNull(results.get(0).getConcludedAt());
    }

    @Test
    void whenOpportunityHasConcludedAt_thenFieldIsSet() {
        when(opportunityRepository.findByStatus(OpportunityStatus.CONCLUDED))
            .thenReturn(Arrays.asList(concludedOpportunity));

        List<OpportunityResponse> results = opportunityService.getOpportunitiesByStatus(OpportunityStatus.CONCLUDED);

        assertNotNull(results.get(0).getConcludedAt());
    }

    @Test
    void whenMultipleOpportunitiesBothStatus_thenCorrectlySorted() {
        Opportunity anotherOpen = new Opportunity();
        anotherOpen.setId(3L);
        anotherOpen.setTitle("Another Open");
        anotherOpen.setDescription("Also open");
        anotherOpen.setSkills("Go");
        anotherOpen.setCategory("Backend");
        anotherOpen.setDuration(15);
        anotherOpen.setVacancies(10);
        anotherOpen.setPoints(200);
        anotherOpen.setStatus(OpportunityStatus.OPEN);
        anotherOpen.setPromoter(testPromoter);
        anotherOpen.setCreatedAt(LocalDateTime.now());

        when(opportunityRepository.findByStatus(OpportunityStatus.OPEN))
            .thenReturn(Arrays.asList(openOpportunity, anotherOpen));

        List<OpportunityResponse> results = opportunityService.getOpportunitiesByStatus(OpportunityStatus.OPEN);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.getStatus() == OpportunityStatus.OPEN));
    }
}
