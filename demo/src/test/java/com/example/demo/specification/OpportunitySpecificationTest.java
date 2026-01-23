package com.example.demo.specification;

import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OpportunitySpecificationTest {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    private Promoter testPromoter;

    @BeforeEach
    void setUp() {
        opportunityRepository.deleteAll();
        promoterRepository.deleteAll();

        testPromoter = new Promoter();
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("test@spec.com");
        testPromoter.setOrganization("Test Org");
        testPromoter = promoterRepository.save(testPromoter);

        // Create test opportunities
        createOpportunity("Java Developer", "Java, Spring Boot", "Tecnologia", 10);
        createOpportunity("Python Developer", "Python, Django", "Tecnologia", 15);
        createOpportunity("Health Helper", "First Aid, CPR", "Saude", 5);
        createOpportunity("Teacher Assistant", "Teaching, Patience", "Educacao", 30);
        createOpportunity("Environmental Care", "Gardening", "Meio Ambiente", 7);
    }

    private void createOpportunity(String title, String skills, String category, int duration) {
        Opportunity opp = new Opportunity();
        opp.setTitle(title);
        opp.setDescription("Description for " + title);
        opp.setSkills(skills);
        opp.setCategory(category);
        opp.setDuration(duration);
        opp.setVacancies(5);
        opp.setPoints(100);
        opp.setPromoter(testPromoter);
        opportunityRepository.save(opp);
    }

    // hasCategory Tests
    @Test
    void whenFilterByCategory_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("Tecnologia");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(o -> o.getCategory().equalsIgnoreCase("Tecnologia")));
    }

    @Test
    void whenFilterByCategoryIgnoreCase_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("TECNOLOGIA");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(2, results.size());
    }

    @Test
    void whenFilterByCategoryWithSpaces_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("  Tecnologia  ");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(2, results.size());
    }

    @Test
    void whenFilterByNullCategory_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory(null);
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterByEmptyCategory_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterByBlankCategory_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("   ");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterByNonExistentCategory_thenReturnEmptyList() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("NonExistent");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertTrue(results.isEmpty());
    }

    // hasSkillsContaining Tests
    @Test
    void whenFilterBySkills_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasSkillsContaining("Java");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertTrue(results.get(0).getSkills().contains("Java"));
    }

    @Test
    void whenFilterBySkillsIgnoreCase_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasSkillsContaining("JAVA");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
    }

    @Test
    void whenFilterByPartialSkill_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasSkillsContaining("Spring");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertTrue(results.get(0).getSkills().contains("Spring"));
    }

    @Test
    void whenFilterByNullSkills_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasSkillsContaining(null);
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterByEmptySkills_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasSkillsContaining("");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterByBlankSkills_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasSkillsContaining("   ");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterByNonExistentSkill_thenReturnEmptyList() {
        Specification<Opportunity> spec = OpportunitySpecification.hasSkillsContaining("NonExistent");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertTrue(results.isEmpty());
    }

    // hasDurationBetween Tests
    @Test
    void whenFilterByDurationBetween_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasDurationBetween(5, 10);
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(o -> o.getDuration() >= 5 && o.getDuration() <= 10));
    }

    @Test
    void whenFilterByMinDurationOnly_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasDurationBetween(10, null);
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(o -> o.getDuration() >= 10));
    }

    @Test
    void whenFilterByMaxDurationOnly_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasDurationBetween(null, 10);
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(o -> o.getDuration() <= 10));
    }

    @Test
    void whenFilterByNullDurations_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.hasDurationBetween(null, null);
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterByExactDuration_thenReturnMatchingOpportunity() {
        Specification<Opportunity> spec = OpportunitySpecification.hasDurationBetween(10, 10);
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertEquals(10, results.get(0).getDuration());
    }

    @Test
    void whenFilterByDurationRangeNoMatch_thenReturnEmptyList() {
        Specification<Opportunity> spec = OpportunitySpecification.hasDurationBetween(100, 200);
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertTrue(results.isEmpty());
    }

    // withFilters (combined) Tests
    @Test
    void whenFilterWithAllParams_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                "Tecnologia", "Java", 5, 15
        );
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertEquals("Java Developer", results.get(0).getTitle());
    }

    @Test
    void whenFilterWithCategoryAndSkills_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                "Tecnologia", "Python", null, null
        );
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertTrue(results.get(0).getSkills().contains("Python"));
    }

    @Test
    void whenFilterWithCategoryAndDuration_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                "Tecnologia", null, 10, 20
        );
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(2, results.size());
    }

    @Test
    void whenFilterWithSkillsAndDuration_thenReturnMatchingOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                null, "Teaching", 20, 40
        );
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertEquals("Teacher Assistant", results.get(0).getTitle());
    }

    @Test
    void whenFilterWithNoParams_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                null, null, null, null
        );
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterWithEmptyStrings_thenReturnAllOpportunities() {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                "", "", null, null
        );
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(5, results.size());
    }

    @Test
    void whenFilterWithNoMatchingCriteria_thenReturnEmptyList() {
        Specification<Opportunity> spec = OpportunitySpecification.withFilters(
                "Tecnologia", "NonExistent", 1, 5
        );
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertTrue(results.isEmpty());
    }

    @Test
    void whenFilterBySaude_thenReturnHealthOpportunity() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("Saude");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertEquals("Health Helper", results.get(0).getTitle());
    }

    @Test
    void whenFilterByEducacao_thenReturnTeachingOpportunity() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("Educacao");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertEquals("Teacher Assistant", results.get(0).getTitle());
    }

    @Test
    void whenFilterByMeioAmbiente_thenReturnEnvironmentalOpportunity() {
        Specification<Opportunity> spec = OpportunitySpecification.hasCategory("Meio Ambiente");
        List<Opportunity> results = opportunityRepository.findAll(spec);

        assertEquals(1, results.size());
        assertEquals("Environmental Care", results.get(0).getTitle());
    }
}
