package com.example.demo.unit.service;

import com.example.demo.dto.PartnerRedemptionStatsResponse;
import com.example.demo.dto.RedeemPointsRequest;
import com.example.demo.dto.RedemptionResponse;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Redemption;
import com.example.demo.entity.Redemption.RedemptionStatus;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.RedemptionRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.RedemptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedemptionService Unit Tests")
class RedemptionServiceTest {

    @Mock
    private RedemptionRepository redemptionRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private BenefitRepository benefitRepository;

    @InjectMocks
    private RedemptionService redemptionService;

    private Volunteer volunteer;
    private Benefit benefit;
    private Redemption redemption;
    private RedeemPointsRequest redeemRequest;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John");
        volunteer.setEmail("john@example.com");
        volunteer.setTotalPoints(500);

        benefit = new Benefit();
        benefit.setId(1L);
        benefit.setName("Discount");
        benefit.setDescription("10% discount");
        benefit.setPointsRequired(100);
        benefit.setCategory(BenefitCategory.PARTNER);
        benefit.setProvider("Partner Store");
        benefit.setActive(true);
        benefit.setCreatedAt(LocalDateTime.now());

        redemption = new Redemption();
        redemption.setId(1L);
        redemption.setVolunteer(volunteer);
        redemption.setBenefit(benefit);
        redemption.setPointsSpent(100);
        redemption.setStatus(RedemptionStatus.COMPLETED);
        redemption.setRedeemedAt(LocalDateTime.now());

        redeemRequest = new RedeemPointsRequest();
        redeemRequest.setVolunteerId(1L);
        redeemRequest.setBenefitId(1L);
    }

    @Nested
    @DisplayName("Redeem Points Tests")
    class RedeemPointsTests {

        @Test
        @DisplayName("Should redeem points successfully")
        void shouldRedeemPointsSuccessfully() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefit));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption r = invocation.getArgument(0);
                r.setId(1L);
                return r;
            });

            RedemptionResponse response = redemptionService.redeemPoints(redeemRequest);

            assertThat(response.getPointsSpent()).isEqualTo(100);
            assertThat(response.getStatus()).isEqualTo(RedemptionStatus.COMPLETED);
            verify(volunteerRepository).save(argThat(v -> v.getTotalPoints() == 400)); // 500 - 100
        }

        @Test
        @DisplayName("Should fail when volunteer not found")
        void shouldFailWhenVolunteerNotFound() {
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());
            redeemRequest.setVolunteerId(999L);

            assertThatThrownBy(() -> redemptionService.redeemPoints(redeemRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Volunteer not found");
        }

        @Test
        @DisplayName("Should fail when benefit not found")
        void shouldFailWhenBenefitNotFound() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());
            redeemRequest.setBenefitId(999L);

            assertThatThrownBy(() -> redemptionService.redeemPoints(redeemRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Benefit not found");
        }

        @Test
        @DisplayName("Should fail when benefit is inactive")
        void shouldFailWhenBenefitIsInactive() {
            benefit.setActive(false);
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefit));

            assertThatThrownBy(() -> redemptionService.redeemPoints(redeemRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("not active");
        }

        @Test
        @DisplayName("Should fail when insufficient points")
        void shouldFailWhenInsufficientPoints() {
            volunteer.setTotalPoints(50); // Less than required 100
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefit));

            assertThatThrownBy(() -> redemptionService.redeemPoints(redeemRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Insufficient points");
        }
    }

    @Nested
    @DisplayName("Get Redemptions Tests")
    class GetRedemptionsTests {

        @Test
        @DisplayName("Should get redemption by ID")
        void shouldGetRedemptionById() {
            when(redemptionRepository.findById(1L)).thenReturn(Optional.of(redemption));

            RedemptionResponse response = redemptionService.getRedemptionById(1L);

            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception for non-existent redemption")
        void shouldThrowExceptionForNonExistent() {
            when(redemptionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> redemptionService.getRedemptionById(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should get redemptions by volunteer")
        void shouldGetRedemptionsByVolunteer() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdOrderByRedeemedAtDesc(1L))
                    .thenReturn(Collections.singletonList(redemption));

            List<RedemptionResponse> responses = redemptionService.getRedemptionsByVolunteer(1L);

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("Should get completed redemptions by volunteer")
        void shouldGetCompletedRedemptionsByVolunteer() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdAndStatus(1L, RedemptionStatus.COMPLETED))
                    .thenReturn(Collections.singletonList(redemption));

            List<RedemptionResponse> responses = redemptionService.getCompletedRedemptionsByVolunteer(1L);

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("Should get redemptions by provider")
        void shouldGetRedemptionsByProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Partner"))
                    .thenReturn(Collections.singletonList(benefit));
            when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Partner"))
                    .thenReturn(Collections.singletonList(redemption));

            List<RedemptionResponse> responses = redemptionService.getRedemptionsByProvider("Partner");

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception when no benefits for provider")
        void shouldThrowExceptionWhenNoBenefitsForProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Unknown"))
                    .thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> redemptionService.getRedemptionsByProvider("Unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Points Statistics Tests")
    class PointsStatisticsTests {

        @Test
        @DisplayName("Should get total points spent by volunteer")
        void shouldGetTotalPointsSpent() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.sumPointsSpentByVolunteerId(1L)).thenReturn(300);

            Integer totalSpent = redemptionService.getTotalPointsSpent(1L);

            assertThat(totalSpent).isEqualTo(300);
        }

        @Test
        @DisplayName("Should get redemption count by volunteer")
        void shouldGetRedemptionCount() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.countCompletedByVolunteerId(1L)).thenReturn(5L);

            Long count = redemptionService.getRedemptionCount(1L);

            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found for stats")
        void shouldThrowExceptionWhenVolunteerNotFoundForStats() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> redemptionService.getTotalPointsSpent(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Partner Stats Tests")
    class PartnerStatsTests {

        @Test
        @DisplayName("Should get partner redemption stats")
        void shouldGetPartnerRedemptionStats() {
            when(benefitRepository.findByProviderContainingIgnoreCase("Partner"))
                    .thenReturn(Collections.singletonList(benefit));
            when(redemptionRepository.countCompletedByBenefitId(1L)).thenReturn(10L);
            when(redemptionRepository.sumPointsSpentByBenefitId(1L)).thenReturn(1000L);
            when(redemptionRepository.findByBenefitProviderIgnoreCaseOrderByRedeemedAtDesc("Partner"))
                    .thenReturn(Collections.singletonList(redemption));

            PartnerRedemptionStatsResponse stats = redemptionService.getPartnerRedemptionStats("Partner");

            assertThat(stats.getProvider()).isEqualTo("Partner");
            assertThat(stats.getTotalBenefits()).isEqualTo(1);
            assertThat(stats.getTotalRedemptions()).isEqualTo(10L);
            assertThat(stats.getTotalPointsRedeemed()).isEqualTo(1000L);
        }

        @Test
        @DisplayName("Should throw exception when no partner benefits found")
        void shouldThrowExceptionWhenNoPartnerBenefitsFound() {
            Benefit uaBenefit = new Benefit();
            uaBenefit.setCategory(BenefitCategory.UA);
            uaBenefit.setProvider("UA");

            when(benefitRepository.findByProviderContainingIgnoreCase("UA"))
                    .thenReturn(Collections.singletonList(uaBenefit));

            assertThatThrownBy(() -> redemptionService.getPartnerRedemptionStats("UA"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No PARTNER benefits");
        }
    }
}
