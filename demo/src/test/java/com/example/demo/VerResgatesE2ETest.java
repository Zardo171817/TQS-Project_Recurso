package com.example.demo;

import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.RedemptionRepository;
import com.example.demo.repository.VolunteerRepository;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Ver Resgates - E2E Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VerResgatesE2ETest {

    @LocalServerPort private int port;
    @Autowired private BenefitRepository benefitRepository;
    @Autowired private VolunteerRepository volunteerRepository;
    @Autowired private RedemptionRepository redemptionRepository;

    private static WebDriver driver;
    private static WebDriverWait wait;
    private Volunteer volunteer;
    private Benefit partnerBenefit;

    @BeforeAll
    static void setUpDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDownDriver() {
        if (driver != null) driver.quit();
    }

    @BeforeEach
    void setUp() {
        redemptionRepository.deleteAll();
        benefitRepository.deleteAll();
        volunteerRepository.deleteAll();

        volunteer = new Volunteer();
        volunteer.setName("Joao Santos");
        volunteer.setEmail("joao@test.com");
        volunteer.setTotalPoints(500);
        volunteer = volunteerRepository.save(volunteer);

        partnerBenefit = new Benefit();
        partnerBenefit.setName("Desconto Livraria");
        partnerBenefit.setDescription("15% desconto");
        partnerBenefit.setPointsRequired(100);
        partnerBenefit.setCategory(BenefitCategory.PARTNER);
        partnerBenefit.setProvider("Livraria Bertrand");
        partnerBenefit.setActive(true);
        partnerBenefit.setCreatedAt(LocalDateTime.now());
        partnerBenefit = benefitRepository.save(partnerBenefit);
    }

    @AfterEach
    void tearDown() {
        redemptionRepository.deleteAll();
        benefitRepository.deleteAll();
        volunteerRepository.deleteAll();
    }

    @Test @Order(1)
    @DisplayName("Page loads correctly")
    void pageLoadsCorrectly() {
        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2")));
        assertTrue(title.getText().contains("Resgates"));
    }

    @Test @Order(2)
    @DisplayName("Search form is displayed")
    void searchFormIsDisplayed() {
        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        assertTrue(driver.findElement(By.id("searchForm")).isDisplayed());
        assertTrue(driver.findElement(By.id("providerName")).isDisplayed());
        assertTrue(driver.findElement(By.id("searchBtn")).isDisplayed());
    }

    @Test @Order(3)
    @DisplayName("Search displays stats correctly")
    void searchDisplaysStatsCorrectly() {
        createRedemption();
        createRedemption();

        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerName")));
        input.sendKeys("Livraria Bertrand");
        driver.findElement(By.id("searchBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultsSection")));
        assertEquals("1", driver.findElement(By.id("statTotalBenefits")).getText());
        assertEquals("2", driver.findElement(By.id("statTotalRedemptions")).getText());
        assertEquals("200", driver.findElement(By.id("statTotalPoints")).getText());
    }

    @Test @Order(4)
    @DisplayName("Search shows error for unknown provider")
    void searchShowsErrorForUnknownProvider() {
        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerName")));
        input.sendKeys("Unknown Provider");
        driver.findElement(By.id("searchBtn")).click();

        WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("messageContainer")));
        assertTrue(msg.getText().contains("Erro"));
    }

    @Test @Order(5)
    @DisplayName("Benefit cards are displayed")
    void benefitCardsAreDisplayed() {
        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerName")));
        input.sendKeys("Livraria Bertrand");
        driver.findElement(By.id("searchBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultsSection")));
        List<WebElement> cards = driver.findElements(By.cssSelector(".benefit-detail-card"));
        assertEquals(1, cards.size());
    }

    @Test @Order(6)
    @DisplayName("Redemptions table is displayed")
    void redemptionsTableIsDisplayed() {
        createRedemption();

        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerName")));
        input.sendKeys("Livraria Bertrand");
        driver.findElement(By.id("searchBtn")).click();

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".redemptions-table")));
        assertTrue(table.isDisplayed());
        List<WebElement> rows = driver.findElements(By.cssSelector(".redemptions-table tbody tr"));
        assertEquals(1, rows.size());
    }

    @Test @Order(7)
    @DisplayName("Table shows volunteer name")
    void tableShowsVolunteerName() {
        createRedemption();

        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerName")));
        input.sendKeys("Livraria Bertrand");
        driver.findElement(By.id("searchBtn")).click();

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".redemptions-table")));
        assertTrue(table.getText().contains("Joao Santos"));
    }

    @Test @Order(8)
    @DisplayName("Empty state shown when no redemptions")
    void emptyStateShownWhenNoRedemptions() {
        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerName")));
        input.sendKeys("Livraria Bertrand");
        driver.findElement(By.id("searchBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultsSection")));
        assertEquals("0", driver.findElement(By.id("statTotalRedemptions")).getText());
        assertTrue(driver.findElement(By.cssSelector("#redemptionsTableContainer .empty-state")).isDisplayed());
    }

    @Test @Order(9)
    @DisplayName("Partial search works")
    void partialSearchWorks() {
        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerName")));
        input.sendKeys("Bertrand");
        driver.findElement(By.id("searchBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultsSection")));
        assertEquals("1", driver.findElement(By.id("statTotalBenefits")).getText());
    }

    @Test @Order(10)
    @DisplayName("Stats bar shows three items")
    void statsBarShowsThreeItems() {
        driver.get("http://localhost:" + port + "/partner-redemptions.html");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("providerName")));
        input.sendKeys("Livraria Bertrand");
        driver.findElement(By.id("searchBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultsSection")));
        List<WebElement> statItems = driver.findElements(By.cssSelector(".stat-item"));
        assertEquals(3, statItems.size());
    }

    private Redemption createRedemption() {
        Redemption r = new Redemption();
        r.setVolunteer(volunteer);
        r.setBenefit(partnerBenefit);
        r.setPointsSpent(partnerBenefit.getPointsRequired());
        r.setStatus(RedemptionStatus.COMPLETED);
        r.setRedeemedAt(LocalDateTime.now());
        return redemptionRepository.save(r);
    }
}
