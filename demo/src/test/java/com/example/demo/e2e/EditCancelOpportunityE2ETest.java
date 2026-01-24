package com.example.demo.e2e;

import com.example.demo.entity.Opportunity;
import com.example.demo.entity.Promoter;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.repository.PromoterRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EditCancelOpportunityE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private PromoterRepository promoterRepository;

    private static WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;
    private Promoter testPromoter;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;

        opportunityRepository.deleteAll();
        promoterRepository.deleteAll();

        testPromoter = new Promoter();
        testPromoter.setName("E2E Test Promoter");
        testPromoter.setEmail("e2e@test.com");
        testPromoter.setOrganization("E2E Org");
        testPromoter = promoterRepository.save(testPromoter);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Feature: Editar/Cancelar Oportunidades - E2E Test
    @Test
    void testEditOpportunity_withValidData_shouldUpdateSuccessfully() {
        // Create an opportunity to edit
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle("Original E2E Title");
        opportunity.setDescription("Original E2E description for testing");
        opportunity.setSkills("Java, Spring");
        opportunity.setCategory("Tecnologia");
        opportunity.setDuration(10);
        opportunity.setVacancies(5);
        opportunity.setPoints(100);
        opportunity.setPromoter(testPromoter);
        opportunity = opportunityRepository.save(opportunity);

        // Navigate to opportunities page
        driver.get(baseUrl + "/opportunities.html");

        // Wait for opportunities to load
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        // Find and click the Edit button
        WebElement editButton = longWait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-edit"))
        );
        editButton.click();

        // Wait for modal to appear
        WebElement editModal = longWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("editModal"))
        );
        assertTrue(editModal.isDisplayed());

        // Clear and update fields
        WebElement titleInput = driver.findElement(By.id("editTitle"));
        titleInput.clear();
        titleInput.sendKeys("Updated E2E Title");

        WebElement descriptionInput = driver.findElement(By.id("editDescription"));
        descriptionInput.clear();
        descriptionInput.sendKeys("Updated E2E description for testing purposes");

        WebElement skillsInput = driver.findElement(By.id("editSkills"));
        skillsInput.clear();
        skillsInput.sendKeys("Python, Django, Testing");

        Select categorySelect = new Select(driver.findElement(By.id("editCategory")));
        categorySelect.selectByValue("Educacao");

        WebElement durationInput = driver.findElement(By.id("editDuration"));
        durationInput.clear();
        durationInput.sendKeys("20");

        WebElement vacanciesInput = driver.findElement(By.id("editVacancies"));
        vacanciesInput.clear();
        vacanciesInput.sendKeys("15");

        WebElement pointsInput = driver.findElement(By.id("editPoints"));
        pointsInput.clear();
        pointsInput.sendKeys("200");

        // Submit the form
        WebElement submitButton = driver.findElement(By.cssSelector("#editForm button[type='submit']"));
        submitButton.click();

        // Wait for modal to close (indicating success)
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("editModal")));

        // Wait for page to reload
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        // Verify the card is updated
        WebElement opportunityCard = longWait.until(
                ExpectedConditions.presenceOfElementLocated(By.className("opportunity-card"))
        );
        assertTrue(opportunityCard.getText().contains("Updated E2E Title"));

        // Verify in database
        Opportunity updatedOpportunity = opportunityRepository.findById(opportunity.getId()).orElseThrow();
        assertEquals("Updated E2E Title", updatedOpportunity.getTitle());
        assertEquals("Educacao", updatedOpportunity.getCategory());
        assertEquals(20, updatedOpportunity.getDuration());
    }

    @Test
    void testCancelOpportunity_withConfirmation_shouldDeleteSuccessfully() {
        // Create an opportunity to cancel
        Opportunity opportunity = new Opportunity();
        opportunity.setTitle("To Cancel E2E");
        opportunity.setDescription("This opportunity will be cancelled");
        opportunity.setSkills("Testing");
        opportunity.setCategory("Tecnologia");
        opportunity.setDuration(5);
        opportunity.setVacancies(3);
        opportunity.setPoints(50);
        opportunity.setPromoter(testPromoter);
        opportunity = opportunityRepository.save(opportunity);

        Long opportunityId = opportunity.getId();

        // Navigate to opportunities page
        driver.get(baseUrl + "/opportunities.html");

        // Wait for opportunities to load
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        // Find and click the Cancel button
        WebElement cancelButton = longWait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-delete"))
        );
        cancelButton.click();

        // Wait for delete confirmation modal to appear
        WebElement deleteModal = longWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("deleteModal"))
        );
        assertTrue(deleteModal.isDisplayed());

        // Verify the opportunity title is shown in confirmation
        WebElement deleteTitle = driver.findElement(By.id("deleteOpportunityTitle"));
        assertTrue(deleteTitle.getText().contains("To Cancel E2E"));

        // Confirm deletion
        WebElement confirmDeleteButton = driver.findElement(By.id("confirmDelete"));
        confirmDeleteButton.click();

        // Wait for modal to close (indicating success)
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("deleteModal")));

        // Wait for page to reload and show empty state or no cards
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        // Verify opportunity is removed from database
        assertFalse(opportunityRepository.existsById(opportunityId));
    }
}
