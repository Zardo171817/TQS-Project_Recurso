package com.example.demo.entity;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.CreateApplicationRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class ApplicationEntityTest {

    @Test
    void testVolunteerAllArgsConstructor() {
        Volunteer volunteer = new Volunteer(1L, "John Doe", "john@test.com", "123456789", "Java, Python");

        assertEquals(1L, volunteer.getId());
        assertEquals("John Doe", volunteer.getName());
        assertEquals("john@test.com", volunteer.getEmail());
        assertEquals("123456789", volunteer.getPhone());
        assertEquals("Java, Python", volunteer.getSkills());
    }

    @Test
    void testApplicationAllArgsConstructor() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        opportunity.setTitle("Test Opp");
        LocalDateTime now = LocalDateTime.now();

        Application application = new Application(1L, volunteer, opportunity, ApplicationStatus.ACCEPTED, "Motivation", now);

        assertEquals(1L, application.getId());
        assertEquals(volunteer, application.getVolunteer());
        assertEquals(opportunity, application.getOpportunity());
        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
        assertEquals("Motivation", application.getMotivation());
        assertEquals(now, application.getAppliedAt());
    }

    @Test
    void testApplicationOnCreateSetsDefaults() {
        Application application = new Application();
        assertNull(application.getAppliedAt());

        application.onCreate();

        assertNotNull(application.getAppliedAt());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
    }

    @Test
    void testApplicationOnCreatePreservesExistingStatus() {
        Application application = new Application();
        application.setStatus(ApplicationStatus.ACCEPTED);

        application.onCreate();

        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
        assertNotNull(application.getAppliedAt());
    }

    @Test
    void testCreateApplicationRequestAllArgsConstructor() {
        CreateApplicationRequest request = new CreateApplicationRequest(
                1L, "John", "john@test.com", "999888777", "Skills", "I want to help"
        );

        assertEquals(1L, request.getOpportunityId());
        assertEquals("John", request.getVolunteerName());
        assertEquals("john@test.com", request.getVolunteerEmail());
        assertEquals("999888777", request.getVolunteerPhone());
        assertEquals("Skills", request.getVolunteerSkills());
        assertEquals("I want to help", request.getMotivation());
    }

    @Test
    void testApplicationResponseAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse response = new ApplicationResponse(
                1L, 2L, "John", "john@test.com", 3L, "Opp Title",
                ApplicationStatus.PENDING, "Motivation", now
        );

        assertEquals(1L, response.getId());
        assertEquals(2L, response.getVolunteerId());
        assertEquals("John", response.getVolunteerName());
        assertEquals("john@test.com", response.getVolunteerEmail());
        assertEquals(3L, response.getOpportunityId());
        assertEquals("Opp Title", response.getOpportunityTitle());
        assertEquals(ApplicationStatus.PENDING, response.getStatus());
        assertEquals("Motivation", response.getMotivation());
        assertEquals(now, response.getAppliedAt());
    }

    @Test
    void testApplicationResponseFromEntity() {
        Volunteer volunteer = new Volunteer(1L, "Vol Name", "vol@test.com", "123", "Java");
        Opportunity opportunity = new Opportunity();
        opportunity.setId(2L);
        opportunity.setTitle("Opp Title");

        Application application = new Application();
        application.setId(10L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setMotivation("My motivation");
        application.setAppliedAt(LocalDateTime.of(2026, 1, 24, 10, 0));

        ApplicationResponse response = ApplicationResponse.fromEntity(application);

        assertEquals(10L, response.getId());
        assertEquals(1L, response.getVolunteerId());
        assertEquals("Vol Name", response.getVolunteerName());
        assertEquals("vol@test.com", response.getVolunteerEmail());
        assertEquals(2L, response.getOpportunityId());
        assertEquals("Opp Title", response.getOpportunityTitle());
        assertEquals(ApplicationStatus.ACCEPTED, response.getStatus());
        assertEquals("My motivation", response.getMotivation());
    }

    // Testes para métodos Lombok (equals, hashCode, toString)
    @Test
    void testVolunteerEqualsAndHashCode() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v2 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v3 = new Volunteer(2L, "Jane", "jane@test.com", "456", "Python");

        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1, v3);
        assertNotEquals(v1, null);
        assertNotEquals(v1, "string");
    }

    @Test
    void testVolunteerToString() {
        Volunteer volunteer = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        String str = volunteer.toString();

        assertThat(str).contains("John");
        assertThat(str).contains("john@test.com");
    }

    @Test
    void testApplicationEqualsAndHashCode() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();

        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a2 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a3 = new Application(2L, volunteer, opportunity, ApplicationStatus.ACCEPTED, "Other", now);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1, a3);
    }

    @Test
    void testCreateApplicationRequestEqualsAndHashCode() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r3 = new CreateApplicationRequest(2L, "Jane", "jane@test.com", "456", "Python", "Other");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void testApplicationResponseEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r3 = new ApplicationResponse(2L, 3L, "Jane", "jane@test.com", 4L, "Other", ApplicationStatus.ACCEPTED, "Other", now);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
    }

    @Test
    void testApplicationStatusValues() {
        ApplicationStatus[] values = ApplicationStatus.values();
        assertEquals(3, values.length);
        assertEquals(ApplicationStatus.PENDING, ApplicationStatus.valueOf("PENDING"));
        assertEquals(ApplicationStatus.ACCEPTED, ApplicationStatus.valueOf("ACCEPTED"));
        assertEquals(ApplicationStatus.REJECTED, ApplicationStatus.valueOf("REJECTED"));
    }

    // Testes para setters individuais - CreateApplicationRequest
    @Test
    void testCreateApplicationRequestNoArgsConstructorAndSetters() {
        CreateApplicationRequest request = new CreateApplicationRequest();

        request.setOpportunityId(5L);
        request.setVolunteerName("Maria");
        request.setVolunteerEmail("maria@test.com");
        request.setVolunteerPhone("912345678");
        request.setVolunteerSkills("Communication, Leadership");
        request.setMotivation("I want to contribute");

        assertEquals(5L, request.getOpportunityId());
        assertEquals("Maria", request.getVolunteerName());
        assertEquals("maria@test.com", request.getVolunteerEmail());
        assertEquals("912345678", request.getVolunteerPhone());
        assertEquals("Communication, Leadership", request.getVolunteerSkills());
        assertEquals("I want to contribute", request.getMotivation());
    }

    @Test
    void testCreateApplicationRequestToString() {
        CreateApplicationRequest request = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        String str = request.toString();

        assertThat(str).contains("John");
        assertThat(str).contains("john@test.com");
        assertThat(str).contains("Java");
    }

    // Testes para setters individuais - ApplicationResponse
    @Test
    void testApplicationResponseNoArgsConstructorAndSetters() {
        ApplicationResponse response = new ApplicationResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setId(10L);
        response.setVolunteerId(20L);
        response.setVolunteerName("Carlos");
        response.setVolunteerEmail("carlos@test.com");
        response.setOpportunityId(30L);
        response.setOpportunityTitle("Help Event");
        response.setStatus(ApplicationStatus.ACCEPTED);
        response.setMotivation("Quero ajudar");
        response.setAppliedAt(now);

        assertEquals(10L, response.getId());
        assertEquals(20L, response.getVolunteerId());
        assertEquals("Carlos", response.getVolunteerName());
        assertEquals("carlos@test.com", response.getVolunteerEmail());
        assertEquals(30L, response.getOpportunityId());
        assertEquals("Help Event", response.getOpportunityTitle());
        assertEquals(ApplicationStatus.ACCEPTED, response.getStatus());
        assertEquals("Quero ajudar", response.getMotivation());
        assertEquals(now, response.getAppliedAt());
    }

    @Test
    void testApplicationResponseToString() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse response = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        String str = response.toString();

        assertThat(str).contains("John");
        assertThat(str).contains("john@test.com");
        assertThat(str).contains("Title");
    }

    // Testes para setters individuais - Volunteer
    @Test
    void testVolunteerNoArgsConstructorAndSetters() {
        Volunteer volunteer = new Volunteer();

        volunteer.setId(15L);
        volunteer.setName("Pedro");
        volunteer.setEmail("pedro@test.com");
        volunteer.setPhone("987654321");
        volunteer.setSkills("Excel, Word");

        assertEquals(15L, volunteer.getId());
        assertEquals("Pedro", volunteer.getName());
        assertEquals("pedro@test.com", volunteer.getEmail());
        assertEquals("987654321", volunteer.getPhone());
        assertEquals("Excel, Word", volunteer.getSkills());
    }

    // Testes para setters individuais - Application
    @Test
    void testApplicationNoArgsConstructorAndSetters() {
        Application application = new Application();
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();

        application.setId(100L);
        application.setVolunteer(volunteer);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.REJECTED);
        application.setMotivation("My reason");
        application.setAppliedAt(now);

        assertEquals(100L, application.getId());
        assertEquals(volunteer, application.getVolunteer());
        assertEquals(opportunity, application.getOpportunity());
        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        assertEquals("My reason", application.getMotivation());
        assertEquals(now, application.getAppliedAt());
    }

    @Test
    void testApplicationToString() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application application = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);

        String str = application.toString();
        assertThat(str).contains("Motivation");
    }

    // Testes com valores null
    @Test
    void testVolunteerWithNullValues() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Test");
        volunteer.setEmail("test@test.com");
        volunteer.setPhone(null);
        volunteer.setSkills(null);

        assertNull(volunteer.getPhone());
        assertNull(volunteer.getSkills());
    }

    @Test
    void testCreateApplicationRequestWithNullOptionalFields() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(1L);
        request.setVolunteerName("Test");
        request.setVolunteerEmail("test@test.com");
        request.setVolunteerPhone(null);
        request.setVolunteerSkills(null);
        request.setMotivation(null);

        assertNull(request.getVolunteerPhone());
        assertNull(request.getVolunteerSkills());
        assertNull(request.getMotivation());
    }

    @Test
    void testApplicationResponseWithNullMotivation() {
        ApplicationResponse response = new ApplicationResponse();
        response.setMotivation(null);
        assertNull(response.getMotivation());
    }

    // Testes adicionais para cobertura de Lombok equals/hashCode edge cases
    @Test
    void testVolunteerEqualsWithSameObject() {
        Volunteer volunteer = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        assertEquals(volunteer, volunteer);
    }

    @Test
    void testApplicationEqualsWithSameObject() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application application = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        assertEquals(application, application);
    }

    @Test
    void testCreateApplicationRequestEqualsWithSameObject() {
        CreateApplicationRequest request = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        assertEquals(request, request);
    }

    @Test
    void testApplicationResponseEqualsWithSameObject() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse response = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertEquals(response, response);
    }

    @Test
    void testVolunteerEqualsWithDifferentClass() {
        Volunteer volunteer = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        assertNotEquals(volunteer, new Object());
    }

    @Test
    void testApplicationEqualsWithNull() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application application = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(application, null);
    }

    @Test
    void testCreateApplicationRequestEqualsWithNull() {
        CreateApplicationRequest request = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        assertNotEquals(request, null);
    }

    @Test
    void testApplicationResponseEqualsWithNull() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse response = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(response, null);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentClass() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse response = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(response, "string");
    }

    @Test
    void testCreateApplicationRequestEqualsWithDifferentClass() {
        CreateApplicationRequest request = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        assertNotEquals(request, "string");
    }

    @Test
    void testApplicationEqualsWithDifferentClass() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application application = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(application, "string");
    }

    // Testes de hashCode consistency
    @Test
    void testVolunteerHashCodeConsistency() {
        Volunteer volunteer = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        int hash1 = volunteer.hashCode();
        int hash2 = volunteer.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testApplicationHashCodeConsistency() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application application = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        int hash1 = application.hashCode();
        int hash2 = application.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testCreateApplicationRequestHashCodeConsistency() {
        CreateApplicationRequest request = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        int hash1 = request.hashCode();
        int hash2 = request.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testApplicationResponseHashCodeConsistency() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse response = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        int hash1 = response.hashCode();
        int hash2 = response.hashCode();
        assertEquals(hash1, hash2);
    }

    // Testes para campos individuais diferentes em equals
    @Test
    void testVolunteerEqualsWithDifferentId() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v2 = new Volunteer(2L, "John", "john@test.com", "123", "Java");
        assertNotEquals(v1, v2);
    }

    @Test
    void testVolunteerEqualsWithDifferentName() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v2 = new Volunteer(1L, "Jane", "john@test.com", "123", "Java");
        assertNotEquals(v1, v2);
    }

    @Test
    void testVolunteerEqualsWithDifferentEmail() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v2 = new Volunteer(1L, "John", "jane@test.com", "123", "Java");
        assertNotEquals(v1, v2);
    }

    @Test
    void testVolunteerEqualsWithDifferentPhone() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v2 = new Volunteer(1L, "John", "john@test.com", "456", "Java");
        assertNotEquals(v1, v2);
    }

    @Test
    void testVolunteerEqualsWithDifferentSkills() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v2 = new Volunteer(1L, "John", "john@test.com", "123", "Python");
        assertNotEquals(v1, v2);
    }

    @Test
    void testCreateApplicationRequestEqualsWithDifferentOpportunityId() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(2L, "John", "john@test.com", "123", "Java", "Motivation");
        assertNotEquals(r1, r2);
    }

    @Test
    void testCreateApplicationRequestEqualsWithDifferentVolunteerName() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "Jane", "john@test.com", "123", "Java", "Motivation");
        assertNotEquals(r1, r2);
    }

    @Test
    void testCreateApplicationRequestEqualsWithDifferentVolunteerEmail() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "jane@test.com", "123", "Java", "Motivation");
        assertNotEquals(r1, r2);
    }

    @Test
    void testCreateApplicationRequestEqualsWithDifferentVolunteerPhone() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", "456", "Java", "Motivation");
        assertNotEquals(r1, r2);
    }

    @Test
    void testCreateApplicationRequestEqualsWithDifferentVolunteerSkills() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Python", "Motivation");
        assertNotEquals(r1, r2);
    }

    @Test
    void testCreateApplicationRequestEqualsWithDifferentMotivation() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Different");
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentId() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(2L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentVolunteerId() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 3L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentVolunteerName() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "Jane", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentVolunteerEmail() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "jane@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentOpportunityId() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 4L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentOpportunityTitle() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Other", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentStatus() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.ACCEPTED, "Motivation", now);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentMotivation() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Different", now);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsWithDifferentAppliedAt() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusDays(1);
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", later);
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationEqualsWithDifferentId() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a2 = new Application(2L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(a1, a2);
    }

    @Test
    void testApplicationEqualsWithDifferentVolunteer() {
        Volunteer v1 = new Volunteer(1L, "Test1", "test1@test.com", null, null);
        Volunteer v2 = new Volunteer(2L, "Test2", "test2@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application a1 = new Application(1L, v1, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a2 = new Application(1L, v2, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(a1, a2);
    }

    @Test
    void testApplicationEqualsWithDifferentOpportunity() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity o1 = new Opportunity();
        o1.setId(1L);
        Opportunity o2 = new Opportunity();
        o2.setId(2L);
        LocalDateTime now = LocalDateTime.now();
        Application a1 = new Application(1L, volunteer, o1, ApplicationStatus.PENDING, "Motivation", now);
        Application a2 = new Application(1L, volunteer, o2, ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(a1, a2);
    }

    @Test
    void testApplicationEqualsWithDifferentStatus() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a2 = new Application(1L, volunteer, opportunity, ApplicationStatus.ACCEPTED, "Motivation", now);
        assertNotEquals(a1, a2);
    }

    @Test
    void testApplicationEqualsWithDifferentMotivation() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation1", now);
        Application a2 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation2", now);
        assertNotEquals(a1, a2);
    }

    @Test
    void testApplicationEqualsWithDifferentAppliedAt() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusDays(1);
        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a2 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", later);
        assertNotEquals(a1, a2);
    }

    // Testes com campos null
    @Test
    void testVolunteerEqualsWithNullFields() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", null, null);
        Volunteer v2 = new Volunteer(1L, "John", "john@test.com", null, null);
        assertEquals(v1, v2);
    }

    @Test
    void testVolunteerHashCodeWithNullFields() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", null, null);
        Volunteer v2 = new Volunteer(1L, "John", "john@test.com", null, null);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void testCreateApplicationRequestEqualsWithNullFields() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", null, null, null);
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", null, null, null);
        assertEquals(r1, r2);
    }

    @Test
    void testCreateApplicationRequestHashCodeWithNullFields() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", null, null, null);
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", null, null, null);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testApplicationResponseEqualsWithNullFields() {
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, null, null);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, null, null);
        assertEquals(r1, r2);
    }

    @Test
    void testApplicationResponseHashCodeWithNullFields() {
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, null, null);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, null, null);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testApplicationEqualsWithNullMotivation() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, null, now);
        Application a2 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, null, now);
        assertEquals(a1, a2);
    }

    @Test
    void testApplicationHashCodeWithNullMotivation() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, null, now);
        Application a2 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, null, now);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    // Testes de toString contendo campos
    @Test
    void testVolunteerToStringContainsAllFields() {
        Volunteer volunteer = new Volunteer(1L, "John", "john@test.com", "123456", "Java");
        String str = volunteer.toString();
        assertThat(str).contains("1");
        assertThat(str).contains("John");
        assertThat(str).contains("john@test.com");
        assertThat(str).contains("123456");
        assertThat(str).contains("Java");
    }

    @Test
    void testCreateApplicationRequestToStringContainsAllFields() {
        CreateApplicationRequest request = new CreateApplicationRequest(1L, "John", "john@test.com", "123456", "Java", "Motivation");
        String str = request.toString();
        assertThat(str).contains("1");
        assertThat(str).contains("John");
        assertThat(str).contains("john@test.com");
        assertThat(str).contains("123456");
        assertThat(str).contains("Java");
        assertThat(str).contains("Motivation");
    }

    @Test
    void testApplicationResponseToStringContainsAllFields() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 24, 10, 0);
        ApplicationResponse response = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        String str = response.toString();
        assertThat(str).contains("1");
        assertThat(str).contains("2");
        assertThat(str).contains("John");
        assertThat(str).contains("john@test.com");
        assertThat(str).contains("3");
        assertThat(str).contains("Title");
        assertThat(str).contains("PENDING");
        assertThat(str).contains("Motivation");
    }

    // Testes para canEqual (implícito via subclasses scenario)
    @Test
    void testVolunteerEqualsSymmetry() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        Volunteer v2 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        assertTrue(v1.equals(v2) && v2.equals(v1));
    }

    @Test
    void testApplicationEqualsSymmetry() {
        Volunteer volunteer = new Volunteer(1L, "Test", "test@test.com", null, null);
        Opportunity opportunity = new Opportunity();
        opportunity.setId(1L);
        LocalDateTime now = LocalDateTime.now();
        Application a1 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        Application a2 = new Application(1L, volunteer, opportunity, ApplicationStatus.PENDING, "Motivation", now);
        assertTrue(a1.equals(a2) && a2.equals(a1));
    }

    @Test
    void testCreateApplicationRequestEqualsSymmetry() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        assertTrue(r1.equals(r2) && r2.equals(r1));
    }

    @Test
    void testApplicationResponseEqualsSymmetry() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertTrue(r1.equals(r2) && r2.equals(r1));
    }

    // Mais testes para getters individuais não testados
    @Test
    void testVolunteerGetId() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(999L);
        assertEquals(999L, volunteer.getId());
    }

    @Test
    void testApplicationGetId() {
        Application application = new Application();
        application.setId(999L);
        assertEquals(999L, application.getId());
    }

    @Test
    void testCreateApplicationRequestGetOpportunityId() {
        CreateApplicationRequest request = new CreateApplicationRequest();
        request.setOpportunityId(999L);
        assertEquals(999L, request.getOpportunityId());
    }

    @Test
    void testApplicationResponseGetId() {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(999L);
        assertEquals(999L, response.getId());
    }

    // Testes para Application default status
    @Test
    void testApplicationDefaultStatus() {
        Application application = new Application();
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
    }

    // Testes para valores vazios
    @Test
    void testVolunteerWithEmptyStrings() {
        Volunteer volunteer = new Volunteer(1L, "", "", "", "");
        assertEquals("", volunteer.getName());
        assertEquals("", volunteer.getEmail());
        assertEquals("", volunteer.getPhone());
        assertEquals("", volunteer.getSkills());
    }

    @Test
    void testCreateApplicationRequestWithEmptyStrings() {
        CreateApplicationRequest request = new CreateApplicationRequest(1L, "", "", "", "", "");
        assertEquals("", request.getVolunteerName());
        assertEquals("", request.getVolunteerEmail());
        assertEquals("", request.getVolunteerPhone());
        assertEquals("", request.getVolunteerSkills());
        assertEquals("", request.getMotivation());
    }

    @Test
    void testApplicationResponseWithEmptyStrings() {
        ApplicationResponse response = new ApplicationResponse(1L, 2L, "", "", 3L, "", ApplicationStatus.PENDING, "", null);
        assertEquals("", response.getVolunteerName());
        assertEquals("", response.getVolunteerEmail());
        assertEquals("", response.getOpportunityTitle());
        assertEquals("", response.getMotivation());
    }

    // Testes de null vs non-null em equals
    @Test
    void testVolunteerEqualsOneNullPhone() {
        Volunteer v1 = new Volunteer(1L, "John", "john@test.com", null, "Java");
        Volunteer v2 = new Volunteer(1L, "John", "john@test.com", "123", "Java");
        assertNotEquals(v1, v2);
    }

    @Test
    void testCreateApplicationRequestEqualsOneNullPhone() {
        CreateApplicationRequest r1 = new CreateApplicationRequest(1L, "John", "john@test.com", null, "Java", "Motivation");
        CreateApplicationRequest r2 = new CreateApplicationRequest(1L, "John", "john@test.com", "123", "Java", "Motivation");
        assertNotEquals(r1, r2);
    }

    @Test
    void testApplicationResponseEqualsOneNullMotivation() {
        LocalDateTime now = LocalDateTime.now();
        ApplicationResponse r1 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, null, now);
        ApplicationResponse r2 = new ApplicationResponse(1L, 2L, "John", "john@test.com", 3L, "Title", ApplicationStatus.PENDING, "Motivation", now);
        assertNotEquals(r1, r2);
    }
}
