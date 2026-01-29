package com.example.demo.e2e;

import com.example.demo.integration.AbstractIntegrationTest;
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
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayName("E2E Selenium Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeleniumE2ETest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private static boolean chromeAvailable = true;

    @BeforeAll
    static void setupClass() {
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            chromeAvailable = false;
        }
    }

    @BeforeEach
    void setUp() {
        assumeTrue(chromeAvailable, "Chrome/ChromeDriver not available, skipping Selenium tests");

        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--remote-allow-origins=*");

            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } catch (Exception e) {
            assumeTrue(false, "Chrome driver setup failed: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    @Order(1)
    @DisplayName("Should display home page")
    void shouldDisplayHomePage() {
        driver.get(getBaseUrl() + "/index.html");

        assertThat(driver.getTitle()).contains("Marketplace");
    }

    @Test
    @Order(2)
    @DisplayName("Should display login page")
    void shouldDisplayLoginPage() {
        driver.get(getBaseUrl() + "/login.html");

        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));

        assertThat(emailInput.isDisplayed()).isTrue();
        assertThat(passwordInput.isDisplayed()).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Should display register page")
    void shouldDisplayRegisterPage() {
        driver.get(getBaseUrl() + "/register.html");

        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement userTypeSelect = driver.findElement(By.id("userType"));

        assertThat(nameInput.isDisplayed()).isTrue();
        assertThat(emailInput.isDisplayed()).isTrue();
        assertThat(userTypeSelect.isDisplayed()).isTrue();
    }

    @Test
    @Order(4)
    @DisplayName("Should register volunteer successfully")
    void shouldRegisterVolunteer() {
        driver.get(getBaseUrl() + "/register.html");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name"))).sendKeys("Test Volunteer");
        driver.findElement(By.id("email")).sendKeys("volunteer.e2e@test.com");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("confirmPassword")).sendKeys("password123");

        Select userTypeSelect = new Select(driver.findElement(By.id("userType")));
        userTypeSelect.selectByValue("VOLUNTEER");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for success message
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message.success")));
        WebElement message = driver.findElement(By.cssSelector(".message.success"));
        assertThat(message.getText()).contains("sucesso");
    }

    @Test
    @Order(5)
    @DisplayName("Should login with registered user")
    void shouldLoginSuccessfully() {
        // First register
        driver.get(getBaseUrl() + "/register.html");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name"))).sendKeys("Login Test");
        driver.findElement(By.id("email")).sendKeys("login.e2e@test.com");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("confirmPassword")).sendKeys("password123");
        new Select(driver.findElement(By.id("userType"))).selectByValue("VOLUNTEER");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // Now login
        driver.get(getBaseUrl() + "/login.html");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email"))).sendKeys("login.e2e@test.com");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for success or redirect
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message.success")),
                ExpectedConditions.urlContains("index"),
                ExpectedConditions.urlContains("volunteer")
        ));
    }

    @Test
    @Order(6)
    @DisplayName("Should display opportunities page")
    void shouldDisplayOpportunitiesPage() {
        driver.get(getBaseUrl() + "/opportunities.html");

        assertThat(driver.getTitle()).contains("Oportunidades");
    }

    @Test
    @Order(7)
    @DisplayName("Should display ranking page")
    void shouldDisplayRankingPage() {
        driver.get(getBaseUrl() + "/volunteer-ranking.html");

        assertThat(driver.getTitle()).contains("Ranking");
    }

    @Test
    @Order(8)
    @DisplayName("Should show error for wrong password")
    void shouldShowErrorForWrongPassword() {
        // First register
        driver.get(getBaseUrl() + "/register.html");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name"))).sendKeys("Wrong Pass");
        driver.findElement(By.id("email")).sendKeys("wrongpass.e2e@test.com");
        driver.findElement(By.id("password")).sendKeys("correctpass");
        driver.findElement(By.id("confirmPassword")).sendKeys("correctpass");
        new Select(driver.findElement(By.id("userType"))).selectByValue("VOLUNTEER");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        // Try login with wrong password
        driver.get(getBaseUrl() + "/login.html");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email"))).sendKeys("wrongpass.e2e@test.com");
        driver.findElement(By.id("password")).sendKeys("wrongpassword");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message.error")));
        WebElement message = driver.findElement(By.cssSelector(".message.error"));
        assertThat(message.isDisplayed()).isTrue();
    }
}
