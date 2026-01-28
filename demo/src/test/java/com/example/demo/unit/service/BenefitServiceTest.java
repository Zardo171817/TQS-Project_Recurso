package com.example.demo.unit.service;

import com.example.demo.dto.BenefitResponse;
import com.example.demo.dto.CreateBenefitRequest;
import com.example.demo.dto.UpdateBenefitRequest;
import com.example.demo.entity.Benefit;
import com.example.demo.entity.Benefit.BenefitCategory;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BenefitRepository;
import com.example.demo.repository.VolunteerRepository;
import com.example.demo.service.BenefitService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BenefitService Unit Tests")
class BenefitServiceTest {

    @Mock
    private BenefitRepository benefitRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private BenefitService benefitService;

    private Benefit uaBenefit;
    private Benefit partnerBenefit;
    private Volunteer volunteer;

    @BeforeEach
    void setUp() {
        uaBenefit = new Benefit();
        uaBenefit.setId(1L);
        uaBenefit.setName("UA Discount");
        uaBenefit.setDescription("Discount at UA canteen");
        uaBenefit.setPointsRequired(100);
        uaBenefit.setCategory(BenefitCategory.UA);
        uaBenefit.setProvider("UA");
        uaBenefit.setActive(true);
        uaBenefit.setCreatedAt(LocalDateTime.now());

        partnerBenefit = new Benefit();
        partnerBenefit.setId(2L);
        partnerBenefit.setName("Partner Discount");
        partnerBenefit.setDescription("10% off at partner store");
        partnerBenefit.setPointsRequired(200);
        partnerBenefit.setCategory(BenefitCategory.PARTNER);
        partnerBenefit.setProvider("Partner Store");
        partnerBenefit.setActive(true);
        partnerBenefit.setCreatedAt(LocalDateTime.now());

        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("John");
        volunteer.setEmail("john@example.com");
        volunteer.setTotalPoints(150);
    }

    @Nested
    @DisplayName("Get Benefits Tests")
    class GetBenefitsTests {

        @Test
        @DisplayName("Should get all active benefits")
        void shouldGetAllActiveBenefits() {
            when(benefitRepository.findByActiveTrue()).thenReturn(Arrays.asList(uaBenefit, partnerBenefit));

            List<BenefitResponse> responses = benefitService.getAllActiveBenefits();

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("Should get benefit by ID")
        void shouldGetBenefitById() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(uaBenefit));

            BenefitResponse response = benefitService.getBenefitById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("UA Discount");
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
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.UA))
                    .thenReturn(Collections.singletonList(uaBenefit));

            List<BenefitResponse> responses = benefitService.getBenefitsByCategory(BenefitCategory.UA);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getCategory()).isEqualTo(BenefitCategory.UA);
        }

        @Test
        @DisplayName("Should get affordable benefits for volunteer")
        void shouldGetAffordableBenefitsForVolunteer() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(150))
                    .thenReturn(Collections.singletonList(uaBenefit)); // 100 points required <= 150

            List<BenefitResponse> responses = benefitService.getAffordableBenefitsForVolunteer(1L);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getPointsRequired()).isLessThanOrEqualTo(150);
        }

        @Test
        @DisplayName("Should throw exception if volunteer not found when getting affordable benefits")
        void shouldThrowExceptionIfVolunteerNotFound() {
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> benefitService.getAffordableBenefitsForVolunteer(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should get benefits by provider")
        void shouldGetBenefitsByProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Partner"))
                    .thenReturn(Collections.singletonList(partnerBenefit));

            List<BenefitResponse> responses = benefitService.getBenefitsByProvider("Partner");

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("Should get all providers")
        void shouldGetAllProviders() {
            when(benefitRepository.findAllActiveProviders())
                    .thenReturn(Arrays.asList("UA", "Partner Store"));

            List<String> providers = benefitService.getAllProviders();

            assertThat(providers).hasSize(2);
            assertThat(providers).contains("UA", "Partner Store");
        }
    }

    @Nested
    @DisplayName("Sorted Benefits Tests")
    class SortedBenefitsTests {

        @Test
        @DisplayName("Should get benefits sorted by points ascending")
        void shouldGetBenefitsSortedByPointsAsc() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Arrays.asList(uaBenefit, partnerBenefit)); // 100, 200

            List<BenefitResponse> responses = benefitService.getBenefitsSortedByPointsAsc();

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getPointsRequired()).isLessThan(responses.get(1).getPointsRequired());
        }

        @Test
        @DisplayName("Should get benefits sorted by points descending")
        void shouldGetBenefitsSortedByPointsDesc() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredDesc())
                    .thenReturn(Arrays.asList(partnerBenefit, uaBenefit)); // 200, 100

            List<BenefitResponse> responses = benefitService.getBenefitsSortedByPointsDesc();

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getPointsRequired()).isGreaterThan(responses.get(1).getPointsRequired());
        }
    }

    @Nested
    @DisplayName("Catalog Tests")
    class CatalogTests {

        @Test
        @DisplayName("Should get catalog for volunteer")
        void shouldGetCatalogForVolunteer() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Arrays.asList(uaBenefit, partnerBenefit));

            List<BenefitResponse> responses = benefitService.getCatalogForVolunteer(1L);

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("Should throw exception if volunteer not found for catalog")
        void shouldThrowExceptionIfVolunteerNotFoundForCatalog() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> benefitService.getCatalogForVolunteer(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Partner Benefit CRUD Tests")
    class PartnerBenefitCrudTests {

        @Test
        @DisplayName("Should create partner benefit")
        void shouldCreatePartnerBenefit() {
            CreateBenefitRequest request = new CreateBenefitRequest();
            request.setName("New Partner Benefit");
            request.setDescription("New description");
            request.setPointsRequired(300);
            request.setProvider("New Partner");
            request.setImageUrl("http://example.com/image.jpg");

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit b = invocation.getArgument(0);
                b.setId(3L);
                return b;
            });

            BenefitResponse response = benefitService.createPartnerBenefit(request);

            assertThat(response.getName()).isEqualTo("New Partner Benefit");
            verify(benefitRepository).save(argThat(b -> b.getCategory() == BenefitCategory.PARTNER));
        }

        @Test
        @DisplayName("Should update partner benefit")
        void shouldUpdatePartnerBenefit() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Updated Name");
            request.setPointsRequired(250);

            when(benefitRepository.findById(2L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenReturn(partnerBenefit);

            BenefitResponse response = benefitService.updatePartnerBenefit(2L, request);

            assertThat(response).isNotNull();
            verify(benefitRepository).save(any(Benefit.class));
        }

        @Test
        @DisplayName("Should fail to update non-partner benefit")
        void shouldFailToUpdateNonPartnerBenefit() {
            UpdateBenefitRequest request = new UpdateBenefitRequest();
            request.setName("Updated Name");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(uaBenefit)); // UA category

            assertThatThrownBy(() -> benefitService.updatePartnerBenefit(1L, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only PARTNER benefits");
        }

        @Test
        @DisplayName("Should deactivate partner benefit")
        void shouldDeactivatePartnerBenefit() {
            when(benefitRepository.findById(2L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenReturn(partnerBenefit);

            benefitService.deactivatePartnerBenefit(2L);

            verify(benefitRepository).save(argThat(b -> !b.getActive()));
        }

        @Test
        @DisplayName("Should fail to deactivate non-partner benefit")
        void shouldFailToDeactivateNonPartnerBenefit() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(uaBenefit));

            assertThatThrownBy(() -> benefitService.deactivatePartnerBenefit(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only PARTNER benefits");
        }

        @Test
        @DisplayName("Should get partner benefits")
        void shouldGetPartnerBenefits() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                    .thenReturn(Collections.singletonList(partnerBenefit));

            List<BenefitResponse> responses = benefitService.getPartnerBenefits();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getCategory()).isEqualTo(BenefitCategory.PARTNER);
        }

        @Test
        @DisplayName("Should get partner benefits by provider")
        void shouldGetPartnerBenefitsByProvider() {
            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Partner"))
                    .thenReturn(Collections.singletonList(partnerBenefit));

            List<BenefitResponse> responses = benefitService.getPartnerBenefitsByProvider("Partner");

            assertThat(responses).hasSize(1);
        }
    }
}
