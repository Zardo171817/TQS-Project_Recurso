package com.example.demo.controller;

import com.example.demo.dto.ApplicationResponse;
import com.example.demo.dto.VolunteerPointsResponse;
import com.example.demo.dto.VolunteerResponse;
import com.example.demo.exception.GlobalExceptionHandler;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.VolunteerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VolunteerController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Ver Saldo Atual Pontos Voluntario - Controller Tests")
class VerSaldoAtualPontosVoluntarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerService volunteerService;

    @Nested
    @DisplayName("GET /api/volunteers/{id}/points Tests")
    class GetVolunteerPointsTests {

        @Test
        @DisplayName("Deve retornar 200 OK com pontos do voluntário")
        void shouldReturn200WithVolunteerPoints() throws Exception {
            VolunteerPointsResponse response = new VolunteerPointsResponse(1L, "Maria Silva", "maria@test.com", 150);
            when(volunteerService.getVolunteerPoints(1L)).thenReturn(response);

            mockMvc.perform(get("/api/volunteers/1/points")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Maria Silva"))
                    .andExpect(jsonPath("$.email").value("maria@test.com"))
                    .andExpect(jsonPath("$.totalPoints").value(150));

            verify(volunteerService, times(1)).getVolunteerPoints(1L);
        }

        @Test
        @DisplayName("Deve retornar 200 OK com zero pontos")
        void shouldReturn200WithZeroPoints() throws Exception {
            VolunteerPointsResponse response = new VolunteerPointsResponse(2L, "João Santos", "joao@test.com", 0);
            when(volunteerService.getVolunteerPoints(2L)).thenReturn(response);

            mockMvc.perform(get("/api/volunteers/2/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPoints").value(0));
        }

        @Test
        @DisplayName("Deve retornar 404 quando voluntário não existe")
        void shouldReturn404WhenVolunteerNotFound() throws Exception {
            when(volunteerService.getVolunteerPoints(999L))
                    .thenThrow(new ResourceNotFoundException("Volunteer not found with id: 999"));

            mockMvc.perform(get("/api/volunteers/999/points"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Volunteer not found with id: 999"));
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 100L, 9999L})
        @DisplayName("Deve aceitar diferentes IDs de voluntários")
        void shouldAcceptDifferentVolunteerIds(Long volunteerId) throws Exception {
            VolunteerPointsResponse response = new VolunteerPointsResponse(volunteerId, "Test", "test@test.com", 100);
            when(volunteerService.getVolunteerPoints(volunteerId)).thenReturn(response);

            mockMvc.perform(get("/api/volunteers/" + volunteerId + "/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(volunteerId));
        }

        @Test
        @DisplayName("Deve retornar pontos altos corretamente")
        void shouldReturnHighPointsCorrectly() throws Exception {
            VolunteerPointsResponse response = new VolunteerPointsResponse(1L, "Top Volunteer", "top@test.com", 99999);
            when(volunteerService.getVolunteerPoints(1L)).thenReturn(response);

            mockMvc.perform(get("/api/volunteers/1/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPoints").value(99999));
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/ranking Tests")
    class GetVolunteersRankingTests {

        @Test
        @DisplayName("Deve retornar 200 OK com ranking de voluntários")
        void shouldReturn200WithVolunteersRanking() throws Exception {
            List<VolunteerPointsResponse> ranking = Arrays.asList(
                    new VolunteerPointsResponse(2L, "João Santos", "joao@test.com", 300),
                    new VolunteerPointsResponse(1L, "Maria Silva", "maria@test.com", 150),
                    new VolunteerPointsResponse(3L, "Ana Costa", "ana@test.com", 75)
            );
            when(volunteerService.getVolunteersRanking()).thenReturn(ranking);

            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].totalPoints").value(300))
                    .andExpect(jsonPath("$[0].name").value("João Santos"))
                    .andExpect(jsonPath("$[1].totalPoints").value(150))
                    .andExpect(jsonPath("$[2].totalPoints").value(75));

            verify(volunteerService, times(1)).getVolunteersRanking();
        }

        @Test
        @DisplayName("Deve retornar 200 OK com lista vazia quando não há voluntários")
        void shouldReturn200WithEmptyListWhenNoVolunteers() throws Exception {
            when(volunteerService.getVolunteersRanking()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Deve retornar ranking com um único voluntário")
        void shouldReturnRankingWithSingleVolunteer() throws Exception {
            List<VolunteerPointsResponse> ranking = Collections.singletonList(
                    new VolunteerPointsResponse(1L, "Único", "unico@test.com", 100)
            );
            when(volunteerService.getVolunteersRanking()).thenReturn(ranking);

            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Único"));
        }

        @Test
        @DisplayName("Deve incluir todos os campos no ranking")
        void shouldIncludeAllFieldsInRanking() throws Exception {
            List<VolunteerPointsResponse> ranking = Collections.singletonList(
                    new VolunteerPointsResponse(5L, "Complete User", "complete@test.com", 500)
            );
            when(volunteerService.getVolunteersRanking()).thenReturn(ranking);

            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[0].name").exists())
                    .andExpect(jsonPath("$[0].email").exists())
                    .andExpect(jsonPath("$[0].totalPoints").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers/top/{limit} Tests")
    class GetTopVolunteersTests {

        @Test
        @DisplayName("Deve retornar 200 OK com top voluntários")
        void shouldReturn200WithTopVolunteers() throws Exception {
            List<VolunteerPointsResponse> top = Arrays.asList(
                    new VolunteerPointsResponse(2L, "João Santos", "joao@test.com", 300),
                    new VolunteerPointsResponse(1L, "Maria Silva", "maria@test.com", 150)
            );
            when(volunteerService.getTopVolunteers(2)).thenReturn(top);

            mockMvc.perform(get("/api/volunteers/top/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].totalPoints").value(300))
                    .andExpect(jsonPath("$[1].totalPoints").value(150));

            verify(volunteerService, times(1)).getTopVolunteers(2);
        }

        @Test
        @DisplayName("Deve retornar top 1 voluntário")
        void shouldReturnTopOneVolunteer() throws Exception {
            List<VolunteerPointsResponse> top = Collections.singletonList(
                    new VolunteerPointsResponse(1L, "Top User", "top@test.com", 1000)
            );
            when(volunteerService.getTopVolunteers(1)).thenReturn(top);

            mockMvc.perform(get("/api/volunteers/top/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Top User"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia para limite grande sem voluntários")
        void shouldReturnEmptyListForLargeLimitWithoutVolunteers() throws Exception {
            when(volunteerService.getTopVolunteers(100)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/top/100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, 50})
        @DisplayName("Deve aceitar diferentes valores de limite")
        void shouldAcceptDifferentLimitValues(int limit) throws Exception {
            when(volunteerService.getTopVolunteers(limit)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/top/" + limit))
                    .andExpect(status().isOk());

            verify(volunteerService, times(1)).getTopVolunteers(limit);
        }

        @Test
        @DisplayName("Deve retornar top 5 voluntários ordenados")
        void shouldReturnTop5VolunteersSorted() throws Exception {
            List<VolunteerPointsResponse> top = Arrays.asList(
                    new VolunteerPointsResponse(1L, "V1", "v1@test.com", 500),
                    new VolunteerPointsResponse(2L, "V2", "v2@test.com", 400),
                    new VolunteerPointsResponse(3L, "V3", "v3@test.com", 300),
                    new VolunteerPointsResponse(4L, "V4", "v4@test.com", 200),
                    new VolunteerPointsResponse(5L, "V5", "v5@test.com", 100)
            );
            when(volunteerService.getTopVolunteers(5)).thenReturn(top);

            mockMvc.perform(get("/api/volunteers/top/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(5)))
                    .andExpect(jsonPath("$[0].totalPoints").value(500))
                    .andExpect(jsonPath("$[4].totalPoints").value(100));
        }
    }

    @Nested
    @DisplayName("GET /api/volunteers Tests - Basic Volunteer Endpoints")
    class BasicVolunteerEndpointsTests {

        @Test
        @DisplayName("Deve retornar lista de todos os voluntários")
        void shouldReturnAllVolunteers() throws Exception {
            VolunteerResponse v1 = new VolunteerResponse();
            v1.setId(1L);
            v1.setName("Maria");
            v1.setEmail("maria@test.com");

            when(volunteerService.getAllVolunteers()).thenReturn(Collections.singletonList(v1));

            mockMvc.perform(get("/api/volunteers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Deve retornar voluntário por ID")
        void shouldReturnVolunteerById() throws Exception {
            VolunteerResponse v1 = new VolunteerResponse();
            v1.setId(1L);
            v1.setName("Maria");
            v1.setEmail("maria@test.com");

            when(volunteerService.getVolunteerById(1L)).thenReturn(v1);

            mockMvc.perform(get("/api/volunteers/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("Deve retornar voluntário por email")
        void shouldReturnVolunteerByEmail() throws Exception {
            VolunteerResponse v1 = new VolunteerResponse();
            v1.setId(1L);
            v1.setName("Maria");
            v1.setEmail("maria@test.com");

            when(volunteerService.getVolunteerByEmail("maria@test.com")).thenReturn(v1);

            mockMvc.perform(get("/api/volunteers/email/maria@test.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("maria@test.com"));
        }

        @Test
        @DisplayName("Deve verificar se email existe")
        void shouldCheckIfEmailExists() throws Exception {
            when(volunteerService.existsByEmail("maria@test.com")).thenReturn(true);

            mockMvc.perform(get("/api/volunteers/exists/maria@test.com"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"));
        }

        @Test
        @DisplayName("Deve retornar participações confirmadas")
        void shouldReturnConfirmedParticipations() throws Exception {
            ApplicationResponse app = new ApplicationResponse();
            app.setId(1L);
            app.setParticipationConfirmed(true);

            when(volunteerService.getConfirmedParticipations(1L)).thenReturn(Collections.singletonList(app));

            mockMvc.perform(get("/api/volunteers/1/confirmed-participations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("Content Type and Response Format Tests")
    class ContentTypeAndResponseFormatTests {

        @Test
        @DisplayName("Deve retornar JSON como content type")
        void shouldReturnJsonContentType() throws Exception {
            VolunteerPointsResponse response = new VolunteerPointsResponse(1L, "Test", "test@test.com", 100);
            when(volunteerService.getVolunteerPoints(1L)).thenReturn(response);

            mockMvc.perform(get("/api/volunteers/1/points"))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Deve retornar estrutura JSON correta para pontos")
        void shouldReturnCorrectJsonStructureForPoints() throws Exception {
            VolunteerPointsResponse response = new VolunteerPointsResponse(1L, "Test", "test@test.com", 100);
            when(volunteerService.getVolunteerPoints(1L)).thenReturn(response);

            mockMvc.perform(get("/api/volunteers/1/points"))
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").isString())
                    .andExpect(jsonPath("$.email").isString())
                    .andExpect(jsonPath("$.totalPoints").isNumber());
        }

        @Test
        @DisplayName("Deve retornar array JSON para ranking")
        void shouldReturnJsonArrayForRanking() throws Exception {
            when(volunteerService.getVolunteersRanking()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/ranking"))
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Deve retornar array JSON para top volunteers")
        void shouldReturnJsonArrayForTopVolunteers() throws Exception {
            when(volunteerService.getTopVolunteers(5)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/volunteers/top/5"))
                    .andExpect(jsonPath("$").isArray());
        }
    }
}
