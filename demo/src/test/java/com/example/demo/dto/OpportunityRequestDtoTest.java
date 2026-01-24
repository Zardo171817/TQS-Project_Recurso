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

    // Additional Update Request Validation Tests
    @Test
    void whenUpdateOpportunityRequestShortTitle_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("AB");
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
    void whenUpdateOpportunityRequestLongTitle_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("A".repeat(101));
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
    void whenUpdateOpportunityRequestShortDescription_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Short");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description")));
    }

    @Test
    void whenUpdateOpportunityRequestLongDescription_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("A".repeat(1001));
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description")));
    }

    @Test
    void whenUpdateOpportunityRequestShortSkills_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("AB");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Skills")));
    }

    @Test
    void whenUpdateOpportunityRequestLongSkills_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("A".repeat(201));
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Skills")));
    }

    @Test
    void whenUpdateOpportunityRequestShortCategory_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("AB");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category")));
    }

    @Test
    void whenUpdateOpportunityRequestLongCategory_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("A".repeat(101));
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category")));
    }

    @Test
    void whenUpdateOpportunityRequestZeroDuration_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(0);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Duration")));
    }

    @Test
    void whenUpdateOpportunityRequestNegativeDuration_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(-5);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Duration")));
    }

    @Test
    void whenUpdateOpportunityRequestNullVacancies_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(null);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Vacancies")));
    }

    @Test
    void whenUpdateOpportunityRequestZeroVacancies_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(0);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Vacancies")));
    }

    @Test
    void whenUpdateOpportunityRequestNegativeVacancies_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(-3);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Vacancies")));
    }

    @Test
    void whenUpdateOpportunityRequestNullPoints_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(null);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Points")));
    }

    @Test
    void whenUpdateOpportunityRequestNegativePoints_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(-10);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Points")));
    }

    @Test
    void whenUpdateOpportunityRequestZeroPoints_thenNoViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(0);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenUpdateOpportunityRequestNullTitle_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle(null);
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenUpdateOpportunityRequestNullDescription_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription(null);
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenUpdateOpportunityRequestNullSkills_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills(null);
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenUpdateOpportunityRequestNullCategory_thenViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory(null);
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenUpdateOpportunityRequestWithAllMaxValidValues_thenNoViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("A".repeat(100));
        request.setDescription("A".repeat(1000));
        request.setSkills("A".repeat(200));
        request.setCategory("A".repeat(100));
        request.setDuration(365);
        request.setVacancies(1000);
        request.setPoints(10000);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenUpdateOpportunityRequestWithAllMinValidValues_thenNoViolation() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("ABC");
        request.setDescription("A".repeat(10));
        request.setSkills("ABC");
        request.setCategory("ABC");
        request.setDuration(1);
        request.setVacancies(1);
        request.setPoints(0);

        Set<ConstraintViolation<UpdateOpportunityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenUpdateRequestNoArgsConstructor_thenAllFieldsNull() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();

        assertNull(request.getTitle());
        assertNull(request.getDescription());
        assertNull(request.getSkills());
        assertNull(request.getCategory());
        assertNull(request.getDuration());
        assertNull(request.getVacancies());
        assertNull(request.getPoints());
    }

    @Test
    void whenUpdateRequestSetters_thenGettersReturnCorrectValues() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest();
        request.setTitle("Test Title");
        request.setDescription("Test Description");
        request.setSkills("Test Skills");
        request.setCategory("Test Category");
        request.setDuration(15);
        request.setVacancies(8);
        request.setPoints(150);

        assertEquals("Test Title", request.getTitle());
        assertEquals("Test Description", request.getDescription());
        assertEquals("Test Skills", request.getSkills());
        assertEquals("Test Category", request.getCategory());
        assertEquals(15, request.getDuration());
        assertEquals(8, request.getVacancies());
        assertEquals(150, request.getPoints());
    }

    @Test
    void whenTwoUpdateRequestsWithDifferentValues_thenNotEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest(
                "Title1", "Description that is long enough", "Skills", "Category", 10, 5, 100);
        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest(
                "Title2", "Description that is long enough", "Skills", "Category", 10, 5, 100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenUpdateRequestToString_thenContainsAllFields() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest(
                "Title", "Description", "Skills", "Category", 10, 5, 100);

        String toString = request.toString();
        assertTrue(toString.contains("Title"));
        assertTrue(toString.contains("Description"));
        assertTrue(toString.contains("Skills"));
        assertTrue(toString.contains("Category"));
    }

    // Additional CreateOpportunityRequest Tests for Full Coverage
    @Test
    void whenCreateOpportunityRequestShortTitle_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("AB");
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
    void whenCreateOpportunityRequestLongTitle_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("A".repeat(101));
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
    void whenCreateOpportunityRequestLongDescription_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("A".repeat(1001));
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
    void whenCreateOpportunityRequestBlankSkills_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Skills")));
    }

    @Test
    void whenCreateOpportunityRequestShortSkills_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("AB");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Skills")));
    }

    @Test
    void whenCreateOpportunityRequestLongSkills_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("A".repeat(201));
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Skills")));
    }

    @Test
    void whenCreateOpportunityRequestBlankCategory_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category")));
    }

    @Test
    void whenCreateOpportunityRequestShortCategory_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("AB");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category")));
    }

    @Test
    void whenCreateOpportunityRequestLongCategory_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("A".repeat(101));
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category")));
    }

    @Test
    void whenCreateOpportunityRequestNullDuration_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(null);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Duration")));
    }

    @Test
    void whenCreateOpportunityRequestZeroDuration_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(0);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Duration")));
    }

    @Test
    void whenCreateOpportunityRequestNullVacancies_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(null);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Vacancies")));
    }

    @Test
    void whenCreateOpportunityRequestZeroVacancies_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(0);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Vacancies")));
    }

    @Test
    void whenCreateOpportunityRequestNullPoints_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(null);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Points")));
    }

    @Test
    void whenCreateOpportunityRequestNullTitle_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle(null);
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCreateOpportunityRequestNullDescription_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription(null);
        request.setSkills("Java");
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCreateOpportunityRequestNullSkills_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills(null);
        request.setCategory("Tech");
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCreateOpportunityRequestNullCategory_thenViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Valid Title");
        request.setDescription("Valid Description that is long enough");
        request.setSkills("Java");
        request.setCategory(null);
        request.setDuration(10);
        request.setVacancies(5);
        request.setPoints(100);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCreateOpportunityRequestWithAllMaxValidValues_thenNoViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("A".repeat(100));
        request.setDescription("A".repeat(1000));
        request.setSkills("A".repeat(200));
        request.setCategory("A".repeat(100));
        request.setDuration(365);
        request.setVacancies(1000);
        request.setPoints(10000);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenCreateOpportunityRequestWithAllMinValidValues_thenNoViolation() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("ABC");
        request.setDescription("A".repeat(10));
        request.setSkills("ABC");
        request.setCategory("ABC");
        request.setDuration(1);
        request.setVacancies(1);
        request.setPoints(0);
        request.setPromoterId(1L);

        Set<ConstraintViolation<CreateOpportunityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenCreateRequestNoArgsConstructor_thenAllFieldsNull() {
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
    void whenCreateRequestSetters_thenGettersReturnCorrectValues() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();
        request.setTitle("Test Title");
        request.setDescription("Test Description");
        request.setSkills("Test Skills");
        request.setCategory("Test Category");
        request.setDuration(15);
        request.setVacancies(8);
        request.setPoints(150);
        request.setPromoterId(99L);

        assertEquals("Test Title", request.getTitle());
        assertEquals("Test Description", request.getDescription());
        assertEquals("Test Skills", request.getSkills());
        assertEquals("Test Category", request.getCategory());
        assertEquals(15, request.getDuration());
        assertEquals(8, request.getVacancies());
        assertEquals(150, request.getPoints());
        assertEquals(99L, request.getPromoterId());
    }

    @Test
    void whenTwoCreateRequestsWithDifferentValues_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title1", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title2", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenTwoCreateRequestsWithDifferentPromoterId_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 2L);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenCreateRequestToString_thenContainsAllFields() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Title", "Description", "Skills", "Category", 10, 5, 100, 1L);

        String toString = request.toString();
        assertTrue(toString.contains("Title"));
        assertTrue(toString.contains("Description"));
        assertTrue(toString.contains("Skills"));
        assertTrue(toString.contains("Category"));
        assertTrue(toString.contains("promoterId"));
    }

    // Equals edge cases for better branch coverage
    @Test
    void whenCreateRequestEqualsNull_thenFalse() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);

        assertNotEquals(null, request);
    }

    @Test
    void whenCreateRequestEqualsDifferentType_thenFalse() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);

        assertNotEquals("string", request);
    }

    @Test
    void whenCreateRequestEqualsSelf_thenTrue() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);

        assertEquals(request, request);
    }

    @Test
    void whenUpdateRequestEqualsNull_thenFalse() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100);

        assertNotEquals(null, request);
    }

    @Test
    void whenUpdateRequestEqualsDifferentType_thenFalse() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100);

        assertNotEquals("string", request);
    }

    @Test
    void whenUpdateRequestEqualsSelf_thenTrue() {
        UpdateOpportunityRequest request = new UpdateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100);

        assertEquals(request, request);
    }

    @Test
    void whenCreateRequestWithNullPromoterIdComparedToNonNull_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description that is long enough");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);
        request1.setPromoterId(null);

        CreateOpportunityRequest request2 = new CreateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description that is long enough");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);
        request2.setPromoterId(1L);

        assertNotEquals(request1, request2);
        assertNotEquals(request2, request1);
    }

    @Test
    void whenCreateRequestsWithBothNullPromoterId_thenEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description that is long enough");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);
        request1.setPromoterId(null);

        CreateOpportunityRequest request2 = new CreateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description that is long enough");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);
        request2.setPromoterId(null);

        assertEquals(request1, request2);
    }

    // Tests for fields with null values in equals comparison
    @Test
    void whenRequestsWithDifferentNullFields_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest();
        request1.setTitle(null);
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);
        request1.setPromoterId(1L);

        CreateOpportunityRequest request2 = new CreateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);
        request2.setPromoterId(1L);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenUpdateRequestsWithNullDescription_thenNotEqualsNonNull() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription(null);
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenUpdateRequestsWithNullSkills_thenNotEqualsNonNull() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills(null);
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenUpdateRequestsWithNullCategory_thenNotEqualsNonNull() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory(null);
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenUpdateRequestsWithNullDuration_thenNotEqualsNonNull() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(null);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenUpdateRequestsWithNullVacancies_thenNotEqualsNonNull() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(null);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenUpdateRequestsWithNullPoints_thenNotEqualsNonNull() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(null);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    // HashCode with null values
    @Test
    void whenRequestsWithAllNullValues_thenHashCodeConsistent() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest();
        CreateOpportunityRequest request2 = new CreateOpportunityRequest();

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void whenUpdateRequestsWithAllNullValues_thenHashCodeConsistent() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    // Can equal tests for Lombok
    @Test
    void whenCreateRequestCanEqual_thenCorrectBehavior() {
        CreateOpportunityRequest request = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100);

        // Create and Update are different classes so they should not be equal
        assertNotEquals(request, updateRequest);
        assertNotEquals(updateRequest, request);
    }

    // Different hashCode for different values
    @Test
    void whenRequestsWithDifferentValues_thenDifferentHashCode() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title1", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title2", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);

        assertNotEquals(request1.hashCode(), request2.hashCode());
    }

    // Additional equals branch coverage tests
    @Test
    void whenRequestsWithDifferentDescriptions_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description one that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description two that is long enough", "Skills", "Category", 10, 5, 100, 1L);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenRequestsWithDifferentSkills_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills1", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills2", "Category", 10, 5, 100, 1L);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenRequestsWithDifferentCategories_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category1", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category2", 10, 5, 100, 1L);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenRequestsWithDifferentDurations_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 20, 5, 100, 1L);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenRequestsWithDifferentVacancies_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 10, 100, 1L);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenRequestsWithDifferentPoints_thenNotEquals() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        CreateOpportunityRequest request2 = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 200, 1L);

        assertNotEquals(request1, request2);
    }

    // canEqual coverage - comparing different subclasses
    @Test
    void whenCreateAndUpdateRequestsCompared_thenNotEquals() {
        CreateOpportunityRequest createRequest = new CreateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100, 1L);
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest(
                "Title", "Description that is long enough", "Skills", "Category", 10, 5, 100);

        assertFalse(createRequest.equals(updateRequest));
        assertFalse(updateRequest.equals(createRequest));
    }

    // Test null field comparisons in equals - more thorough coverage
    @Test
    void whenFirstRequestHasNullTitleSecondHasValue_thenNotEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle(null);
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
        assertNotEquals(request2, request1);
    }

    @Test
    void whenBothRequestsHaveNullTitle_thenEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle(null);
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle(null);
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertEquals(request1, request2);
    }

    @Test
    void whenBothRequestsHaveNullDescription_thenEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription(null);
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription(null);
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertEquals(request1, request2);
    }

    @Test
    void whenBothRequestsHaveNullSkills_thenEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills(null);
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills(null);
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertEquals(request1, request2);
    }

    @Test
    void whenBothRequestsHaveNullCategory_thenEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory(null);
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory(null);
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertEquals(request1, request2);
    }

    @Test
    void whenBothRequestsHaveNullDuration_thenEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(null);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(null);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertEquals(request1, request2);
    }

    @Test
    void whenBothRequestsHaveNullVacancies_thenEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(null);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(null);
        request2.setPoints(100);

        assertEquals(request1, request2);
    }

    @Test
    void whenBothRequestsHaveNullPoints_thenEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(null);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(null);

        assertEquals(request1, request2);
    }

    // Additional reverse null comparison tests
    @Test
    void whenSecondRequestHasNullDescriptionFirstHasValue_thenNotEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription(null);
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenSecondRequestHasNullSkillsFirstHasValue_thenNotEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills(null);
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenSecondRequestHasNullCategoryFirstHasValue_thenNotEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory(null);
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenSecondRequestHasNullDurationFirstHasValue_thenNotEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(null);
        request2.setVacancies(5);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenSecondRequestHasNullVacanciesFirstHasValue_thenNotEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(null);
        request2.setPoints(100);

        assertNotEquals(request1, request2);
    }

    @Test
    void whenSecondRequestHasNullPointsFirstHasValue_thenNotEquals() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        request1.setTitle("Title");
        request1.setDescription("Description");
        request1.setSkills("Skills");
        request1.setCategory("Category");
        request1.setDuration(10);
        request1.setVacancies(5);
        request1.setPoints(100);

        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();
        request2.setTitle("Title");
        request2.setDescription("Description");
        request2.setSkills("Skills");
        request2.setCategory("Category");
        request2.setDuration(10);
        request2.setVacancies(5);
        request2.setPoints(null);

        assertNotEquals(request1, request2);
    }

    // canEqual test via reflection to ensure branch coverage
    @Test
    void whenCanEqualCalledWithValidObject_thenReturnsTrue() {
        CreateOpportunityRequest request1 = new CreateOpportunityRequest();
        CreateOpportunityRequest request2 = new CreateOpportunityRequest();

        // This tests that canEqual returns true for objects of the same class
        assertTrue(request1.canEqual(request2));
    }

    @Test
    void whenCanEqualCalledWithInvalidObject_thenReturnsFalse() {
        CreateOpportunityRequest request = new CreateOpportunityRequest();

        // canEqual should return false for objects of different classes
        assertFalse(request.canEqual("string"));
        assertFalse(request.canEqual(Integer.valueOf(1)));
    }

    @Test
    void whenUpdateRequestCanEqualCalledWithValidObject_thenReturnsTrue() {
        UpdateOpportunityRequest request1 = new UpdateOpportunityRequest();
        UpdateOpportunityRequest request2 = new UpdateOpportunityRequest();

        assertTrue(request1.canEqual(request2));
    }

    @Test
    void whenUpdateRequestCanEqualCalledWithCreateRequest_thenReturnsFalse() {
        UpdateOpportunityRequest updateRequest = new UpdateOpportunityRequest();
        CreateOpportunityRequest createRequest = new CreateOpportunityRequest();

        // UpdateOpportunityRequest.canEqual should return false for CreateOpportunityRequest
        assertFalse(updateRequest.canEqual(createRequest));
    }
}
