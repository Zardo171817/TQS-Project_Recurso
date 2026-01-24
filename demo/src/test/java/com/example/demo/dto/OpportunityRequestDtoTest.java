package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OpportunityRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // CreateOpportunityRequest Tests
    @Test
    void whenCreateOpportunityRequestValid_thenNoViolations() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Valid Title",
                "Valid Description that is long enough",
                "Java, Spring",
                "Tecnologia",
                10,
                5,
                100,
                1L
        );

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenCreateOpportunityRequestBlankTitle_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title")));
    }

    @Test
    void whenCreateOpportunityRequestShortDescription_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Short");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description")));
    }

    @Test
    void whenCreateOpportunityRequestNullPromoterId_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(null);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Promoter ID")));
    }

    @Test
    void whenCreateOpportunityRequestNegativeDuration_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(-1);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Duration")));
    }

    @Test
    void whenCreateOpportunityRequestNegativeVacancies_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(-1);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Vacancies")));
    }

    @Test
    void whenCreateOpportunityRequestNegativePoints_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(-1);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Points")));
    }

    @Test
    void whenCreateOpportunityRequestZeroPoints_thenNoViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(0);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    // UpdateOpportunityRequest Tests
    @Test
    void whenUpdateOpportunityRequestValid_thenNoViolations() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest(
                "Valid Title",
                "Valid Description that is long enough",
                "Java, Spring",
                "Tecnologia",
                10,
                5,
                100
        );

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenUpdateOpportunityRequestBlankTitle_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title")));
    }

    @Test
    void whenUpdateOpportunityRequestBlankSkills_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Skills")));
    }

    @Test
    void whenUpdateOpportunityRequestBlankCategory_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category")));
    }

    @Test
    void whenUpdateOpportunityRequestNullDuration_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(null);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Duration")));
    }

    // Equals and HashCode Tests
    @Test
    void whenTwoCreateRequestsWithSameValues_thenEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void whenTwoUpdateRequestsWithSameValues_thenEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100);
        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void whenCreateRequestGetters_thenReturnCorrectValues() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Title", "Description text", "Skills", "Category", 10, 5, 100, 1L);

        assertEquals("Title", request.getTitle());
        assertEquals("Description text", request.getDescription());
        assertEquals("Skills", request.getSkills());
        assertEquals("Category", request.getCategory());
        assertEquals(10, request.getDuration());
        assertEquals(5, request.getVacancies());
        assertEquals(100, request.getPoints());
        assertEquals(1L, request.getPromoterId());
    }

    @Test
    void whenUpdateRequestGetters_thenReturnCorrectValues() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest(
                "Title", "Description text", "Skills", "Category", 10, 5, 100);

        assertEquals("Title", request.getTitle());
        assertEquals("Description text", request.getDescription());
        assertEquals("Skills", request.getSkills());
        assertEquals("Category", request.getCategory());
        assertEquals(10, request.getDuration());
        assertEquals(5, request.getVacancies());
        assertEquals(100, request.getPoints());
    }
}
