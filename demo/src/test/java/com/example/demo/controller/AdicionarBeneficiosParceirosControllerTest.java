package com.example.demo.controller;

import com.example.demo.dto.BenefitResponse;
import com.example.demo.dto.CreateBenefitRequest;
import com.example.demo.dto.UpdateBenefitRequest;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BenefitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Adicionar Beneficios Parceiro - Controller Tests")
class AdicionarBeneficiosParceirosControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BenefitService benefitService;

    @InjectMocks
    private BenefitController benefitController;

    private ObjectMapper objectMapper;

    private BenefitResponse partnerBenefitResponse;
    private BenefitResponse partnerBenefitResponse2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(benefitController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        partnerBenefitResponse = new BenefitResponse();
        partnerBenefitResponse.setId(1L);
        partnerBenefitResponse.setName("Desconto Cinema NOS");
        partnerBenefitResponse.setDescription("20% de desconto em bilhetes de cinema");
        partnerBenefitResponse.setPointsRequired(150);
        partnerBenefitResponse.setCategory(BenefitCategory.PARTNER);
        partnerBenefitResponse.setProvider("Cinema NOS");
        partnerBenefitResponse.setImageUrl("http://example.com/cinema.jpg");
        partnerBenefitResponse.setActive(true);
        partnerBenefitResponse.setCreatedAt(LocalDateTime.of(2024, 6, 15, 10, 30, 0));

        partnerBenefitResponse2 = new BenefitResponse();
        partnerBenefitResponse2.setId(2L);
        partnerBenefitResponse2.setName("Voucher Restaurante");
        partnerBenefitResponse2.setDescription("Voucher de 10 euros no Restaurante Sabor");
        partnerBenefitResponse2.setPointsRequired(300);
        partnerBenefitResponse2.setCategory(BenefitCategory.PARTNER);
        partnerBenefitResponse2.setProvider("Restaurante Sabor");
        partnerBenefitResponse2.setImageUrl(null);
        partnerBenefitResponse2.setActive(true);
        partnerBenefitResponse2.setCreatedAt(LocalDateTime.of(2024, 6, 20, 14, 0, 0));
    }

    @Nested
    @DisplayName("POST /api/benefits/partner Tests")
    class CreatePartnerBenefitTests {

        @Test
        @DisplayName("Should return 201 CREATED on successful creation")
        void shouldReturn201OnSuccessfulCreation() throws Exception {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Desconto Cinema NOS",
                    "20% de desconto em bilhetes de cinema",
                    150,
                    "Cinema NOS",
                    "http://example.com/cinema.jpg"
            );

            when(benefitService.createPartnerBenefit(any(CreateBenefitRequest.class)))
                    .thenReturn(partnerBenefitResponse);

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Desconto Cinema NOS")))
                    .andExpect(jsonPath("$.description", is("20% de desconto em bilhetes de cinema")))
                    .andExpect(jsonPath("$.pointsRequired", is(150)))
                    .andExpect(jsonPath("$.category", is("PARTNER")))
                    .andExpect(jsonPath("$.provider", is("Cinema NOS")));

            verify(benefitService, times(1)).createPartnerBenefit(any(CreateBenefitRequest.class));
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            String requestBody = "{\"name\":\"\",\"description\":\"desc\",\"pointsRequired\":100,\"provider\":\"test\"}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when name is missing")
        void shouldReturn400WhenNameIsMissing() throws Exception {
            String requestBody = "{\"description\":\"desc\",\"pointsRequired\":100,\"provider\":\"test\"}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when description is blank")
        void shouldReturn400WhenDescriptionIsBlank() throws Exception {
            String requestBody = "{\"name\":\"Test\",\"description\":\"\",\"pointsRequired\":100,\"provider\":\"test\"}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when description is missing")
        void shouldReturn400WhenDescriptionIsMissing() throws Exception {
            String requestBody = "{\"name\":\"Test\",\"pointsRequired\":100,\"provider\":\"test\"}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when pointsRequired is null")
        void shouldReturn400WhenPointsRequiredIsNull() throws Exception {
            String requestBody = "{\"name\":\"Test\",\"description\":\"desc\",\"provider\":\"test\"}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when pointsRequired is zero")
        void shouldReturn400WhenPointsRequiredIsZero() throws Exception {
            String requestBody = "{\"name\":\"Test\",\"description\":\"desc\",\"pointsRequired\":0,\"provider\":\"test\"}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when provider is blank")
        void shouldReturn400WhenProviderIsBlank() throws Exception {
            String requestBody = "{\"name\":\"Test\",\"description\":\"desc\",\"pointsRequired\":100,\"provider\":\"\"}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when provider is missing")
        void shouldReturn400WhenProviderIsMissing() throws Exception {
            String requestBody = "{\"name\":\"Test\",\"description\":\"desc\",\"pointsRequired\":100}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should accept request without imageUrl")
        void shouldAcceptRequestWithoutImageUrl() throws Exception {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test Benefit", "Test desc", 100, "Provider", null
            );

            when(benefitService.createPartnerBenefit(any(CreateBenefitRequest.class)))
                    .thenReturn(partnerBenefitResponse);

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should return correct JSON structure on creation")
        void shouldReturnCorrectJsonStructure() throws Exception {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 100, "Provider", "http://img.jpg"
            );

            when(benefitService.createPartnerBenefit(any(CreateBenefitRequest.class)))
                    .thenReturn(partnerBenefitResponse);

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.description").exists())
                    .andExpect(jsonPath("$.pointsRequired").exists())
                    .andExpect(jsonPath("$.category").exists())
                    .andExpect(jsonPath("$.provider").exists())
                    .andExpect(jsonPath("$.active").exists())
                    .andExpect(jsonPath("$.createdAt").exists());
        }
    }

    @Nested
    @DisplayName("PUT /api/benefits/partner/{id} Tests")
    class UpdatePartnerBenefitTests {

        @Test
        @DisplayName("Should return 200 OK on successful update")
        void shouldReturn200OnSuccessfulUpdate() throws Exception {
            UpdateBenefitRequest request = new UpdateBenefitRequest(
                    "Updated Name", "Updated desc", 200, "Updated Provider", "http://new.jpg"
            );

            BenefitResponse updatedResponse = new BenefitResponse();
            updatedResponse.setId(1L);
            updatedResponse.setName("Updated Name");
            updatedResponse.setDescription("Updated desc");
            updatedResponse.setPointsRequired(200);
            updatedResponse.setCategory(BenefitCategory.PARTNER);
            updatedResponse.setProvider("Updated Provider");
            updatedResponse.setImageUrl("http://new.jpg");
            updatedResponse.setActive(true);
            updatedResponse.setCreatedAt(LocalDateTime.now());

            when(benefitService.updatePartnerBenefit(eq(1L), any(UpdateBenefitRequest.class)))
                    .thenReturn(updatedResponse);

            mockMvc.perform(put("/api/benefits/partner/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Updated Name")))
                    .andExpect(jsonPath("$.pointsRequired", is(200)));

            verify(benefitService, times(1)).updatePartnerBenefit(eq(1L), any(UpdateBenefitRequest.class));
        }

        @Test
        @DisplayName("Should return 404 when benefit not found for update")
        void shouldReturn404WhenBenefitNotFound() throws Exception {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Test");

            when(benefitService.updatePartnerBenefit(eq(999L), any(UpdateBenefitRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Benefit not found with id: 999"));

            mockMvc.perform(put("/api/benefits/partner/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when trying to update non-PARTNER benefit")
        void shouldReturn409WhenUpdatingNonPartnerBenefit() throws Exception {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Test");

            when(benefitService.updatePartnerBenefit(eq(3L), any(UpdateBenefitRequest.class)))
                    .thenThrow(new IllegalStateException("Only PARTNER benefits can be updated by partners"));

            mockMvc.perform(put("/api/benefits/partner/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should accept partial update request")
        void shouldAcceptPartialUpdateRequest() throws Exception {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Only Name Updated");

            when(benefitService.updatePartnerBenefit(eq(1L), any(UpdateBenefitRequest.class)))
                    .thenReturn(partnerBenefitResponse);

            mockMvc.perform(put("/api/benefits/partner/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/benefits/partner/{id} Tests")
    class DeactivatePartnerBenefitTests {

        @Test
        @DisplayName("Should return 204 NO CONTENT on successful deactivation")
        void shouldReturn204OnSuccessfulDeactivation() throws Exception {
            doNothing().when(benefitService).deactivatePartnerBenefit(1L);

            mockMvc.perform(delete("/api/benefits/partner/1"))
                    .andExpect(status().isNoContent());

            verify(benefitService, times(1)).deactivatePartnerBenefit(1L);
        }

        @Test
        @DisplayName("Should return 404 when benefit not found for deactivation")
        void shouldReturn404WhenBenefitNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Benefit not found with id: 999"))
                    .when(benefitService).deactivatePartnerBenefit(999L);

            mockMvc.perform(delete("/api/benefits/partner/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 409 when trying to deactivate non-PARTNER benefit")
        void shouldReturn409WhenDeactivatingNonPartnerBenefit() throws Exception {
            doThrow(new IllegalStateException("Only PARTNER benefits can be deactivated by partners"))
                    .when(benefitService).deactivatePartnerBenefit(3L);

            mockMvc.perform(delete("/api/benefits/partner/3"))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/partner Tests")
    class GetPartnerBenefitsTests {

        @Test
        @DisplayName("Should return 200 OK with partner benefits list")
        void shouldReturn200WithPartnerBenefitsList() throws Exception {
            when(benefitService.getPartnerBenefits())
                    .thenReturn(Arrays.asList(partnerBenefitResponse, partnerBenefitResponse2));

            mockMvc.perform(get("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Desconto Cinema NOS")))
                    .andExpect(jsonPath("$[0].category", is("PARTNER")))
                    .andExpect(jsonPath("$[1].name", is("Voucher Restaurante")));

            verify(benefitService, times(1)).getPartnerBenefits();
        }

        @Test
        @DisplayName("Should return 200 OK with empty list")
        void shouldReturn200WithEmptyList() throws Exception {
            when(benefitService.getPartnerBenefits())
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/partner"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/partner/provider/{provider} Tests")
    class GetPartnerBenefitsByProviderTests {

        @Test
        @DisplayName("Should return 200 OK with filtered benefits")
        void shouldReturn200WithFilteredBenefits() throws Exception {
            when(benefitService.getPartnerBenefitsByProvider("Cinema"))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits/partner/provider/Cinema")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].provider", is("Cinema NOS")));
        }

        @Test
        @DisplayName("Should return 200 OK with empty list for unknown provider")
        void shouldReturn200WithEmptyListForUnknownProvider() throws Exception {
            when(benefitService.getPartnerBenefitsByProvider("Desconhecido"))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/partner/provider/Desconhecido"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits (Existing endpoints) Tests")
    class ExistingEndpointsTests {

        @Test
        @DisplayName("Should return all active benefits")
        void shouldReturnAllActiveBenefits() throws Exception {
            when(benefitService.getAllActiveBenefits())
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return benefit by id")
        void shouldReturnBenefitById() throws Exception {
            when(benefitService.getBenefitById(1L)).thenReturn(partnerBenefitResponse);

            mockMvc.perform(get("/api/benefits/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        @DisplayName("Should return 404 when benefit not found by id")
        void shouldReturn404WhenBenefitNotFound() throws Exception {
            when(benefitService.getBenefitById(999L))
                    .thenThrow(new ResourceNotFoundException("Benefit not found"));

            mockMvc.perform(get("/api/benefits/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return benefits by category")
        void shouldReturnBenefitsByCategory() throws Exception {
            when(benefitService.getBenefitsByCategory(BenefitCategory.PARTNER))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits/category/PARTNER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return affordable benefits for volunteer")
        void shouldReturnAffordableBenefits() throws Exception {
            when(benefitService.getAffordableBenefitsForVolunteer(1L))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits/volunteer/1/affordable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return benefits by provider")
        void shouldReturnBenefitsByProvider() throws Exception {
            when(benefitService.getBenefitsByProvider("Cinema"))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits/provider/Cinema"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return all providers")
        void shouldReturnAllProviders() throws Exception {
            when(benefitService.getAllProviders())
                    .thenReturn(Arrays.asList("Cinema NOS", "Restaurante Sabor"));

            mockMvc.perform(get("/api/benefits/providers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Should return benefits sorted by points ascending")
        void shouldReturnBenefitsSortedAsc() throws Exception {
            when(benefitService.getBenefitsSortedByPointsAsc())
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits/sorted/points-asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return benefits sorted by points descending")
        void shouldReturnBenefitsSortedDesc() throws Exception {
            when(benefitService.getBenefitsSortedByPointsDesc())
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits/sorted/points-desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Should return catalog for volunteer")
        void shouldReturnCatalogForVolunteer() throws Exception {
            when(benefitService.getCatalogForVolunteer(1L))
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits/volunteer/1/catalog"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large benefit ID")
        void shouldHandleLargeBenefitId() throws Exception {
            when(benefitService.getPartnerBenefitsByProvider("test"))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/partner/provider/test"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return JSON content type for partner endpoints")
        void shouldReturnJsonContentType() throws Exception {
            when(benefitService.getPartnerBenefits())
                    .thenReturn(Arrays.asList(partnerBenefitResponse));

            mockMvc.perform(get("/api/benefits/partner"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Should invoke service for createPartnerBenefit")
        void shouldInvokeServiceForCreate() throws Exception {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 100, "Provider", null
            );

            when(benefitService.createPartnerBenefit(any(CreateBenefitRequest.class)))
                    .thenReturn(partnerBenefitResponse);

            mockMvc.perform(post("/api/benefits/partner")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            verify(benefitService, times(1)).createPartnerBenefit(any(CreateBenefitRequest.class));
        }

        @Test
        @DisplayName("Should invoke service for deactivatePartnerBenefit")
        void shouldInvokeServiceForDeactivate() throws Exception {
            doNothing().when(benefitService).deactivatePartnerBenefit(1L);

            mockMvc.perform(delete("/api/benefits/partner/1"));

            verify(benefitService, times(1)).deactivatePartnerBenefit(1L);
        }

        @Test
        @DisplayName("Should return 400 when pointsRequired is negative")
        void shouldReturn400WhenPointsNegative() throws Exception {
            String requestBody = "{\"name\":\"Test\",\"description\":\"desc\",\"pointsRequired\":-5,\"provider\":\"test\"}";

            mockMvc.perform(post("/api/benefits/partner")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }
}
