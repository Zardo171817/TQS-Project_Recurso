package com.example.demo.service;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityFilterRequest;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
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
    private Opportunity testOpportunity2;

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
        testOpportunity.setCategory("Tecnologia");
        testOpportunity.setDuration(10);
        testOpportunity.setVacancies(5);
        testOpportunity.setPoints(100);
        testOpportunity.setPromoter(testPromoter);
        testOpportunity.setCreatedAt(LocalDateTime.now());

        testOpportunity2 = new Opportunity();
        testOpportunity2.setId(2L);
        testOpportunity2.setTitle("Health Opportunity");
        testOpportunity2.setDescription("This is a health opportunity");
        testOpportunity2.setSkills("First Aid, CPR");
        testOpportunity2.setCategory("Saude");
        testOpportunity2.setDuration(5);
        testOpportunity2.setVacancies(10);
        testOpportunity2.setPoints(50);
        testOpportunity2.setPromoter(testPromoter);
        testOpportunity2.setCreatedAt(LocalDateTime.now());
    }

    // Feature 1: Criar Oportunidade - Unit Tests
    @Test
    void whenCreateOpportunityWithValidData_thenReturnOpportunityResponse() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Test Opportunity");
        request.setDescription("This is a test opportunity");
        request.setSkills("Java, Spring Boot");
        request.setCategory("Tecnologia");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        when(promoterRepository.findById(1L)).thenReturn(Optional.of(testPromoter));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(testOpportunity);

        OpportunityResponse response = opportunityService.createOpportunity(request);

        assertNotNull(response);
        assertEquals("Test Opportunity", response.getTitle());
        assertEquals("Tecnologia", response.getCategory());
        assertEquals(10, response.getDuration());
        assertEquals(5, response.getVacancies());
        assertEquals(100, response.getPoints());
        verify(opportunityRepository, times(1)).save(any(Opportunity.class));
    }

    @Test
    void whenCreateOpportunityWithInvalidPromoter_thenThrowException() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Test");
        request.setDescription("Test Description");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(5);
        request.setVacancies(2);
        request.setPoints(50);
        request.setPromoterId(999L);

        when(promoterRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> opportunityService.createOpportunity(request)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(opportunityRepository, never()).save(any(Opportunity.class));
    }

    // Get Opportunity by ID Tests
    @Test
    void whenGetOpportunityByIdExists_thenReturnOpportunity() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        OpportunityResponse response = opportunityService.getOpportunityById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Opportunity", response.getTitle());
        verify(opportunityRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetOpportunityByIdNotExists_thenThrowException() {
        when(opportunityRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> opportunityService.getOpportunityById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    // Get All Opportunities Tests
    @Test
    void whenGetAllOpportunities_thenReturnList() {
        when(opportunityRepository.findAll()).thenReturn(Arrays.asList(testOpportunity, testOpportunity2));

        List<OpportunityResponse> responses = opportunityService.getAllOpportunities();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(opportunityRepository, times(1)).findAll();
    }

    @Test
    void whenGetAllOpportunitiesEmpty_thenReturnEmptyList() {
        when(opportunityRepository.findAll()).thenReturn(Collections.emptyList());

        List<OpportunityResponse> responses = opportunityService.getAllOpportunities();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // Get Opportunities by Promoter Tests
    @Test
    void whenGetOpportunitiesByPromoterExists_thenReturnList() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterId(1L))
                .thenReturn(Arrays.asList(testOpportunity, testOpportunity2));

        List<OpportunityResponse> responses = opportunityService.getOpportunitiesByPromoter(1L);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(opportunityRepository, times(1)).findByPromoterId(1L);
    }

    @Test
    void whenGetOpportunitiesByPromoterNotExists_thenThrowException() {
        when(promoterRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> opportunityService.getOpportunitiesByPromoter(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(opportunityRepository, never()).findByPromoterId(any());
    }

    @Test
    void whenGetOpportunitiesByPromoterEmpty_thenReturnEmptyList() {
        when(promoterRepository.existsById(1L)).thenReturn(true);
        when(opportunityRepository.findByPromoterId(1L)).thenReturn(Collections.emptyList());

        List<OpportunityResponse> responses = opportunityService.getOpportunitiesByPromoter(1L);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // Feature 2: Ver/Filtrar Oportunidades - Unit Tests
    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesByCategory_thenReturnFilteredList() {
        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(testOpportunity));

        List<OpportunityResponse> responses = opportunityService.filterOpportunitiesByParams(
                "Tecnologia", null, null, null
        );

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Tecnologia", responses.get(0).getCategory());
        verify(opportunityRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesBySkills_thenReturnFilteredList() {
        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(testOpportunity));

        List<OpportunityResponse> responses = opportunityService.filterOpportunitiesByParams(
                null, "Java", null, null
        );

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getSkills().contains("Java"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesByDurationRange_thenReturnFilteredList() {
        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(testOpportunity, testOpportunity2));

        List<OpportunityResponse> responses = opportunityService.filterOpportunitiesByParams(
                null, null, 5, 15
        );

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesByMinDurationOnly_thenReturnFilteredList() {
        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(testOpportunity));

        List<OpportunityResponse> responses = opportunityService.filterOpportunitiesByParams(
                null, null, 8, null
        );

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesByMaxDurationOnly_thenReturnFilteredList() {
        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(testOpportunity2));

        List<OpportunityResponse> responses = opportunityService.filterOpportunitiesByParams(
                null, null, null, 7
        );

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesWithAllParams_thenReturnFilteredList() {
        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(testOpportunity));

        List<OpportunityResponse> responses = opportunityService.filterOpportunitiesByParams(
                "Tecnologia", "Java", 5, 15
        );

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Tecnologia", responses.get(0).getCategory());
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesNoMatch_thenReturnEmptyList() {
        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Collections.emptyList());

        List<OpportunityResponse> responses = opportunityService.filterOpportunitiesByParams(
                "NonExistent", null, null, null
        );

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesWithFilterRequest_thenReturnFilteredList() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest();
        filter.setCategory("Tecnologia");
        filter.setSkills("Java");
        filter.setMinDuration(5);
        filter.setMaxDuration(15);

        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(testOpportunity));

        List<OpportunityResponse> responses = opportunityService.filterOpportunities(filter);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void whenFilterOpportunitiesWithEmptyFilterRequest_thenReturnAllOpportunities() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest();

        when(opportunityRepository.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(testOpportunity, testOpportunity2));

        List<OpportunityResponse> responses = opportunityService.filterOpportunities(filter);

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    // Get All Categories Tests
    @Test
    void whenGetAllCategories_thenReturnCategoryList() {
        List<String> categories = Arrays.asList("Tecnologia", "Saude", "Educacao");
        when(opportunityRepository.findAllCategories()).thenReturn(categories);

        List<String> result = opportunityService.getAllCategories();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Tecnologia"));
        assertTrue(result.contains("Saude"));
        verify(opportunityRepository, times(1)).findAllCategories();
    }

    @Test
    void whenGetAllCategoriesEmpty_thenReturnEmptyList() {
        when(opportunityRepository.findAllCategories()).thenReturn(Collections.emptyList());

        List<String> result = opportunityService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
