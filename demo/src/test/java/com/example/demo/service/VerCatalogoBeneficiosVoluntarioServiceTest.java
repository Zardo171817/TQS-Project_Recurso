package com.example.demo.service;

import com.example.demo.dto.BenefitResponse;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ver Catalogo Beneficios Voluntario - Service Tests")
class VerCatalogoBeneficiosVoluntarioServiceTest {

    @Mock
    private BenefitRepository benefitRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private BenefitService benefitService;

    private Benefit benefitUA;
    private Benefit benefitPartner;
    private Volunteer volunteer;

    @BeforeEach
    void setUp() {
        benefitUA = new Benefit();
        benefitUA.setId(1L);
        benefitUA.setName("Desconto Cantina UA");
        benefitUA.setDescription("10% desconto na cantina da UA");
        benefitUA.setPointsRequired(100);
        benefitUA.setCategory(BenefitCategory.UA);
        benefitUA.setProvider("Universidade de Aveiro");
        benefitUA.setImageUrl("http://example.com/cantina.jpg");
        benefitUA.setActive(true);
        benefitUA.setCreatedAt(LocalDateTime.now());

        benefitPartner = new Benefit();
        benefitPartner.setId(2L);
        benefitPartner.setName("Desconto Cinema");
        benefitPartner.setDescription("20% desconto em bilhetes de cinema");
        benefitPartner.setPointsRequired(200);
        benefitPartner.setCategory(BenefitCategory.PARTNER);
        benefitPartner.setProvider("Cinema NOS");
        benefitPartner.setImageUrl("http://example.com/cinema.jpg");
        benefitPartner.setActive(true);
        benefitPartner.setCreatedAt(LocalDateTime.now());

        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("João Silva");
        volunteer.setEmail("joao@example.com");
        volunteer.setTotalPoints(150);
    }

    @Nested
    @DisplayName("getAllActiveBenefits Tests")
    class GetAllActiveBenefitsTests {

        @Test
        @DisplayName("Should return all active benefits")
        void shouldReturnAllActiveBenefits() {
            when(benefitRepository.findByActiveTrue()).thenReturn(Arrays.asList(benefitUA, benefitPartner));

            List<BenefitResponse> result = benefitService.getAllActiveBenefits();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Desconto Cantina UA", result.get(0).getName());
            assertEquals("Desconto Cinema", result.get(1).getName());
            verify(benefitRepository, times(1)).findByActiveTrue();
        }

        @Test
        @DisplayName("Should return empty list when no active benefits")
        void shouldReturnEmptyListWhenNoActiveBenefits() {
            when(benefitRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getAllActiveBenefits();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(benefitRepository, times(1)).findByActiveTrue();
        }

        @Test
        @DisplayName("Should return only active benefits")
        void shouldReturnOnlyActiveBenefits() {
            Benefit inactiveBenefit = new Benefit();
            inactiveBenefit.setId(3L);
            inactiveBenefit.setActive(false);

            when(benefitRepository.findByActiveTrue()).thenReturn(Arrays.asList(benefitUA));

            List<BenefitResponse> result = benefitService.getAllActiveBenefits();

            assertEquals(1, result.size());
            assertTrue(result.get(0).getActive());
        }
    }

    @Nested
    @DisplayName("getBenefitById Tests")
    class GetBenefitByIdTests {

        @Test
        @DisplayName("Should return benefit when found")
        void shouldReturnBenefitWhenFound() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Desconto Cantina UA", result.getName());
            assertEquals(100, result.getPointsRequired());
            verify(benefitRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when benefit not found")
        void shouldThrowExceptionWhenBenefitNotFound() {
            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> benefitService.getBenefitById(999L)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(benefitRepository, times(1)).findById(999L);
        }

        @Test
        @DisplayName("Should return correct benefit details")
        void shouldReturnCorrectBenefitDetails() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertEquals("10% desconto na cantina da UA", result.getDescription());
            assertEquals(BenefitCategory.UA, result.getCategory());
            assertEquals("Universidade de Aveiro", result.getProvider());
            assertEquals("http://example.com/cantina.jpg", result.getImageUrl());
        }
    }

    @Nested
    @DisplayName("getBenefitsByCategory Tests")
    class GetBenefitsByCategoryTests {

        @Test
        @DisplayName("Should return UA benefits")
        void shouldReturnUABenefits() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.UA))
                    .thenReturn(Arrays.asList(benefitUA));

            List<BenefitResponse> result = benefitService.getBenefitsByCategory(BenefitCategory.UA);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(BenefitCategory.UA, result.get(0).getCategory());
        }

        @Test
        @DisplayName("Should return Partner benefits")
        void shouldReturnPartnerBenefits() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                    .thenReturn(Arrays.asList(benefitPartner));

            List<BenefitResponse> result = benefitService.getBenefitsByCategory(BenefitCategory.PARTNER);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(BenefitCategory.PARTNER, result.get(0).getCategory());
        }

        @Test
        @DisplayName("Should return empty list when no benefits in category")
        void shouldReturnEmptyListWhenNoBenefitsInCategory() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.UA))
                    .thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getBenefitsByCategory(BenefitCategory.UA);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getAffordableBenefitsForVolunteer Tests")
    class GetAffordableBenefitsForVolunteerTests {

        @Test
        @DisplayName("Should return affordable benefits for volunteer")
        void shouldReturnAffordableBenefitsForVolunteer() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(150))
                    .thenReturn(Arrays.asList(benefitUA));

            List<BenefitResponse> result = benefitService.getAffordableBenefitsForVolunteer(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Desconto Cantina UA", result.get(0).getName());
            assertTrue(result.get(0).getPointsRequired() <= 150);
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> benefitService.getAffordableBenefitsForVolunteer(999L)
            );

            assertTrue(exception.getMessage().contains("999"));
        }

        @Test
        @DisplayName("Should return empty list when volunteer has no points")
        void shouldReturnEmptyListWhenVolunteerHasNoPoints() {
            volunteer.setTotalPoints(0);
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(0))
                    .thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getAffordableBenefitsForVolunteer(1L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return all benefits when volunteer has many points")
        void shouldReturnAllBenefitsWhenVolunteerHasManyPoints() {
            volunteer.setTotalPoints(1000);
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(1000))
                    .thenReturn(Arrays.asList(benefitUA, benefitPartner));

            List<BenefitResponse> result = benefitService.getAffordableBenefitsForVolunteer(1L);

            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("getBenefitsByProvider Tests")
    class GetBenefitsByProviderTests {

        @Test
        @DisplayName("Should return benefits by provider")
        void shouldReturnBenefitsByProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Universidade"))
                    .thenReturn(Arrays.asList(benefitUA));

            List<BenefitResponse> result = benefitService.getBenefitsByProvider("Universidade");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertTrue(result.get(0).getProvider().contains("Universidade"));
        }

        @Test
        @DisplayName("Should return empty list when provider not found")
        void shouldReturnEmptyListWhenProviderNotFound() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("NonExistent"))
                    .thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getBenefitsByProvider("NonExistent");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should be case insensitive")
        void shouldBeCaseInsensitive() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("UNIVERSIDADE"))
                    .thenReturn(Arrays.asList(benefitUA));

            List<BenefitResponse> result = benefitService.getBenefitsByProvider("UNIVERSIDADE");

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("getAllProviders Tests")
    class GetAllProvidersTests {

        @Test
        @DisplayName("Should return all providers")
        void shouldReturnAllProviders() {
            when(benefitRepository.findAllActiveProviders())
                    .thenReturn(Arrays.asList("Cinema NOS", "Universidade de Aveiro"));

            List<String> result = benefitService.getAllProviders();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains("Cinema NOS"));
            assertTrue(result.contains("Universidade de Aveiro"));
        }

        @Test
        @DisplayName("Should return empty list when no providers")
        void shouldReturnEmptyListWhenNoProviders() {
            when(benefitRepository.findAllActiveProviders()).thenReturn(Collections.emptyList());

            List<String> result = benefitService.getAllProviders();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getBenefitsSortedByPointsAsc Tests")
    class GetBenefitsSortedByPointsAscTests {

        @Test
        @DisplayName("Should return benefits sorted by points ascending")
        void shouldReturnBenefitsSortedByPointsAsc() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Arrays.asList(benefitUA, benefitPartner));

            List<BenefitResponse> result = benefitService.getBenefitsSortedByPointsAsc();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(100, result.get(0).getPointsRequired());
            assertEquals(200, result.get(1).getPointsRequired());
        }

        @Test
        @DisplayName("Should return empty list when no benefits")
        void shouldReturnEmptyListWhenNoBenefits() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getBenefitsSortedByPointsAsc();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getBenefitsSortedByPointsDesc Tests")
    class GetBenefitsSortedByPointsDescTests {

        @Test
        @DisplayName("Should return benefits sorted by points descending")
        void shouldReturnBenefitsSortedByPointsDesc() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredDesc())
                    .thenReturn(Arrays.asList(benefitPartner, benefitUA));

            List<BenefitResponse> result = benefitService.getBenefitsSortedByPointsDesc();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(200, result.get(0).getPointsRequired());
            assertEquals(100, result.get(1).getPointsRequired());
        }
    }

    @Nested
    @DisplayName("getCatalogForVolunteer Tests")
    class GetCatalogForVolunteerTests {

        @Test
        @DisplayName("Should return catalog for volunteer")
        void shouldReturnCatalogForVolunteer() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Arrays.asList(benefitUA, benefitPartner));

            List<BenefitResponse> result = benefitService.getCatalogForVolunteer(1L);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(volunteerRepository, times(1)).existsById(1L);
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> benefitService.getCatalogForVolunteer(999L)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(benefitRepository, never()).findByActiveTrueOrderByPointsRequiredAsc();
        }

        @Test
        @DisplayName("Should return empty catalog when no benefits available")
        void shouldReturnEmptyCatalogWhenNoBenefitsAvailable() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getCatalogForVolunteer(1L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle benefit with null imageUrl")
        void shouldHandleBenefitWithNullImageUrl() {
            benefitUA.setImageUrl(null);
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertNotNull(result);
            assertNull(result.getImageUrl());
        }

        @Test
        @DisplayName("Should handle benefit with zero points required")
        void shouldHandleBenefitWithZeroPointsRequired() {
            benefitUA.setPointsRequired(0);
            when(benefitRepository.findByActiveTrue()).thenReturn(Arrays.asList(benefitUA));

            List<BenefitResponse> result = benefitService.getAllActiveBenefits();

            assertEquals(1, result.size());
            assertEquals(0, result.get(0).getPointsRequired());
        }

        @Test
        @DisplayName("Should handle very large points values")
        void shouldHandleVeryLargePointsValues() {
            benefitUA.setPointsRequired(Integer.MAX_VALUE);
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertEquals(Integer.MAX_VALUE, result.getPointsRequired());
        }

        @Test
        @DisplayName("Should handle special characters in benefit name")
        void shouldHandleSpecialCharactersInBenefitName() {
            benefitUA.setName("Desconto 50% - Promoção Especial! @#$%");
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertEquals("Desconto 50% - Promoção Especial! @#$%", result.getName());
        }

        @Test
        @DisplayName("Should handle unicode characters in description")
        void shouldHandleUnicodeCharactersInDescription() {
            benefitUA.setDescription("Desconto válido para estudantes 学生 студенты");
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertEquals("Desconto válido para estudantes 学生 студенты", result.getDescription());
        }

        @Test
        @DisplayName("Should handle long benefit ID")
        void shouldHandleLongBenefitId() {
            benefitUA.setId(Long.MAX_VALUE);
            when(benefitRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.of(benefitUA));

            BenefitResponse result = benefitService.getBenefitById(Long.MAX_VALUE);

            assertEquals(Long.MAX_VALUE, result.getId());
        }
    }

    @Nested
    @DisplayName("Repository Integration Tests")
    class RepositoryIntegrationTests {

        @Test
        @DisplayName("Should verify repository method calls")
        void shouldVerifyRepositoryMethodCalls() {
            when(benefitRepository.findByActiveTrue()).thenReturn(Arrays.asList(benefitUA));

            benefitService.getAllActiveBenefits();

            verify(benefitRepository, times(1)).findByActiveTrue();
            verifyNoMoreInteractions(benefitRepository);
        }

        @Test
        @DisplayName("Should verify volunteer repository call for affordable benefits")
        void shouldVerifyVolunteerRepositoryCallForAffordableBenefits() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(150))
                    .thenReturn(Arrays.asList(benefitUA));

            benefitService.getAffordableBenefitsForVolunteer(1L);

            verify(volunteerRepository, times(1)).findById(1L);
            verify(benefitRepository, times(1)).findByPointsRequiredLessThanEqualAndActiveTrue(150);
        }

        @Test
        @DisplayName("Should verify catalog retrieval order")
        void shouldVerifyCatalogRetrievalOrder() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Arrays.asList(benefitUA, benefitPartner));

            benefitService.getCatalogForVolunteer(1L);

            verify(benefitRepository, times(1)).findByActiveTrueOrderByPointsRequiredAsc();
        }
    }

    @Nested
    @DisplayName("Data Mapping Tests")
    class DataMappingTests {

        @Test
        @DisplayName("Should correctly map all benefit fields")
        void shouldCorrectlyMapAllBenefitFields() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertEquals(benefitUA.getId(), result.getId());
            assertEquals(benefitUA.getName(), result.getName());
            assertEquals(benefitUA.getDescription(), result.getDescription());
            assertEquals(benefitUA.getPointsRequired(), result.getPointsRequired());
            assertEquals(benefitUA.getCategory(), result.getCategory());
            assertEquals(benefitUA.getProvider(), result.getProvider());
            assertEquals(benefitUA.getImageUrl(), result.getImageUrl());
            assertEquals(benefitUA.getActive(), result.getActive());
            assertEquals(benefitUA.getCreatedAt(), result.getCreatedAt());
        }

        @Test
        @DisplayName("Should correctly map multiple benefits")
        void shouldCorrectlyMapMultipleBenefits() {
            when(benefitRepository.findByActiveTrue()).thenReturn(Arrays.asList(benefitUA, benefitPartner));

            List<BenefitResponse> result = benefitService.getAllActiveBenefits();

            assertEquals(2, result.size());
            assertEquals(benefitUA.getName(), result.get(0).getName());
            assertEquals(benefitPartner.getName(), result.get(1).getName());
        }
    }
}
