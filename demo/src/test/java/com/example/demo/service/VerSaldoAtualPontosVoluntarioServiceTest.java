package com.example.demo.service;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.dto.VolunteerResponse;
import com.example.demo.entity.Volunteer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ApplicationRepository;
import com.example.demo.repository.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ver Saldo Atual Pontos Voluntario - Service Tests")
class VerSaldoAtualPontosVoluntarioServiceTest {

    @Mock
    private VolunteerRepository volunteerRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private VolunteerService volunteerService;

    private Volunteer volunteer1;
    private Volunteer volunteer2;
    private Volunteer volunteer3;

    @BeforeEach
    void setUp() {
        volunteer1 = new Volunteer();
        volunteer1.setId(1L);
        volunteer1.setName("Maria Silva");
        volunteer1.setEmail("maria@test.com");
        volunteer1.setPhone("912345678");
        volunteer1.setSkills("Organização, Comunicação");
        volunteer1.setTotalPoints(150);

        volunteer2 = new Volunteer();
        volunteer2.setId(2L);
        volunteer2.setName("João Santos");
        volunteer2.setEmail("joao@test.com");
        volunteer2.setPhone("923456789");
        volunteer2.setSkills("Limpeza, Jardinagem");
        volunteer2.setTotalPoints(300);

        volunteer3 = new Volunteer();
        volunteer3.setId(3L);
        volunteer3.setName("Ana Costa");
        volunteer3.setEmail("ana@test.com");
        volunteer3.setPhone("934567890");
        volunteer3.setSkills("Ensino, Mentoria");
        volunteer3.setTotalPoints(75);
    }

    @Nested
    @DisplayName("getVolunteerPoints() Tests")
    class GetVolunteerPointsTests {

        @Test
        @DisplayName("Deve retornar pontos do voluntário com sucesso")
        void shouldReturnVolunteerPointsSuccessfully() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer1));

            VolunteerPointsResponse response = volunteerService.getVolunteerPoints(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Maria Silva");
            assertThat(response.getEmail()).isEqualTo("maria@test.com");
            assertThat(response.getTotalPoints()).isEqualTo(150);
            verify(volunteerRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve retornar zero pontos para voluntário sem participações")
        void shouldReturnZeroPointsForVolunteerWithoutParticipations() {
            Volunteer newVolunteer = new Volunteer();
            newVolunteer.setId(4L);
            newVolunteer.setName("Pedro Novo");
            newVolunteer.setEmail("pedro@test.com");
            newVolunteer.setTotalPoints(0);

            when(volunteerRepository.findById(4L)).thenReturn(Optional.of(newVolunteer));

            VolunteerPointsResponse response = volunteerService.getVolunteerPoints(4L);

            assertThat(response.getTotalPoints()).isZero();
        }

        @Test
        @DisplayName("Deve lançar exceção quando voluntário não encontrado")
        void shouldThrowExceptionWhenVolunteerNotFound() {
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> volunteerService.getVolunteerPoints(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Volunteer not found with id: 999");
            verify(volunteerRepository, times(1)).findById(999L);
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 2L, 3L})
        @DisplayName("Deve retornar pontos para diferentes voluntários")
        void shouldReturnPointsForDifferentVolunteers(Long volunteerId) {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(volunteerId);
            volunteer.setName("Volunteer " + volunteerId);
            volunteer.setEmail("volunteer" + volunteerId + "@test.com");
            volunteer.setTotalPoints(volunteerId.intValue() * 100);

            when(volunteerRepository.findById(volunteerId)).thenReturn(Optional.of(volunteer));

            VolunteerPointsResponse response = volunteerService.getVolunteerPoints(volunteerId);

            assertThat(response.getId()).isEqualTo(volunteerId);
            assertThat(response.getTotalPoints()).isEqualTo(volunteerId.intValue() * 100);
        }

        @Test
        @DisplayName("Deve retornar pontos altos corretamente")
        void shouldReturnHighPointsCorrectly() {
            volunteer1.setTotalPoints(10000);
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer1));

            VolunteerPointsResponse response = volunteerService.getVolunteerPoints(1L);

            assertThat(response.getTotalPoints()).isEqualTo(10000);
        }
    }

    @Nested
    @DisplayName("getVolunteersRanking() Tests")
    class GetVolunteersRankingTests {

        @Test
        @DisplayName("Deve retornar ranking ordenado por pontos decrescente")
        void shouldReturnRankingSortedByPointsDescending() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2, volunteer3));

            List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

            assertThat(ranking).hasSize(3);
            assertThat(ranking.get(0).getTotalPoints()).isEqualTo(300);
            assertThat(ranking.get(0).getName()).isEqualTo("João Santos");
            assertThat(ranking.get(1).getTotalPoints()).isEqualTo(150);
            assertThat(ranking.get(1).getName()).isEqualTo("Maria Silva");
            assertThat(ranking.get(2).getTotalPoints()).isEqualTo(75);
            assertThat(ranking.get(2).getName()).isEqualTo("Ana Costa");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há voluntários")
        void shouldReturnEmptyListWhenNoVolunteers() {
            when(volunteerRepository.findAll()).thenReturn(Collections.emptyList());

            List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

            assertThat(ranking).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar ranking com um único voluntário")
        void shouldReturnRankingWithSingleVolunteer() {
            when(volunteerRepository.findAll()).thenReturn(Collections.singletonList(volunteer1));

            List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

            assertThat(ranking).hasSize(1);
            assertThat(ranking.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Deve manter ordem estável para voluntários com mesmos pontos")
        void shouldMaintainStableOrderForEqualPoints() {
            volunteer1.setTotalPoints(100);
            volunteer2.setTotalPoints(100);
            volunteer3.setTotalPoints(100);

            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2, volunteer3));

            List<VolunteerPointsResponse> ranking = volunteerService.getVolunteersRanking();

            assertThat(ranking).hasSize(3);
            assertThat(ranking).extracting(VolunteerPointsResponse::getTotalPoints)
                    .containsOnly(100, 100, 100);
        }
    }

    @Nested
    @DisplayName("getTopVolunteers() Tests")
    class GetTopVolunteersTests {

        @Test
        @DisplayName("Deve retornar top N voluntários corretamente")
        void shouldReturnTopNVolunteersCorrectly() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2, volunteer3));

            List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(2);

            assertThat(top).hasSize(2);
            assertThat(top.get(0).getTotalPoints()).isEqualTo(300);
            assertThat(top.get(1).getTotalPoints()).isEqualTo(150);
        }

        @Test
        @DisplayName("Deve retornar top 1 voluntário")
        void shouldReturnTopOneVolunteer() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2, volunteer3));

            List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(1);

            assertThat(top).hasSize(1);
            assertThat(top.get(0).getName()).isEqualTo("João Santos");
        }

        @Test
        @DisplayName("Deve retornar todos quando limite maior que total")
        void shouldReturnAllWhenLimitExceedsTotal() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2));

            List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(10);

            assertThat(top).hasSize(2);
        }

        @Test
        @DisplayName("Deve retornar lista vazia para limite zero")
        void shouldReturnEmptyListForZeroLimit() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2, volunteer3));

            List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(0);

            assertThat(top).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há voluntários")
        void shouldReturnEmptyListWhenNoVolunteers() {
            when(volunteerRepository.findAll()).thenReturn(Collections.emptyList());

            List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(5);

            assertThat(top).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 5, 10})
        @DisplayName("Deve retornar tamanho correto para diferentes limites")
        void shouldReturnCorrectSizeForDifferentLimits(int limit) {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2, volunteer3));

            List<VolunteerPointsResponse> top = volunteerService.getTopVolunteers(limit);

            assertThat(top).hasSizeLessThanOrEqualTo(Math.min(limit, 3));
        }
    }

    @Nested
    @DisplayName("getVolunteerById() Tests")
    class GetVolunteerByIdTests {

        @Test
        @DisplayName("Deve retornar voluntário por ID com sucesso")
        void shouldReturnVolunteerByIdSuccessfully() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer1));

            VolunteerResponse response = volunteerService.getVolunteerById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Maria Silva");
        }

        @Test
        @DisplayName("Deve lançar exceção quando voluntário não encontrado por ID")
        void shouldThrowExceptionWhenVolunteerNotFoundById() {
            when(volunteerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> volunteerService.getVolunteerById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Volunteer not found with id: 999");
        }
    }

    @Nested
    @DisplayName("getVolunteerByEmail() Tests")
    class GetVolunteerByEmailTests {

        @Test
        @DisplayName("Deve retornar voluntário por email com sucesso")
        void shouldReturnVolunteerByEmailSuccessfully() {
            when(volunteerRepository.findByEmail("maria@test.com")).thenReturn(Optional.of(volunteer1));

            VolunteerResponse response = volunteerService.getVolunteerByEmail("maria@test.com");

            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo("maria@test.com");
        }

        @Test
        @DisplayName("Deve lançar exceção quando voluntário não encontrado por email")
        void shouldThrowExceptionWhenVolunteerNotFoundByEmail() {
            when(volunteerRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> volunteerService.getVolunteerByEmail("notfound@test.com"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Volunteer not found with email: notfound@test.com");
        }
    }

    @Nested
    @DisplayName("getAllVolunteers() Tests")
    class GetAllVolunteersTests {

        @Test
        @DisplayName("Deve retornar todos os voluntários")
        void shouldReturnAllVolunteers() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2, volunteer3));

            List<VolunteerResponse> volunteers = volunteerService.getAllVolunteers();

            assertThat(volunteers).hasSize(3);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há voluntários")
        void shouldReturnEmptyListWhenNoVolunteers() {
            when(volunteerRepository.findAll()).thenReturn(Collections.emptyList());

            List<VolunteerResponse> volunteers = volunteerService.getAllVolunteers();

            assertThat(volunteers).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail() Tests")
    class ExistsByEmailTests {

        @Test
        @DisplayName("Deve retornar true quando email existe")
        void shouldReturnTrueWhenEmailExists() {
            when(volunteerRepository.existsByEmail("maria@test.com")).thenReturn(true);

            boolean exists = volunteerService.existsByEmail("maria@test.com");

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando email não existe")
        void shouldReturnFalseWhenEmailDoesNotExist() {
            when(volunteerRepository.existsByEmail("notfound@test.com")).thenReturn(false);

            boolean exists = volunteerService.existsByEmail("notfound@test.com");

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("getConfirmedParticipations() Tests")
    class GetConfirmedParticipationsTests {

        @Test
        @DisplayName("Deve retornar participações confirmadas")
        void shouldReturnConfirmedParticipations() {
            when(volunteerRepository.existsById(1L)).thenReturn(true);
            when(applicationRepository.findByVolunteerIdAndParticipationConfirmed(1L, true))
                    .thenReturn(Collections.emptyList());

            List<ApplicationResponse> participations = volunteerService.getConfirmedParticipations(1L);

            assertThat(participations).isEmpty();
        }

        @Test
        @DisplayName("Deve lançar exceção quando voluntário não encontrado para participações")
        void shouldThrowExceptionWhenVolunteerNotFoundForParticipations() {
            when(volunteerRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> volunteerService.getConfirmedParticipations(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Volunteer not found with id: 999");
        }
    }

    @Nested
    @DisplayName("Repository Interaction Tests")
    class RepositoryInteractionTests {

        @Test
        @DisplayName("Deve chamar findById apenas uma vez")
        void shouldCallFindByIdOnce() {
            when(volunteerRepository.findById(1L)).thenReturn(Optional.of(volunteer1));

            volunteerService.getVolunteerPoints(1L);

            verify(volunteerRepository, times(1)).findById(1L);
            verifyNoMoreInteractions(volunteerRepository);
        }

        @Test
        @DisplayName("Deve chamar findAll apenas uma vez para ranking")
        void shouldCallFindAllOnceForRanking() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2));

            volunteerService.getVolunteersRanking();

            verify(volunteerRepository, times(1)).findAll();
            verifyNoMoreInteractions(volunteerRepository);
        }

        @Test
        @DisplayName("Deve chamar findAll apenas uma vez para top volunteers")
        void shouldCallFindAllOnceForTopVolunteers() {
            when(volunteerRepository.findAll()).thenReturn(Arrays.asList(volunteer1, volunteer2));

            volunteerService.getTopVolunteers(5);

            verify(volunteerRepository, times(1)).findAll();
            verifyNoMoreInteractions(volunteerRepository);
        }
    }
}
