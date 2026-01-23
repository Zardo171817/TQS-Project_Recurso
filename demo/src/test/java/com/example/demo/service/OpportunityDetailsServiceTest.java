package com.example.demo.service;

import com.example.demo.dto.OpportunityResponse;
import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Feature: Ver Detalhes de Oportunidades (Voluntario)
 * Tests the service layer for retrieving complete opportunity details
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Feature: Ver Detalhes de Oportunidades - Unit Tests")
class OpportunityDetailsServiceTest {

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
        testPromoter.setName("Cruz Vermelha");
        testPromoter.setEmail("contato@cruzvermelha.org");
        testPromoter.setOrganization("Cruz Vermelha Portuguesa");

        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Apoio a Idosos em Lares");
        testOpportunity.setDescription("Procuramos voluntarios para fazer companhia e apoiar idosos em lares. As atividades incluem conversas, jogos de mesa, leitura e passeios pelo jardim. E uma oportunidade de fazer a diferenca na vida de quem mais precisa.");
        testOpportunity.setSkills("Paciencia, Comunicacao, Empatia");
        testOpportunity.setCategory("Assistencia Social");
        testOpportunity.setDuration(30);
        testOpportunity.setVacancies(15);
        testOpportunity.setPoints(300);
        testOpportunity.setPromoter(testPromoter);
        testOpportunity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve retornar todos os detalhes completos da oportunidade quando existir")
    void whenGetOpportunityById_thenReturnCompleteDetails() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        OpportunityResponse response = opportunityService.getOpportunityById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Apoio a Idosos em Lares", response.getTitle());
        assertEquals("Procuramos voluntarios para fazer companhia e apoiar idosos em lares. As atividades incluem conversas, jogos de mesa, leitura e passeios pelo jardim. E uma oportunidade de fazer a diferenca na vida de quem mais precisa.", response.getDescription());
        assertEquals("Paciencia, Comunicacao, Empatia", response.getSkills());
        assertEquals("Assistencia Social", response.getCategory());
        assertEquals(30, response.getDuration());
        assertEquals(15, response.getVacancies());
        assertEquals(300, response.getPoints());
        assertEquals(1L, response.getPromoterId());
        assertEquals("Cruz Vermelha", response.getPromoterName());
        assertNotNull(response.getCreatedAt());

        verify(opportunityRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lancar ResourceNotFoundException quando oportunidade nao existir")
    void whenGetOpportunityByIdNotFound_thenThrowResourceNotFoundException() {
        Long nonExistentId = 999L;
        when(opportunityRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> opportunityService.getOpportunityById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(opportunityRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Deve incluir informacoes do promotor nos detalhes da oportunidade")
    void whenGetOpportunityById_thenIncludePromoterInfo() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        OpportunityResponse response = opportunityService.getOpportunityById(1L);

        assertNotNull(response.getPromoterId());
        assertNotNull(response.getPromoterName());
        assertEquals("Cruz Vermelha", response.getPromoterName());
    }

    @Test
    @DisplayName("Deve incluir data de criacao nos detalhes da oportunidade")
    void whenGetOpportunityById_thenIncludeCreatedAt() {
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        OpportunityResponse response = opportunityService.getOpportunityById(1L);

        assertNotNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Deve retornar descricao completa sem truncamento")
    void whenGetOpportunityById_thenReturnFullDescription() {
        String longDescription = "Esta e uma descricao muito longa que deve ser retornada completamente sem nenhum truncamento para que o voluntario possa ler todos os detalhes da oportunidade antes de decidir se vai se candidatar.";
        testOpportunity.setDescription(longDescription);
        when(opportunityRepository.findById(1L)).thenReturn(Optional.of(testOpportunity));

        OpportunityResponse response = opportunityService.getOpportunityById(1L);

        assertEquals(longDescription, response.getDescription());
        assertEquals(longDescription.length(), response.getDescription().length());
    }
}
