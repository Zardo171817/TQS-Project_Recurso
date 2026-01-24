package com.example.demo.entity;

import com.example.demo.dto.OpportunityResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OpportunityEntityTest {

    @Test
    void testOpportunityNoArgsConstructorAndSetters() {
        Opportunity opportunity = new Opportunity();
        Promoter promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test");
        promoter.setEmail("test@test.com");
        promoter.setOrganization("Org");

        LocalDateTime now = LocalDateTime.now();

        opportunity.setId(1L);
        opportunity.setTitle("New Title");
        opportunity.setDescription("New Description");
        opportunity.setSkills("Python");
        opportunity.setCategory("Education");
        opportunity.setDuration(20);
        opportunity.setVacancies(10);
        opportunity.setPoints(200);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(now);

        assertEquals(1L, opportunity.getId());
        assertEquals("New Title", opportunity.getTitle());
        assertEquals(OpportunityStatus.OPEN, opportunity.getStatus());
    }

    @Test
    void testOpportunityOnCreate() {
        Opportunity opportunity = new Opportunity();
        assertNull(opportunity.getCreatedAt());

        opportunity.onCreate();

        assertNotNull(opportunity.getCreatedAt());
        assertEquals(OpportunityStatus.OPEN, opportunity.getStatus());
    }

    @Test
    void testOpportunityResponseFromEntity() {
        Promoter promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Promoter Name");
        promoter.setEmail("promoter@test.com");
        promoter.setOrganization("Org");

        Opportunity opportunity = new Opportunity();
        opportunity.setId(10L);
        opportunity.setTitle("Opp Title");
        opportunity.setDescription("Description");
        opportunity.setSkills("Java");
        opportunity.setCategory("Tech");
        opportunity.setDuration(15);
        opportunity.setVacancies(8);
        opportunity.setPoints(150);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());

        OpportunityResponse response = OpportunityResponse.fromEntity(opportunity);

        assertEquals(10L, response.getId());
        assertEquals("Opp Title", response.getTitle());
        assertEquals(OpportunityStatus.OPEN, response.getStatus());
    }

    @Test
    void testOpportunityConcludedStatus() {
        Opportunity opportunity = new Opportunity();
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setConcludedAt(LocalDateTime.now());

        assertEquals(OpportunityStatus.CONCLUDED, opportunity.getStatus());
        assertNotNull(opportunity.getConcludedAt());
    }

    @Test
    void testPromoterNoArgsConstructorAndSetters() {
        Promoter promoter = new Promoter();

        promoter.setId(2L);
        promoter.setName("New Promoter");
        promoter.setEmail("new@email.com");
        promoter.setOrganization("New Org");

        assertEquals(2L, promoter.getId());
        assertEquals("New Promoter", promoter.getName());
    }
}
