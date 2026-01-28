package com.example.demo.unit.service;

import com.example.demo.dto.CreatePromoterProfileRequest;
import com.example.demo.dto.UpdatePromoterProfileRequest;
import com.example.demo.dto.PromoterProfileResponse;
import com.example.demo.entity.Promoter;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.service.PromoterProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromoterProfileService Unit Tests")
class PromoterProfileServiceTest {

    @Mock
    private PromoterRepository promoterRepository;

    @InjectMocks
    private PromoterProfileService promoterProfileService;

    private Promoter promoter;
    private CreatePromoterProfileRequest createRequest;
    private UpdatePromoterProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Organization");
        promoter.setEmail("org@example.com");
        promoter.setOrganization("Test Org");
        promoter.setDescription("Test Description");
        promoter.setPhone("123456789");
        promoter.setAreaOfActivity("Social");
        promoter.setOrganizationType("NGO");

        createRequest = new CreatePromoterProfileRequest();
        createRequest.setName("Test Organization");
        createRequest.setEmail("org@example.com");
        createRequest.setOrganization("Test Org");
        createRequest.setDescription("Test Description");
        createRequest.setAreaOfActivity("Social");
        createRequest.setOrganizationType("NGO");

        updateRequest = new UpdatePromoterProfileRequest();
    }

    @Nested
    @DisplayName("Create Profile Tests")
    class CreateProfileTests {

        @Test
        @DisplayName("Should create profile successfully")
        void shouldCreateProfileSuccessfully() {
            when(promoterRepository.existsByEmail(anyString())).thenReturn(false);
            when(promoterRepository.save(any(Promoter.class))).thenAnswer(invocation -> {
                Promoter p = invocation.getArgument(0);
                p.setId(1L);
                return p;
            });

            PromoterProfileResponse response = promoterProfileService.createProfile(createRequest);

            assertThat(response.getName()).isEqualTo("Test Organization");
            assertThat(response.getOrganization()).isEqualTo("Test Org");
            verify(promoterRepository).save(any(Promoter.class));
        }

        @Test
        @DisplayName("Should fail when email already exists")
        void shouldFailWhenEmailExists() {
            when(promoterRepository.existsByEmail("org@example.com")).thenReturn(true);

            assertThatThrownBy(() -> promoterProfileService.createProfile(createRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already exists");

            verify(promoterRepository, never()).save(any(Promoter.class));
        }
    }

    @Nested
    @DisplayName("Update Profile Tests")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update profile successfully")
        void shouldUpdateProfileSuccessfully() {
            updateRequest.setName("Updated Name");
            updateRequest.setOrganization("Updated Org");

            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));
            when(promoterRepository.save(any(Promoter.class))).thenReturn(promoter);

            PromoterProfileResponse response = promoterProfileService.updateProfile(1L, updateRequest);

            assertThat(response).isNotNull();
            verify(promoterRepository).save(any(Promoter.class));
        }

        @Test
        @DisplayName("Should fail update when no fields provided")
        void shouldFailUpdateWhenNoFieldsProvided() {
            UpdatePromoterProfileRequest emptyRequest = new UpdatePromoterProfileRequest();
            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));

            assertThatThrownBy(() -> promoterProfileService.updateProfile(1L, emptyRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("No fields to update");
        }

        @Test
        @DisplayName("Should throw exception for non-existent promoter")
        void shouldThrowExceptionForNonExistentPromoter() {
            updateRequest.setName("Updated");
            when(promoterRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> promoterProfileService.updateProfile(999L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Get Profile Tests")
    class GetProfileTests {

        @Test
        @DisplayName("Should get profile by ID")
        void shouldGetProfileById() {
            when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));

            PromoterProfileResponse response = promoterProfileService.getProfile(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("Should get profile by email")
        void shouldGetProfileByEmail() {
            when(promoterRepository.findByEmail("org@example.com")).thenReturn(Optional.of(promoter));

            PromoterProfileResponse response = promoterProfileService.getProfileByEmail("org@example.com");

            assertThat(response.getEmail()).isEqualTo("org@example.com");
        }

        @Test
        @DisplayName("Should get all profiles")
        void shouldGetAllProfiles() {
            Promoter promoter2 = new Promoter();
            promoter2.setId(2L);
            promoter2.setName("Another Org");
            promoter2.setEmail("another@example.com");
            promoter2.setOrganization("Another");

            when(promoterRepository.findAll()).thenReturn(Arrays.asList(promoter, promoter2));

            List<PromoterProfileResponse> profiles = promoterProfileService.getAllProfiles();

            assertThat(profiles).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Search Profile Tests")
    class SearchProfileTests {

        @Test
        @DisplayName("Should find promoters by organization")
        void shouldFindPromotersByOrganization() {
            when(promoterRepository.findByOrganizationContainingIgnoreCase("Test"))
                    .thenReturn(Collections.singletonList(promoter));

            List<PromoterProfileResponse> results = promoterProfileService.findPromotersByOrganization("Test");

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("Should find promoters by area of activity")
        void shouldFindPromotersByAreaOfActivity() {
            when(promoterRepository.findByAreaOfActivityContainingIgnoreCase("Social"))
                    .thenReturn(Collections.singletonList(promoter));

            List<PromoterProfileResponse> results = promoterProfileService.findPromotersByAreaOfActivity("Social");

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("Should find promoters by organization type")
        void shouldFindPromotersByOrganizationType() {
            when(promoterRepository.findByOrganizationTypeContainingIgnoreCase("NGO"))
                    .thenReturn(Collections.singletonList(promoter));

            List<PromoterProfileResponse> results = promoterProfileService.findPromotersByOrganizationType("NGO");

            assertThat(results).hasSize(1);
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

            verify(promoterRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent profile")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            when(promoterRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> promoterProfileService.deleteProfile(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(promoterRepository, never()).deleteById(any());
        }
    }
}
