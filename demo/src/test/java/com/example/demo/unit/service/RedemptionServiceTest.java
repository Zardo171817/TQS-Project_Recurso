package com.example.demo.unit.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John");
        volunteer.setEmail("john@example.com");
        volunteer.setTotalPoints(500);

        benefit = new Benefit();
        benefit.setId(1L);
        benefit.setName("Test Benefit");
        benefit.setPointsRequired(100);
        benefit.setCategory(BenefitCategory.PARTNER);
        benefit.setProvider("Test Provider");
        benefit.setActive(true);

        redemption = new Redemption();
        redemption.setId(1L);
        redemption.setVolunteer(volunteer);
        redemption.setBenefit(benefit);
        redemption.setPointsSpent(100);
        redemption.setStatus(RedemptionStatus.COMPLETED);
    }

    //resgate
    @Test
    @DisplayName("Should redeem points successfully")
    void shouldRedeemPointsSuccessfully() {
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(1L);
        request.setBenefitId(1L);

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefit));
        when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
            Redemption r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });
        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);

        RedemptionResponse response = redemptionService.redeemPoints(request);

        assertThat(response.getPointsSpent()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should fail when insufficient points")
    void shouldFailWhenInsufficientPoints() {
        volunteer.setTotalPoints(50); // Less than required
        RedeemPointsRequest request = new RedeemPointsRequest();
        request.setVolunteerId(1L);
        request.setBenefitId(1L);

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefit));

        assertThatThrownBy(() -> redemptionService.redeemPoints(request))
                .isInstanceOf(IllegalStateException.class);
    }

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
        when(redemptionRepository.findByVolunteerIdOrderByRedeemedAtDesc(1L)).thenReturn(Arrays.asList(redemption));

        List<RedemptionResponse> responses = redemptionService.getRedemptionsByVolunteer(1L);

        assertThat(responses).hasSize(1);
    }
}
