package com.example.demo.controller;

import com.example.demo.dto.BenefitResponse;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.BenefitService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ver Catalogo Beneficios Voluntario - Controller Tests")
class VerCatalogoBeneficiosVoluntarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BenefitService benefitService;

    @InjectMocks
    private BenefitController benefitController;

    private BenefitResponse benefitUA;
    private BenefitResponse benefitPartner;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(benefitController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        benefitUA = new BenefitResponse();
        benefitUA.setId(1L);
        benefitUA.setName("Desconto Cantina UA");
        benefitUA.setDescription("10% desconto na cantina da UA");
        benefitUA.setPointsRequired(100);
        benefitUA.setCategory(BenefitCategory.UA);
        benefitUA.setProvider("Universidade de Aveiro");
        benefitUA.setImageUrl("http://example.com/cantina.jpg");
        benefitUA.setActive(true);
        benefitUA.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));

        benefitPartner = new BenefitResponse();
        benefitPartner.setId(2L);
        benefitPartner.setName("Desconto Cinema");
        benefitPartner.setDescription("20% desconto em bilhetes de cinema");
        benefitPartner.setPointsRequired(200);
        benefitPartner.setCategory(BenefitCategory.PARTNER);
        benefitPartner.setProvider("Cinema NOS");
        benefitPartner.setImageUrl("http://example.com/cinema.jpg");
        benefitPartner.setActive(true);
        benefitPartner.setCreatedAt(LocalDateTime.of(2024, 1, 20, 14, 0, 0));
    }

    @Nested
    @DisplayName("GET /api/benefits Tests")
    class GetAllActiveBenefitsTests {

        @Test
        @DisplayName("Should return 200 OK with all active benefits")
        void shouldReturn200WithAllActiveBenefits() throws Exception {
            when(benefitService.getAllActiveBenefits()).thenReturn(Arrays.asList(benefitUA, benefitPartner));

            mockMvc.perform(get("/api/benefits")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Desconto Cantina UA")))
                    .andExpect(jsonPath("$[1].name", is("Desconto Cinema")));

            verify(benefitService, times(1)).getAllActiveBenefits();
        }

        @Test
        @DisplayName("Should return 200 OK with empty list when no benefits")
        void shouldReturn200WithEmptyListWhenNoBenefits() throws Exception {
            when(benefitService.getAllActiveBenefits()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Should return correct JSON structure")
        void shouldReturnCorrectJsonStructure() throws Exception {
            when(benefitService.getAllActiveBenefits()).thenReturn(Arrays.asList(benefitUA));

            mockMvc.perform(get("/api/benefits"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("Desconto Cantina UA")))
                    .andExpect(jsonPath("$[0].description", is("10% desconto na cantina da UA")))
                    .andExpect(jsonPath("$[0].pointsRequired", is(100)))
                    .andExpect(jsonPath("$[0].category", is("UA")))
                    .andExpect(jsonPath("$[0].provider", is("Universidade de Aveiro")))
                    .andExpect(jsonPath("$[0].imageUrl", is("http://example.com/cantina.jpg")))
                    .andExpect(jsonPath("$[0].active", is(true)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/{id} Tests")
    class GetBenefitByIdTests {

        @Test
        @DisplayName("Should return 200 OK when benefit found")
        void shouldReturn200WhenBenefitFound() throws Exception {
            when(benefitService.getBenefitById(1L)).thenReturn(benefitUA);

            mockMvc.perform(get("/api/benefits/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Desconto Cantina UA")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when benefit not found")
        void shouldReturn404WhenBenefitNotFound() throws Exception {
            when(benefitService.getBenefitById(999L))
                    .thenThrow(new ResourceNotFoundException("Benefit not found with id: 999"));

            mockMvc.perform(get("/api/benefits/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return all benefit details")
        void shouldReturnAllBenefitDetails() throws Exception {
            when(benefitService.getBenefitById(2L)).thenReturn(benefitPartner);

            mockMvc.perform(get("/api/benefits/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(2)))
                    .andExpect(jsonPath("$.name", is("Desconto Cinema")))
                    .andExpect(jsonPath("$.description", is("20% desconto em bilhetes de cinema")))
                    .andExpect(jsonPath("$.pointsRequired", is(200)))
                    .andExpect(jsonPath("$.category", is("PARTNER")))
                    .andExpect(jsonPath("$.provider", is("Cinema NOS")));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/category/{category} Tests")
    class GetBenefitsByCategoryTests {

        @Test
        @DisplayName("Should return UA category benefits")
        void shouldReturnUACategoryBenefits() throws Exception {
            when(benefitService.getBenefitsByCategory(BenefitCategory.UA))
                    .thenReturn(Arrays.asList(benefitUA));

            mockMvc.perform(get("/api/benefits/category/UA")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].category", is("UA")));
        }

        @Test
        @DisplayName("Should return PARTNER category benefits")
        void shouldReturnPartnerCategoryBenefits() throws Exception {
            when(benefitService.getBenefitsByCategory(BenefitCategory.PARTNER))
                    .thenReturn(Arrays.asList(benefitPartner));

            mockMvc.perform(get("/api/benefits/category/PARTNER")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].category", is("PARTNER")));
        }

        @Test
        @DisplayName("Should return empty list when no benefits in category")
        void shouldReturnEmptyListWhenNoBenefitsInCategory() throws Exception {
            when(benefitService.getBenefitsByCategory(BenefitCategory.UA))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/category/UA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/volunteer/{volunteerId}/affordable Tests")
    class GetAffordableBenefitsTests {

        @Test
        @DisplayName("Should return affordable benefits for volunteer")
        void shouldReturnAffordableBenefitsForVolunteer() throws Exception {
            when(benefitService.getAffordableBenefitsForVolunteer(1L))
                    .thenReturn(Arrays.asList(benefitUA));

            mockMvc.perform(get("/api/benefits/volunteer/1/affordable")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("Desconto Cantina UA")));
        }

        @Test
        @DisplayName("Should return 404 when volunteer not found")
        void shouldReturn404WhenVolunteerNotFound() throws Exception {
            when(benefitService.getAffordableBenefitsForVolunteer(999L))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(get("/api/benefits/volunteer/999/affordable"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return empty list when no affordable benefits")
        void shouldReturnEmptyListWhenNoAffordableBenefits() throws Exception {
            when(benefitService.getAffordableBenefitsForVolunteer(1L))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/volunteer/1/affordable"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/provider/{provider} Tests")
    class GetBenefitsByProviderTests {

        @Test
        @DisplayName("Should return benefits by provider")
        void shouldReturnBenefitsByProvider() throws Exception {
            when(benefitService.getBenefitsByProvider("Universidade"))
                    .thenReturn(Arrays.asList(benefitUA));

            mockMvc.perform(get("/api/benefits/provider/Universidade")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].provider", containsString("Universidade")));
        }

        @Test
        @DisplayName("Should return empty list when provider not found")
        void shouldReturnEmptyListWhenProviderNotFound() throws Exception {
            when(benefitService.getBenefitsByProvider("NonExistent"))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/provider/NonExistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/providers Tests")
    class GetAllProvidersTests {

        @Test
        @DisplayName("Should return all providers")
        void shouldReturnAllProviders() throws Exception {
            when(benefitService.getAllProviders())
                    .thenReturn(Arrays.asList("Cinema NOS", "Universidade de Aveiro"));

            mockMvc.perform(get("/api/benefits/providers")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0]", is("Cinema NOS")))
                    .andExpect(jsonPath("$[1]", is("Universidade de Aveiro")));
        }

        @Test
        @DisplayName("Should return empty list when no providers")
        void shouldReturnEmptyListWhenNoProviders() throws Exception {
            when(benefitService.getAllProviders()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/providers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/sorted/points-asc Tests")
    class GetBenefitsSortedByPointsAscTests {

        @Test
        @DisplayName("Should return benefits sorted by points ascending")
        void shouldReturnBenefitsSortedByPointsAsc() throws Exception {
            when(benefitService.getBenefitsSortedByPointsAsc())
                    .thenReturn(Arrays.asList(benefitUA, benefitPartner));

            mockMvc.perform(get("/api/benefits/sorted/points-asc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].pointsRequired", is(100)))
                    .andExpect(jsonPath("$[1].pointsRequired", is(200)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/sorted/points-desc Tests")
    class GetBenefitsSortedByPointsDescTests {

        @Test
        @DisplayName("Should return benefits sorted by points descending")
        void shouldReturnBenefitsSortedByPointsDesc() throws Exception {
            when(benefitService.getBenefitsSortedByPointsDesc())
                    .thenReturn(Arrays.asList(benefitPartner, benefitUA));

            mockMvc.perform(get("/api/benefits/sorted/points-desc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].pointsRequired", is(200)))
                    .andExpect(jsonPath("$[1].pointsRequired", is(100)));
        }
    }

    @Nested
    @DisplayName("GET /api/benefits/volunteer/{volunteerId}/catalog Tests")
    class GetCatalogForVolunteerTests {

        @Test
        @DisplayName("Should return catalog for volunteer")
        void shouldReturnCatalogForVolunteer() throws Exception {
            when(benefitService.getCatalogForVolunteer(1L))
                    .thenReturn(Arrays.asList(benefitUA, benefitPartner));

            mockMvc.perform(get("/api/benefits/volunteer/1/catalog")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Desconto Cantina UA")))
                    .andExpect(jsonPath("$[1].name", is("Desconto Cinema")));
        }

        @Test
        @DisplayName("Should return 404 when volunteer not found")
        void shouldReturn404WhenVolunteerNotFoundForCatalog() throws Exception {
            when(benefitService.getCatalogForVolunteer(999L))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(get("/api/benefits/volunteer/999/catalog"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return empty catalog when no benefits")
        void shouldReturnEmptyCatalogWhenNoBenefits() throws Exception {
            when(benefitService.getCatalogForVolunteer(1L))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/volunteer/1/catalog"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large volunteer ID")
        void shouldHandleLargeVolunteerId() throws Exception {
            when(benefitService.getCatalogForVolunteer(Long.MAX_VALUE))
                    .thenReturn(Arrays.asList(benefitUA));

            mockMvc.perform(get("/api/benefits/volunteer/" + Long.MAX_VALUE + "/catalog"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should handle special characters in provider name")
        void shouldHandleSpecialCharactersInProviderName() throws Exception {
            when(benefitService.getBenefitsByProvider("Test%20Provider"))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/provider/Test%20Provider"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return JSON content type")
        void shouldReturnJsonContentType() throws Exception {
            when(benefitService.getAllActiveBenefits()).thenReturn(Arrays.asList(benefitUA));

            mockMvc.perform(get("/api/benefits"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Should handle benefit with null imageUrl")
        void shouldHandleBenefitWithNullImageUrl() throws Exception {
            benefitUA.setImageUrl(null);
            when(benefitService.getAllActiveBenefits()).thenReturn(Arrays.asList(benefitUA));

            mockMvc.perform(get("/api/benefits"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].imageUrl").doesNotExist());
        }
    }

    @Nested
    @DisplayName("Service Invocation Tests")
    class ServiceInvocationTests {

        @Test
        @DisplayName("Should invoke service method for getAllActiveBenefits")
        void shouldInvokeServiceForGetAllActiveBenefits() throws Exception {
            when(benefitService.getAllActiveBenefits()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits"));

            verify(benefitService, times(1)).getAllActiveBenefits();
        }

        @Test
        @DisplayName("Should invoke service method for getBenefitById")
        void shouldInvokeServiceForGetBenefitById() throws Exception {
            when(benefitService.getBenefitById(1L)).thenReturn(benefitUA);

            mockMvc.perform(get("/api/benefits/1"));

            verify(benefitService, times(1)).getBenefitById(1L);
        }

        @Test
        @DisplayName("Should invoke service method for getCatalogForVolunteer")
        void shouldInvokeServiceForGetCatalogForVolunteer() throws Exception {
            when(benefitService.getCatalogForVolunteer(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/benefits/volunteer/1/catalog"));

            verify(benefitService, times(1)).getCatalogForVolunteer(1L);
        }
    }

    @Nested
    @DisplayName("Response Content Tests")
    class ResponseContentTests {

        @Test
        @DisplayName("Should include all required fields in response")
        void shouldIncludeAllRequiredFieldsInResponse() throws Exception {
            when(benefitService.getBenefitById(1L)).thenReturn(benefitUA);

            mockMvc.perform(get("/api/benefits/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.description").exists())
                    .andExpect(jsonPath("$.pointsRequired").exists())
                    .andExpect(jsonPath("$.category").exists())
                    .andExpect(jsonPath("$.provider").exists())
                    .andExpect(jsonPath("$.active").exists())
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("Should return correct date format")
        void shouldReturnCorrectDateFormat() throws Exception {
            when(benefitService.getBenefitById(1L)).thenReturn(benefitUA);

            mockMvc.perform(get("/api/benefits/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.createdAt[0]", is(2024)))
                    .andExpect(jsonPath("$.createdAt[1]", is(1)))
                    .andExpect(jsonPath("$.createdAt[2]", is(15)));
        }

        @Test
        @DisplayName("Should handle multiple benefits with different categories")
        void shouldHandleMultipleBenefitsWithDifferentCategories() throws Exception {
            when(benefitService.getAllActiveBenefits())
                    .thenReturn(Arrays.asList(benefitUA, benefitPartner));

            mockMvc.perform(get("/api/benefits"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].category", is("UA")))
                    .andExpect(jsonPath("$[1].category", is("PARTNER")));
        }
    }
}
