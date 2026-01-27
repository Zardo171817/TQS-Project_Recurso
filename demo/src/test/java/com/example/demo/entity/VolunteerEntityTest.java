package com.example.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Volunteer Entity Tests")
class VolunteerEntityTest {

    private Volunteer volunteer;

    @BeforeEach
    void setUp() {
        volunteer = new Volunteer();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Deve criar voluntário com construtor vazio")
        void shouldCreateVolunteerWithNoArgsConstructor() {
            Volunteer newVolunteer = new Volunteer();

            assertThat(newVolunteer).isNotNull();
            assertThat(newVolunteer.getId()).isNull();
            assertThat(newVolunteer.getTotalPoints()).isEqualTo(0);
        }

        @Test
        @DisplayName("Deve criar voluntário com todos os argumentos")
        void shouldCreateVolunteerWithAllArgsConstructor() {
            Volunteer fullVolunteer = new Volunteer(1L, "Maria Silva", "maria@test.com", "912345678", "Organização", 150);

            assertThat(fullVolunteer.getId()).isEqualTo(1L);
            assertThat(fullVolunteer.getName()).isEqualTo("Maria Silva");
            assertThat(fullVolunteer.getEmail()).isEqualTo("maria@test.com");
            assertThat(fullVolunteer.getPhone()).isEqualTo("912345678");
            assertThat(fullVolunteer.getSkills()).isEqualTo("Organização");
            assertThat(fullVolunteer.getTotalPoints()).isEqualTo(150);
        }
    }

    @Nested
    @DisplayName("TotalPoints Field Tests")
    class TotalPointsTests {

        @Test
        @DisplayName("Deve ter valor padrão de zero para totalPoints")
        void shouldHaveDefaultZeroTotalPoints() {
            assertThat(volunteer.getTotalPoints()).isEqualTo(0);
        }

        @Test
        @DisplayName("Deve permitir definir pontos positivos")
        void shouldAllowSettingPositivePoints() {
            volunteer.setTotalPoints(100);

            assertThat(volunteer.getTotalPoints()).isEqualTo(100);
        }

        @Test
        @DisplayName("Deve permitir definir pontos altos")
        void shouldAllowSettingHighPoints() {
            volunteer.setTotalPoints(10000);

            assertThat(volunteer.getTotalPoints()).isEqualTo(10000);
        }

        @Test
        @DisplayName("Deve permitir definir zero pontos")
        void shouldAllowSettingZeroPoints() {
            volunteer.setTotalPoints(100);
            volunteer.setTotalPoints(0);

            assertThat(volunteer.getTotalPoints()).isZero();
        }

        @Test
        @DisplayName("Deve permitir incrementar pontos")
        void shouldAllowIncrementingPoints() {
            volunteer.setTotalPoints(100);
            volunteer.setTotalPoints(volunteer.getTotalPoints() + 50);

            assertThat(volunteer.getTotalPoints()).isEqualTo(150);
        }
    }

    @Nested
    @DisplayName("@PrePersist onCreate() Tests")
    class PrePersistTests {

        @Test
        @DisplayName("Deve inicializar totalPoints para zero quando null no onCreate")
        void shouldInitializeTotalPointsToZeroWhenNullOnCreate() {
            Volunteer newVolunteer = new Volunteer();
            newVolunteer.setTotalPoints(null);

            newVolunteer.onCreate();

            assertThat(newVolunteer.getTotalPoints()).isZero();
        }

        @Test
        @DisplayName("Deve manter totalPoints quando já tem valor no onCreate")
        void shouldKeepTotalPointsWhenAlreadySetOnCreate() {
            volunteer.setTotalPoints(200);

            volunteer.onCreate();

            assertThat(volunteer.getTotalPoints()).isEqualTo(200);
        }

        @Test
        @DisplayName("Deve manter zero quando totalPoints é zero no onCreate")
        void shouldKeepZeroWhenTotalPointsIsZeroOnCreate() {
            volunteer.setTotalPoints(0);

            volunteer.onCreate();

            assertThat(volunteer.getTotalPoints()).isZero();
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Deve permitir definir e obter ID")
        void shouldAllowSettingAndGettingId() {
            volunteer.setId(5L);

            assertThat(volunteer.getId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("Deve permitir definir e obter nome")
        void shouldAllowSettingAndGettingName() {
            volunteer.setName("João Santos");

            assertThat(volunteer.getName()).isEqualTo("João Santos");
        }

        @Test
        @DisplayName("Deve permitir definir e obter email")
        void shouldAllowSettingAndGettingEmail() {
            volunteer.setEmail("joao@test.com");

            assertThat(volunteer.getEmail()).isEqualTo("joao@test.com");
        }

        @Test
        @DisplayName("Deve permitir definir e obter telefone")
        void shouldAllowSettingAndGettingPhone() {
            volunteer.setPhone("923456789");

            assertThat(volunteer.getPhone()).isEqualTo("923456789");
        }

        @Test
        @DisplayName("Deve permitir definir e obter skills")
        void shouldAllowSettingAndGettingSkills() {
            volunteer.setSkills("Limpeza, Jardinagem");

            assertThat(volunteer.getSkills()).isEqualTo("Limpeza, Jardinagem");
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Deve ter equals funcionando para objetos iguais")
        void shouldHaveCorrectEqualsForEqualObjects() {
            Volunteer v1 = new Volunteer(1L, "Test", "test@test.com", "123", "Skills", 100);
            Volunteer v2 = new Volunteer(1L, "Test", "test@test.com", "123", "Skills", 100);

            assertThat(v1).isEqualTo(v2);
        }

        @Test
        @DisplayName("Deve ter equals diferente para objetos diferentes")
        void shouldHaveDifferentEqualsForDifferentObjects() {
            Volunteer v1 = new Volunteer(1L, "Test1", "test1@test.com", "123", "Skills", 100);
            Volunteer v2 = new Volunteer(2L, "Test2", "test2@test.com", "456", "Skills", 200);

            assertThat(v1).isNotEqualTo(v2);
        }

        @Test
        @DisplayName("Deve ter hashCode consistente")
        void shouldHaveConsistentHashCode() {
            Volunteer v1 = new Volunteer(1L, "Test", "test@test.com", "123", "Skills", 100);
            Volunteer v2 = new Volunteer(1L, "Test", "test@test.com", "123", "Skills", 100);

            assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Deve ter toString não nulo")
        void shouldHaveNonNullToString() {
            volunteer.setId(1L);
            volunteer.setName("Test");
            volunteer.setEmail("test@test.com");
            volunteer.setTotalPoints(100);

            String toString = volunteer.toString();

            assertThat(toString).isNotNull();
            assertThat(toString).isNotEmpty();
        }

        @Test
        @DisplayName("Deve incluir campos principais no toString")
        void shouldIncludeMainFieldsInToString() {
            volunteer.setId(1L);
            volunteer.setName("Maria Silva");
            volunteer.setEmail("maria@test.com");
            volunteer.setTotalPoints(150);

            String toString = volunteer.toString();

            assertThat(toString).contains("1");
            assertThat(toString).contains("Maria Silva");
            assertThat(toString).contains("maria@test.com");
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Deve lidar com nome vazio")
        void shouldHandleEmptyName() {
            volunteer.setName("");

            assertThat(volunteer.getName()).isEmpty();
        }

        @Test
        @DisplayName("Deve lidar com nome com espaços")
        void shouldHandleNameWithSpaces() {
            volunteer.setName("Maria da Silva Santos");

            assertThat(volunteer.getName()).isEqualTo("Maria da Silva Santos");
        }

        @Test
        @DisplayName("Deve lidar com email longo")
        void shouldHandleLongEmail() {
            String longEmail = "verylongemail.with.many.parts.and.subdomains@subdomain.example.company.org";
            volunteer.setEmail(longEmail);

            assertThat(volunteer.getEmail()).isEqualTo(longEmail);
        }

        @Test
        @DisplayName("Deve lidar com skills múltiplas")
        void shouldHandleMultipleSkills() {
            volunteer.setSkills("Organização, Limpeza, Jardinagem, Comunicação, Ensino");

            assertThat(volunteer.getSkills()).contains("Organização");
            assertThat(volunteer.getSkills()).contains("Ensino");
        }

        @Test
        @DisplayName("Deve lidar com telefone null")
        void shouldHandleNullPhone() {
            volunteer.setPhone(null);

            assertThat(volunteer.getPhone()).isNull();
        }

        @Test
        @DisplayName("Deve lidar com skills null")
        void shouldHandleNullSkills() {
            volunteer.setSkills(null);

            assertThat(volunteer.getSkills()).isNull();
        }
    }
}
