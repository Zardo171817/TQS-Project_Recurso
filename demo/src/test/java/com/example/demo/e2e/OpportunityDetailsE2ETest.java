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

/**
 * End-to-End tests for Feature: Ver Detalhes de Oportunidades (Voluntario)
 * Tests the complete user flow from viewing opportunities list to viewing details
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Feature: Ver Detalhes de Oportunidades - E2E Tests")
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
    @DisplayName("Voluntario deve conseguir navegar da lista para os detalhes de uma oportunidade")
    void testVolunteerCanNavigateToOpportunityDetails() {
        Opportunity opportunity = createOpportunity(
                "Plantio de Arvores no Parque",
                "Participe do nosso mutirao de plantio de arvores. Vamos reflorestar uma area do parque municipal com especies nativas.",
                "Disposicao Fisica, Trabalho ao Ar Livre",
                "Meio Ambiente",
                2
        );

        driver.get(baseUrl + "/volunteer-opportunities.html");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("opportunity-card"), 0));

        WebElement detailsButton = driver.findElement(By.cssSelector(".opportunity-card .btn-details"));
        detailsButton.click();

        wait.until(ExpectedConditions.urlContains("opportunity-details.html"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("opportunityTitle")));
        assertEquals("Plantio de Arvores no Parque", titleElement.getText());
    }

    @Test
    @DisplayName("Pagina de detalhes deve mostrar todas as informacoes da oportunidade")
    void testDetailsPageShowsAllOpportunityInformation() {
        Opportunity opportunity = createOpportunity(
                "Aulas de Reforco Escolar",
                "Ajude criancas com dificuldades escolares oferecendo aulas de reforco em matematica e portugues.",
                "Pedagogia, Paciencia, Matematica, Portugues",
                "Educacao",
                30
        );

        driver.get(baseUrl + "/opportunity-details.html?id=" + opportunity.getId());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("opportunityDetails")));

        assertEquals("Aulas de Reforco Escolar", driver.findElement(By.id("opportunityTitle")).getText());
        assertEquals("Educacao", driver.findElement(By.id("opportunityCategory")).getText());
        assertTrue(driver.findElement(By.id("opportunityDescription")).getText().contains("Ajude criancas"));
        assertTrue(driver.findElement(By.id("opportunityDuration")).getText().contains("30"));
        assertTrue(driver.findElement(By.id("opportunityVacancies")).getText().contains("5"));
        assertTrue(driver.findElement(By.id("opportunityPoints")).getText().contains("100"));
        assertEquals("E2E Details Test Promoter", driver.findElement(By.id("opportunityPromoter")).getText());

        WebElement skillsContainer = driver.findElement(By.id("opportunitySkills"));
        assertTrue(skillsContainer.getText().contains("Pedagogia"));
        assertTrue(skillsContainer.getText().contains("Paciencia"));
    }

    @Test
    @DisplayName("Voluntario deve conseguir voltar para a lista de oportunidades")
    void testVolunteerCanNavigateBackToList() {
        Opportunity opportunity = createOpportunity(
                "Test Back Navigation",
                "Testing back navigation functionality",
                "Test Skill",
                "Tecnologia",
                5
        );

        driver.get(baseUrl + "/opportunity-details.html?id=" + opportunity.getId());
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        WebElement backLink = driver.findElement(By.cssSelector(".back-link a"));
        backLink.click();

        wait.until(ExpectedConditions.urlContains("volunteer-opportunities.html"));
        assertTrue(driver.getCurrentUrl().contains("volunteer-opportunities.html"));
    }

    @Test
    @DisplayName("Deve mostrar mensagem de erro quando oportunidade nao existe")
    void testShowsErrorWhenOpportunityNotFound() {
        driver.get(baseUrl + "/opportunity-details.html?id=99999");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        WebElement messageContainer = driver.findElement(By.id("messageContainer"));
        assertTrue(messageContainer.getText().contains("nao encontrada") ||
                   messageContainer.getText().contains("Erro"));
    }

    @Test
    @DisplayName("Deve mostrar mensagem de erro quando ID nao e fornecido")
    void testShowsErrorWhenIdNotProvided() {
        driver.get(baseUrl + "/opportunity-details.html");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loadingIndicator")));

        WebElement messageContainer = driver.findElement(By.id("messageContainer"));
        assertTrue(messageContainer.getText().contains("nao especificado") ||
                   messageContainer.getText().contains("ID"));
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
