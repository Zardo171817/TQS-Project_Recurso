package com.example.demo.dto;

import com.example.demo.entity.Promoter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PromoterProfileResponse Tests")
class PromoterProfileResponseTest {

    @Nested
    @DisplayName("FromEntity Tests")
    class FromEntityTests {

        @Test
        @DisplayName("Should convert entity to response with all fields")
        void shouldConvertEntityToResponseWithAllFields() {
            LocalDateTime createdAt = LocalDateTime.now().minusDays(10);
            LocalDateTime updatedAt = LocalDateTime.now();

            Promoter promoter = new Promoter();
            promoter.setId(1L);
            promoter.setName("Maria Santos");
            promoter.setEmail("maria@org.org");
            promoter.setOrganization("Associacao Solidaria");
            promoter.setDescription("Uma organizacao dedicada ao bem-estar social");
            promoter.setPhone("+351 234 567 890");
            promoter.setWebsite("https://www.org.org");
            promoter.setAddress("Rua Principal, 123, Aveiro");
            promoter.setLogoUrl("https://logo.png");
            promoter.setOrganizationType("ONG");
            promoter.setAreaOfActivity("Educacao, Saude");
            promoter.setFoundedYear("2010");
            promoter.setNumberOfEmployees("21-50");
            promoter.setSocialMedia("Facebook: /assoc");
            promoter.setProfileCreatedAt(createdAt);
            promoter.setProfileUpdatedAt(updatedAt);

            PromoterProfileResponse response = PromoterProfileResponse.fromEntity(promoter);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Maria Santos");
            assertThat(response.getEmail()).isEqualTo("maria@org.org");
            assertThat(response.getOrganization()).isEqualTo("Associacao Solidaria");
            assertThat(response.getDescription()).isEqualTo("Uma organizacao dedicada ao bem-estar social");
            assertThat(response.getPhone()).isEqualTo("+351 234 567 890");
            assertThat(response.getWebsite()).isEqualTo("https://www.org.org");
            assertThat(response.getAddress()).isEqualTo("Rua Principal, 123, Aveiro");
            assertThat(response.getLogoUrl()).isEqualTo("https://logo.png");
            assertThat(response.getOrganizationType()).isEqualTo("ONG");
            assertThat(response.getAreaOfActivity()).isEqualTo("Educacao, Saude");
            assertThat(response.getFoundedYear()).isEqualTo("2010");
            assertThat(response.getNumberOfEmployees()).isEqualTo("21-50");
            assertThat(response.getSocialMedia()).isEqualTo("Facebook: /assoc");
            assertThat(response.getProfileCreatedAt()).isEqualTo(createdAt);
            assertThat(response.getProfileUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("Should convert entity with null optional fields")
        void shouldConvertEntityWithNullOptionalFields() {
            Promoter promoter = new Promoter();
            promoter.setId(1L);
            promoter.setName("Maria Santos");
            promoter.setEmail("maria@org.org");
            promoter.setOrganization("Associacao Solidaria");

            PromoterProfileResponse response = PromoterProfileResponse.fromEntity(promoter);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Maria Santos");
            assertThat(response.getEmail()).isEqualTo("maria@org.org");
            assertThat(response.getOrganization()).isEqualTo("Associacao Solidaria");
            assertThat(response.getDescription()).isNull();
            assertThat(response.getPhone()).isNull();
            assertThat(response.getWebsite()).isNull();
            assertThat(response.getAddress()).isNull();
            assertThat(response.getLogoUrl()).isNull();
            assertThat(response.getOrganizationType()).isNull();
            assertThat(response.getAreaOfActivity()).isNull();
            assertThat(response.getFoundedYear()).isNull();
            assertThat(response.getNumberOfEmployees()).isNull();
            assertThat(response.getSocialMedia()).isNull();
            assertThat(response.getProfileCreatedAt()).isNull();
            assertThat(response.getProfileUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("Should handle entity with empty strings")
        void shouldHandleEntityWithEmptyStrings() {
            Promoter promoter = new Promoter();
            promoter.setId(1L);
            promoter.setName("Maria Santos");
            promoter.setEmail("maria@org.org");
            promoter.setOrganization("Associacao Solidaria");
            promoter.setDescription("");
            promoter.setPhone("");

            PromoterProfileResponse response = PromoterProfileResponse.fromEntity(promoter);

            assertThat(response.getDescription()).isEmpty();
            assertThat(response.getPhone()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with no args constructor")
        void shouldCreateWithNoArgsConstructor() {
            PromoterProfileResponse response = new PromoterProfileResponse();

            assertThat(response.getId()).isNull();
            assertThat(response.getName()).isNull();
            assertThat(response.getEmail()).isNull();
        }

        @Test
        @DisplayName("Should create with all args constructor")
        void shouldCreateWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();

            PromoterProfileResponse response = new PromoterProfileResponse(
                    1L, "Name", "email@org.org", "Org", "Desc", "Phone",
                    "Website", "Address", "Logo", "Type", "Area",
                    "Year", "Emp", "Social", now, now
            );

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Name");
            assertThat(response.getEmail()).isEqualTo("email@org.org");
            assertThat(response.getOrganization()).isEqualTo("Org");
            assertThat(response.getDescription()).isEqualTo("Desc");
            assertThat(response.getPhone()).isEqualTo("Phone");
            assertThat(response.getWebsite()).isEqualTo("Website");
            assertThat(response.getAddress()).isEqualTo("Address");
            assertThat(response.getLogoUrl()).isEqualTo("Logo");
            assertThat(response.getOrganizationType()).isEqualTo("Type");
            assertThat(response.getAreaOfActivity()).isEqualTo("Area");
            assertThat(response.getFoundedYear()).isEqualTo("Year");
            assertThat(response.getNumberOfEmployees()).isEqualTo("Emp");
            assertThat(response.getSocialMedia()).isEqualTo("Social");
            assertThat(response.getProfileCreatedAt()).isEqualTo(now);
            assertThat(response.getProfileUpdatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set all fields correctly")
        void shouldGetAndSetAllFieldsCorrectly() {
            PromoterProfileResponse response = new PromoterProfileResponse();
            LocalDateTime now = LocalDateTime.now();

            response.setId(1L);
            response.setName("Test Name");
            response.setEmail("test@org.org");
            response.setOrganization("Test Org");
            response.setDescription("Test Description");
            response.setPhone("123456789");
            response.setWebsite("https://test.org");
            response.setAddress("Test Address");
            response.setLogoUrl("https://logo.png");
            response.setOrganizationType("ONG");
            response.setAreaOfActivity("Education");
            response.setFoundedYear("2020");
            response.setNumberOfEmployees("1-5");
            response.setSocialMedia("Twitter: @test");
            response.setProfileCreatedAt(now);
            response.setProfileUpdatedAt(now);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Test Name");
            assertThat(response.getEmail()).isEqualTo("test@org.org");
            assertThat(response.getOrganization()).isEqualTo("Test Org");
            assertThat(response.getDescription()).isEqualTo("Test Description");
            assertThat(response.getPhone()).isEqualTo("123456789");
            assertThat(response.getWebsite()).isEqualTo("https://test.org");
            assertThat(response.getAddress()).isEqualTo("Test Address");
            assertThat(response.getLogoUrl()).isEqualTo("https://logo.png");
            assertThat(response.getOrganizationType()).isEqualTo("ONG");
            assertThat(response.getAreaOfActivity()).isEqualTo("Education");
            assertThat(response.getFoundedYear()).isEqualTo("2020");
            assertThat(response.getNumberOfEmployees()).isEqualTo("1-5");
            assertThat(response.getSocialMedia()).isEqualTo("Twitter: @test");
            assertThat(response.getProfileCreatedAt()).isEqualTo(now);
            assertThat(response.getProfileUpdatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            LocalDateTime now = LocalDateTime.now();

            PromoterProfileResponse response1 = new PromoterProfileResponse(
                    1L, "Name", "email@org.org", "Org", "Desc", "Phone",
                    "Website", "Address", "Logo", "Type", "Area",
                    "Year", "Emp", "Social", now, now
            );

            PromoterProfileResponse response2 = new PromoterProfileResponse(
                    1L, "Name", "email@org.org", "Org", "Desc", "Phone",
                    "Website", "Address", "Logo", "Type", "Area",
                    "Year", "Emp", "Social", now, now
            );

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when id is different")
        void shouldNotBeEqualWhenIdIsDifferent() {
            PromoterProfileResponse response1 = new PromoterProfileResponse();
            response1.setId(1L);

            PromoterProfileResponse response2 = new PromoterProfileResponse();
            response2.setId(2L);

            assertThat(response1).isNotEqualTo(response2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate valid toString")
        void shouldGenerateValidToString() {
            PromoterProfileResponse response = new PromoterProfileResponse();
            response.setId(1L);
            response.setName("Maria Santos");
            response.setEmail("maria@org.org");

            String result = response.toString();

            assertThat(result).contains("PromoterProfileResponse");
            assertThat(result).contains("1");
            assertThat(result).contains("Maria Santos");
            assertThat(result).contains("maria@org.org");
        }
    }
}
