package com.example.demo.dto;

import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    private Promoter testPromoter;
    private Opportunity testOpportunity;

    @BeforeEach
    void setUp() {
        testPromoter = new Promoter();
        testPromoter.setId(1L);
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("test@promoter.com");
        testPromoter.setOrganization("Test Org");

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
        testOpportunity.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
    }

    // CreateOpportunityRequest Tests
    @Test
    void testCreateOpportunityRequestGettersSetters() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();

        request.setTitle("New Opportunity");
        request.setDescription("New Description");
        request.setSkills("Python, Django");
        request.setCategory("Tecnologia");
        request.setDuration(15);
        request.setVacancies(3);
        request.setPoints(200);
        request.setPromoterId(2L);

        assertEquals("New Opportunity", request.getTitle());
        assertEquals("New Description", request.getDescription());
        assertEquals("Python, Django", request.getSkills());
        assertEquals("Tecnologia", request.getCategory());
        assertEquals(15, request.getDuration());
        assertEquals(3, request.getVacancies());
        assertEquals(200, request.getPoints());
        assertEquals(2L, request.getPromoterId());
    }

    @Test
    void testCreateOpportunityRequestAllArgsConstructor() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Title", "Description", "Skills", "Category", 10, 5, 100, 1L
        );

        assertEquals("Title", request.getTitle());
        assertEquals("Description", request.getDescription());
        assertEquals("Skills", request.getSkills());
        assertEquals("Category", request.getCategory());
        assertEquals(10, request.getDuration());
        assertEquals(5, request.getVacancies());
        assertEquals(100, request.getPoints());
        assertEquals(1L, request.getPromoterId());
    }

    @Test
    void testCreateOpportunityRequestNoArgsConstructor() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();

        assertNull(request.getTitle());
        assertNull(request.getDescription());
        assertNull(request.getSkills());
        assertNull(request.getCategory());
        assertNull(request.getDuration());
        assertNull(request.getVacancies());
        assertNull(request.getPoints());
        assertNull(request.getPromoterId());
    }

    @Test
    void testCreateOpportunityRequestEqualsAndHashCode() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description", "Skills", "Category", 10, 5, 100, 1L
        );
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description", "Skills", "Category", 10, 5, 100, 1L
        );
        CreateOpportunityRequest request3 = new CreateOpportunityRequest(
                "Different", "Description", "Skills", "Category", 10, 5, 100, 1L
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testCreateOpportunityRequestToString() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Title", "Description", "Skills", "Category", 10, 5, 100, 1L
        );

        String toString = request.toString();
        assertTrue(toString.contains("Title"));
        assertTrue(toString.contains("Description"));
    }

    // OpportunityResponse Tests
    @Test
    void testOpportunityResponseFromEntity() {
        OpportunityResponse response = OpportunityResponse.fromEntity(testOpportunity);

        assertEquals(1L, response.getId());
        assertEquals("Test Opportunity", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals("Java, Spring", response.getSkills());
        assertEquals("Tecnologia", response.getCategory());
        assertEquals(10, response.getDuration());
        assertEquals(5, response.getVacancies());
        assertEquals(100, response.getPoints());
        assertEquals(1L, response.getPromoterId());
        assertEquals("Test Promoter", response.getPromoterName());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), response.getCreatedAt());
    }

    @Test
    void testOpportunityResponseGettersSetters() {
        OpportunityResponse response = new OpportunityResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setId(2L);
        response.setTitle("Another Opportunity");
        response.setDescription("Another Description");
        response.setSkills("React, Node");
        response.setCategory("Tecnologia");
        response.setDuration(20);
        response.setVacancies(8);
        response.setPoints(300);
        response.setPromoterId(3L);
        response.setPromoterName("Another Promoter");
        response.setCreatedAt(now);

        assertEquals(2L, response.getId());
        assertEquals("Another Opportunity", response.getTitle());
        assertEquals("Another Description", response.getDescription());
        assertEquals("React, Node", response.getSkills());
        assertEquals("Tecnologia", response.getCategory());
        assertEquals(20, response.getDuration());
        assertEquals(8, response.getVacancies());
        assertEquals(300, response.getPoints());
        assertEquals(3L, response.getPromoterId());
        assertEquals("Another Promoter", response.getPromoterName());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void testOpportunityResponseAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        OpportunityResponse response = new OpportunityResponse(
                1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, 1L, "Promoter", now
        );

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Desc", response.getDescription());
        assertEquals("Skills", response.getSkills());
        assertEquals("Cat", response.getCategory());
        assertEquals(10, response.getDuration());
        assertEquals(5, response.getVacancies());
        assertEquals(100, response.getPoints());
        assertEquals(1L, response.getPromoterId());
        assertEquals("Promoter", response.getPromoterName());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void testOpportunityResponseEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        OpportunityResponse response1 = new OpportunityResponse(
                1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, 1L, "Promoter", now
        );
        OpportunityResponse response2 = new OpportunityResponse(
                1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, 1L, "Promoter", now
        );

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testOpportunityResponseToString() {
        OpportunityResponse response = OpportunityResponse.fromEntity(testOpportunity);
        String toString = response.toString();

        assertTrue(toString.contains("Test Opportunity"));
        assertTrue(toString.contains("Tecnologia"));
    }

    // OpportunityFilterRequest Tests
    @Test
    void testOpportunityFilterRequestGettersSetters() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest();

        filter.setCategory("Saude");
        filter.setSkills("First Aid");
        filter.setMinDuration(5);
        filter.setMaxDuration(30);

        assertEquals("Saude", filter.getCategory());
        assertEquals("First Aid", filter.getSkills());
        assertEquals(5, filter.getMinDuration());
        assertEquals(30, filter.getMaxDuration());
    }

    @Test
    void testOpportunityFilterRequestAllArgsConstructor() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest(
                "Educacao", "Teaching", 10, 20
        );

        assertEquals("Educacao", filter.getCategory());
        assertEquals("Teaching", filter.getSkills());
        assertEquals(10, filter.getMinDuration());
        assertEquals(20, filter.getMaxDuration());
    }

    @Test
    void testOpportunityFilterRequestNoArgsConstructor() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest();

        assertNull(filter.getCategory());
        assertNull(filter.getSkills());
        assertNull(filter.getMinDuration());
        assertNull(filter.getMaxDuration());
    }

    @Test
    void testOpportunityFilterRequestEqualsAndHashCode() {
        OpportunityFilterRequest filter1 = new OpportunityFilterRequest("Cat", "Skills", 5, 10);
        OpportunityFilterRequest filter2 = new OpportunityFilterRequest("Cat", "Skills", 5, 10);
        OpportunityFilterRequest filter3 = new OpportunityFilterRequest("Different", "Skills", 5, 10);

        assertEquals(filter1, filter2);
        assertEquals(filter1.hashCode(), filter2.hashCode());
        assertNotEquals(filter1, filter3);
    }

    @Test
    void testOpportunityFilterRequestToString() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest("Cat", "Skills", 5, 10);
        String toString = filter.toString();

        assertTrue(toString.contains("Cat"));
        assertTrue(toString.contains("Skills"));
    }

    @Test
    void testOpportunityFilterRequestWithNullValues() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest(null, null, null, null);

        assertNull(filter.getCategory());
        assertNull(filter.getSkills());
        assertNull(filter.getMinDuration());
        assertNull(filter.getMaxDuration());
    }

    @Test
    void testOpportunityFilterRequestPartialValues() {
        OpportunityFilterRequest filter = new OpportunityFilterRequest();
        filter.setCategory("Tech");
        filter.setMinDuration(5);

        assertEquals("Tech", filter.getCategory());
        assertNull(filter.getSkills());
        assertEquals(5, filter.getMinDuration());
        assertNull(filter.getMaxDuration());
    }
}
