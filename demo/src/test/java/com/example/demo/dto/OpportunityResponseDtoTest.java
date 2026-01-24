package com.example.demo.dto;

import com.example.demo.entity.Opportunity;
import com.example.demo.entity.OpportunityStatus;
import com.example.demo.entity.Promoter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OpportunityResponseDtoTest {

    private Opportunity opportunity;
    private Promoter promoter;

    @BeforeEach
    void setUp() {
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Promoter");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Test Org");

        opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Test Opportunity");
        opportunity.setDescription("Test Description");
        opportunity.setSkills("Java, Spring");
        opportunity.setCategory("Technology");
        opportunity.setDuration(10);
        opportunity.setVacancies(5);
        opportunity.setPoints(100);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void whenFromEntityWithOpenStatus_thenStatusIsOpen() {
        OpportunityResponse response = OpportunityResponse.fromEntity(opportunity);

        assertEquals(OpportunityStatus.OPEN, response.getStatus());
        assertNull(response.getConcludedAt());
    }

    @Test
    void whenFromEntityWithConcludedStatus_thenStatusIsConcluded() {
        LocalDateTime concludedTime = LocalDateTime.now();
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setConcludedAt(concludedTime);

        OpportunityResponse response = OpportunityResponse.fromEntity(opportunity);

        assertEquals(OpportunityStatus.CONCLUDED, response.getStatus());
        assertEquals(concludedTime, response.getConcludedAt());
    }

    @Test
    void whenFromEntity_thenAllFieldsMapped() {
        OpportunityResponse response = OpportunityResponse.fromEntity(opportunity);

        assertEquals(opportunity.getId(), response.getId());
        assertEquals(opportunity.getTitle(), response.getTitle());
        assertEquals(opportunity.getDescription(), response.getDescription());
        assertEquals(opportunity.getSkills(), response.getSkills());
        assertEquals(opportunity.getCategory(), response.getCategory());
        assertEquals(opportunity.getDuration(), response.getDuration());
        assertEquals(opportunity.getVacancies(), response.getVacancies());
        assertEquals(opportunity.getPoints(), response.getPoints());
        assertEquals(promoter.getId(), response.getPromoterId());
        assertEquals(promoter.getName(), response.getPromoterName());
        assertEquals(opportunity.getCreatedAt(), response.getCreatedAt());
        assertEquals(opportunity.getStatus(), response.getStatus());
    }

    @Test
    void whenOpportunityResponseNoArgsConstructor_thenFieldsNull() {
        OpportunityResponse response = new OpportunityResponse();

        assertNull(response.getId());
        assertNull(response.getTitle());
        assertNull(response.getStatus());
    }

    @Test
    void whenOpportunityResponseSetters_thenFieldsSet() {
        OpportunityResponse response = new OpportunityResponse();
        response.setId(1L);
        response.setTitle("Test");
        response.setStatus(OpportunityStatus.CONCLUDED);
        response.setConcludedAt(LocalDateTime.now());

        assertEquals(1L, response.getId());
        assertEquals("Test", response.getTitle());
        assertEquals(OpportunityStatus.CONCLUDED, response.getStatus());
        assertNotNull(response.getConcludedAt());
    }

    @Test
    void whenOpportunityResponseEquals_thenCorrect() {
        OpportunityResponse r1 = OpportunityResponse.fromEntity(opportunity);
        OpportunityResponse r2 = OpportunityResponse.fromEntity(opportunity);

        assertEquals(r1.getId(), r2.getId());
        assertEquals(r1.getStatus(), r2.getStatus());
    }

    @Test
    void whenOpportunityResponseToString_thenContainsFields() {
        OpportunityResponse response = OpportunityResponse.fromEntity(opportunity);
        String str = response.toString();

        assertTrue(str.contains("Test Opportunity"));
        assertTrue(str.contains("OPEN"));
    }
}
