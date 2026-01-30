package com.example.demo.unit.service;

import com.example.demo.dto.BenefitResponse;
import com.example.demo.dto.CreateBenefitRequest;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.BenefitService;
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
@DisplayName("BenefitService Unit Tests")
class BenefitServiceTest {

    @Mock
    private BenefitRepository benefitRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private BenefitService benefitService;

    private Benefit benefit;

    @BeforeEach
    void setUp() {
        benefit = new Benefit();
        benefit.setId(1L);
        benefit.setName("Test Benefit");
        benefit.setDescription("Test Description");
        benefit.setPointsRequired(100);
        benefit.setCategory(BenefitCategory.PARTNER);
        benefit.setProvider("Test Provider");
        benefit.setActive(true);
    }

    @Test
    @DisplayName("Should get all active benefits")
    void shouldGetAllActiveBenefits() {
        when(benefitRepository.findByActiveTrue()).thenReturn(Arrays.asList(benefit));

        List<BenefitResponse> responses = benefitService.getAllActiveBenefits();

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should get benefit by ID")
    void shouldGetBenefitById() {
        when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefit));

        BenefitResponse response = benefitService.getBenefitById(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existent benefit")
    void shouldThrowExceptionForNonExistent() {
        when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> benefitService.getBenefitById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should get benefits by category")
    void shouldGetBenefitsByCategory() {
        when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                .thenReturn(Arrays.asList(benefit));

        List<BenefitResponse> responses = benefitService.getBenefitsByCategory(BenefitCategory.PARTNER);

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should get affordable benefits for volunteer")
    void shouldGetAffordableBenefits() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setTotalPoints(150);

        when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
        when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(150))
                .thenReturn(Arrays.asList(benefit));

        List<BenefitResponse> responses = benefitService.getAffordableBenefitsForVolunteer(1L);

        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("Should create partner benefit")
    void shouldCreatePartnerBenefit() {
        CreateBenefitRequest request = new CreateBenefitRequest();
        request.setName("New Benefit");
        request.setDescription("Description");
        request.setPointsRequired(50);
        request.setProvider("Provider");
        
        //gravação funciona?
        when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
            Benefit b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });

        BenefitResponse response = benefitService.createPartnerBenefit(request);

        assertThat(response.getName()).isEqualTo("New Benefit");
    }
}
