package com.example.demo.service;

import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.dto.UpdateOpportunityRequest;
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
    }

    // Feature 1: Criar Oportunidade
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
    }

    // Feature 2: Ver/Filtrar Oportunidades
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
    }

    // Feature 3: Ver Detalhes
    @Test
    void whenGetOpportunityByIdExists_thenReturnOpportunity() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        OpportunityResponse response = opportunityService.getOpportunityById(1L);

        assertNotNull(response);
        assertEquals("Test Opportunity", response.getTitle());
    }

    // Feature 4: Editar/Cancelar
    @Test
    void whenUpdateOpportunityWithValidData_thenReturnUpdatedOpportunity() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description here");
        request.setSkills("Updated Skills");
        request.setCategory("Saude");
        request.setDuration(20);
        request.setVacancies(15);
        request.setPoints(200);

        Opportunity updatedOpportunity = new Opportunity();
        updatedOpportunity.setId(1L);
        updatedOpportunity.setTitle("Updated Title");
        updatedOpportunity.setDescription("Updated Description here");
        updatedOpportunity.setSkills("Updated Skills");
        updatedOpportunity.setCategory("Saude");
        updatedOpportunity.setDuration(20);
        updatedOpportunity.setVacancies(15);
        updatedOpportunity.setPoints(200);
        updatedOpportunity.setPromoter(testPromoter);
        updatedOpportunity.setCreatedAt(testOpportunity.getCreatedAt());

        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenReturn(updatedOpportunity);

        OpportunityResponse response = opportunityService.updateOpportunity(1L, request);

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
    }

    @Test
    void whenDeleteOpportunityExists_thenDeleteSuccessfully() {
        when(opportunityRepository.existsById(1L)).thenReturn(true);
        doNothing().when(opportunityRepository).deleteById(1L);

        opportunityService.deleteOpportunity(1L);

        verify(opportunityRepository, times(1)).deleteById(1L);
    }
}
