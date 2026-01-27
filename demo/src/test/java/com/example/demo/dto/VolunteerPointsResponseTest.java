package com.example.demo.dto;

import com.example.demo.entity.Volunteer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("VolunteerPointsResponse DTO Tests")
class VolunteerPointsResponseTest {

    private Volunteer volunteer;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Maria Silva");
        volunteer.setEmail("maria@test.com");
        volunteer.setPhone("912345678");
        volunteer.setSkills("Organização");
        volunteer.setTotalPoints(150);
    }

    @Nested
    @DisplayName("fromEntity() Tests")
    class FromEntityTests {

        @Test
        @DisplayName("Deve converter Volunteer entity para DTO corretamente")
        void shouldConvertVolunteerEntityToDto() {
            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Maria Silva");
            assertThat(response.getEmail()).isEqualTo("maria@test.com");
            assertThat(response.getTotalPoints()).isEqualTo(150);
        }

        @Test
        @DisplayName("Deve converter voluntário com zero pontos")
        void shouldConvertVolunteerWithZeroPoints() {
            volunteer.setTotalPoints(0);

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getTotalPoints()).isZero();
        }

        @Test
        @DisplayName("Deve converter voluntário com pontos altos")
        void shouldConvertVolunteerWithHighPoints() {
            volunteer.setTotalPoints(99999);

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getTotalPoints()).isEqualTo(99999);
        }

        @Test
        @DisplayName("Deve preservar nome completo com espaços")
        void shouldPreserveFullNameWithSpaces() {
            volunteer.setName("Maria da Silva Santos");

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getName()).isEqualTo("Maria da Silva Santos");
        }

        @Test
        @DisplayName("Deve preservar email com caracteres especiais")
        void shouldPreserveEmailWithSpecialCharacters() {
            volunteer.setEmail("maria.silva+test@company-example.com");

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getEmail()).isEqualTo("maria.silva+test@company-example.com");
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Deve criar DTO com construtor vazio")
        void shouldCreateDtoWithNoArgsConstructor() {
            VolunteerPointsResponse response = new VolunteerPointsResponse();

            assertThat(response).isNotNull();
            assertThat(response.getId()).isNull();
            assertThat(response.getName()).isNull();
            assertThat(response.getEmail()).isNull();
            assertThat(response.getTotalPoints()).isNull();
        }

        @Test
        @DisplayName("Deve criar DTO com construtor completo")
        void shouldCreateDtoWithAllArgsConstructor() {
            VolunteerPointsResponse response = new VolunteerPointsResponse(5L, "João Santos", "joao@test.com", 300);

            assertThat(response.getId()).isEqualTo(5L);
            assertThat(response.getName()).isEqualTo("João Santos");
            assertThat(response.getEmail()).isEqualTo("joao@test.com");
            assertThat(response.getTotalPoints()).isEqualTo(300);
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Deve permitir alterar ID via setter")
        void shouldAllowIdChange() {
            VolunteerPointsResponse response = new VolunteerPointsResponse();
            response.setId(10L);

            assertThat(response.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("Deve permitir alterar nome via setter")
        void shouldAllowNameChange() {
            VolunteerPointsResponse response = new VolunteerPointsResponse();
            response.setName("Novo Nome");

            assertThat(response.getName()).isEqualTo("Novo Nome");
        }

        @Test
        @DisplayName("Deve permitir alterar email via setter")
        void shouldAllowEmailChange() {
            VolunteerPointsResponse response = new VolunteerPointsResponse();
            response.setEmail("novo@email.com");

            assertThat(response.getEmail()).isEqualTo("novo@email.com");
        }

        @Test
        @DisplayName("Deve permitir alterar pontos via setter")
        void shouldAllowTotalPointsChange() {
            VolunteerPointsResponse response = new VolunteerPointsResponse();
            response.setTotalPoints(500);

            assertThat(response.getTotalPoints()).isEqualTo(500);
        }
    }

    @Nested
    @DisplayName("Equals, HashCode and ToString Tests")
    class EqualsHashCodeToStringTests {

        @Test
        @DisplayName("Deve ter equals funcionando corretamente")
        void shouldHaveCorrectEquals() {
            VolunteerPointsResponse response1 = new VolunteerPointsResponse(1L, "Test", "test@test.com", 100);
            VolunteerPointsResponse response2 = new VolunteerPointsResponse(1L, "Test", "test@test.com", 100);

            assertThat(response1).isEqualTo(response2);
        }

        @Test
        @DisplayName("Deve ter equals diferente para objetos diferentes")
        void shouldHaveDifferentEqualsForDifferentObjects() {
            VolunteerPointsResponse response1 = new VolunteerPointsResponse(1L, "Test", "test@test.com", 100);
            VolunteerPointsResponse response2 = new VolunteerPointsResponse(2L, "Test", "test@test.com", 100);

            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("Deve ter hashCode consistente")
        void shouldHaveConsistentHashCode() {
            VolunteerPointsResponse response1 = new VolunteerPointsResponse(1L, "Test", "test@test.com", 100);
            VolunteerPointsResponse response2 = new VolunteerPointsResponse(1L, "Test", "test@test.com", 100);

            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("Deve ter toString não nulo")
        void shouldHaveNonNullToString() {
            VolunteerPointsResponse response = new VolunteerPointsResponse(1L, "Test", "test@test.com", 100);

            assertThat(response.toString()).isNotNull();
            assertThat(response.toString()).contains("1");
            assertThat(response.toString()).contains("Test");
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Deve lidar com nome vazio")
        void shouldHandleEmptyName() {
            volunteer.setName("");

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getName()).isEmpty();
        }

        @Test
        @DisplayName("Deve lidar com pontos negativos")
        void shouldHandleNegativePoints() {
            volunteer.setTotalPoints(-10);

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getTotalPoints()).isEqualTo(-10);
        }

        @Test
        @DisplayName("Deve lidar com ID máximo de Long")
        void shouldHandleMaxLongId() {
            volunteer.setId(Long.MAX_VALUE);

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getId()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("Deve lidar com pontos máximo de Integer")
        void shouldHandleMaxIntegerPoints() {
            volunteer.setTotalPoints(Integer.MAX_VALUE);

            VolunteerPointsResponse response = VolunteerPointsResponse.fromEntity(volunteer);

            assertThat(response.getTotalPoints()).isEqualTo(Integer.MAX_VALUE);
        }
    }
}
