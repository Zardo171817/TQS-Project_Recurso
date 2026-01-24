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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VolunteerOpportunitiesE2ETest {

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
        testPromoter.setName("E2E Volunteer Test Promoter");
        testPromoter.setEmail("e2evolunteer@test.com");
        testPromoter.setOrganization("E2E Volunteer Org");
        testPromoter = promoterRepository.save(testPromoter);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Feature 2: Ver/Filtrar Oportunidades - E2E Test
    @Test
    void testFilterOpportunitiesByCategory() {
        createOpportunity("Tech Opportunity", "Java", "Tecnologia", 10);
        createOpportunity("Health Opportunity", "First Aid", "Saude", 15);

        driver.get(baseUrl + "/opportunities.html");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("opportunity-card"), 0));

        Select categorySelect = new Select(driver.findElement(By.id("filterCategory")));
        categorySelect.selectByValue("Tecnologia");
        driver.findElement(By.id("applyFilters")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        List<WebElement> cards = driver.findElements(By.className("opportunity-card"));
        assertEquals(1, cards.size());
        assertTrue(cards.get(0).getText().contains("Tech Opportunity"));
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
}
