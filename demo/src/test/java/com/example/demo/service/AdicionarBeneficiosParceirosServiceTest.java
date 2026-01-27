package com.example.demo.service;

import com.example.demo.dto.BenefitResponse;
import com.example.demo.dto.CreateBenefitRequest;
import com.example.demo.dto.UpdateBenefitRequest;
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
@DisplayName("Adicionar Beneficios Parceiro - Service Tests")
class AdicionarBeneficiosParceirosServiceTest {

    @Mock
    private BenefitRepository benefitRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @InjectMocks
    private BenefitService benefitService;

    private Benefit partnerBenefit;
    private Benefit partnerBenefit2;
    private Benefit uaBenefit;
    private CreateBenefitRequest createRequest;

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
        partnerBenefit2.setImageUrl(null);
        partnerBenefit2.setActive(true);
        partnerBenefit2.setCreatedAt(LocalDateTime.now());

        uaBenefit = new Benefit();
        uaBenefit.setId(3L);
        uaBenefit.setName("Desconto Cantina UA");
        uaBenefit.setDescription("10% desconto na cantina da UA");
        uaBenefit.setPointsRequired(100);
        uaBenefit.setCategory(BenefitCategory.UA);
        uaBenefit.setProvider("Universidade de Aveiro");
        uaBenefit.setImageUrl(null);
        uaBenefit.setActive(true);
        uaBenefit.setCreatedAt(LocalDateTime.now());

        createRequest = new CreateBenefitRequest();
        createRequest.setName("Desconto Farmacia");
        createRequest.setDescription("15% desconto em produtos de saude");
        createRequest.setPointsRequired(200);
        createRequest.setProvider("Farmacia Central");
        createRequest.setImageUrl("http://example.com/farmacia.jpg");
    }

    @Nested
    @DisplayName("createPartnerBenefit Tests")
    class CreatePartnerBenefitTests {

        @Test
        @DisplayName("Should create a partner benefit successfully")
        void shouldCreatePartnerBenefitSuccessfully() {
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                saved.setCreatedAt(LocalDateTime.now());
                saved.setActive(true);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(createRequest);

            assertNotNull(result);
            assertEquals("Desconto Farmacia", result.getName());
            assertEquals("15% desconto em produtos de saude", result.getDescription());
            assertEquals(200, result.getPointsRequired());
            assertEquals("Farmacia Central", result.getProvider());
            assertEquals("http://example.com/farmacia.jpg", result.getImageUrl());
        }

        @Test
        @DisplayName("Should set category to PARTNER automatically")
        void shouldSetCategoryToPartner() {
            ArgumentCaptor<Benefit> captor = ArgumentCaptor.forClass(Benefit.class);
            when(benefitRepository.save(captor.capture())).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            benefitService.createPartnerBenefit(createRequest);

            assertEquals(BenefitCategory.PARTNER, captor.getValue().getCategory());
        }

        @Test
        @DisplayName("Should handle null imageUrl")
        void shouldHandleNullImageUrl() {
            createRequest.setImageUrl(null);

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(createRequest);

            assertNotNull(result);
            assertNull(result.getImageUrl());
        }

        @Test
        @DisplayName("Should set all fields correctly from request")
        void shouldSetAllFieldsCorrectlyFromRequest() {
            ArgumentCaptor<Benefit> captor = ArgumentCaptor.forClass(Benefit.class);
            when(benefitRepository.save(captor.capture())).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            benefitService.createPartnerBenefit(createRequest);

            Benefit captured = captor.getValue();
            assertEquals("Desconto Farmacia", captured.getName());
            assertEquals("15% desconto em produtos de saude", captured.getDescription());
            assertEquals(200, captured.getPointsRequired());
            assertEquals(BenefitCategory.PARTNER, captured.getCategory());
            assertEquals("Farmacia Central", captured.getProvider());
            assertEquals("http://example.com/farmacia.jpg", captured.getImageUrl());
        }

        @Test
        @DisplayName("Should call repository save exactly once")
        void shouldCallRepositorySaveOnce() {
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            benefitService.createPartnerBenefit(createRequest);

            verify(benefitRepository, times(1)).save(any(Benefit.class));
        }

        @Test
        @DisplayName("Should return response with correct id from saved entity")
        void shouldReturnResponseWithCorrectId() {
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(42L);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(createRequest);

            assertEquals(42L, result.getId());
        }

        @Test
        @DisplayName("Should handle benefit with long description")
        void shouldHandleBenefitWithLongDescription() {
            String longDescription = "A".repeat(1000);
            createRequest.setDescription(longDescription);

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(createRequest);

            assertEquals(longDescription, result.getDescription());
        }

        @Test
        @DisplayName("Should handle benefit with special characters in name")
        void shouldHandleBenefitWithSpecialCharacters() {
            createRequest.setName("Cafe & Restaurante - Descricao Especial");

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(createRequest);

            assertEquals("Cafe & Restaurante - Descricao Especial", result.getName());
        }

        @Test
        @DisplayName("Should handle minimum points required value")
        void shouldHandleMinimumPointsRequired() {
            createRequest.setPointsRequired(1);

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(createRequest);

            assertEquals(1, result.getPointsRequired());
        }

        @Test
        @DisplayName("Should handle large points required value")
        void shouldHandleLargePointsRequired() {
            createRequest.setPointsRequired(999999);

            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> {
                Benefit saved = invocation.getArgument(0);
                saved.setId(10L);
                return saved;
            });

            BenefitResponse result = benefitService.createPartnerBenefit(createRequest);

            assertEquals(999999, result.getPointsRequired());
        }
    }

    @Nested
    @DisplayName("updatePartnerBenefit Tests")
    class UpdatePartnerBenefitTests {

        @Test
        @DisplayName("Should update partner benefit successfully")
        void shouldUpdatePartnerBenefitSuccessfully() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setName("Desconto Cinema Atualizado");
            updateRequest.setDescription("25% de desconto em bilhetes");
            updateRequest.setPointsRequired(180);
            updateRequest.setProvider("Cinema NOS Atualizado");
            updateRequest.setImageUrl("http://example.com/new-cinema.jpg");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertNotNull(result);
            assertEquals("Desconto Cinema Atualizado", result.getName());
            assertEquals("25% de desconto em bilhetes", result.getDescription());
            assertEquals(180, result.getPointsRequired());
            assertEquals("Cinema NOS Atualizado", result.getProvider());
        }

        @Test
        @DisplayName("Should throw exception when benefit not found")
        void shouldThrowExceptionWhenBenefitNotFound() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setName("Test");

            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> benefitService.updatePartnerBenefit(999L, updateRequest)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(benefitRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when trying to update UA benefit")
        void shouldThrowExceptionWhenUpdatingUaBenefit() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setName("Test");

            when(benefitRepository.findById(3L)).thenReturn(Optional.of(uaBenefit));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> benefitService.updatePartnerBenefit(3L, updateRequest)
            );

            assertTrue(exception.getMessage().contains("PARTNER"));
            verify(benefitRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should only update non-null fields")
        void shouldOnlyUpdateNonNullFields() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setName("Novo Nome");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals("Novo Nome", result.getName());
            assertEquals("20% de desconto em bilhetes de cinema", result.getDescription());
            assertEquals(150, result.getPointsRequired());
            assertEquals("Cinema NOS", result.getProvider());
        }

        @Test
        @DisplayName("Should update only description when only description provided")
        void shouldUpdateOnlyDescription() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setDescription("Nova descricao do beneficio");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals("Desconto Cinema NOS", result.getName());
            assertEquals("Nova descricao do beneficio", result.getDescription());
        }

        @Test
        @DisplayName("Should update only points when only points provided")
        void shouldUpdateOnlyPoints() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setPointsRequired(500);

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals(500, result.getPointsRequired());
            assertEquals("Desconto Cinema NOS", result.getName());
        }

        @Test
        @DisplayName("Should update imageUrl")
        void shouldUpdateImageUrl() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setImageUrl("http://example.com/updated.jpg");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals("http://example.com/updated.jpg", result.getImageUrl());
        }

        @Test
        @DisplayName("Should call save after update")
        void shouldCallSaveAfterUpdate() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setName("Updated");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            benefitService.updatePartnerBenefit(1L, updateRequest);

            verify(benefitRepository, times(1)).findById(1L);
            verify(benefitRepository, times(1)).save(any(Benefit.class));
        }

        @Test
        @DisplayName("Should update provider field")
        void shouldUpdateProviderField() {
            UpdateBenefitRequest updateRequest = new UpdateBenefitRequest();
            updateRequest.setProvider("Novo Parceiro");

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BenefitResponse result = benefitService.updatePartnerBenefit(1L, updateRequest);

            assertEquals("Novo Parceiro", result.getProvider());
        }
    }

    @Nested
    @DisplayName("deactivatePartnerBenefit Tests")
    class DeactivatePartnerBenefitTests {

        @Test
        @DisplayName("Should deactivate partner benefit successfully")
        void shouldDeactivatePartnerBenefitSuccessfully() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            benefitService.deactivatePartnerBenefit(1L);

            assertFalse(partnerBenefit.getActive());
            verify(benefitRepository, times(1)).save(partnerBenefit);
        }

        @Test
        @DisplayName("Should throw exception when benefit not found")
        void shouldThrowExceptionWhenBenefitNotFound() {
            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> benefitService.deactivatePartnerBenefit(999L)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(benefitRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when trying to deactivate UA benefit")
        void shouldThrowExceptionWhenDeactivatingUaBenefit() {
            when(benefitRepository.findById(3L)).thenReturn(Optional.of(uaBenefit));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> benefitService.deactivatePartnerBenefit(3L)
            );

            assertTrue(exception.getMessage().contains("PARTNER"));
            verify(benefitRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should verify repository calls on deactivation")
        void shouldVerifyRepositoryCallsOnDeactivation() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenReturn(partnerBenefit);

            benefitService.deactivatePartnerBenefit(1L);

            verify(benefitRepository, times(1)).findById(1L);
            verify(benefitRepository, times(1)).save(partnerBenefit);
        }

        @Test
        @DisplayName("Should set active to false on deactivation")
        void shouldSetActiveToFalse() {
            assertTrue(partnerBenefit.getActive());

            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));
            when(benefitRepository.save(any(Benefit.class))).thenReturn(partnerBenefit);

            benefitService.deactivatePartnerBenefit(1L);

            assertFalse(partnerBenefit.getActive());
        }
    }

    @Nested
    @DisplayName("getPartnerBenefits Tests")
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
        @DisplayName("Should verify repository call")
        void shouldVerifyRepositoryCall() {
            when(benefitRepository.findByCategoryAndActiveTrue(BenefitCategory.PARTNER))
                    .thenReturn(Collections.emptyList());

            benefitService.getPartnerBenefits();

            verify(benefitRepository, times(1)).findByCategoryAndActiveTrue(BenefitCategory.PARTNER);
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
    @DisplayName("getPartnerBenefitsByProvider Tests")
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

        @Test
        @DisplayName("Should return multiple partner benefits for same provider")
        void shouldReturnMultiplePartnerBenefitsForSameProvider() {
            Benefit anotherCinemaBenefit = new Benefit();
            anotherCinemaBenefit.setId(5L);
            anotherCinemaBenefit.setName("Pipocas Gratis");
            anotherCinemaBenefit.setDescription("Pipocas gratis com bilhete");
            anotherCinemaBenefit.setPointsRequired(50);
            anotherCinemaBenefit.setCategory(BenefitCategory.PARTNER);
            anotherCinemaBenefit.setProvider("Cinema NOS");
            anotherCinemaBenefit.setActive(true);
            anotherCinemaBenefit.setCreatedAt(LocalDateTime.now());

            when(benefitRepository.findByProviderContainingIgnoreCaseAndActiveTrue("Cinema"))
                    .thenReturn(Arrays.asList(partnerBenefit, anotherCinemaBenefit));

            List<BenefitResponse> result = benefitService.getPartnerBenefitsByProvider("Cinema");

            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("getAllActiveBenefits Tests")
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
    @DisplayName("getBenefitById Tests")
    class GetBenefitByIdTests {

        @Test
        @DisplayName("Should return benefit when found")
        void shouldReturnBenefitWhenFound() {
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(partnerBenefit));

            BenefitResponse result = benefitService.getBenefitById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Desconto Cinema NOS", result.getName());
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
    @DisplayName("getBenefitsByCategory Tests")
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
    @DisplayName("getAffordableBenefitsForVolunteer Tests")
    class GetAffordableBenefitsTests {

        @Test
        @DisplayName("Should return affordable benefits for volunteer")
        void shouldReturnAffordableBenefits() {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(1L);
            volunteer.setTotalPoints(200);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findByPointsRequiredLessThanEqualAndActiveTrue(200))
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
    @DisplayName("getBenefitsByProvider Tests")
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
    @DisplayName("getAllProviders Tests")
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
    @DisplayName("getBenefitsSorted Tests")
    class GetBenefitsSortedTests {

        @Test
        @DisplayName("Should return benefits sorted by points ascending")
        void shouldReturnBenefitsSortedAsc() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredAsc())
                    .thenReturn(Arrays.asList(uaBenefit, partnerBenefit));

            List<BenefitResponse> result = benefitService.getBenefitsSortedByPointsAsc();

            assertEquals(2, result.size());
            assertTrue(result.get(0).getPointsRequired() <= result.get(1).getPointsRequired());
        }

        @Test
        @DisplayName("Should return benefits sorted by points descending")
        void shouldReturnBenefitsSortedDesc() {
            when(benefitRepository.findByActiveTrueOrderByPointsRequiredDesc())
                    .thenReturn(Arrays.asList(partnerBenefit, uaBenefit));

            List<BenefitResponse> result = benefitService.getBenefitsSortedByPointsDesc();

            assertEquals(2, result.size());
            assertTrue(result.get(0).getPointsRequired() >= result.get(1).getPointsRequired());
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
}
