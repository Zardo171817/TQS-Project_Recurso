package com.example.demo.controller;

import com.example.demo.dto.CreatePromoterProfileRequest;
import com.example.demo.dto.UpdatePromoterProfileRequest;
import com.example.demo.entity.Promoter;
import com.example.demo.repository.PromoterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PromoterProfileController Integration Tests")
class PromoterProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PromoterRepository promoterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private CreatePromoterProfileRequest validCreateRequest;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM applications");
        jdbcTemplate.execute("DELETE FROM opportunities");
        jdbcTemplate.execute("DELETE FROM promoters");

        validCreateRequest = new CreatePromoterProfileRequest();
        validCreateRequest.setName("Maria Santos");
        validCreateRequest.setEmail("maria@organizacao.org");
        validCreateRequest.setOrganization("Associacao Solidaria");
        validCreateRequest.setDescription("Uma organizacao dedicada ao bem-estar social");
        validCreateRequest.setPhone("+351 234 567 890");
        validCreateRequest.setWebsite("https://www.associacaosolidaria.org");
        validCreateRequest.setAddress("Rua Principal, 123, Aveiro");
        validCreateRequest.setOrganizationType("ONG");
        validCreateRequest.setAreaOfActivity("Educacao, Saude");
        validCreateRequest.setFoundedYear("2010");
        validCreateRequest.setNumberOfEmployees("21-50");
        validCreateRequest.setSocialMedia("Facebook: /assoc");
    }

    @Nested
    @DisplayName("Create Profile Tests")
    class CreateProfileTests {

        @Test
        @DisplayName("Should create profile successfully with all fields")
        void shouldCreateProfileSuccessfully() throws Exception {
            mockMvc.perform(post("/api/promoters/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value("Maria Santos"))
                    .andExpect(jsonPath("$.email").value("maria@organizacao.org"))
                    .andExpect(jsonPath("$.organization").value("Associacao Solidaria"))
                    .andExpect(jsonPath("$.description").value("Uma organizacao dedicada ao bem-estar social"))
                    .andExpect(jsonPath("$.phone").value("+351 234 567 890"))
                    .andExpect(jsonPath("$.website").value("https://www.associacaosolidaria.org"))
                    .andExpect(jsonPath("$.address").value("Rua Principal, 123, Aveiro"))
                    .andExpect(jsonPath("$.organizationType").value("ONG"))
                    .andExpect(jsonPath("$.areaOfActivity").value("Educacao, Saude"))
                    .andExpect(jsonPath("$.foundedYear").value("2010"))
                    .andExpect(jsonPath("$.numberOfEmployees").value("21-50"))
                    .andExpect(jsonPath("$.socialMedia").value("Facebook: /assoc"))
                    .andExpect(jsonPath("$.profileCreatedAt").exists());

            assertThat(promoterRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should create profile with minimal required fields")
        void shouldCreateProfileWithMinimalFields() throws Exception {
            CreatePromoterProfileRequest minimalRequest = new CreatePromoterProfileRequest();
            minimalRequest.setName("Joao Silva");
            minimalRequest.setEmail("joao@test.org");
            minimalRequest.setOrganization("Test Org");

            mockMvc.perform(post("/api/promoters/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(minimalRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Joao Silva"))
                    .andExpect(jsonPath("$.email").value("joao@test.org"))
                    .andExpect(jsonPath("$.organization").value("Test Org"))
                    .andExpect(jsonPath("$.phone").doesNotExist());
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            validCreateRequest.setName("");

            mockMvc.perform(post("/api/promoters/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is too short")
        void shouldReturn400WhenNameTooShort() throws Exception {
            validCreateRequest.setName("A");

            mockMvc.perform(post("/api/promoters/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            validCreateRequest.setEmail("invalid-email");

            mockMvc.perform(post("/api/promoters/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when email is blank")
        void shouldReturn400WhenEmailBlank() throws Exception {
            validCreateRequest.setEmail("");

            mockMvc.perform(post("/api/promoters/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when organization is blank")
        void shouldReturn400WhenOrganizationBlank() throws Exception {
            validCreateRequest.setOrganization("");

            mockMvc.perform(post("/api/promoters/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when email already exists")
        void shouldReturn400WhenEmailExists() throws Exception {
            Promoter existing = new Promoter();
            existing.setName("Existing");
            existing.setEmail("maria@organizacao.org");
            existing.setOrganization("Existing Org");
            promoterRepository.save(existing);

            mockMvc.perform(post("/api/promoters/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Email already exists")));
        }
    }

    @Nested
    @DisplayName("Get Profile Tests")
    class GetProfileTests {

        private Promoter savedPromoter;

        @BeforeEach
        void setUp() {
            savedPromoter = new Promoter();
            savedPromoter.setName("Maria Santos");
            savedPromoter.setEmail("maria@organizacao.org");
            savedPromoter.setOrganization("Associacao Solidaria");
            savedPromoter.setDescription("Uma organizacao dedicada ao bem-estar social");
            savedPromoter.setPhone("+351 234 567 890");
            savedPromoter.setOrganizationType("ONG");
            savedPromoter.setAreaOfActivity("Educacao, Saude");
            savedPromoter.setProfileCreatedAt(LocalDateTime.now());
            savedPromoter = promoterRepository.save(savedPromoter);
        }

        @Test
        @DisplayName("Should get profile by id successfully")
        void shouldGetProfileByIdSuccessfully() throws Exception {
            mockMvc.perform(get("/api/promoters/profile/{id}", savedPromoter.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(savedPromoter.getId()))
                    .andExpect(jsonPath("$.name").value("Maria Santos"))
                    .andExpect(jsonPath("$.email").value("maria@organizacao.org"))
                    .andExpect(jsonPath("$.organization").value("Associacao Solidaria"));
        }

        @Test
        @DisplayName("Should return 404 when profile not found by id")
        void shouldReturn404WhenProfileNotFoundById() throws Exception {
            mockMvc.perform(get("/api/promoters/profile/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Promoter not found with id: 999")));
        }

        @Test
        @DisplayName("Should get profile by email successfully")
        void shouldGetProfileByEmailSuccessfully() throws Exception {
            mockMvc.perform(get("/api/promoters/profile/email/{email}", "maria@organizacao.org"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("maria@organizacao.org"))
                    .andExpect(jsonPath("$.name").value("Maria Santos"));
        }

        @Test
        @DisplayName("Should return 404 when profile not found by email")
        void shouldReturn404WhenProfileNotFoundByEmail() throws Exception {
            mockMvc.perform(get("/api/promoters/profile/email/{email}", "unknown@test.org"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Promoter not found with email")));
        }
    }

    @Nested
    @DisplayName("Get All Profiles Tests")
    class GetAllProfilesTests {

        @Test
        @DisplayName("Should return all profiles")
        void shouldReturnAllProfiles() throws Exception {
            Promoter promoter1 = new Promoter();
            promoter1.setName("Maria Santos");
            promoter1.setEmail("maria@org.org");
            promoter1.setOrganization("Org 1");
            promoterRepository.save(promoter1);

            Promoter promoter2 = new Promoter();
            promoter2.setName("Joao Silva");
            promoter2.setEmail("joao@org.org");
            promoter2.setOrganization("Org 2");
            promoterRepository.save(promoter2);

            mockMvc.perform(get("/api/promoters/profiles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("Maria Santos"))
                    .andExpect(jsonPath("$[1].name").value("Joao Silva"));
        }

        @Test
        @DisplayName("Should return empty list when no profiles exist")
        void shouldReturnEmptyListWhenNoProfilesExist() throws Exception {
            mockMvc.perform(get("/api/promoters/profiles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Update Profile Tests")
    class UpdateProfileTests {

        private Promoter savedPromoter;

        @BeforeEach
        void setUp() {
            savedPromoter = new Promoter();
            savedPromoter.setName("Maria Santos");
            savedPromoter.setEmail("maria@organizacao.org");
            savedPromoter.setOrganization("Associacao Solidaria");
            savedPromoter.setDescription("Original description");
            savedPromoter.setProfileCreatedAt(LocalDateTime.now());
            savedPromoter = promoterRepository.save(savedPromoter);
        }

        @Test
        @DisplayName("Should update profile successfully")
        void shouldUpdateProfileSuccessfully() throws Exception {
            UpdatePromoterProfileRequest updateRequest = new UpdatePromoterProfileRequest();
            updateRequest.setName("Maria Santos Silva");
            updateRequest.setOrganization("Associacao Solidaria de Aveiro");
            updateRequest.setDescription("Updated description");

            mockMvc.perform(put("/api/promoters/profile/{id}", savedPromoter.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Maria Santos Silva"))
                    .andExpect(jsonPath("$.organization").value("Associacao Solidaria de Aveiro"))
                    .andExpect(jsonPath("$.description").value("Updated description"))
                    .andExpect(jsonPath("$.profileUpdatedAt").exists());
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() throws Exception {
            UpdatePromoterProfileRequest updateRequest = new UpdatePromoterProfileRequest();
            updateRequest.setName("New Name Only");

            mockMvc.perform(put("/api/promoters/profile/{id}", savedPromoter.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New Name Only"))
                    .andExpect(jsonPath("$.organization").value("Associacao Solidaria"))
                    .andExpect(jsonPath("$.description").value("Original description"));
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent profile")
        void shouldReturn404WhenUpdatingNonExistentProfile() throws Exception {
            UpdatePromoterProfileRequest updateRequest = new UpdatePromoterProfileRequest();
            updateRequest.setName("New Name");

            mockMvc.perform(put("/api/promoters/profile/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 when no updates provided")
        void shouldReturn400WhenNoUpdatesProvided() throws Exception {
            UpdatePromoterProfileRequest emptyRequest = new UpdatePromoterProfileRequest();

            mockMvc.perform(put("/api/promoters/profile/{id}", savedPromoter.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emptyRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("No fields to update")));
        }

        @Test
        @DisplayName("Should return 400 when name too short")
        void shouldReturn400WhenNameTooShort() throws Exception {
            UpdatePromoterProfileRequest updateRequest = new UpdatePromoterProfileRequest();
            updateRequest.setName("A");

            mockMvc.perform(put("/api/promoters/profile/{id}", savedPromoter.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Search Profiles Tests")
    class SearchProfilesTests {

        @BeforeEach
        void setUp() {
            Promoter promoter1 = new Promoter();
            promoter1.setName("Maria Santos");
            promoter1.setEmail("maria@org.org");
            promoter1.setOrganization("Associacao Solidaria");
            promoter1.setOrganizationType("ONG");
            promoter1.setAreaOfActivity("Educacao, Saude");
            promoterRepository.save(promoter1);

            Promoter promoter2 = new Promoter();
            promoter2.setName("Joao Silva");
            promoter2.setEmail("joao@org.org");
            promoter2.setOrganization("Instituto Cultural");
            promoter2.setOrganizationType("IPSS");
            promoter2.setAreaOfActivity("Cultura, Arte");
            promoterRepository.save(promoter2);
        }

        @Test
        @DisplayName("Should find promoters by organization")
        void shouldFindPromotersByOrganization() throws Exception {
            mockMvc.perform(get("/api/promoters/profiles/organization/{organization}", "Solidaria"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].organization").value(containsString("Solidaria")));
        }

        @Test
        @DisplayName("Should find promoters by area of activity")
        void shouldFindPromotersByAreaOfActivity() throws Exception {
            mockMvc.perform(get("/api/promoters/profiles/area/{areaOfActivity}", "Educacao"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].areaOfActivity").value(containsString("Educacao")));
        }

        @Test
        @DisplayName("Should find promoters by organization type")
        void shouldFindPromotersByOrganizationType() throws Exception {
            mockMvc.perform(get("/api/promoters/profiles/type/{organizationType}", "ONG"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].organizationType").value("ONG"));
        }

        @Test
        @DisplayName("Should return empty list when no match found")
        void shouldReturnEmptyListWhenNoMatchFound() throws Exception {
            mockMvc.perform(get("/api/promoters/profiles/organization/{organization}", "NonExistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Delete Profile Tests")
    class DeleteProfileTests {

        private Promoter savedPromoter;

        @BeforeEach
        void setUp() {
            savedPromoter = new Promoter();
            savedPromoter.setName("Maria Santos");
            savedPromoter.setEmail("maria@organizacao.org");
            savedPromoter.setOrganization("Associacao Solidaria");
            savedPromoter = promoterRepository.save(savedPromoter);
        }

        @Test
        @DisplayName("Should delete profile successfully")
        void shouldDeleteProfileSuccessfully() throws Exception {
            mockMvc.perform(delete("/api/promoters/profile/{id}", savedPromoter.getId()))
                    .andExpect(status().isNoContent());

            assertThat(promoterRepository.findById(savedPromoter.getId())).isEmpty();
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent profile")
        void shouldReturn404WhenDeletingNonExistentProfile() throws Exception {
            mockMvc.perform(delete("/api/promoters/profile/{id}", 999L))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Get All Promoters (Legacy Endpoint) Tests")
    class GetAllPromotersTests {

        @Test
        @DisplayName("Should return all promoters from legacy endpoint")
        void shouldReturnAllPromotersFromLegacyEndpoint() throws Exception {
            Promoter promoter = new Promoter();
            promoter.setName("Test Promoter");
            promoter.setEmail("test@org.org");
            promoter.setOrganization("Test Org");
            promoterRepository.save(promoter);

            mockMvc.perform(get("/api/promoters"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Test Promoter"));
        }
    }
}
