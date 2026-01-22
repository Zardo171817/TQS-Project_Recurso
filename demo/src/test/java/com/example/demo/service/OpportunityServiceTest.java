package com.example.demo.service;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private PromoterRepository promoterRepository;

    @InjectMocks
    private OpportunityService opportunityService;

    private Promoter testPromoter;
    private Opportunity testOpportunity;
    private CreateOpportunityRequest validRequest;

    @BeforeEach
    void setUp() {
        testPromoter = new Promoter();
        testPromoter.setId(1L);
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("promoter@test.com");
        testPromoter.setOrganization("Test Org");

        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Test Opportunity");
        testOpportunity.setDescription("This is a test opportunity");
        testOpportunity.setSkills("Java, Spring Boot");
        testOpportunity.setDuration(10);
        testOpportunity.setVacancies(5);
        testOpportunity.setPoints(100);
        testOpportunity.setPromoter(testPromoter);
        testOpportunity.setCreatedAt(LocalDateTime.now());

        validRequest = new CreateOpportunityRequest();
        validRequest.setTitle("Test Opportunity");
        validRequest.setDescription("This is a test opportunity");
        validRequest.setSkills("Java, Spring Boot");
        validRequest.setDuration(10);
        validRequest.setVacancies(5);
        validRequest.setPoints(100);
        validRequest.setPromoterId(1L);
    }

    @Test
    void whenCreateOpportunityWithValidData_thenReturnOpportunityResponse() {
        when(promoterRepository.findById(1L)).thenReturn(Optional.of(testPromoter));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(testOpportunity);

        OpportunityResponse response = opportunityService.createOpportunity(validRequest);

        assertNotNull(response);
        assertEquals("Test Opportunity", response.getTitle());
        assertEquals("This is a test opportunity", response.getDescription());
        assertEquals("Java, Spring Boot", response.getSkills());
        assertEquals(10, response.getDuration());
        assertEquals(5, response.getVacancies());
        assertEquals(100, response.getPoints());
        assertEquals(1L, response.getPromoterId());
        assertEquals("Test Promoter", response.getPromoterName());

        verify(promoterRepository, times(1)).findById(1L);
        verify(opportunityRepository, times(1)).save(any(Opportunity.class));
    }

    @Test
    void whenCreateOpportunityWithInvalidPromoterId_thenThrowResourceNotFoundException() {
        when(promoterRepository.findById(999L)).thenReturn(Optional.empty());

        validRequest.setPromoterId(999L);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> opportunityService.createOpportunity(validRequest)
        );

        assertEquals("Promoter not found with id: 999", exception.getMessage());
        verify(promoterRepository, times(1)).findById(999L);
        verify(opportunityRepository, never()).save(any(Opportunity.class));
    }

    @Test
    void whenGetOpportunityByIdWithValidId_thenReturnOpportunityResponse() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        OpportunityResponse response = opportunityService.getOpportunityById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Opportunity", response.getTitle());
        verify(opportunityRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetOpportunityByIdWithInvalidId_thenThrowResourceNotFoundException() {
        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> opportunityService.getOpportunityById(999L)
        );

        assertEquals("Opportunity not found with id: 999", exception.getMessage());
        verify(opportunityRepository, times(1)).findById(999L);
    }

    @Test
    void whenGetAllOpportunities_thenReturnListOfOpportunities() {
        Opportunity opportunity2 = new Opportunity();
        opportunity2.setId(2L);
        opportunity2.setTitle("Second Opportunity");
        opportunity2.setDescription("Second description");
        opportunity2.setSkills("Python, Django");
        opportunity2.setDuration(20);
        opportunity2.setVacancies(3);
        opportunity2.setPoints(150);
        opportunity2.setPromoter(testPromoter);
        opportunity2.setCreatedAt(LocalDateTime.now());

        when(opportunityRepository.findAll()).thenReturn(Arrays.asList(testOpportunity, opportunity2));

        List<OpportunityResponse> responses = opportunityService.getAllOpportunities();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Test Opportunity", responses.get(0).getTitle());
        assertEquals("Second Opportunity", responses.get(1).getTitle());
        verify(opportunityRepository, times(1)).findAll();
    }

    @Test
    void whenGetOpportunitiesByPromoterWithValidId_thenReturnListOfOpportunities() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterId(1L)).thenReturn(Arrays.asList(testOpportunity));

        List<OpportunityResponse> responses = opportunityService.getOpportunitiesByPromoter(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Opportunity", responses.get(0).getTitle());
        verify(promoterRepository, times(1)).existsById(1L);
        verify(opportunityRepository, times(1)).findByPromoterId(1L);
    }

    @Test
    void whenGetOpportunitiesByPromoterWithInvalidId_thenThrowResourceNotFoundException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> opportunityService.getOpportunitiesByPromoter(999L)
        );

        assertEquals("Promoter not found with id: 999", exception.getMessage());
        verify(promoterRepository, times(1)).existsById(999L);
        verify(opportunityRepository, never()).findByPromoterId(anyLong());
    }
}
