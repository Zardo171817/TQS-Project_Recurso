package com.example.demo.e2e;

import com.example.demo.entity.Volunteer;
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
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Volunteer Profile E2E Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VolunteerProfileE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private VolunteerRepository volunteerRepository;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

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

        volunteerRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should load volunteer profile page")
    void shouldLoadVolunteerProfilePage() {
        driver.get(baseUrl + "/volunteer-profile.html");

        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("h2")
        ));

        assertTrue(pageTitle.getText().contains("Gestao do Perfil"));
        assertTrue(driver.getTitle().contains("Perfil do Voluntario"));
    }

    @Test
    @Order(2)
    @DisplayName("Should display create profile form")
    void shouldDisplayCreateProfileForm() {
        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profileForm")));

        WebElement nameInput = driver.findElement(By.id("profileName"));
        WebElement emailInput = driver.findElement(By.id("profileEmail"));
        WebElement phoneInput = driver.findElement(By.id("profilePhone"));
        WebElement skillsInput = driver.findElement(By.id("profileSkills"));
        WebElement interestsInput = driver.findElement(By.id("profileInterests"));
        WebElement availabilityInput = driver.findElement(By.id("profileAvailability"));
        WebElement bioInput = driver.findElement(By.id("profileBio"));
        WebElement submitBtn = driver.findElement(By.id("submitBtn"));

        assertTrue(nameInput.isDisplayed());
        assertTrue(emailInput.isDisplayed());
        assertTrue(phoneInput.isDisplayed());
        assertTrue(skillsInput.isDisplayed());
        assertTrue(interestsInput.isDisplayed());
        assertTrue(availabilityInput.isDisplayed());
        assertTrue(bioInput.isDisplayed());
        assertTrue(submitBtn.isDisplayed());
        assertEquals("Criar Perfil", submitBtn.getText());
    }

    @Test
    @Order(3)
    @DisplayName("Should create a new volunteer profile")
    void shouldCreateNewVolunteerProfile() {
        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profileForm")));

        driver.findElement(By.id("profileName")).sendKeys("Joao Silva");
        driver.findElement(By.id("profileEmail")).sendKeys("joao.silva@email.com");
        driver.findElement(By.id("profilePhone")).sendKeys("+351912345678");
        driver.findElement(By.id("profileSkills")).sendKeys("Java, Python, Communication");
        driver.findElement(By.id("profileInterests")).sendKeys("Education, Environment");
        driver.findElement(By.id("profileAvailability")).sendKeys("Weekends, Evenings");
        driver.findElement(By.id("profileBio")).sendKeys("Passionate about helping others");

        driver.findElement(By.id("submitBtn")).click();

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".message.success")
        ));

        assertTrue(successMessage.getText().contains("Perfil criado com sucesso"));

        WebElement profileCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".profile-card")
        ));

        assertTrue(profileCard.getText().contains("Joao Silva"));
        assertTrue(profileCard.getText().contains("joao.silva@email.com"));

        assertTrue(volunteerRepository.existsByEmail("joao.silva@email.com"));
    }

    @Test
    @Order(4)
    @DisplayName("Should show validation error for invalid name")
    void shouldShowValidationErrorForInvalidName() {
        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profileForm")));

        driver.findElement(By.id("profileName")).sendKeys("J");
        driver.findElement(By.id("profileEmail")).sendKeys("test@email.com");

        driver.findElement(By.id("submitBtn")).click();

        WebElement nameError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("nameError")
        ));

        assertTrue(nameError.isDisplayed());
    }

    @Test
    @Order(5)
    @DisplayName("Should search for existing profile by email")
    void shouldSearchForExistingProfileByEmail() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Maria Santos");
        volunteer.setEmail("maria.santos@email.com");
        volunteer.setPhone("+351987654321");
        volunteer.setSkills("Leadership, First Aid");
        volunteer.setInterests("Health, Animals");
        volunteer.setAvailability("Mornings");
        volunteer.setBio("Love animals and helping people");
        volunteer.setTotalPoints(50);
        volunteer.setProfileCreatedAt(LocalDateTime.now());
        volunteerRepository.save(volunteer);

        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchEmail")));

        driver.findElement(By.id("searchEmail")).sendKeys("maria.santos@email.com");
        driver.findElement(By.id("searchBtn")).click();

        WebElement profileCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".profile-card")
        ));

        assertTrue(profileCard.getText().contains("Maria Santos"));
        assertTrue(profileCard.getText().contains("maria.santos@email.com"));
        assertTrue(profileCard.getText().contains("Leadership"));
        assertTrue(profileCard.getText().contains("50 Pontos"));
    }

    @Test
    @Order(6)
    @DisplayName("Should show error message when profile not found")
    void shouldShowErrorWhenProfileNotFound() {
        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchEmail")));

        driver.findElement(By.id("searchEmail")).sendKeys("nonexistent@email.com");
        driver.findElement(By.id("searchBtn")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".message.error")
        ));

        assertTrue(errorMessage.getText().contains("nao encontrado"));
    }

    @Test
    @Order(7)
    @DisplayName("Should edit existing profile")
    void shouldEditExistingProfile() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Pedro Costa");
        volunteer.setEmail("pedro.costa@email.com");
        volunteer.setSkills("Programming");
        volunteer.setTotalPoints(0);
        volunteer.setProfileCreatedAt(LocalDateTime.now());
        volunteerRepository.save(volunteer);

        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchEmail")));
        driver.findElement(By.id("searchEmail")).sendKeys("pedro.costa@email.com");
        driver.findElement(By.id("searchBtn")).click();

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-edit")
        ));
        editButton.click();

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profileName")));
        nameField.clear();
        nameField.sendKeys("Pedro Costa Updated");

        WebElement skillsField = driver.findElement(By.id("profileSkills"));
        skillsField.clear();
        skillsField.sendKeys("Programming, Leadership, Communication");

        WebElement submitBtn = driver.findElement(By.id("submitBtn"));
        assertEquals("Atualizar Perfil", submitBtn.getText());

        submitBtn.click();

        // Wait for success message to appear and verify
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message.success")));

        // Re-fetch the element to avoid stale reference
        try {
            Thread.sleep(500); // Small delay to ensure DOM is updated
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement successMessage = driver.findElement(By.cssSelector(".message.success"));
        String messageText = successMessage.getText();
        assertTrue(messageText.contains("atualizado") || messageText.contains("sucesso"),
                "Expected success message but got: " + messageText);

        Volunteer updated = volunteerRepository.findByEmail("pedro.costa@email.com").orElse(null);
        assertNotNull(updated);
        assertEquals("Pedro Costa Updated", updated.getName());
        assertTrue(updated.getSkills().contains("Leadership"));
    }

    @Test
    @Order(8)
    @DisplayName("Should delete profile")
    void shouldDeleteProfile() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Ana Ferreira");
        volunteer.setEmail("ana.ferreira@email.com");
        volunteer.setTotalPoints(0);
        volunteer.setProfileCreatedAt(LocalDateTime.now());
        volunteerRepository.save(volunteer);

        assertTrue(volunteerRepository.existsByEmail("ana.ferreira@email.com"));

        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchEmail")));
        driver.findElement(By.id("searchEmail")).sendKeys("ana.ferreira@email.com");
        driver.findElement(By.id("searchBtn")).click();

        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-delete")
        ));

        deleteButton.click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        // Wait for the page to update after delete
        try {
            Thread.sleep(500); // Small delay to ensure DOM is updated
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for success message and re-fetch to avoid stale reference
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".message.success")));
        WebElement successMessage = driver.findElement(By.cssSelector(".message.success"));
        String messageText = successMessage.getText();
        assertTrue(messageText.contains("excluido") || messageText.contains("sucesso"),
                "Expected success message but got: " + messageText);

        assertFalse(volunteerRepository.existsByEmail("ana.ferreira@email.com"));
    }

    @Test
    @Order(9)
    @DisplayName("Should display error when creating duplicate email")
    void shouldDisplayErrorForDuplicateEmail() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Existing User");
        volunteer.setEmail("existing@email.com");
        volunteer.setTotalPoints(0);
        volunteer.setProfileCreatedAt(LocalDateTime.now());
        volunteerRepository.save(volunteer);

        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profileForm")));

        driver.findElement(By.id("profileName")).sendKeys("New User");
        driver.findElement(By.id("profileEmail")).sendKeys("existing@email.com");

        driver.findElement(By.id("submitBtn")).click();

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".message.error")
        ));

        assertTrue(errorMessage.getText().contains("Email already exists"));
    }

    @Test
    @Order(10)
    @DisplayName("Should cancel edit and reset form")
    void shouldCancelEditAndResetForm() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Test User");
        volunteer.setEmail("test.user@email.com");
        volunteer.setTotalPoints(0);
        volunteer.setProfileCreatedAt(LocalDateTime.now());
        volunteerRepository.save(volunteer);

        driver.get(baseUrl + "/volunteer-profile.html");

        driver.findElement(By.id("searchEmail")).sendKeys("test.user@email.com");
        driver.findElement(By.id("searchBtn")).click();

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-edit")
        ));
        editButton.click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("formTitle"), "Editar Perfil"
        ));

        WebElement cancelBtn = driver.findElement(By.id("cancelBtn"));
        assertTrue(cancelBtn.isDisplayed());
        cancelBtn.click();

        WebElement formTitle = driver.findElement(By.id("formTitle"));
        assertEquals("Criar Novo Perfil", formTitle.getText());

        WebElement submitBtn = driver.findElement(By.id("submitBtn"));
        assertEquals("Criar Perfil", submitBtn.getText());

        WebElement nameField = driver.findElement(By.id("profileName"));
        assertEquals("", nameField.getAttribute("value"));
    }

    @Test
    @Order(11)
    @DisplayName("Should display skills, interests and availability as tags")
    void shouldDisplayProfileDataAsTags() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Tag Test User");
        volunteer.setEmail("tags@email.com");
        volunteer.setSkills("Java, Python, JavaScript");
        volunteer.setInterests("Education, Environment, Health");
        volunteer.setAvailability("Weekends, Mornings");
        volunteer.setTotalPoints(100);
        volunteer.setProfileCreatedAt(LocalDateTime.now());
        volunteerRepository.save(volunteer);

        driver.get(baseUrl + "/volunteer-profile.html");

        driver.findElement(By.id("searchEmail")).sendKeys("tags@email.com");
        driver.findElement(By.id("searchBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".profile-card")));

        java.util.List<WebElement> skillTags = driver.findElements(By.cssSelector(".tag.skill"));
        java.util.List<WebElement> interestTags = driver.findElements(By.cssSelector(".tag.interest"));
        java.util.List<WebElement> availabilityTags = driver.findElements(By.cssSelector(".tag.availability"));

        assertEquals(3, skillTags.size());
        assertEquals(3, interestTags.size());
        assertEquals(2, availabilityTags.size());
    }

    @Test
    @Order(12)
    @DisplayName("Should display points badge correctly")
    void shouldDisplayPointsBadge() {
        Volunteer volunteer = new Volunteer();
        volunteer.setName("Points User");
        volunteer.setEmail("points@email.com");
        volunteer.setTotalPoints(250);
        volunteer.setProfileCreatedAt(LocalDateTime.now());
        volunteerRepository.save(volunteer);

        driver.get(baseUrl + "/volunteer-profile.html");

        driver.findElement(By.id("searchEmail")).sendKeys("points@email.com");
        driver.findElement(By.id("searchBtn")).click();

        WebElement pointsBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".points-badge")
        ));

        assertTrue(pointsBadge.getText().contains("250"));
        assertTrue(pointsBadge.getText().contains("Pontos"));
    }

    @Test
    @Order(13)
    @DisplayName("Complete E2E flow: Create, Search, Edit, Delete")
    void completeE2EFlow() {
        driver.get(baseUrl + "/volunteer-profile.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profileForm")));
        driver.findElement(By.id("profileName")).sendKeys("Complete Flow User");
        driver.findElement(By.id("profileEmail")).sendKeys("complete.flow@email.com");
        driver.findElement(By.id("profileSkills")).sendKeys("Testing");
        driver.findElement(By.id("submitBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message.success")));
        assertTrue(volunteerRepository.existsByEmail("complete.flow@email.com"));

        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchEmail")));
        driver.findElement(By.id("searchEmail")).sendKeys("complete.flow@email.com");
        driver.findElement(By.id("searchBtn")).click();

        WebElement profileCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".profile-card")
        ));
        assertTrue(profileCard.getText().contains("Complete Flow User"));

        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-edit")
        ));
        editButton.click();

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profileName")));
        nameField.clear();
        nameField.sendKeys("Complete Flow User Updated");
        driver.findElement(By.id("submitBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message.success")));

        Volunteer updated = volunteerRepository.findByEmail("complete.flow@email.com").orElse(null);
        assertNotNull(updated);
        assertEquals("Complete Flow User Updated", updated.getName());

        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn-delete")
        ));
        deleteButton.click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message.success")));

        assertFalse(volunteerRepository.existsByEmail("complete.flow@email.com"));
    }
}
