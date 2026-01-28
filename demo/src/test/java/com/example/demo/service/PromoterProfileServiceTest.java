package com.example.demo.service;

import com.example.demo.dto.CreatePromoterProfileRequest;
import com.example.demo.dto.UpdatePromoterProfileRequest;
import com.example.demo.dto.PromoterProfileResponse;
import com.example.demo.entity.Promoter;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PromoterRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromoterProfileService Tests")
class PromoterProfileServiceTest {

    @Mock
    private PromoterRepository promoterRepository;

    @InjectMocks
    private PromoterProfileService promoterProfileService;

    private CreatePromoterProfileRequest createRequest;
    private UpdatePromoterProfileRequest updateRequest;
    private Promoter promoter;

    @BeforeEach
    void setUp() {
        createRequest = new CreatePromoterProfileRequest();
        createRequest.setName("Maria Santos");
        createRequest.setEmail("maria@organizacao.org");
        createRequest.setOrganization("Associacao Solidaria");
        createRequest.setDescription("Uma organizacao dedicada ao bem-estar social");
        createRequest.setPhone("+351 234 567 890");
        createRequest.setWebsite("https://www.associacaosolidaria.org");
        createRequest.setAddress("Rua Principal, 123, Aveiro");
        createRequest.setOrganizationType("ONG");
        createRequest.setAreaOfActivity("Educacao, Saude");
        createRequest.setFoundedYear("2010");
        createRequest.setNumberOfEmployees("21-50");
        createRequest.setSocialMedia("Facebook: /assoc, Instagram: @assoc");

        updateRequest = new UpdatePromoterProfileRequest();
        updateRequest.setName("Maria Santos Silva");
        updateRequest.setOrganization("Associacao Solidaria de Aveiro");

        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Maria Santos");
        promoter.setEmail("maria@organizacao.org");
        promoter.setOrganization("Associacao Solidaria");
        promoter.setDescription("Uma organizacao dedicada ao bem-estar social");
        promoter.setPhone("+351 234 567 890");
        promoter.setWebsite("https://www.associacaosolidaria.org");
        promoter.setAddress("Rua Principal, 123, Aveiro");
        promoter.setOrganizationType("ONG");
        promoter.setAreaOfActivity("Educacao, Saude");
        promoter.setFoundedYear("2010");
        promoter.setNumberOfEmployees("21-50");
        promoter.setSocialMedia("Facebook: /assoc, Instagram: @assoc");
        promoter.setProfileCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Create Profile Tests")
    class CreateProfileTests {

        @Test
        @DisplayName("Should create profile successfully with all fields")
        void shouldCreateProfileSuccessfully() {
            when(promoterRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
            when(promoterRepository.save(any(Promoter.class))).thenReturn(promoter);

            PromoterProfileResponse response = promoterProfileService.createProfile(createRequest);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Maria Santos");
            assertThat(response.getEmail()).isEqualTo("maria@organizacao.org");
            assertThat(response.getOrganization()).isEqualTo("Associacao Solidaria");
            assertThat(response.getDescription()).isEqualTo("Uma organizacao dedicada ao bem-estar social");
            assertThat(response.getPhone()).isEqualTo("+351 234 567 890");
            assertThat(response.getOrganizationType()).isEqualTo("ONG");

            verify(promoterRepository).existsByEmail(createRequest.getEmail());
            verify(promoterRepository).save(any(Promoter.class));
        }

        @Test
        @DisplayName("Should create profile with minimal required fields")
        void shouldCreateProfileWithMinimalFields() {
            CreatePromoterProfileRequest minimalRequest = new CreatePromoterProfileRequest();
            minimalRequest.setName("Joao Silva");
            minimalRequest.setEmail("joao@test.org");
            minimalRequest.setOrganization("Test Org");

            Promoter minimalPromoter = new Promoter();
            minimalPromoter.setId(2L);
            minimalPromoter.setName("Joao Silva");
            minimalPromoter.setEmail("joao@test.org");
            minimalPromoter.setOrganization("Test Org");
            minimalPromoter.setProfileCreatedAt(LocalDateTime.now());

            when(promoterRepository.existsByEmail(minimalRequest.getEmail())).thenReturn(false);
            when(promoterRepository.save(any(Promoter.class))).thenReturn(minimalPromoter);

            PromoterProfileResponse response = promoterProfileService.createProfile(minimalRequest);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Joao Silva");
            assertThat(response.getEmail()).isEqualTo("joao@test.org");
            assertThat(response.getOrganization()).isEqualTo("Test Org");
            assertThat(response.getPhone()).isNull();
            assertThat(response.getDescription()).isNull();
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(promoterRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> promoterProfileService.createProfile(createRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already exists");

            verify(promoterRepository, never()).save(any(Promoter.class));
        }

        @Test
        @DisplayName("Should set profileCreatedAt when creating profile")
        void shouldSetProfileCreatedAtWhenCreating() {
            when(promoterRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
            when(promoterRepository.save(any(Promoter.class))).thenAnswer(invocation -> {
                Promoter saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            ArgumentCaptor<Promoter> captor = ArgumentCaptor.forClass(Promoter.class);

            promoterProfileService.createProfile(createRequest);

            verify(promoterRepository).save(captor.capture());
            assertThat(captor.getValue().getProfileCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should map all request fields to entity")
        void shouldMapAllRequestFieldsToEntity() {
            when(promoterRepository.existsByEmail(createRequest.getEmail())).thenReturn(false);
            when(promoterRepository.save(any(Promoter.class))).thenAnswer(invocation -> {
                Promoter saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });

            ArgumentCaptor<Promoter> captor = ArgumentCaptor.forClass(Promoter.class);

            promoterProfileService.createProfile(createRequest);

            verify(promoterRepository).save(captor.capture());
            Promoter captured = captor.getValue();

            assertThat(captured.getName()).isEqualTo(createRequest.getName());
            assertThat(captured.getEmail()).isEqualTo(createRequest.getEmail());
            assertThat(captured.getOrganization()).isEqualTo(createRequest.getOrganization());
            assertThat(captured.getDescription()).isEqualTo(createRequest.getDescription());
            assertThat(captured.getPhone()).isEqualTo(createRequest.getPhone());
            assertThat(captured.getWebsite()).isEqualTo(createRequest.getWebsite());
            assertThat(captured.getAddress()).isEqualTo(createRequest.getAddress());
            assertThat(captured.getOrganizationType()).isEqualTo(createRequest.getOrganizationType());
            assertThat(captured.getAreaOfActivity()).isEqualTo(createRequest.getAreaOfActivity());
            assertThat(captured.getFoundedYear()).isEqualTo(createRequest.getFoundedYear());
            assertThat(captured.getNumberOfEmployees()).isEqualTo(createRequest.getNumberOfEmployees());
            assertThat(captured.getSocialMedia()).isEqualTo(createRequest.getSocialMedia());
        }
    }

    @Nested
    @DisplayName("Update Profile Tests")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update profile successfully")
        void shouldUpdateProfileSuccessfully() {
            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));
            when(promoterRepository.save(any(Promoter.class))).thenReturn(promoter);

            PromoterProfileResponse response = promoterProfileService.updateProfile(1L, updateRequest);

            assertThat(response).isNotNull();
            verify(promoterRepository).findById(1L);
            verify(promoterRepository).save(any(Promoter.class));
        }

        @Test
        @DisplayName("Should throw exception when promoter not found")
        void shouldThrowExceptionWhenPromoterNotFound() {
            when(promoterRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> promoterProfileService.updateProfile(999L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Promoter not found with id: 999");
        }

        @Test
        @DisplayName("Should throw exception when no updates provided")
        void shouldThrowExceptionWhenNoUpdatesProvided() {
            UpdatePromoterProfileRequest emptyRequest = new UpdatePromoterProfileRequest();
            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));

            assertThatThrownBy(() -> promoterProfileService.updateProfile(1L, emptyRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No fields to update provided");
        }

        @Test
        @DisplayName("Should update only name when only name provided")
        void shouldUpdateOnlyNameWhenOnlyNameProvided() {
            UpdatePromoterProfileRequest nameOnlyRequest = new UpdatePromoterProfileRequest();
            nameOnlyRequest.setName("New Name");

            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));
            when(promoterRepository.save(any(Promoter.class))).thenReturn(promoter);

            ArgumentCaptor<Promoter> captor = ArgumentCaptor.forClass(Promoter.class);

            promoterProfileService.updateProfile(1L, nameOnlyRequest);

            verify(promoterRepository).save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("New Name");
            assertThat(captor.getValue().getProfileUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should update all provided fields")
        void shouldUpdateAllProvidedFields() {
            UpdatePromoterProfileRequest fullRequest = new UpdatePromoterProfileRequest();
            fullRequest.setName("Updated Name");
            fullRequest.setOrganization("Updated Org");
            fullRequest.setDescription("Updated Description");
            fullRequest.setPhone("+351 999 888 777");
            fullRequest.setWebsite("https://updated.org");
            fullRequest.setAddress("New Address");
            fullRequest.setLogoUrl("https://logo.png");
            fullRequest.setOrganizationType("IPSS");
            fullRequest.setAreaOfActivity("New Area");
            fullRequest.setFoundedYear("2020");
            fullRequest.setNumberOfEmployees("1-5");
            fullRequest.setSocialMedia("Twitter: @new");

            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));
            when(promoterRepository.save(any(Promoter.class))).thenReturn(promoter);

            ArgumentCaptor<Promoter> captor = ArgumentCaptor.forClass(Promoter.class);

            promoterProfileService.updateProfile(1L, fullRequest);

            verify(promoterRepository).save(captor.capture());
            Promoter captured = captor.getValue();

            assertThat(captured.getName()).isEqualTo("Updated Name");
            assertThat(captured.getOrganization()).isEqualTo("Updated Org");
            assertThat(captured.getDescription()).isEqualTo("Updated Description");
            assertThat(captured.getPhone()).isEqualTo("+351 999 888 777");
            assertThat(captured.getWebsite()).isEqualTo("https://updated.org");
            assertThat(captured.getAddress()).isEqualTo("New Address");
            assertThat(captured.getLogoUrl()).isEqualTo("https://logo.png");
            assertThat(captured.getOrganizationType()).isEqualTo("IPSS");
            assertThat(captured.getAreaOfActivity()).isEqualTo("New Area");
            assertThat(captured.getFoundedYear()).isEqualTo("2020");
            assertThat(captured.getNumberOfEmployees()).isEqualTo("1-5");
            assertThat(captured.getSocialMedia()).isEqualTo("Twitter: @new");
        }

        @Test
        @DisplayName("Should set profileUpdatedAt when updating")
        void shouldSetProfileUpdatedAtWhenUpdating() {
            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));
            when(promoterRepository.save(any(Promoter.class))).thenReturn(promoter);

            ArgumentCaptor<Promoter> captor = ArgumentCaptor.forClass(Promoter.class);

            promoterProfileService.updateProfile(1L, updateRequest);

            verify(promoterRepository).save(captor.capture());
            assertThat(captor.getValue().getProfileUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Get Profile Tests")
    class GetProfileTests {

        @Test
        @DisplayName("Should get profile by id successfully")
        void shouldGetProfileByIdSuccessfully() {
            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));

            PromoterProfileResponse response = promoterProfileService.getProfile(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Maria Santos");
            assertThat(response.getEmail()).isEqualTo("maria@organizacao.org");
        }

        @Test
        @DisplayName("Should throw exception when profile not found by id")
        void shouldThrowExceptionWhenProfileNotFoundById() {
            when(promoterRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> promoterProfileService.getProfile(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Promoter not found with id: 999");
        }

        @Test
        @DisplayName("Should get profile by email successfully")
        void shouldGetProfileByEmailSuccessfully() {
            when(promoterRepository.findByEmail("maria@organizacao.org")).thenReturn(Optional.of(promoter));

            PromoterProfileResponse response = promoterProfileService.getProfileByEmail("maria@organizacao.org");

            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("maria@organizacao.org");
        }

        @Test
        @DisplayName("Should throw exception when profile not found by email")
        void shouldThrowExceptionWhenProfileNotFoundByEmail() {
            when(promoterRepository.findByEmail("unknown@test.org")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> promoterProfileService.getProfileByEmail("unknown@test.org"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Promoter not found with email: unknown@test.org");
        }
    }

    @Nested
    @DisplayName("Get All Profiles Tests")
    class GetAllProfilesTests {

        @Test
        @DisplayName("Should return all profiles")
        void shouldReturnAllProfiles() {
            Promoter promoter2 = new Promoter();
            promoter2.setId(2L);
            promoter2.setName("Joao Silva");
            promoter2.setEmail("joao@org.org");
            promoter2.setOrganization("Org 2");

            when(promoterRepository.findAll()).thenReturn(Arrays.asList(promoter, promoter2));

            List<PromoterProfileResponse> responses = promoterProfileService.getAllProfiles();

            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getName()).isEqualTo("Maria Santos");
            assertThat(responses.get(1).getName()).isEqualTo("Joao Silva");
        }

        @Test
        @DisplayName("Should return empty list when no profiles exist")
        void shouldReturnEmptyListWhenNoProfilesExist() {
            when(promoterRepository.findAll()).thenReturn(Collections.emptyList());

            List<PromoterProfileResponse> responses = promoterProfileService.getAllProfiles();

            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("Search Profiles Tests")
    class SearchProfilesTests {

        @Test
        @DisplayName("Should find promoters by organization")
        void shouldFindPromotersByOrganization() {
            when(promoterRepository.findByOrganizationContainingIgnoreCase("Solidaria"))
                    .thenReturn(Arrays.asList(promoter));

            List<PromoterProfileResponse> responses = promoterProfileService.findPromotersByOrganization("Solidaria");

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getOrganization()).contains("Solidaria");
        }

        @Test
        @DisplayName("Should return empty list when no organization matches")
        void shouldReturnEmptyListWhenNoOrganizationMatches() {
            when(promoterRepository.findByOrganizationContainingIgnoreCase("NonExistent"))
                    .thenReturn(Collections.emptyList());

            List<PromoterProfileResponse> responses = promoterProfileService.findPromotersByOrganization("NonExistent");

            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("Should find promoters by area of activity")
        void shouldFindPromotersByAreaOfActivity() {
            when(promoterRepository.findByAreaOfActivityContainingIgnoreCase("Educacao"))
                    .thenReturn(Arrays.asList(promoter));

            List<PromoterProfileResponse> responses = promoterProfileService.findPromotersByAreaOfActivity("Educacao");

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getAreaOfActivity()).contains("Educacao");
        }

        @Test
        @DisplayName("Should find promoters by organization type")
        void shouldFindPromotersByOrganizationType() {
            when(promoterRepository.findByOrganizationTypeContainingIgnoreCase("ONG"))
                    .thenReturn(Arrays.asList(promoter));

            List<PromoterProfileResponse> responses = promoterProfileService.findPromotersByOrganizationType("ONG");

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getOrganizationType()).isEqualTo("ONG");
        }

        @Test
        @DisplayName("Should return empty list when no organization type matches")
        void shouldReturnEmptyListWhenNoOrganizationTypeMatches() {
            when(promoterRepository.findByOrganizationTypeContainingIgnoreCase("Unknown"))
                    .thenReturn(Collections.emptyList());

            List<PromoterProfileResponse> responses = promoterProfileService.findPromotersByOrganizationType("Unknown");

            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("Delete Profile Tests")
    class DeleteProfileTests {

        @Test
        @DisplayName("Should delete profile successfully")
        void shouldDeleteProfileSuccessfully() {
            when(promoterRepository.existsById(1L)).thenReturn(true);
            doNothing().when(promoterRepository).deleteById(1L);

            promoterProfileService.deleteProfile(1L);

            verify(promoterRepository).existsById(1L);
            verify(promoterRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent profile")
        void shouldThrowExceptionWhenDeletingNonExistentProfile() {
            when(promoterRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> promoterProfileService.deleteProfile(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Promoter not found with id: 999");

            verify(promoterRepository, never()).deleteById(anyLong());
        }
    }
}
