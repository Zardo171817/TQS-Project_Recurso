package com.example.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    private Promoter testPromoter;
    private Opportunity testOpportunity;

    @BeforeEach
    void setUp() {
        testPromoter = new Promoter();
        testPromoter.setId(1L);
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("test@promoter.com");
        testPromoter.setOrganization("Test Org");
        testPromoter.setOpportunities(new ArrayList<>());

        testOpportunity = new Opportunity();
        testOpportunity.setId(1L);
        testOpportunity.setTitle("Test Opportunity");
        testOpportunity.setDescription("Test Description");
        testOpportunity.setSkills("Java, Spring");
        testOpportunity.setCategory("Tecnologia");
        testOpportunity.setDuration(10);
        testOpportunity.setVacancies(5);
        testOpportunity.setPoints(100);
        testOpportunity.setPromoter(testPromoter);
        testOpportunity.setCreatedAt(LocalDateTime.now());
    }

    // Promoter Entity Tests
    @Test
    void testPromoterGettersSetters() {
        Promoter promoter = new Promoter();

        promoter.setId(2L);
        promoter.setName("New Promoter");
        promoter.setEmail("new@promoter.com");
        promoter.setOrganization("New Org");
        promoter.setOpportunities(new ArrayList<>());

        assertEquals(2L, promoter.getId());
        assertEquals("New Promoter", promoter.getName());
        assertEquals("new@promoter.com", promoter.getEmail());
        assertEquals("New Org", promoter.getOrganization());
        assertNotNull(promoter.getOpportunities());
        assertTrue(promoter.getOpportunities().isEmpty());
    }

    @Test
    void testPromoterAllArgsConstructor() {
        List<Opportunity> opportunities = new ArrayList<>();
        Promoter promoter = new Promoter(1L, "Promoter", "email@test.com", "Org", opportunities);

        assertEquals(1L, promoter.getId());
        assertEquals("Promoter", promoter.getName());
        assertEquals("email@test.com", promoter.getEmail());
        assertEquals("Org", promoter.getOrganization());
        assertEquals(opportunities, promoter.getOpportunities());
    }

    @Test
    void testPromoterNoArgsConstructor() {
        Promoter promoter = new Promoter();

        assertNull(promoter.getId());
        assertNull(promoter.getName());
        assertNull(promoter.getEmail());
        assertNull(promoter.getOrganization());
    }

    @Test
    void testPromoterEqualsAndHashCode() {
        List<Opportunity> emptyList = new ArrayList<>();
        Promoter promoter1 = new Promoter(1L, "Name", "email@test.com", "Org", emptyList);
        Promoter promoter2 = new Promoter(1L, "Name", "email@test.com", "Org", emptyList);
        Promoter promoter3 = new Promoter(2L, "Different", "diff@test.com", "DiffOrg", emptyList);

        assertEquals(promoter1, promoter2);
        assertEquals(promoter1.hashCode(), promoter2.hashCode());
        assertNotEquals(promoter1, promoter3);
    }

    @Test
    void testPromoterToString() {
        String toString = testPromoter.toString();

        assertTrue(toString.contains("Test Promoter"));
        assertTrue(toString.contains("test@promoter.com"));
        assertTrue(toString.contains("Test Org"));
    }

    @Test
    void testPromoterWithOpportunities() {
        List<Opportunity> opportunities = new ArrayList<>();
        opportunities.add(testOpportunity);
        testPromoter.setOpportunities(opportunities);

        assertEquals(1, testPromoter.getOpportunities().size());
        assertEquals(testOpportunity, testPromoter.getOpportunities().get(0));
    }

    // Opportunity Entity Tests
    @Test
    void testOpportunityGettersSetters() {
        Opportunity opportunity = new Opportunity();
        LocalDateTime now = LocalDateTime.now();

        opportunity.setId(2L);
        opportunity.setTitle("New Opportunity");
        opportunity.setDescription("New Description");
        opportunity.setSkills("Python, Django");
        opportunity.setCategory("Educacao");
        opportunity.setDuration(20);
        opportunity.setVacancies(10);
        opportunity.setPoints(200);
        opportunity.setPromoter(testPromoter);
        opportunity.setCreatedAt(now);

        assertEquals(2L, opportunity.getId());
        assertEquals("New Opportunity", opportunity.getTitle());
        assertEquals("New Description", opportunity.getDescription());
        assertEquals("Python, Django", opportunity.getSkills());
        assertEquals("Educacao", opportunity.getCategory());
        assertEquals(20, opportunity.getDuration());
        assertEquals(10, opportunity.getVacancies());
        assertEquals(200, opportunity.getPoints());
        assertEquals(testPromoter, opportunity.getPromoter());
        assertEquals(now, opportunity.getCreatedAt());
    }

    @Test
    void testOpportunityAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Opportunity opportunity = new Opportunity(
                1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, testPromoter, now
        );

        assertEquals(1L, opportunity.getId());
        assertEquals("Title", opportunity.getTitle());
        assertEquals("Desc", opportunity.getDescription());
        assertEquals("Skills", opportunity.getSkills());
        assertEquals("Cat", opportunity.getCategory());
        assertEquals(10, opportunity.getDuration());
        assertEquals(5, opportunity.getVacancies());
        assertEquals(100, opportunity.getPoints());
        assertEquals(testPromoter, opportunity.getPromoter());
        assertEquals(now, opportunity.getCreatedAt());
    }

    @Test
    void testOpportunityNoArgsConstructor() {
        Opportunity opportunity = new Opportunity();

        assertNull(opportunity.getId());
        assertNull(opportunity.getTitle());
        assertNull(opportunity.getDescription());
        assertNull(opportunity.getSkills());
        assertNull(opportunity.getCategory());
        assertNull(opportunity.getDuration());
        assertNull(opportunity.getVacancies());
        assertNull(opportunity.getPoints());
        assertNull(opportunity.getPromoter());
        assertNull(opportunity.getCreatedAt());
    }

    @Test
    void testOpportunityEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Opportunity opp1 = new Opportunity(1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, testPromoter, now);
        Opportunity opp2 = new Opportunity(1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, testPromoter, now);
        Opportunity opp3 = new Opportunity(2L, "Different", "Desc", "Skills", "Cat", 10, 5, 100, testPromoter, now);

        assertEquals(opp1, opp2);
        assertEquals(opp1.hashCode(), opp2.hashCode());
        assertNotEquals(opp1, opp3);
    }

    @Test
    void testOpportunityToString() {
        String toString = testOpportunity.toString();

        assertTrue(toString.contains("Test Opportunity"));
        assertTrue(toString.contains("Test Description"));
        assertTrue(toString.contains("Tecnologia"));
    }

    @Test
    void testOpportunityWithDifferentCategories() {
        String[] categories = {"Tecnologia", "Saude", "Educacao", "Meio Ambiente", "Animais"};

        for (String category : categories) {
            Opportunity opportunity = new Opportunity();
            opportunity.setCategory(category);
            assertEquals(category, opportunity.getCategory());
        }
    }

    @Test
    void testOpportunityWithDifferentDurations() {
        int[] durations = {1, 5, 10, 30, 60, 90, 365};

        for (int duration : durations) {
            Opportunity opportunity = new Opportunity();
            opportunity.setDuration(duration);
            assertEquals(duration, opportunity.getDuration());
        }
    }

    @Test
    void testOpportunityWithDifferentPoints() {
        int[] pointValues = {0, 50, 100, 500, 1000};

        for (int points : pointValues) {
            Opportunity opportunity = new Opportunity();
            opportunity.setPoints(points);
            assertEquals(points, opportunity.getPoints());
        }
    }

    @Test
    void testOpportunityWithDifferentVacancies() {
        int[] vacancyValues = {1, 5, 10, 50, 100};

        for (int vacancies : vacancyValues) {
            Opportunity opportunity = new Opportunity();
            opportunity.setVacancies(vacancies);
            assertEquals(vacancies, opportunity.getVacancies());
        }
    }

    @Test
    void testOpportunityPromoterRelationship() {
        Opportunity opportunity = new Opportunity();
        opportunity.setPromoter(testPromoter);

        assertNotNull(opportunity.getPromoter());
        assertEquals("Test Promoter", opportunity.getPromoter().getName());
        assertEquals("test@promoter.com", opportunity.getPromoter().getEmail());
    }

    @Test
    void testOpportunityCreatedAtTimestamp() {
        Opportunity opportunity = new Opportunity();
        LocalDateTime specificTime = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
        opportunity.setCreatedAt(specificTime);

        assertEquals(2024, opportunity.getCreatedAt().getYear());
        assertEquals(6, opportunity.getCreatedAt().getMonthValue());
        assertEquals(15, opportunity.getCreatedAt().getDayOfMonth());
        assertEquals(14, opportunity.getCreatedAt().getHour());
        assertEquals(30, opportunity.getCreatedAt().getMinute());
    }
}
