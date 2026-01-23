package com.example.demo.repository;

import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@org.springframework.transaction.annotation.Transactional
class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    private Promoter testPromoter;
    private Promoter testPromoter2;

    @BeforeEach
    void setUp() {
        // Clear existing data
        opportunityRepository.deleteAll();
        promoterRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        testPromoter = new Promoter();
        testPromoter.setName("Test Promoter");
        testPromoter.setEmail("test@repo.com");
        testPromoter.setOrganization("Test Org");
        testPromoter = entityManager.persistAndFlush(testPromoter);

        testPromoter2 = new Promoter();
        testPromoter2.setName("Second Promoter");
        testPromoter2.setEmail("second@repo.com");
        testPromoter2.setOrganization("Second Org");
        testPromoter2 = entityManager.persistAndFlush(testPromoter2);
    }

    // PromoterRepository Tests
    @Test
    void whenFindAllPromoters_thenReturnAllPromoters() {
        List<Promoter> promoters = promoterRepository.findAll();

        assertTrue(promoters.size() >= 2);
        assertTrue(promoters.stream().anyMatch(p -> p.getName().equals("Test Promoter")));
        assertTrue(promoters.stream().anyMatch(p -> p.getName().equals("Second Promoter")));
    }

    @Test
    void whenFindPromoterById_thenReturnPromoter() {
        Optional<Promoter> found = promoterRepository.findById(testPromoter.getId());

        assertTrue(found.isPresent());
        assertEquals("Test Promoter", found.get().getName());
    }

    @Test
    void whenFindPromoterByIdNotExists_thenReturnEmpty() {
        Optional<Promoter> found = promoterRepository.findById(99999L);

        assertFalse(found.isPresent());
    }

    @Test
    void whenExistsByIdTrue_thenReturnTrue() {
        boolean exists = promoterRepository.existsById(testPromoter.getId());

        assertTrue(exists);
    }

    @Test
    void whenExistsByIdFalse_thenReturnFalse() {
        boolean exists = promoterRepository.existsById(99999L);

        assertFalse(exists);
    }

    @Test
    void whenSavePromoter_thenPromoterIsPersisted() {
        Promoter newPromoter = new Promoter();
        newPromoter.setName("New Promoter");
        newPromoter.setEmail("new@repo.com");
        newPromoter.setOrganization("New Org");

        Promoter saved = promoterRepository.save(newPromoter);

        assertNotNull(saved.getId());
        assertEquals("New Promoter", saved.getName());
    }

    @Test
    void whenDeletePromoter_thenPromoterIsRemoved() {
        long countBefore = promoterRepository.count();
        promoterRepository.delete(testPromoter2);
        entityManager.flush();

        long countAfter = promoterRepository.count();
        assertEquals(countBefore - 1, countAfter);
        assertFalse(promoterRepository.existsById(testPromoter2.getId()));
    }

    // OpportunityRepository Tests
    @Test
    void whenFindByPromoterId_thenReturnOpportunities() {
        createOpportunity("Opp 1", "Java", "Tecnologia", 10, testPromoter);
        createOpportunity("Opp 2", "Python", "Tecnologia", 15, testPromoter);
        createOpportunity("Opp 3", "C++", "Tecnologia", 20, testPromoter2);

        List<Opportunity> opportunities = opportunityRepository.findByPromoterId(testPromoter.getId());

        assertEquals(2, opportunities.size());
        assertTrue(opportunities.stream().allMatch(o -> o.getPromoter().getId().equals(testPromoter.getId())));
    }

    @Test
    void whenFindByPromoterIdEmpty_thenReturnEmptyList() {
        List<Opportunity> opportunities = opportunityRepository.findByPromoterId(testPromoter.getId());

        assertTrue(opportunities.isEmpty());
    }

    @Test
    void whenFindByTitleContaining_thenReturnMatchingOpportunities() {
        createOpportunity("Java Developer", "Java", "Tecnologia", 10, testPromoter);
        createOpportunity("Python Developer", "Python", "Tecnologia", 15, testPromoter);
        createOpportunity("Health Helper", "First Aid", "Saude", 5, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findByTitleContainingIgnoreCase("developer");

        assertEquals(2, opportunities.size());
    }

    @Test
    void whenFindByTitleContainingNoMatch_thenReturnEmptyList() {
        createOpportunity("Java Developer", "Java", "Tecnologia", 10, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findByTitleContainingIgnoreCase("nonexistent");

        assertTrue(opportunities.isEmpty());
    }

    @Test
    void whenFindByCategoryIgnoreCase_thenReturnMatchingOpportunities() {
        createOpportunity("Tech 1", "Java", "Tecnologia", 10, testPromoter);
        createOpportunity("Tech 2", "Python", "Tecnologia", 15, testPromoter);
        createOpportunity("Health", "First Aid", "Saude", 5, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findByCategoryIgnoreCase("TECNOLOGIA");

        assertEquals(2, opportunities.size());
    }

    @Test
    void whenFindByCategoryIgnoreCaseNoMatch_thenReturnEmptyList() {
        createOpportunity("Tech", "Java", "Tecnologia", 10, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findByCategoryIgnoreCase("NonExistent");

        assertTrue(opportunities.isEmpty());
    }

    @Test
    void whenFindBySkillsContaining_thenReturnMatchingOpportunities() {
        createOpportunity("Java Dev", "Java, Spring Boot", "Tecnologia", 10, testPromoter);
        createOpportunity("Python Dev", "Python, Django", "Tecnologia", 15, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findBySkillsContainingIgnoreCase("java");

        assertEquals(1, opportunities.size());
        assertTrue(opportunities.get(0).getSkills().contains("Java"));
    }

    @Test
    void whenFindBySkillsContainingNoMatch_thenReturnEmptyList() {
        createOpportunity("Java Dev", "Java, Spring", "Tecnologia", 10, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findBySkillsContainingIgnoreCase("nonexistent");

        assertTrue(opportunities.isEmpty());
    }

    @Test
    void whenFindByDurationBetween_thenReturnMatchingOpportunities() {
        createOpportunity("Short", "Skill", "Cat", 5, testPromoter);
        createOpportunity("Medium", "Skill", "Cat", 15, testPromoter);
        createOpportunity("Long", "Skill", "Cat", 30, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findByDurationBetween(10, 20);

        assertEquals(1, opportunities.size());
        assertEquals("Medium", opportunities.get(0).getTitle());
    }

    @Test
    void whenFindByDurationBetweenNoMatch_thenReturnEmptyList() {
        createOpportunity("Short", "Skill", "Cat", 5, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findByDurationBetween(100, 200);

        assertTrue(opportunities.isEmpty());
    }

    @Test
    void whenFindByDurationLessThanEqual_thenReturnMatchingOpportunities() {
        createOpportunity("Short", "Skill", "Cat", 5, testPromoter);
        createOpportunity("Medium", "Skill", "Cat", 15, testPromoter);
        createOpportunity("Long", "Skill", "Cat", 30, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findByDurationLessThanEqual(10);

        assertEquals(1, opportunities.size());
        assertEquals("Short", opportunities.get(0).getTitle());
    }

    @Test
    void whenFindByDurationGreaterThanEqual_thenReturnMatchingOpportunities() {
        createOpportunity("Short", "Skill", "Cat", 5, testPromoter);
        createOpportunity("Medium", "Skill", "Cat", 15, testPromoter);
        createOpportunity("Long", "Skill", "Cat", 30, testPromoter);

        List<Opportunity> opportunities = opportunityRepository.findByDurationGreaterThanEqual(20);

        assertEquals(1, opportunities.size());
        assertEquals("Long", opportunities.get(0).getTitle());
    }

    @Test
    void whenFindAllCategories_thenReturnDistinctCategories() {
        createOpportunity("Tech 1", "Java", "Tecnologia", 10, testPromoter);
        createOpportunity("Tech 2", "Python", "Tecnologia", 15, testPromoter);
        createOpportunity("Health", "First Aid", "Saude", 5, testPromoter);
        createOpportunity("Education", "Teaching", "Educacao", 20, testPromoter);

        List<String> categories = opportunityRepository.findAllCategories();

        assertEquals(3, categories.size());
        assertTrue(categories.contains("Tecnologia"));
        assertTrue(categories.contains("Saude"));
        assertTrue(categories.contains("Educacao"));
    }

    @Test
    void whenFindAllCategoriesEmpty_thenReturnEmptyList() {
        List<String> categories = opportunityRepository.findAllCategories();

        assertTrue(categories.isEmpty());
    }

    @Test
    void whenSaveOpportunity_thenOpportunityIsPersisted() {
        Opportunity opp = new Opportunity();
        opp.setTitle("New Opportunity");
        opp.setDescription("New Description");
        opp.setSkills("New Skills");
        opp.setCategory("New Category");
        opp.setDuration(10);
        opp.setVacancies(5);
        opp.setPoints(100);
        opp.setPromoter(testPromoter);

        Opportunity saved = opportunityRepository.save(opp);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("New Opportunity", saved.getTitle());
    }

    @Test
    void whenFindOpportunityById_thenReturnOpportunity() {
        Opportunity opp = createOpportunity("Find Test", "Java", "Tech", 10, testPromoter);

        Optional<Opportunity> found = opportunityRepository.findById(opp.getId());

        assertTrue(found.isPresent());
        assertEquals("Find Test", found.get().getTitle());
    }

    @Test
    void whenFindOpportunityByIdNotExists_thenReturnEmpty() {
        Optional<Opportunity> found = opportunityRepository.findById(99999L);

        assertFalse(found.isPresent());
    }

    @Test
    void whenDeleteOpportunity_thenOpportunityIsRemoved() {
        Opportunity opp = createOpportunity("Delete Test", "Java", "Tech", 10, testPromoter);

        opportunityRepository.delete(opp);
        entityManager.flush();

        Optional<Opportunity> found = opportunityRepository.findById(opp.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void whenCountOpportunities_thenReturnCorrectCount() {
        createOpportunity("Opp 1", "Java", "Tech", 10, testPromoter);
        createOpportunity("Opp 2", "Python", "Tech", 15, testPromoter);
        createOpportunity("Opp 3", "C++", "Tech", 20, testPromoter);

        long count = opportunityRepository.count();

        assertEquals(3, count);
    }

    // Helper method
    private Opportunity createOpportunity(String title, String skills, String category, int duration, Promoter promoter) {
        Opportunity opp = new Opportunity();
        opp.setTitle(title);
        opp.setDescription("Description for " + title);
        opp.setSkills(skills);
        opp.setCategory(category);
        opp.setDuration(duration);
        opp.setVacancies(5);
        opp.setPoints(100);
        opp.setPromoter(promoter);
        return entityManager.persistAndFlush(opp);
    }
}
