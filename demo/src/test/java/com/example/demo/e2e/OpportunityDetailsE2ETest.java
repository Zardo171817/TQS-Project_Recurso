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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpportunityDetailsE2ETest {

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
        testPromoter.setName("E2E Details Test Promoter");
        testPromoter.setEmail("e2edetails@test.com");
        testPromoter.setOrganization("E2E Details Test Org");
        testPromoter = promoterRepository.save(testPromoter);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testDetailsPageShowsAllOpportunityInformation() {
        Opportunity opportunity = createOpportunity(
                "Aulas de Reforco Escolar",
                "Ajude criancas com dificuldades escolares oferecendo aulas de reforco em matematica e portugues.",
                "Pedagogia, Paciencia",
                "Educacao",
                30
        );

        driver.get(baseUrl + "/opportunity-details.html?id=" + opportunity.getId());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("opportunityDetails")));

        assertEquals("Aulas de Reforco Escolar", driver.findElement(By.id("opportunityTitle")).getText());
        assertEquals("Educacao", driver.findElement(By.id("opportunityCategory")).getText());
    }

    private Opportunity createOpportunity(String title, String description, String skills, String category, int duration) {
        Opportunity opp = new Opportunity();
        opp.setTitle(title);
        opp.setDescription(description);
        opp.setSkills(skills);
        opp.setCategory(category);
        opp.setDuration(duration);
        opp.setVacancies(5);
        opp.setPoints(100);
        opp.setPromoter(testPromoter);
        return opportunityRepository.save(opp);
    }
}
