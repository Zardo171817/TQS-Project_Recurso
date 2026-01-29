package com.example.demo.unit.service;

import com.example.demo.dto.CreatePromoterProfileRequest;
import com.example.demo.dto.PromoterProfileResponse;
import com.example.demo.entity.Promoter;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PromoterRepository;
import com.example.demo.service.PromoterProfileService;
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
@DisplayName("PromoterProfileService Unit Tests")
class PromoterProfileServiceTest {

    @Mock
    private PromoterRepository promoterRepository;

    @InjectMocks
    private PromoterProfileService promoterProfileService;

    private Promoter promoter;
    private CreatePromoterProfileRequest createRequest;

    @BeforeEach
    void setUp() {
        promoter = new Promoter();
        promoter.setId(1L);
        promoter.setName("Test Org");
        promoter.setEmail("org@example.com");
        promoter.setOrganization("Test Organization");
        promoter.setAreaOfActivity("Social");

        createRequest = new CreatePromoterProfileRequest();
        createRequest.setName("Test Org");
        createRequest.setEmail("org@example.com");
        createRequest.setOrganization("Test Organization");
    }

    @Test
    @DisplayName("Should create promoter profile")
    void shouldCreatePromoterProfile() {
        when(promoterRepository.existsByEmail("org@example.com")).thenReturn(false);
        when(promoterRepository.save(any(Promoter.class))).thenAnswer(invocation -> {
            Promoter p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PromoterProfileResponse response = promoterProfileService.createProfile(createRequest);

        assertThat(response.getName()).isEqualTo("Test Org");
    }

    @Test
    @DisplayName("Should fail when email already exists")
    void shouldFailWhenEmailExists() {
        when(promoterRepository.existsByEmail("org@example.com")).thenReturn(true);

        assertThatThrownBy(() -> promoterProfileService.createProfile(createRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should get profile by ID")
    void shouldGetProfileById() {
        when(promoterRepository.findById(1L)).thenReturn(Optional.of(promoter));

        PromoterProfileResponse response = promoterProfileService.getProfile(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw exception for non-existent profile")
    void shouldThrowExceptionForNonExistent() {
        when(promoterRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promoterProfileService.getProfile(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should get all promoters")
    void shouldGetAllPromoters() {
        when(promoterRepository.findAll()).thenReturn(Arrays.asList(promoter));

        List<PromoterProfileResponse> responses = promoterProfileService.getAllProfiles();

        assertThat(responses).hasSize(1);
    }
}
