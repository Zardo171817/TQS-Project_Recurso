package com.example.demo.service;

import com.example.demo.dto.CreateVolunteerProfileRequest;
import com.example.demo.dto.UpdateVolunteerProfileRequest;
import com.example.demo.dto.VolunteerProfileResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VolunteerProfileService {

    private final VolunteerRepository volunteerRepository;

    public VolunteerProfileService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    @Transactional
    public VolunteerProfileResponse createProfile(CreateVolunteerProfileRequest request) {
        if (volunteerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Volunteer volunteer = new Volunteer();
        volunteer.setName(request.getName());
        volunteer.setEmail(request.getEmail());
        volunteer.setPhone(request.getPhone());
        volunteer.setSkills(request.getSkills());
        volunteer.setInterests(request.getInterests());
        volunteer.setAvailability(request.getAvailability());
        volunteer.setBio(request.getBio());
        volunteer.setTotalPoints(0);
        volunteer.setProfileCreatedAt(LocalDateTime.now());

        Volunteer savedVolunteer = volunteerRepository.save(volunteer);
        return VolunteerProfileResponse.fromEntity(savedVolunteer);
    }

    @Transactional
    public VolunteerProfileResponse updateProfile(Long volunteerId, UpdateVolunteerProfileRequest request) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));

        if (!request.hasUpdates()) {
            throw new IllegalArgumentException("No fields to update provided");
        }

        if (request.getName() != null) {
            volunteer.setName(request.getName());
        }
        if (request.getPhone() != null) {
            volunteer.setPhone(request.getPhone());
        }
        if (request.getSkills() != null) {
            volunteer.setSkills(request.getSkills());
        }
        if (request.getInterests() != null) {
            volunteer.setInterests(request.getInterests());
        }
        if (request.getAvailability() != null) {
            volunteer.setAvailability(request.getAvailability());
        }
        if (request.getBio() != null) {
            volunteer.setBio(request.getBio());
        }

        volunteer.setProfileUpdatedAt(LocalDateTime.now());
        Volunteer updatedVolunteer = volunteerRepository.save(volunteer);
        return VolunteerProfileResponse.fromEntity(updatedVolunteer);
    }

    @Transactional(readOnly = true)
    public VolunteerProfileResponse getProfile(Long volunteerId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with id: " + volunteerId));
        return VolunteerProfileResponse.fromEntity(volunteer);
    }

    @Transactional(readOnly = true)
    public VolunteerProfileResponse getProfileByEmail(String email) {
        Volunteer volunteer = volunteerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with email: " + email));
        return VolunteerProfileResponse.fromEntity(volunteer);
    }

    @Transactional(readOnly = true)
    public List<VolunteerProfileResponse> getAllProfiles() {
        return volunteerRepository.findAll().stream()
                .map(VolunteerProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VolunteerProfileResponse> findVolunteersBySkills(String skills) {
        return volunteerRepository.findBySkillsContainingIgnoreCase(skills).stream()
                .map(VolunteerProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VolunteerProfileResponse> findVolunteersByInterests(String interests) {
        return volunteerRepository.findByInterestsContainingIgnoreCase(interests).stream()
                .map(VolunteerProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VolunteerProfileResponse> findVolunteersByAvailability(String availability) {
        return volunteerRepository.findByAvailabilityContainingIgnoreCase(availability).stream()
                .map(VolunteerProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProfile(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException("Volunteer not found with id: " + volunteerId);
        }
        volunteerRepository.deleteById(volunteerId);
    }
}
