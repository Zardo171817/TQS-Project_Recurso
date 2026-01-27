package com.example.demo;

import com.example.demo.dto.*;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.RedemptionRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.BenefitService;
import com.example.demo.service.RedemptionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ver Resgates - Service Unit Tests")
class VerResgatesServiceTest {

    @Mock private BenefitRepository benefitRepository;
    @Mock private VolunteerRepository volunteerRepository;
    @Mock private RedemptionRepository redemptionRepository;
    @InjectMocks private BenefitService benefitService;
    @InjectMocks private RedemptionService redemptionService;

    private Benefit partnerBenefit;
    private Benefit uaBenefit;
    private Volunteer volunteer;
    private Redemption redemption;

    @BeforeEach
    void setUp() {
        partnerBenefit = new Benefit();
        partnerBenefit.setId(1L);
        partnerBenefit.setName("Desconto Cinema");
        partnerBenefit.setDescription("20% desconto");
        partnerBenefit.setPointsRequired(150);
        partnerBenefit.setCategory(BenefitCategory.PARTNER);
        partnerBenefit.setProvider("Cinema NOS");
        partnerBenefit.setActive(true);
        partnerBenefit.setCreatedAt(LocalDateTime.now());

        uaBenefit = new Benefit();
        uaBenefit.setId(2L);
        uaBenefit.setName("Desconto Cantina");
        uaBenefit.setDescription("10% desconto");
        uaBenefit.setPointsRequired(100);
        uaBenefit.setCategory(BenefitCategory.UA);
        uaBenefit.setProvider("UA");
        uaBenefit.setActive(true);

        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Maria Silva");
        volunteer.setEmail("maria@test.com");
        volunteer.setTotalPoints(500);

        redemption = new Redemption();
        redemption.setId(1L);
        redemption.setVolunteer(volunteer);
        redemption.setBenefit(partnerBenefit);
        redemption.setPointsSpent(150);
        redemption.setStatus(RedemptionStatus.COMPLETED);
        redemption.setRedeemedAt(LocalDateTime.now());
    }

    // ========== BenefitService Tests ==========

    @Test
    @DisplayName("getPartnerBenefits - returns active partner benefits")
    void getPartnerBenefits_returnsActivePartnerBenefits() {
        when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                .thenReturn(List.of(partnerBenefit));
        List<BenefitResponse> result = benefitService.getPartnerBenefits();
        assertEquals(1, result.size());
        assertEquals(BenefitCategory.PARTNER, result.get(0).getCategory());
    }

    @Test
    @DisplayName("getPartnerBenefits - returns empty list when none")
    void getPartnerBenefits_returnsEmptyList() {
        when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                .thenReturn(Collections.emptyList());
        assertTrue(benefitService.getPartnerBenefits().isEmpty());
    }

    @Test
    @DisplayName("getPartnerBenefitsByProvider - filters by provider")
    void getPartnerBenefitsByProvider_filtersByProvider() {
        when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Cinema"))
                .thenReturn(List.of(partnerBenefit));
        List<BenefitResponse> result = benefitService.getPartnerBenefitsByProvider("Cinema");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getPartnerBenefitsByProvider - filters out UA benefits")
    void getPartnerBenefitsByProvider_filtersOutUaBenefits() {
        when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("UA"))
                .thenReturn(List.of(uaBenefit));
        assertTrue(benefitService.getPartnerBenefitsByProvider("UA").isEmpty());
    }

    @Test
    @DisplayName("createPartnerBenefit - creates with PARTNER category")
    void createPartnerBenefit_createsWithPartnerCategory() {
        CreateBenefitRequest request = new CreateBenefitRequest("Test", "desc", 100, "Provider", null);
        when(benefitRepository.save(any(Benefit.class))).thenAnswer(inv -> {
            Benefit b = inv.getArgument(0);
            b.setId(10L);
            return b;
        });
        BenefitResponse result = benefitService.createPartnerBenefit(request);
        assertNotNull(result);
        verify(benefitRepository).save(argThat(b -> b.getCategory() == BenefitCategory.PARTNER));
    }

    @Test
    @DisplayName("updatePartnerBenefit - updates successfully")
    void updatePartnerBenefit_updatesSuccessfully() {
        UpdateBenefitRequest request = new UpdateBenefitRequest("Updated", null, null, null, null);
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
        when(benefitRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        BenefitResponse result = benefitService.updatePartnerBenefit(1L, request);
        assertEquals("Updated", result.getName());
    }

    @Test
    @DisplayName("updatePartnerBenefit - throws when not found")
    void updatePartnerBenefit_throwsWhenNotFound() {
        when(benefitRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> benefitService.updatePartnerBenefit(999L, new UpdateBenefitRequest()));
    }

    @Test
    @DisplayName("updatePartnerBenefit - throws for UA benefit")
    void updatePartnerBenefit_throwsForUaBenefit() {
        when(benefitRepository.findById(2L)).thenReturn(Optional.of(uaBenefit));
        assertThrows(IllegalStateException.class,
                () -> benefitService.updatePartnerBenefit(2L, new UpdateBenefitRequest()));
    }

    @Test
    @DisplayName("deactivatePartnerBenefit - deactivates successfully")
    void deactivatePartnerBenefit_deactivatesSuccessfully() {
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
        when(benefitRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        benefitService.deactivatePartnerBenefit(1L);
        assertFalse(partnerBenefit.getActive());
    }

    @Test
    @DisplayName("deactivatePartnerBenefit - throws for UA benefit")
    void deactivatePartnerBenefit_throwsForUaBenefit() {
        when(benefitRepository.findById(2L)).thenReturn(Optional.of(uaBenefit));
        assertThrows(IllegalStateException.class, () -> benefitService.deactivatePartnerBenefit(2L));
    }

    @Test
    @DisplayName("getBenefitById - returns benefit")
    void getBenefitById_returnsBenefit() {
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
        assertNotNull(benefitService.getBenefitById(1L));
    }

    @Test
    @DisplayName("getBenefitById - throws when not found")
    void getBenefitById_throwsWhenNotFound() {
        when(benefitRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> benefitService.getBenefitById(999L));
    }

    @Test
    @DisplayName("getAffordableBenefitsForVolunteer - returns affordable")
    void getAffordableBenefitsForVolunteer_returnsAffordable() {
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(500))
                .thenReturn(List.of(partnerBenefit));
        assertEquals(1, benefitService.getAffordableBenefitsForVolunteer(1L).size());
    }

    // ========== RedemptionService Tests ==========

    @Test
    @DisplayName("getPartnerRedemptionStats - returns stats successfully")
    void getPartnerRedemptionStats_returnsStatsSuccessfully() {
        when(benefitRepository.findByProviderContainingIgnoreCase("Cinema NOS"))
                .thenReturn(List.of(partnerBenefit));
        when(redemptionRepository.countCompletedByBenefitId(1L)).thenReturn(5L);
        when(redemptionRepository.sumPointsSpentByBenefitId(1L)).thenReturn(750L);
        when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Cinema NOS"))
                .thenReturn(List.of(redemption));

        PartnerRedemptionStatsResponse stats = redemptionService.getPartnerRedemptionStats("Cinema NOS");

        assertEquals("Cinema NOS", stats.getProvider());
        assertEquals(1, stats.getTotalBenefits());
        assertEquals(5L, stats.getTotalRedemptions());
        assertEquals(750L, stats.getTotalPointsRedeemed());
    }

    @Test
    @DisplayName("getPartnerRedemptionStats - throws when provider not found")
    void getPartnerRedemptionStats_throwsWhenProviderNotFound() {
        when(benefitRepository.findByProviderContainingIgnoreCase("Unknown"))
                .thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class,
                () -> redemptionService.getPartnerRedemptionStats("Unknown"));
    }

    @Test
    @DisplayName("getPartnerRedemptionStats - throws when only UA benefits")
    void getPartnerRedemptionStats_throwsWhenOnlyUaBenefits() {
        when(benefitRepository.findByProviderContainingIgnoreCase("UA"))
                .thenReturn(List.of(uaBenefit));
        assertThrows(ResourceNotFoundException.class,
                () -> redemptionService.getPartnerRedemptionStats("UA"));
    }

    @Test
    @DisplayName("getRedemptionsByProvider - returns redemptions")
    void getRedemptionsByProvider_returnsRedemptions() {
        when(benefitRepository.findByProviderContainingIgnoreCase("Cinema NOS"))
                .thenReturn(List.of(partnerBenefit));
        when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Cinema NOS"))
                .thenReturn(List.of(redemption));
        assertEquals(1, redemptionService.getRedemptionsByProvider("Cinema NOS").size());
    }

    @Test
    @DisplayName("redeemPoints - redeems successfully")
    void redeemPoints_redeemsSuccessfully() {
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(1L);
        request.setBenefitId(1L);

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
        when(volunteerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(redemptionRepository.save(any())).thenAnswer(inv -> {
            Redemption r = inv.getArgument(0);
            r.setId(100L);
            return r;
        });

        RedemptionResponse result = redemptionService.redeemPoints(request);
        assertEquals(150, result.getPointsSpent());
        assertEquals(350, volunteer.getTotalPoints());
    }

    @Test
    @DisplayName("redeemPoints - throws when volunteer not found")
    void redeemPoints_throwsWhenVolunteerNotFound() {
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(999L);
        request.setBenefitId(1L);
        when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> redemptionService.redeemPoints(request));
    }

    @Test
    @DisplayName("redeemPoints - throws when benefit inactive")
    void redeemPoints_throwsWhenBenefitInactive() {
        partnerBenefit.setActive(false);
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(1L);
        request.setBenefitId(1L);
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
        assertThrows(IllegalStateException.class, () -> redemptionService.redeemPoints(request));
    }

    @Test
    @DisplayName("redeemPoints - throws when insufficient points")
    void redeemPoints_throwsWhenInsufficientPoints() {
        volunteer.setTotalPoints(10);
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(1L);
        request.setBenefitId(1L);
        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
        assertThrows(IllegalStateException.class, () -> redemptionService.redeemPoints(request));
    }

    @Test
    @DisplayName("getRedemptionById - returns redemption")
    void getRedemptionById_returnsRedemption() {
        when(redemptionRepository.findById(1L)).thenReturn(Optional.of(redemption));
        assertNotNull(redemptionService.getRedemptionById(1L));
    }

    @Test
    @DisplayName("getRedemptionsByVolunteer - returns redemptions")
    void getRedemptionsByVolunteer_returnsRedemptions() {
        when(volunteerRepository.existsById(1L)).thenReturn(true);
        when(redemptionRepository.findByVolunteerIdOrderByRedeemedAtDesc(1L))
                .thenReturn(List.of(redemption));
        assertEquals(1, redemptionService.getRedemptionsByVolunteer(1L).size());
    }

    @Test
    @DisplayName("getTotalPointsSpent - returns total")
    void getTotalPointsSpent_returnsTotal() {
        when(volunteerRepository.existsById(1L)).thenReturn(true);
        when(redemptionRepository.sumPointsSpentByVolunteerId(1L)).thenReturn(450);
        assertEquals(450, redemptionService.getTotalPointsSpent(1L));
    }

    @Test
    @DisplayName("getRedemptionCount - returns count")
    void getRedemptionCount_returnsCount() {
        when(volunteerRepository.existsById(1L)).thenReturn(true);
        when(redemptionRepository.countCompletedByVolunteerId(1L)).thenReturn(5L);
        assertEquals(5L, redemptionService.getRedemptionCount(1L));
    }
}
