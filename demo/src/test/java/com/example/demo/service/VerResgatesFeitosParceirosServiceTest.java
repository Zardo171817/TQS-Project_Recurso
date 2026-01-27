package com.example.demo.service;

import com.example.demo.dto.BenefitRedemptionDetailResponse;
import com.example.demo.dto.BenefitResponse;
import com.example.demo.dto.CreateBenefitRequest;
import com.example.demo.dto.PartnerRedemptionStatsResponse;
import com.example.demo.dto.UpdateBenefitRequest;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.RedemptionRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ver Resgates Feitos Parceiro - Service Tests")
class VerResgatesFeitosParceirosServiceTest {

    @Mock
    private BenefitRepository benefitRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private RedemptionRepository redemptionRepository;

    @InjectMocks
    private BenefitService benefitService;

    @InjectMocks
    private RedemptionService redemptionService;

    private Benefit partnerBenefit;
    private Benefit partnerBenefit2;
    private Benefit uaBenefit;
    private Volunteer volunteer;
    private Redemption redemption;

    @BeforeEach
    void setUp() {
        partnerBenefit = new Benefit();
        partnerBenefit.setId(1L);
        partnerBenefit.setName("Desconto Cinema NOS");
        partnerBenefit.setDescription("20% de desconto em bilhetes de cinema");
        partnerBenefit.setPointsRequired(150);
        partnerBenefit.setCategory(BenefitCategory.PARTNER);
        partnerBenefit.setProvider("Cinema NOS");
        partnerBenefit.setImageUrl("http://example.com/cinema.jpg");
        partnerBenefit.setActive(true);
        partnerBenefit.setCreatedAt(LocalDateTime.now());

        partnerBenefit2 = new Benefit();
        partnerBenefit2.setId(2L);
        partnerBenefit2.setName("Voucher Restaurante");
        partnerBenefit2.setDescription("Voucher de 10 euros no Restaurante Sabor");
        partnerBenefit2.setPointsRequired(300);
        partnerBenefit2.setCategory(BenefitCategory.PARTNER);
        partnerBenefit2.setProvider("Restaurante Sabor");
        partnerBenefit2.setActive(true);
        partnerBenefit2.setCreatedAt(LocalDateTime.now());

        uaBenefit = new Benefit();
        uaBenefit.setId(3L);
        uaBenefit.setName("Desconto Cantina UA");
        uaBenefit.setDescription("10% desconto na cantina da UA");
        uaBenefit.setPointsRequired(100);
        uaBenefit.setCategory(BenefitCategory.UA);
        uaBenefit.setProvider("Universidade de Aveiro");
        uaBenefit.setActive(true);
        uaBenefit.setCreatedAt(LocalDateTime.now());

        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Maria Silva");
        volunteer.setEmail("maria@email.com");
        volunteer.setTotalPoints(500);

        redemption = new Redemption();
        redemption.setId(1L);
        redemption.setVolunteer(volunteer);
        redemption.setBenefit(partnerBenefit);
        redemption.setPointsSpent(150);
        redemption.setStatus(RedemptionStatus.COMPLETED);
        redemption.setRedeemedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("BenefitService - getPartnerBenefits")
    class GetPartnerBenefitsTests {

        @Test
        @DisplayName("Should return all active partner benefits")
        void shouldReturnAllActivePartnerBenefits() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                    .thenReturn(Arrays.asList(partnerBenefit, partnerBenefit2));

            List<BenefitResponse> result = benefitService.getPartnerBenefits();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Desconto Cinema NOS", result.get(0).getName());
            assertEquals("Voucher Restaurante", result.get(1).getName());
            verify(benefitRepository).findByCategoryAndActiveTrue(BenefitCategory.PARTNER);
        }

        @Test
        @DisplayName("Should return empty list when no partner benefits")
        void shouldReturnEmptyListWhenNoPartnerBenefits() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                    .thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getPartnerBenefits();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return partner benefits with correct category")
        void shouldReturnPartnerBenefitsWithCorrectCategory() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                    .thenReturn(Arrays.asList(partnerBenefit));

            List<BenefitResponse> result = benefitService.getPartnerBenefits();

            assertEquals(1, result.size());
            assertEquals(BenefitCategory.PARTNER, result.get(0).getCategory());
        }
    }

    @Nested
    @DisplayName("BenefitService - getPartnerBenefitsByProvider")
    class GetPartnerBenefitsByProviderTests {

        @Test
        @DisplayName("Should return partner benefits filtered by provider")
        void shouldReturnPartnerBenefitsByProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Cinema"))
                    .thenReturn(Arrays.asList(partnerBenefit));

            List<BenefitResponse> result = benefitService.getPartnerBenefitsByProvider("Cinema");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Cinema NOS", result.get(0).getProvider());
        }

        @Test
        @DisplayName("Should return empty list when no matching provider")
        void shouldReturnEmptyListWhenNoMatchingProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Inexistente"))
                    .thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getPartnerBenefitsByProvider("Inexistente");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should filter out UA benefits from provider search")
        void shouldFilterOutUaBenefitsFromProviderSearch() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Aveiro"))
                    .thenReturn(Arrays.asList(uaBenefit));

            List<BenefitResponse> result = benefitService.getPartnerBenefitsByProvider("Aveiro");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("BenefitService - createPartnerBenefit")
    class CreatePartnerBenefitTests {

        @Test
        @DisplayName("Should create a partner benefit successfully")
        void shouldCreatePartnerBenefitSuccessfully() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Desconto Farmacia", "15% desconto", 200, "Farmacia Central", "http://img.jpg"
            );

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> {
                Benefit saved = inv.getArgument(0);
                saved.setId(10L);
                saved.setCreatedAt(LocalDateTime.now());
                saved.setActive(true);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(request);

            assertNotNull(result);
            assertEquals("Desconto Farmacia", result.getName());
            assertEquals(200, result.getPointsRequired());
            assertEquals("Farmacia Central", result.getProvider());
        }

        @Test
        @DisplayName("Should set category to PARTNER automatically")
        void shouldSetCategoryToPartner() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 100, "Provider", null
            );

            ArgumentCaptor<Benefit> captor = ArgumentCaptor.forClass(Benefit.class);
            when(benefitRepository.save(captor.capture())).thenAnswer(inv -> {
                Benefit saved = inv.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            benefitService.createPartnerBenefit(request);

            assertEquals(BenefitCategory.PARTNER, captor.getValue().getCategory());
        }

        @Test
        @DisplayName("Should handle null imageUrl")
        void shouldHandleNullImageUrl() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 100, "Provider", null
            );

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> {
                Benefit saved = inv.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(request);

            assertNotNull(result);
            assertNull(result.getImageUrl());
        }

        @Test
        @DisplayName("Should call repository save exactly once")
        void shouldCallRepositorySaveOnce() {
            CreateBenefitRequest request = new CreateBenefitRequest(
                    "Test", "desc", 100, "Provider", null
            );

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> {
                Benefit saved = inv.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            benefitService.createPartnerBenefit(request);

            verify(benefitRepository, times(1)).save(any(Benefit.class));
        }
    }

    @Nested
    @DisplayName("BenefitService - updatePartnerBenefit")
    class UpdatePartnerBenefitTests {

        @Test
        @DisplayName("Should update partner benefit successfully")
        void shouldUpdatePartnerBenefitSuccessfully() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest(
                    "Updated Name", "Updated desc", 200, "Updated Provider", "http://new.jpg"
            );

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> inv.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertNotNull(result);
            assertEquals("Updated Name", result.getName());
            assertEquals("Updated desc", result.getDescription());
            assertEquals(200, result.getPointsRequired());
        }

        @Test
        @DisplayName("Should throw exception when benefit not found")
        void shouldThrowExceptionWhenBenefitNotFound() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setName("Test");

            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> benefitService.updatePartnerBenefit(999L, updateRequest));
            verify(benefitRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when trying to update UA benefit")
        void shouldThrowExceptionWhenUpdatingUaBenefit() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setName("Test");

            when(benefitRepository.findById(3L)).thenReturn(Optional.of(uaBenefit));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> benefitService.updatePartnerBenefit(3L, updateRequest));
            assertTrue(ex.getMessage().contains("PARTNER"));
        }

        @Test
        @DisplayName("Should only update non-null fields")
        void shouldOnlyUpdateNonNullFields() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setName("Novo Nome");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> inv.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals("Novo Nome", result.getName());
            assertEquals("20% de desconto em bilhetes de cinema", result.getDescription());
            assertEquals(150, result.getPointsRequired());
        }

        @Test
        @DisplayName("Should update description only")
        void shouldUpdateDescriptionOnly() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setDescription("Nova descricao");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> inv.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals("Nova descricao", result.getDescription());
            assertEquals("Desconto Cinema NOS", result.getName());
        }

        @Test
        @DisplayName("Should update pointsRequired only")
        void shouldUpdatePointsOnly() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setPointsRequired(500);

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> inv.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals(500, result.getPointsRequired());
        }

        @Test
        @DisplayName("Should update provider only")
        void shouldUpdateProviderOnly() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setProvider("Novo Parceiro");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> inv.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals("Novo Parceiro", result.getProvider());
        }

        @Test
        @DisplayName("Should update imageUrl only")
        void shouldUpdateImageUrlOnly() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setImageUrl("http://updated.jpg");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> inv.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals("http://updated.jpg", result.getImageUrl());
        }
    }

    @Nested
    @DisplayName("BenefitService - deactivatePartnerBenefit")
    class DeactivatePartnerBenefitTests {

        @Test
        @DisplayName("Should deactivate partner benefit successfully")
        void shouldDeactivatePartnerBenefitSuccessfully() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> inv.getArgument(0));

            benefitService.deactivatePartnerBenefit(1L);

            assertFalse(partnerBenefit.getActive());
            verify(benefitRepository).save(partnerBenefit);
        }

        @Test
        @DisplayName("Should throw exception when benefit not found for deactivation")
        void shouldThrowExceptionWhenBenefitNotFound() {
            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> benefitService.deactivatePartnerBenefit(999L));
        }

        @Test
        @DisplayName("Should throw exception when trying to deactivate UA benefit")
        void shouldThrowExceptionWhenDeactivatingUaBenefit() {
            when(benefitRepository.findById(3L)).thenReturn(Optional.of(uaBenefit));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> benefitService.deactivatePartnerBenefit(3L));
            assertTrue(ex.getMessage().contains("PARTNER"));
        }
    }

    @Nested
    @DisplayName("BenefitService - getAllActiveBenefits")
    class GetAllActiveBenefitsTests {

        @Test
        @DisplayName("Should return all active benefits")
        void shouldReturnAllActiveBenefits() {
            when(benefitRepository.findByActiveTrue())
                    .thenReturn(Arrays.asList(partnerBenefit, uaBenefit));

            List<BenefitResponse> result = benefitService.getAllActiveBenefits();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no active benefits")
        void shouldReturnEmptyListWhenNoActiveBenefits() {
            when(benefitRepository.findByActiveTrue())
                    .thenReturn(Collections.emptyList());

            List<BenefitResponse> result = benefitService.getAllActiveBenefits();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("BenefitService - getBenefitById")
    class GetBenefitByIdTests {

        @Test
        @DisplayName("Should return benefit when found")
        void shouldReturnBenefitWhenFound() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when benefit not found")
        void shouldThrowExceptionWhenBenefitNotFound() {
            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> benefitService.getBenefitById(999L));
        }
    }

    @Nested
    @DisplayName("BenefitService - getBenefitsByCategory")
    class GetBenefitsByCategoryTests {

        @Test
        @DisplayName("Should return benefits by PARTNER category")
        void shouldReturnBenefitsByPartnerCategory() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                    .thenReturn(Arrays.asList(partnerBenefit, partnerBenefit2));

            List<BenefitResponse> result = benefitService.getBenefitsByCategory(BenefitCategory.PARTNER);

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return benefits by UA category")
        void shouldReturnBenefitsByUaCategory() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.UA))
                    .thenReturn(Arrays.asList(uaBenefit));

            List<BenefitResponse> result = benefitService.getBenefitsByCategory(BenefitCategory.UA);

            assertEquals(1, result.size());
            assertEquals(BenefitCategory.UA, result.get(0).getCategory());
        }
    }

    @Nested
    @DisplayName("BenefitService - getAffordableBenefitsForVolunteer")
    class GetAffordableBenefitsTests {

        @Test
        @DisplayName("Should return affordable benefits for volunteer")
        void shouldReturnAffordableBenefits() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(500))
                    .thenReturn(Arrays.asList(partnerBenefit, uaBenefit));

            List<BenefitResponse> result = benefitService.getAffordableBenefitsForVolunteer(1L);

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> benefitService.getAffordableBenefitsForVolunteer(999L));
        }
    }

    @Nested
    @DisplayName("BenefitService - getBenefitsByProvider")
    class GetBenefitsByProviderTests {

        @Test
        @DisplayName("Should return benefits by provider")
        void shouldReturnBenefitsByProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Cinema"))
                    .thenReturn(Arrays.asList(partnerBenefit));

            List<BenefitResponse> result = benefitService.getBenefitsByProvider("Cinema");

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("BenefitService - getAllProviders")
    class GetAllProvidersTests {

        @Test
        @DisplayName("Should return all providers")
        void shouldReturnAllProviders() {
            when(benefitRepository.findAllActiveProviders())
                    .thenReturn(Arrays.asList("Cinema NOS", "Restaurante Sabor"));

            List<String> result = benefitService.getAllProviders();

            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("BenefitService - getBenefitsSorted")
    class GetBenefitsSortedTests {

        @Test
        @DisplayName("Should return benefits sorted ascending")
        void shouldReturnBenefitsSortedAsc() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Arrays.asList(uaBenefit, partnerBenefit));

            List<BenefitResponse> result = benefitService.getBenefitsSortedByPointsAsc();

            assertEquals(2, result.size());
            assertTrue(result.get(0).getPointsRequired() <= result.get(1).getPointsRequired());
        }

        @Test
        @DisplayName("Should return benefits sorted descending")
        void shouldReturnBenefitsSortedDesc() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredDesc())
                    .thenReturn(Arrays.asList(partnerBenefit, uaBenefit));

            List<BenefitResponse> result = benefitService.getBenefitsSortedByPointsDesc();

            assertEquals(2, result.size());
            assertTrue(result.get(0).getPointsRequired() >= result.get(1).getPointsRequired());
        }
    }

    @Nested
    @DisplayName("BenefitService - getCatalogForVolunteer")
    class GetCatalogForVolunteerTests {

        @Test
        @DisplayName("Should return catalog for volunteer")
        void shouldReturnCatalogForVolunteer() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Arrays.asList(uaBenefit, partnerBenefit));

            List<BenefitResponse> result = benefitService.getCatalogForVolunteer(1L);

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found for catalog")
        void shouldThrowExceptionWhenVolunteerNotFoundForCatalog() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> benefitService.getCatalogForVolunteer(999L));
        }
    }

    @Nested
    @DisplayName("RedemptionService - getPartnerRedemptionStats")
    class GetPartnerRedemptionStatsTests {

        @Test
        @DisplayName("Should return partner redemption stats successfully")
        void shouldReturnPartnerRedemptionStatsSuccessfully() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Cinema NOS"))
                    .thenReturn(Arrays.asList(partnerBenefit));
            when(redemptionRepository.countCompletedByBenefitId(1L)).thenReturn(5L);
            when(redemptionRepository.sumPointsSpentByBenefitId(1L)).thenReturn(750L);
            when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Cinema NOS"))
                    .thenReturn(Arrays.asList(redemption));

            PartnerRedemptionStatsResponse stats = redemptionService.getPartnerRedemptionStats("Cinema NOS");

            assertNotNull(stats);
            assertEquals("Cinema NOS", stats.getProvider());
            assertEquals(1, stats.getTotalBenefits());
            assertEquals(5L, stats.getTotalRedemptions());
            assertEquals(750L, stats.getTotalPointsRedeemed());
            assertEquals(1, stats.getBenefitDetails().size());
            assertEquals(1, stats.getRecentRedemptions().size());
        }

        @Test
        @DisplayName("Should throw exception when no benefits found for provider")
        void shouldThrowExceptionWhenNoBenefitsFound() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Desconhecido"))
                    .thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getPartnerRedemptionStats("Desconhecido"));
        }

        @Test
        @DisplayName("Should throw exception when only UA benefits found for provider")
        void shouldThrowExceptionWhenOnlyUaBenefitsFound() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Universidade de Aveiro"))
                    .thenReturn(Arrays.asList(uaBenefit));

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getPartnerRedemptionStats("Universidade de Aveiro"));
        }

        @Test
        @DisplayName("Should calculate correct totals for multiple benefits")
        void shouldCalculateCorrectTotalsForMultipleBenefits() {
            Benefit benefit2 = new Benefit();
            benefit2.setId(5L);
            benefit2.setName("Pipocas Gratis");
            benefit2.setDescription("Pipocas gratis com bilhete");
            benefit2.setPointsRequired(50);
            benefit2.setCategory(BenefitCategory.PARTNER);
            benefit2.setProvider("Cinema NOS");
            benefit2.setActive(true);
            benefit2.setCreatedAt(LocalDateTime.now());

            when(benefitRepository.findByProviderContainingIgnoreCase("Cinema NOS"))
                    .thenReturn(Arrays.asList(partnerBenefit, benefit2));
            when(redemptionRepository.countCompletedByBenefitId(1L)).thenReturn(3L);
            when(redemptionRepository.sumPointsSpentByBenefitId(1L)).thenReturn(450L);
            when(redemptionRepository.countCompletedByBenefitId(5L)).thenReturn(2L);
            when(redemptionRepository.sumPointsSpentByBenefitId(5L)).thenReturn(100L);
            when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Cinema NOS"))
                    .thenReturn(Collections.emptyList());

            PartnerRedemptionStatsResponse stats = redemptionService.getPartnerRedemptionStats("Cinema NOS");

            assertEquals(2, stats.getTotalBenefits());
            assertEquals(5L, stats.getTotalRedemptions());
            assertEquals(550L, stats.getTotalPointsRedeemed());
            assertEquals(2, stats.getBenefitDetails().size());
        }

        @Test
        @DisplayName("Should return zero stats when no redemptions exist")
        void shouldReturnZeroStatsWhenNoRedemptions() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Cinema NOS"))
                    .thenReturn(Arrays.asList(partnerBenefit));
            when(redemptionRepository.countCompletedByBenefitId(1L)).thenReturn(0L);
            when(redemptionRepository.sumPointsSpentByBenefitId(1L)).thenReturn(0L);
            when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Cinema NOS"))
                    .thenReturn(Collections.emptyList());

            PartnerRedemptionStatsResponse stats = redemptionService.getPartnerRedemptionStats("Cinema NOS");

            assertEquals(0L, stats.getTotalRedemptions());
            assertEquals(0L, stats.getTotalPointsRedeemed());
            assertTrue(stats.getRecentRedemptions().isEmpty());
        }

        @Test
        @DisplayName("Should populate benefit details correctly")
        void shouldPopulateBenefitDetailsCorrectly() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Cinema NOS"))
                    .thenReturn(Arrays.asList(partnerBenefit));
            when(redemptionRepository.countCompletedByBenefitId(1L)).thenReturn(3L);
            when(redemptionRepository.sumPointsSpentByBenefitId(1L)).thenReturn(450L);
            when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Cinema NOS"))
                    .thenReturn(Collections.emptyList());

            PartnerRedemptionStatsResponse stats = redemptionService.getPartnerRedemptionStats("Cinema NOS");

            BenefitRedemptionDetailResponse detail = stats.getBenefitDetails().get(0);
            assertEquals(1L, detail.getBenefitId());
            assertEquals("Desconto Cinema NOS", detail.getBenefitName());
            assertEquals("20% de desconto em bilhetes de cinema", detail.getBenefitDescription());
            assertEquals(150, detail.getPointsRequired());
            assertEquals("Cinema NOS", detail.getProvider());
            assertTrue(detail.getActive());
            assertEquals(3L, detail.getTotalRedemptions());
            assertEquals(450L, detail.getTotalPointsRedeemed());
        }
    }

    @Nested
    @DisplayName("RedemptionService - getRedemptionsByProvider")
    class GetRedemptionsByProviderTests {

        @Test
        @DisplayName("Should return redemptions for provider")
        void shouldReturnRedemptionsForProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Cinema NOS"))
                    .thenReturn(Arrays.asList(partnerBenefit));
            when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Cinema NOS"))
                    .thenReturn(Arrays.asList(redemption));

            var result = redemptionService.getRedemptionsByProvider("Cinema NOS");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should throw exception when no benefits found for provider")
        void shouldThrowExceptionWhenNoBenefitsForProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Desconhecido"))
                    .thenReturn(Collections.emptyList());

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getRedemptionsByProvider("Desconhecido"));
        }

        @Test
        @DisplayName("Should return empty list when no redemptions for valid provider")
        void shouldReturnEmptyListWhenNoRedemptions() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Cinema NOS"))
                    .thenReturn(Arrays.asList(partnerBenefit));
            when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Cinema NOS"))
                    .thenReturn(Collections.emptyList());

            var result = redemptionService.getRedemptionsByProvider("Cinema NOS");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("RedemptionService - redeemPoints")
    class RedeemPointsTests {

        @Test
        @DisplayName("Should redeem points successfully")
        void shouldRedeemPointsSuccessfully() {
            var request = new com.example.demo.dto.RedeemPointsRequest();
            request.setVolunteerId(1L);
            request.setBenefitId(1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(inv -> inv.getArgument(0));
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(inv -> {
                Redemption saved = inv.getArgument(0);
                saved.setId(100L);
                return saved;
            });

            var result = redemptionService.redeemPoints(request);

            assertNotNull(result);
            assertEquals(150, result.getPointsSpent());
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found for redemption")
        void shouldThrowExceptionWhenVolunteerNotFoundForRedemption() {
            var request = new com.example.demo.dto.RedeemPointsRequest();
            request.setVolunteerId(999L);
            request.setBenefitId(1L);

            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.redeemPoints(request));
        }

        @Test
        @DisplayName("Should throw exception when benefit not found for redemption")
        void shouldThrowExceptionWhenBenefitNotFoundForRedemption() {
            var request = new com.example.demo.dto.RedeemPointsRequest();
            request.setVolunteerId(1L);
            request.setBenefitId(999L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.redeemPoints(request));
        }

        @Test
        @DisplayName("Should throw exception when benefit is inactive")
        void shouldThrowExceptionWhenBenefitInactive() {
            partnerBenefit.setActive(false);
            var request = new com.example.demo.dto.RedeemPointsRequest();
            request.setVolunteerId(1L);
            request.setBenefitId(1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));

            assertThrows(IllegalStateException.class,
                    () -> redemptionService.redeemPoints(request));
        }

        @Test
        @DisplayName("Should throw exception when insufficient points")
        void shouldThrowExceptionWhenInsufficientPoints() {
            volunteer.setTotalPoints(10);
            var request = new com.example.demo.dto.RedeemPointsRequest();
            request.setVolunteerId(1L);
            request.setBenefitId(1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));

            assertThrows(IllegalStateException.class,
                    () -> redemptionService.redeemPoints(request));
        }
    }

    @Nested
    @DisplayName("RedemptionService - getRedemptionById")
    class GetRedemptionByIdTests {

        @Test
        @DisplayName("Should return redemption by id")
        void shouldReturnRedemptionById() {
            when(redemptionRepository.findById(1L)).thenReturn(Optional.of(redemption));

            var result = redemptionService.getRedemptionById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when redemption not found")
        void shouldThrowExceptionWhenRedemptionNotFound() {
            when(redemptionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getRedemptionById(999L));
        }
    }

    @Nested
    @DisplayName("RedemptionService - getRedemptionsByVolunteer")
    class GetRedemptionsByVolunteerTests {

        @Test
        @DisplayName("Should return redemptions for volunteer")
        void shouldReturnRedemptionsForVolunteer() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdOrderByRedeemedAtDesc(1L))
                    .thenReturn(Arrays.asList(redemption));

            var result = redemptionService.getRedemptionsByVolunteer(1L);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getRedemptionsByVolunteer(999L));
        }
    }

    @Nested
    @DisplayName("RedemptionService - getCompletedRedemptionsByVolunteer")
    class GetCompletedRedemptionsByVolunteerTests {

        @Test
        @DisplayName("Should return completed redemptions")
        void shouldReturnCompletedRedemptions() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdAndStatus(1L, RedemptionStatus.COMPLETED))
                    .thenReturn(Arrays.asList(redemption));

            var result = redemptionService.getCompletedRedemptionsByVolunteer(1L);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getCompletedRedemptionsByVolunteer(999L));
        }
    }

    @Nested
    @DisplayName("RedemptionService - getTotalPointsSpent")
    class GetTotalPointsSpentTests {

        @Test
        @DisplayName("Should return total points spent")
        void shouldReturnTotalPointsSpent() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.sumPointsSpentByVolunteerId(1L)).thenReturn(450);

            Integer result = redemptionService.getTotalPointsSpent(1L);

            assertEquals(450, result);
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getTotalPointsSpent(999L));
        }
    }

    @Nested
    @DisplayName("RedemptionService - getRedemptionCount")
    class GetRedemptionCountTests {

        @Test
        @DisplayName("Should return redemption count")
        void shouldReturnRedemptionCount() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.countCompletedByVolunteerId(1L)).thenReturn(5L);

            Long result = redemptionService.getRedemptionCount(1L);

            assertEquals(5L, result);
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getRedemptionCount(999L));
        }
    }
}
