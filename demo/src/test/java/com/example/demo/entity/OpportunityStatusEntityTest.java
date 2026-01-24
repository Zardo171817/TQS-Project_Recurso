package com.example.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OpportunityStatusEntityTest {

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
        opportunity.setDescription("Test Description for the opportunity");
        opportunity.setSkills("Java, Spring");
        opportunity.setCategory("Technology");
        opportunity.setDuration(10);
        opportunity.setVacancies(5);
        opportunity.setPoints(100);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void whenNewOpportunity_thenStatusIsOpen() {
        Opportunity newOpp = new Opportunity();
        newOpp.setStatus(OpportunityStatus.OPEN);
        assertEquals(OpportunityStatus.OPEN, newOpp.getStatus());
    }

    @Test
    void whenSetStatusToConcluded_thenStatusIsConcluded() {
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        assertEquals(OpportunityStatus.CONCLUDED, opportunity.getStatus());
    }

    @Test
    void whenSetConcludedAt_thenConcludedAtIsSet() {
        LocalDateTime now = LocalDateTime.now();
        opportunity.setConcludedAt(now);
        assertEquals(now, opportunity.getConcludedAt());
    }

    @Test
    void whenOpportunityStatusEnumValues_thenCorrect() {
        OpportunityStatus[] values = OpportunityStatus.values();
        assertEquals(2, values.length);
        assertEquals(OpportunityStatus.OPEN, values[0]);
        assertEquals(OpportunityStatus.CONCLUDED, values[1]);
    }

    @Test
    void whenOpportunityStatusValueOf_thenCorrect() {
        assertEquals(OpportunityStatus.OPEN, OpportunityStatus.valueOf("OPEN"));
        assertEquals(OpportunityStatus.CONCLUDED, OpportunityStatus.valueOf("CONCLUDED"));
    }

    @Test
    void whenCompareOpportunityStatus_thenCorrect() {
        assertNotEquals(OpportunityStatus.OPEN, OpportunityStatus.CONCLUDED);
        assertEquals(OpportunityStatus.OPEN, OpportunityStatus.OPEN);
    }

    @Test
    void whenOpportunityWithPoints_thenPointsCorrect() {
        assertEquals(100, opportunity.getPoints());
        opportunity.setPoints(200);
        assertEquals(200, opportunity.getPoints());
    }

    @Test
    void whenOpportunityStatusToString_thenCorrect() {
        assertEquals("OPEN", OpportunityStatus.OPEN.toString());
        assertEquals("CONCLUDED", OpportunityStatus.CONCLUDED.toString());
    }

    @Test
    void whenConcludeOpportunity_thenAllFieldsSet() {
        opportunity.setStatus(OpportunityStatus.CONCLUDED);
        opportunity.setConcludedAt(LocalDateTime.now());

        assertEquals(OpportunityStatus.CONCLUDED, opportunity.getStatus());
        assertNotNull(opportunity.getConcludedAt());
        assertNotNull(opportunity.getCreatedAt());
    }
}
