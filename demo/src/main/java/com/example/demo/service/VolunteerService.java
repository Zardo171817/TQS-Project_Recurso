package com.example.demo.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.dto.VolunteerResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final ApplicationRepository applicationRepository;

    public VolunteerService(VolunteerRepository volunteerRepository, ApplicationRepository applicationRepository) {
        this.volunteerRepository = volunteerRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional(readOnly = true)
    public VolunteerResponse getVolunteerById(Long id) {
        Volunteer volunteer = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Volunteer not found with id: " + id));
        return VolunteerResponse.fromEntity(volunteer);
    }

    @Transactional(readOnly = true)
    public VolunteerResponse getVolunteerByEmail(String email) {
        Volunteer volunteer = volunteerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Volunteer not found with email: " + email));
        return VolunteerResponse.fromEntity(volunteer);
    }

    @Transactional(readOnly = true)
    public List<VolunteerResponse> getAllVolunteers() {
        return volunteerRepository.findAll().stream()
                .map(VolunteerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return volunteerRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public VolunteerPointsResponse getVolunteerPoints(Long volunteerId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Volunteer not found with id: " + volunteerId));
        return VolunteerPointsResponse.fromEntity(volunteer);
    }

    @Transactional(readOnly = true)
    public List<VolunteerPointsResponse> getVolunteersRanking() {
        return volunteerRepository.findAll().stream()
                .sorted(Comparator.comparing(Volunteer::getTotalPoints).reversed())
                .map(VolunteerPointsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VolunteerPointsResponse> getTopVolunteers(int limit) {
        return volunteerRepository.findAll().stream()
                .sorted(Comparator.comparing(Volunteer::getTotalPoints).reversed())
                .limit(limit)
                .map(VolunteerPointsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getConfirmedParticipations(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException("Volunteer not found with id: " + volunteerId);
        }
        return applicationRepository.findByVolunteerIdAndParticipationConfirmed(volunteerId, true).stream()
                .map(ApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
