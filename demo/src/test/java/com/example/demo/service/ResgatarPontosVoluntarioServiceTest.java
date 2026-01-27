package com.example.demo.service;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Resgatar Pontos Voluntario - Service Tests")
class ResgatarPontosVoluntarioServiceTest {

    @Mock
    private RedemptionRepository redemptionRepository;

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private BenefitRepository benefitRepository;

    @InjectMocks
    private RedemptionService redemptionService;

    private Volunteer volunteer;
    private Benefit benefitUA;
    private Benefit benefitPartner;
    private Redemption redemption;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Joao Silva");
        volunteer.setEmail("joao@example.com");
        volunteer.setTotalPoints(500);

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

        redemption = new Redemption();
        redemption.setId(1L);
        redemption.setVolunteer(volunteer);
        redemption.setBenefit(benefitUA);
        redemption.setPointsSpent(100);
        redemption.setStatus(RedemptionStatus.COMPLETED);
        redemption.setRedeemedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("redeemPoints Tests")
    class RedeemPointsTests {

        @Test
        @DisplayName("Should successfully redeem points for a benefit")
        void shouldSuccessfullyRedeemPoints() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertNotNull(result);
            assertEquals(100, result.getPointsSpent());
            assertEquals("Desconto Cantina UA", result.getBenefitName());
            verify(volunteerRepository, times(1)).save(any(Volunteer.class));
            verify(redemptionRepository, times(1)).save(any(Redemption.class));
        }

        @Test
        @DisplayName("Should deduct points from volunteer after redemption")
        void shouldDeductPointsFromVolunteer() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            redemptionService.redeemPoints(request);

            assertEquals(400, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            RedeemPointsRequest request = new RedeemPointsRequest(999L, 1L);

            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> redemptionService.redeemPoints(request)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(redemptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when benefit not found")
        void shouldThrowExceptionWhenBenefitNotFound() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 999L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> redemptionService.redeemPoints(request)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(redemptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when benefit is not active")
        void shouldThrowExceptionWhenBenefitNotActive() {
            benefitUA.setActive(false);
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> redemptionService.redeemPoints(request)
            );

            assertTrue(exception.getMessage().contains("not active"));
            verify(redemptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when insufficient points")
        void shouldThrowExceptionWhenInsufficientPoints() {
            volunteer.setTotalPoints(50);
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> redemptionService.redeemPoints(request)
            );

            assertTrue(exception.getMessage().contains("Insufficient points"));
            assertTrue(exception.getMessage().contains("Required: 100"));
            assertTrue(exception.getMessage().contains("Available: 50"));
            verify(redemptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle exact points match")
        void shouldHandleExactPointsMatch() {
            volunteer.setTotalPoints(100);
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertNotNull(result);
            assertEquals(0, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("Should set correct redemption status as COMPLETED")
        void shouldSetCorrectRedemptionStatus() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertEquals(RedemptionStatus.COMPLETED, result.getStatus());
        }

        @Test
        @DisplayName("Should return correct volunteer info in response")
        void shouldReturnCorrectVolunteerInfoInResponse() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertEquals(1L, result.getVolunteerId());
            assertEquals("Joao Silva", result.getVolunteerName());
            assertEquals("joao@example.com", result.getVolunteerEmail());
        }

        @Test
        @DisplayName("Should return correct benefit info in response")
        void shouldReturnCorrectBenefitInfoInResponse() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertEquals(1L, result.getBenefitId());
            assertEquals("Desconto Cantina UA", result.getBenefitName());
            assertEquals("10% desconto na cantina da UA", result.getBenefitDescription());
            assertEquals("Universidade de Aveiro", result.getBenefitProvider());
        }

        @Test
        @DisplayName("Should redeem partner benefit successfully")
        void shouldRedeemPartnerBenefitSuccessfully() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 2L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(2L)).thenReturn(Optional.of(benefitPartner));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(2L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertNotNull(result);
            assertEquals(200, result.getPointsSpent());
            assertEquals("Desconto Cinema", result.getBenefitName());
            assertEquals(300, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("Should throw exception when volunteer has zero points")
        void shouldThrowExceptionWhenVolunteerHasZeroPoints() {
            volunteer.setTotalPoints(0);
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            assertThrows(IllegalStateException.class,
                    () -> redemptionService.redeemPoints(request));
        }
    }

    @Nested
    @DisplayName("getRedemptionById Tests")
    class GetRedemptionByIdTests {

        @Test
        @DisplayName("Should return redemption when found")
        void shouldReturnRedemptionWhenFound() {
            when(redemptionRepository.findById(1L)).thenReturn(Optional.of(redemption));

            RedemptionResponse result = redemptionService.getRedemptionById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(100, result.getPointsSpent());
            assertEquals(RedemptionStatus.COMPLETED, result.getStatus());
            verify(redemptionRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when redemption not found")
        void shouldThrowExceptionWhenRedemptionNotFound() {
            when(redemptionRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> redemptionService.getRedemptionById(999L)
            );

            assertTrue(exception.getMessage().contains("999"));
        }

        @Test
        @DisplayName("Should return correct volunteer data in redemption")
        void shouldReturnCorrectVolunteerDataInRedemption() {
            when(redemptionRepository.findById(1L)).thenReturn(Optional.of(redemption));

            RedemptionResponse result = redemptionService.getRedemptionById(1L);

            assertEquals("Joao Silva", result.getVolunteerName());
            assertEquals("joao@example.com", result.getVolunteerEmail());
        }

        @Test
        @DisplayName("Should return correct benefit data in redemption")
        void shouldReturnCorrectBenefitDataInRedemption() {
            when(redemptionRepository.findById(1L)).thenReturn(Optional.of(redemption));

            RedemptionResponse result = redemptionService.getRedemptionById(1L);

            assertEquals("Desconto Cantina UA", result.getBenefitName());
            assertEquals("Universidade de Aveiro", result.getBenefitProvider());
        }
    }

    @Nested
    @DisplayName("getRedemptionsByVolunteer Tests")
    class GetRedemptionsByVolunteerTests {

        @Test
        @DisplayName("Should return all redemptions for volunteer")
        void shouldReturnAllRedemptionsForVolunteer() {
            Redemption redemption2 = new Redemption();
            redemption2.setId(2L);
            redemption2.setVolunteer(volunteer);
            redemption2.setBenefit(benefitPartner);
            redemption2.setPointsSpent(200);
            redemption2.setStatus(RedemptionStatus.COMPLETED);
            redemption2.setRedeemedAt(LocalDateTime.now());

            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdOrderByRedeemedAtDesc(1L))
                    .thenReturn(Arrays.asList(redemption2, redemption));

            List<RedemptionResponse> result = redemptionService.getRedemptionsByVolunteer(1L);

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no redemptions")
        void shouldReturnEmptyListWhenNoRedemptions() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdOrderByRedeemedAtDesc(1L))
                    .thenReturn(Collections.emptyList());

            List<RedemptionResponse> result = redemptionService.getRedemptionsByVolunteer(1L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getRedemptionsByVolunteer(999L));
        }

        @Test
        @DisplayName("Should verify repository calls")
        void shouldVerifyRepositoryCalls() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdOrderByRedeemedAtDesc(1L))
                    .thenReturn(Arrays.asList(redemption));

            redemptionService.getRedemptionsByVolunteer(1L);

            verify(volunteerRepository, times(1)).existsById(1L);
            verify(redemptionRepository, times(1)).findByVolunteerIdOrderByRedeemedAtDesc(1L);
        }
    }

    @Nested
    @DisplayName("getCompletedRedemptionsByVolunteer Tests")
    class GetCompletedRedemptionsByVolunteerTests {

        @Test
        @DisplayName("Should return only completed redemptions")
        void shouldReturnOnlyCompletedRedemptions() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdAndStatus(1L, RedemptionStatus.COMPLETED))
                    .thenReturn(Arrays.asList(redemption));

            List<RedemptionResponse> result = redemptionService.getCompletedRedemptionsByVolunteer(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(RedemptionStatus.COMPLETED, result.get(0).getStatus());
        }

        @Test
        @DisplayName("Should return empty list when no completed redemptions")
        void shouldReturnEmptyListWhenNoCompletedRedemptions() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.findByVolunteerIdAndStatus(1L, RedemptionStatus.COMPLETED))
                    .thenReturn(Collections.emptyList());

            List<RedemptionResponse> result = redemptionService.getCompletedRedemptionsByVolunteer(1L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getCompletedRedemptionsByVolunteer(999L));
        }
    }

    @Nested
    @DisplayName("getTotalPointsSpent Tests")
    class GetTotalPointsSpentTests {

        @Test
        @DisplayName("Should return total points spent")
        void shouldReturnTotalPointsSpent() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.sumPointsSpentByVolunteerId(1L)).thenReturn(300);

            Integer result = redemptionService.getTotalPointsSpent(1L);

            assertEquals(300, result);
        }

        @Test
        @DisplayName("Should return zero when no points spent")
        void shouldReturnZeroWhenNoPointsSpent() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.sumPointsSpentByVolunteerId(1L)).thenReturn(0);

            Integer result = redemptionService.getTotalPointsSpent(1L);

            assertEquals(0, result);
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getTotalPointsSpent(999L));
        }

        @Test
        @DisplayName("Should verify repository calls")
        void shouldVerifyRepositoryCalls() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.sumPointsSpentByVolunteerId(1L)).thenReturn(100);

            redemptionService.getTotalPointsSpent(1L);

            verify(volunteerRepository, times(1)).existsById(1L);
            verify(redemptionRepository, times(1)).sumPointsSpentByVolunteerId(1L);
        }
    }

    @Nested
    @DisplayName("getRedemptionCount Tests")
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
        @DisplayName("Should return zero count when no redemptions")
        void shouldReturnZeroCountWhenNoRedemptions() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.countCompletedByVolunteerId(1L)).thenReturn(0L);

            Long result = redemptionService.getRedemptionCount(1L);

            assertEquals(0L, result);
        }

        @Test
        @DisplayName("Should throw exception when volunteer not found")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.getRedemptionCount(999L));
        }

        @Test
        @DisplayName("Should verify repository calls")
        void shouldVerifyRepositoryCalls() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(redemptionRepository.countCompletedByVolunteerId(1L)).thenReturn(3L);

            redemptionService.getRedemptionCount(1L);

            verify(volunteerRepository, times(1)).existsById(1L);
            verify(redemptionRepository, times(1)).countCompletedByVolunteerId(1L);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle large points values")
        void shouldHandleLargePointsValues() {
            volunteer.setTotalPoints(Integer.MAX_VALUE);
            benefitUA.setPointsRequired(Integer.MAX_VALUE);
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertNotNull(result);
            assertEquals(0, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("Should handle volunteer with special characters in name")
        void shouldHandleVolunteerWithSpecialCharactersInName() {
            volunteer.setName("Maria da Conceicao Santos - Jr.");
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertEquals("Maria da Conceicao Santos - Jr.", result.getVolunteerName());
        }

        @Test
        @DisplayName("Should handle benefit with unicode description")
        void shouldHandleBenefitWithUnicodeDescription() {
            benefitUA.setDescription("Desconto valido para estudantes internacionais");
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            RedemptionResponse result = redemptionService.redeemPoints(request);

            assertEquals("Desconto valido para estudantes internacionais", result.getBenefitDescription());
        }

        @Test
        @DisplayName("Should handle multiple consecutive redemptions")
        void shouldHandleMultipleConsecutiveRedemptions() {
            volunteer.setTotalPoints(300);
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            redemptionService.redeemPoints(request);
            assertEquals(200, volunteer.getTotalPoints());

            redemptionService.redeemPoints(request);
            assertEquals(100, volunteer.getTotalPoints());

            redemptionService.redeemPoints(request);
            assertEquals(0, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("Should not modify volunteer points on failure")
        void shouldNotModifyVolunteerPointsOnFailure() {
            volunteer.setTotalPoints(50);
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));

            assertThrows(IllegalStateException.class,
                    () -> redemptionService.redeemPoints(request));

            assertEquals(50, volunteer.getTotalPoints());
        }
    }

    @Nested
    @DisplayName("Repository Integration Tests")
    class RepositoryIntegrationTests {

        @Test
        @DisplayName("Should verify volunteer repository save is called on redemption")
        void shouldVerifyVolunteerRepositorySaveOnRedemption() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 1L);

            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitUA));
            when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer);
            when(redemptionRepository.save(any(Redemption.class))).thenAnswer(invocation -> {
                Redemption saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            redemptionService.redeemPoints(request);

            verify(volunteerRepository, times(1)).findById(1L);
            verify(benefitRepository, times(1)).findById(1L);
            verify(volunteerRepository, times(1)).save(volunteer);
            verify(redemptionRepository, times(1)).save(any(Redemption.class));
        }

        @Test
        @DisplayName("Should not call save when volunteer not found")
        void shouldNotCallSaveWhenVolunteerNotFound() {
            RedeemPointsRequest request = new RedeemPointsRequest(999L, 1L);
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.redeemPoints(request));

            verify(volunteerRepository, never()).save(any());
            verify(redemptionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should not call redemption save when benefit not found")
        void shouldNotCallRedemptionSaveWhenBenefitNotFound() {
            RedeemPointsRequest request = new RedeemPointsRequest(1L, 999L);
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer));
            when(benefitRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> redemptionService.redeemPoints(request));

            verify(volunteerRepository, never()).save(any());
            verify(redemptionRepository, never()).save(any());
        }
    }
}
