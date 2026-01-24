package com.example.demo.entity;

import com.example.demo.dto.OpportunityFilterRequest;
import com.example.demo.dto.OpportunityResponse;
import com.example.demo.dto.CreateOpportunityRequest;
import com.example.demo.dto.UpdateOpportunityRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OpportunityEntityTest {

    @Test
    void testOpportunityAllArgsConstructor() {
        Promoter promoter = new Promoter(1L, "Test Promoter", "promoter@test.com", "Test Org", null);
        LocalDateTime now = LocalDateTime.now();

        Opportunity opportunity = new Opportunity(1L, "Title", "Description", "Java", "Tech", 10, 5, 100, promoter, now);

        assertEquals(1L, opportunity.getId());
        assertEquals("Title", opportunity.getTitle());
        assertEquals("Description", opportunity.getDescription());
        assertEquals("Java", opportunity.getSkills());
        assertEquals("Tech", opportunity.getCategory());
        assertEquals(10, opportunity.getDuration());
        assertEquals(5, opportunity.getVacancies());
        assertEquals(100, opportunity.getPoints());
        assertEquals(promoter, opportunity.getPromoter());
        assertEquals(now, opportunity.getCreatedAt());
    }

    @Test
    void testOpportunityNoArgsConstructorAndSetters() {
        Opportunity opportunity = new Opportunity();
        Promoter promoter = new Promoter(1L, "Test", "test@test.com", "Org", null);
        LocalDateTime now = LocalDateTime.now();

        opportunity.setId(1L);
        opportunity.setTitle("New Title");
        opportunity.setDescription("New Description");
        opportunity.setSkills("Python");
        opportunity.setCategory("Education");
        opportunity.setDuration(20);
        opportunity.setVacancies(10);
        opportunity.setPoints(200);
        opportunity.setPromoter(promoter);
        opportunity.setCreatedAt(now);

        assertEquals(1L, opportunity.getId());
        assertEquals("New Title", opportunity.getTitle());
        assertEquals("New Description", opportunity.getDescription());
        assertEquals("Python", opportunity.getSkills());
        assertEquals("Education", opportunity.getCategory());
        assertEquals(20, opportunity.getDuration());
        assertEquals(10, opportunity.getVacancies());
        assertEquals(200, opportunity.getPoints());
        assertEquals(promoter, opportunity.getPromoter());
        assertEquals(now, opportunity.getCreatedAt());
    }

    @Test
    void testOpportunityOnCreate() {
        Opportunity opportunity = new Opportunity();
        assertNull(opportunity.getCreatedAt());

        opportunity.onCreate();

        assertNotNull(opportunity.getCreatedAt());
    }

    @Test
    void testOpportunityResponseFromEntity() {
        Promoter promoter = new Promoter(1L, "Promoter Name", "promoter@test.com", "Org", null);
        LocalDateTime now = LocalDateTime.now();

        Opportunity opportunity = new Opportunity(10L, "Opp Title", "Description", "Java", "Tech", 15, 8, 150, promoter, now);

        OpportunityResponse response = OpportunityResponse.fromEntity(opportunity);

        assertEquals(10L, response.getId());
        assertEquals("Opp Title", response.getTitle());
        assertEquals("Description", response.getDescription());
        assertEquals("Java", response.getSkills());
        assertEquals("Tech", response.getCategory());
        assertEquals(15, response.getDuration());
        assertEquals(8, response.getVacancies());
        assertEquals(150, response.getPoints());
        assertEquals(1L, response.getPromoterId());
        assertEquals("Promoter Name", response.getPromoterName());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void testOpportunityResponseAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        OpportunityResponse response = new OpportunityResponse(1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, 2L, "Promoter", now);

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Desc", response.getDescription());
        assertEquals("Skills", response.getSkills());
        assertEquals("Cat", response.getCategory());
        assertEquals(10, response.getDuration());
        assertEquals(5, response.getVacancies());
        assertEquals(100, response.getPoints());
        assertEquals(2L, response.getPromoterId());
        assertEquals("Promoter", response.getPromoterName());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void testOpportunityResponseNoArgsConstructorAndSetters() {
        OpportunityResponse response = new OpportunityResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setId(1L);
        response.setTitle("Title");
        response.setDescription("Desc");
        response.setSkills("Skills");
        response.setCategory("Cat");
        response.setDuration(10);
        response.setVacancies(5);
        response.setPoints(100);
        response.setPromoterId(2L);
        response.setPromoterName("Promoter");
        response.setCreatedAt(now);

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Desc", response.getDescription());
        assertEquals("Skills", response.getSkills());
        assertEquals("Cat", response.getCategory());
        assertEquals(10, response.getDuration());
        assertEquals(5, response.getVacancies());
        assertEquals(100, response.getPoints());
        assertEquals(2L, response.getPromoterId());
        assertEquals("Promoter", response.getPromoterName());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void testOpportunityFilterRequestAllArgsConstructor() {
        OpportunityFilterRequest request = new OpportunityFilterRequest("Tech", "Java", 5, 20);

        assertEquals("Tech", request.getCategory());
        assertEquals("Java", request.getSkills());
        assertEquals(5, request.getMinDuration());
        assertEquals(20, request.getMaxDuration());
    }

    @Test
    void testOpportunityFilterRequestNoArgsConstructorAndSetters() {
        OpportunityFilterRequest request = new OpportunityFilterRequest();

        request.setCategory("Education");
        request.setSkills("Teaching");
        request.setMinDuration(10);
        request.setMaxDuration(30);

        assertEquals("Education", request.getCategory());
        assertEquals("Teaching", request.getSkills());
        assertEquals(10, request.getMinDuration());
        assertEquals(30, request.getMaxDuration());
    }

    @Test
    void testCreateOpportunityRequestAllArgsAndSetters() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();

        request.setTitle("New Opportunity");
        request.setDescription("This is a new opportunity description");
        request.setSkills("Communication");
        request.setCategory("Social");
        request.setDuration(15);
        request.setVacancies(10);
        request.setPoints(50);
        request.setPromoterId(1L);

        assertEquals("New Opportunity", request.getTitle());
        assertEquals("This is a new opportunity description", request.getDescription());
        assertEquals("Communication", request.getSkills());
        assertEquals("Social", request.getCategory());
        assertEquals(15, request.getDuration());
        assertEquals(10, request.getVacancies());
        assertEquals(50, request.getPoints());
        assertEquals(1L, request.getPromoterId());
    }

    @Test
    void testUpdateOpportunityRequestSetters() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();

        request.setTitle("Updated Title");
        request.setDescription("Updated description text");
        request.setSkills("Updated Skills");
        request.setCategory("Updated Category");
        request.setDuration(25);
        request.setVacancies(15);
        request.setPoints(200);

        assertEquals("Updated Title", request.getTitle());
        assertEquals("Updated description text", request.getDescription());
        assertEquals("Updated Skills", request.getSkills());
        assertEquals("Updated Category", request.getCategory());
        assertEquals(25, request.getDuration());
        assertEquals(15, request.getVacancies());
        assertEquals(200, request.getPoints());
    }

    @Test
    void testPromoterAllArgsConstructor() {
        Promoter promoter = new Promoter(1L, "Test Name", "test@email.com", "Test Organization", null);

        assertEquals(1L, promoter.getId());
        assertEquals("Test Name", promoter.getName());
        assertEquals("test@email.com", promoter.getEmail());
        assertEquals("Test Organization", promoter.getOrganization());
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
        assertEquals("new@email.com", promoter.getEmail());
        assertEquals("New Org", promoter.getOrganization());
    }

    // Lombok equals/hashCode/toString tests for Opportunity
    @Test
    void testOpportunityEquals() {
        Promoter promoter = new Promoter(1L, "Test", "test@test.com", "Org", null);
        LocalDateTime now = LocalDateTime.now();

        Opportunity o1 = new Opportunity(1L, "Title", "Desc", "Java", "Tech", 10, 5, 100, promoter, now);
        Opportunity o2 = new Opportunity(1L, "Title", "Desc", "Java", "Tech", 10, 5, 100, promoter, now);
        Opportunity o3 = new Opportunity(2L, "Other", "Other", "Python", "Edu", 20, 10, 200, promoter, now);

        assertEquals(o1, o2);
        assertNotEquals(o1, o3);
        assertEquals(o1, o1);
        assertNotEquals(o1, null);
        assertNotEquals(o1, "string");
    }

    @Test
    void testOpportunityHashCode() {
        Promoter promoter = new Promoter(1L, "Test", "test@test.com", "Org", null);
        LocalDateTime now = LocalDateTime.now();

        Opportunity o1 = new Opportunity(1L, "Title", "Desc", "Java", "Tech", 10, 5, 100, promoter, now);
        Opportunity o2 = new Opportunity(1L, "Title", "Desc", "Java", "Tech", 10, 5, 100, promoter, now);

        assertEquals(o1.hashCode(), o2.hashCode());
    }

    @Test
    void testOpportunityToString() {
        Promoter promoter = new Promoter(1L, "Test", "test@test.com", "Org", null);
        LocalDateTime now = LocalDateTime.now();

        Opportunity opportunity = new Opportunity(1L, "Test Title", "Desc", "Java", "Tech", 10, 5, 100, promoter, now);
        String str = opportunity.toString();

        assertTrue(str.contains("Test Title"));
    }

    // Lombok equals/hashCode/toString tests for Promoter
    @Test
    void testPromoterEquals() {
        Promoter p1 = new Promoter(1L, "Name", "email@test.com", "Org", null);
        Promoter p2 = new Promoter(1L, "Name", "email@test.com", "Org", null);
        Promoter p3 = new Promoter(2L, "Other", "other@test.com", "Other Org", null);

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1, p1);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "string");
    }

    @Test
    void testPromoterHashCode() {
        Promoter p1 = new Promoter(1L, "Name", "email@test.com", "Org", null);
        Promoter p2 = new Promoter(1L, "Name", "email@test.com", "Org", null);

        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testPromoterToString() {
        Promoter promoter = new Promoter(1L, "Test Name", "test@email.com", "Test Org", null);
        String str = promoter.toString();

        assertTrue(str.contains("Test Name"));
    }

    // Lombok equals/hashCode/toString tests for OpportunityResponse
    @Test
    void testOpportunityResponseEquals() {
        LocalDateTime now = LocalDateTime.now();

        OpportunityResponse r1 = new OpportunityResponse(1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, 2L, "Promoter", now);
        OpportunityResponse r2 = new OpportunityResponse(1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, 2L, "Promoter", now);
        OpportunityResponse r3 = new OpportunityResponse(2L, "Other", "Other", "Other", "Other", 20, 10, 200, 3L, "Other", now);

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1, r1);
        assertNotEquals(r1, null);
        assertNotEquals(r1, "string");
    }

    @Test
    void testOpportunityResponseHashCode() {
        LocalDateTime now = LocalDateTime.now();

        OpportunityResponse r1 = new OpportunityResponse(1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, 2L, "Promoter", now);
        OpportunityResponse r2 = new OpportunityResponse(1L, "Title", "Desc", "Skills", "Cat", 10, 5, 100, 2L, "Promoter", now);

        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testOpportunityResponseToString() {
        LocalDateTime now = LocalDateTime.now();

        OpportunityResponse response = new OpportunityResponse(1L, "Test Title", "Desc", "Skills", "Cat", 10, 5, 100, 2L, "Promoter", now);
        String str = response.toString();

        assertTrue(str.contains("Test Title"));
    }

    // Lombok equals/hashCode/toString tests for OpportunityFilterRequest
    @Test
    void testOpportunityFilterRequestEquals() {
        OpportunityFilterRequest r1 = new OpportunityFilterRequest("Tech", "Java", 5, 20);
        OpportunityFilterRequest r2 = new OpportunityFilterRequest("Tech", "Java", 5, 20);
        OpportunityFilterRequest r3 = new OpportunityFilterRequest("Edu", "Python", 10, 30);

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1, r1);
        assertNotEquals(r1, null);
        assertNotEquals(r1, "string");
    }

    @Test
    void testOpportunityFilterRequestHashCode() {
        OpportunityFilterRequest r1 = new OpportunityFilterRequest("Tech", "Java", 5, 20);
        OpportunityFilterRequest r2 = new OpportunityFilterRequest("Tech", "Java", 5, 20);

        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testOpportunityFilterRequestToString() {
        OpportunityFilterRequest request = new OpportunityFilterRequest("Technology", "Java", 5, 20);
        String str = request.toString();

        assertTrue(str.contains("Technology"));
    }

    // Lombok tests for CreateOpportunityRequest
    @Test
    void testCreateOpportunityRequestEquals() {
        CreateOpportunityRequest r1 = new CreateOpportunityRequest();
        r1.setTitle("Title");
        r1.setDescription("Description text");
        r1.setSkills("Skills");
        r1.setCategory("Cat");
        r1.setDuration(10);
        r1.setVacancies(5);
        r1.setPoints(100);
        r1.setPromoterId(1L);

        CreateOpportunityRequest r2 = new CreateOpportunityRequest();
        r2.setTitle("Title");
        r2.setDescription("Description text");
        r2.setSkills("Skills");
        r2.setCategory("Cat");
        r2.setDuration(10);
        r2.setVacancies(5);
        r2.setPoints(100);
        r2.setPromoterId(1L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testCreateOpportunityRequestToString() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Test Title");
        String str = request.toString();

        assertTrue(str.contains("Test Title"));
    }

    // Lombok tests for UpdateOpportunityRequest
    @Test
    void testUpdateOpportunityRequestEquals() {
        UpdateOpportunityRequest r1 = new UpdateOpportunityRequest();
        r1.setTitle("Title");
        r1.setDescription("Description text");
        r1.setSkills("Skills");
        r1.setCategory("Cat");
        r1.setDuration(10);
        r1.setVacancies(5);
        r1.setPoints(100);

        UpdateOpportunityRequest r2 = new UpdateOpportunityRequest();
        r2.setTitle("Title");
        r2.setDescription("Description text");
        r2.setSkills("Skills");
        r2.setCategory("Cat");
        r2.setDuration(10);
        r2.setVacancies(5);
        r2.setPoints(100);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testUpdateOpportunityRequestToString() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Update Title");
        String str = request.toString();

        assertTrue(str.contains("Update Title"));
    }

    // Test for Promoter with opportunities list
    @Test
    void testPromoterWithOpportunities() {
        Promoter promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Promoter");
        promoter.setEmail("test@test.com");
        promoter.setOrganization("Test Org");

        assertNotNull(promoter.getOpportunities());
    }
}
