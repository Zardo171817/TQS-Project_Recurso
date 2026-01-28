package com.example.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Promoter Entity Tests")
class PromoterEntityTest {

    private Promoter promoter;

    @BeforeEach
    void setUp() {
        promoter = new Promoter();
    }

    @Nested
    @DisplayName("Basic Field Tests")
    class BasicFieldTests {

        @Test
        @DisplayName("Should set and get id correctly")
        void shouldSetAndGetIdCorrectly() {
            promoter.setId(1L);
            assertThat(promoter.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should set and get name correctly")
        void shouldSetAndGetNameCorrectly() {
            promoter.setName("Maria Santos");
            assertThat(promoter.getName()).isEqualTo("Maria Santos");
        }

        @Test
        @DisplayName("Should set and get email correctly")
        void shouldSetAndGetEmailCorrectly() {
            promoter.setEmail("maria@org.org");
            assertThat(promoter.getEmail()).isEqualTo("maria@org.org");
        }

        @Test
        @DisplayName("Should set and get organization correctly")
        void shouldSetAndGetOrganizationCorrectly() {
            promoter.setOrganization("Associacao Solidaria");
            assertThat(promoter.getOrganization()).isEqualTo("Associacao Solidaria");
        }

        @Test
        @DisplayName("Should set and get description correctly")
        void shouldSetAndGetDescriptionCorrectly() {
            promoter.setDescription("Uma organizacao social");
            assertThat(promoter.getDescription()).isEqualTo("Uma organizacao social");
        }

        @Test
        @DisplayName("Should set and get phone correctly")
        void shouldSetAndGetPhoneCorrectly() {
            promoter.setPhone("+351 234 567 890");
            assertThat(promoter.getPhone()).isEqualTo("+351 234 567 890");
        }

        @Test
        @DisplayName("Should set and get website correctly")
        void shouldSetAndGetWebsiteCorrectly() {
            promoter.setWebsite("https://www.org.org");
            assertThat(promoter.getWebsite()).isEqualTo("https://www.org.org");
        }

        @Test
        @DisplayName("Should set and get address correctly")
        void shouldSetAndGetAddressCorrectly() {
            promoter.setAddress("Rua Principal, 123");
            assertThat(promoter.getAddress()).isEqualTo("Rua Principal, 123");
        }

        @Test
        @DisplayName("Should set and get logoUrl correctly")
        void shouldSetAndGetLogoUrlCorrectly() {
            promoter.setLogoUrl("https://logo.png");
            assertThat(promoter.getLogoUrl()).isEqualTo("https://logo.png");
        }

        @Test
        @DisplayName("Should set and get organizationType correctly")
        void shouldSetAndGetOrganizationTypeCorrectly() {
            promoter.setOrganizationType("ONG");
            assertThat(promoter.getOrganizationType()).isEqualTo("ONG");
        }

        @Test
        @DisplayName("Should set and get areaOfActivity correctly")
        void shouldSetAndGetAreaOfActivityCorrectly() {
            promoter.setAreaOfActivity("Educacao, Saude");
            assertThat(promoter.getAreaOfActivity()).isEqualTo("Educacao, Saude");
        }

        @Test
        @DisplayName("Should set and get foundedYear correctly")
        void shouldSetAndGetFoundedYearCorrectly() {
            promoter.setFoundedYear("2010");
            assertThat(promoter.getFoundedYear()).isEqualTo("2010");
        }

        @Test
        @DisplayName("Should set and get numberOfEmployees correctly")
        void shouldSetAndGetNumberOfEmployeesCorrectly() {
            promoter.setNumberOfEmployees("21-50");
            assertThat(promoter.getNumberOfEmployees()).isEqualTo("21-50");
        }

        @Test
        @DisplayName("Should set and get socialMedia correctly")
        void shouldSetAndGetSocialMediaCorrectly() {
            promoter.setSocialMedia("Facebook: /org");
            assertThat(promoter.getSocialMedia()).isEqualTo("Facebook: /org");
        }

        @Test
        @DisplayName("Should set and get profileCreatedAt correctly")
        void shouldSetAndGetProfileCreatedAtCorrectly() {
            LocalDateTime now = LocalDateTime.now();
            promoter.setProfileCreatedAt(now);
            assertThat(promoter.getProfileCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should set and get profileUpdatedAt correctly")
        void shouldSetAndGetProfileUpdatedAtCorrectly() {
            LocalDateTime now = LocalDateTime.now();
            promoter.setProfileUpdatedAt(now);
            assertThat(promoter.getProfileUpdatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should initialize opportunities list as empty")
        void shouldInitializeOpportunitiesListAsEmpty() {
            Promoter newPromoter = new Promoter();
            newPromoter.setOpportunities(new ArrayList<>());
            assertThat(newPromoter.getOpportunities()).isEmpty();
        }

        @Test
        @DisplayName("Should set and get opportunities correctly")
        void shouldSetAndGetOpportunitiesCorrectly() {
            List<Opportunity> opportunities = new ArrayList<>();
            Opportunity opportunity = new Opportunity();
            opportunity.setId(1L);
            opportunity.setTitle("Test Opportunity");
            opportunities.add(opportunity);

            promoter.setOpportunities(opportunities);

            assertThat(promoter.getOpportunities()).hasSize(1);
            assertThat(promoter.getOpportunities().get(0).getTitle()).isEqualTo("Test Opportunity");
        }

        @Test
        @DisplayName("Should add opportunity to list")
        void shouldAddOpportunityToList() {
            promoter.setOpportunities(new ArrayList<>());

            Opportunity opportunity = new Opportunity();
            opportunity.setId(1L);
            opportunity.setTitle("New Opportunity");

            promoter.getOpportunities().add(opportunity);

            assertThat(promoter.getOpportunities()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create with no args constructor")
        void shouldCreateWithNoArgsConstructor() {
            Promoter newPromoter = new Promoter();

            assertThat(newPromoter.getId()).isNull();
            assertThat(newPromoter.getName()).isNull();
            assertThat(newPromoter.getEmail()).isNull();
        }

        @Test
        @DisplayName("Should create with all args constructor")
        void shouldCreateWithAllArgsConstructor() {
            LocalDateTime now = LocalDateTime.now();
            List<Opportunity> opportunities = new ArrayList<>();

            Promoter newPromoter = new Promoter(
                    1L, "Name", "email@org.org", "Organization",
                    "Description", "Phone", "Website", "Address",
                    "LogoUrl", "Type", "Area", "Year", "Employees",
                    "Social", now, now, opportunities
            );

            assertThat(newPromoter.getId()).isEqualTo(1L);
            assertThat(newPromoter.getName()).isEqualTo("Name");
            assertThat(newPromoter.getEmail()).isEqualTo("email@org.org");
            assertThat(newPromoter.getOrganization()).isEqualTo("Organization");
            assertThat(newPromoter.getDescription()).isEqualTo("Description");
            assertThat(newPromoter.getPhone()).isEqualTo("Phone");
            assertThat(newPromoter.getWebsite()).isEqualTo("Website");
            assertThat(newPromoter.getAddress()).isEqualTo("Address");
            assertThat(newPromoter.getLogoUrl()).isEqualTo("LogoUrl");
            assertThat(newPromoter.getOrganizationType()).isEqualTo("Type");
            assertThat(newPromoter.getAreaOfActivity()).isEqualTo("Area");
            assertThat(newPromoter.getFoundedYear()).isEqualTo("Year");
            assertThat(newPromoter.getNumberOfEmployees()).isEqualTo("Employees");
            assertThat(newPromoter.getSocialMedia()).isEqualTo("Social");
            assertThat(newPromoter.getProfileCreatedAt()).isEqualTo(now);
            assertThat(newPromoter.getProfileUpdatedAt()).isEqualTo(now);
            assertThat(newPromoter.getOpportunities()).isEqualTo(opportunities);
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when all fields are the same")
        void shouldBeEqualWhenAllFieldsAreSame() {
            Promoter promoter1 = new Promoter();
            promoter1.setId(1L);
            promoter1.setName("Maria Santos");
            promoter1.setEmail("maria@org.org");
            promoter1.setOrganization("Org");

            Promoter promoter2 = new Promoter();
            promoter2.setId(1L);
            promoter2.setName("Maria Santos");
            promoter2.setEmail("maria@org.org");
            promoter2.setOrganization("Org");

            assertThat(promoter1).isEqualTo(promoter2);
            assertThat(promoter1.hashCode()).isEqualTo(promoter2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when id is different")
        void shouldNotBeEqualWhenIdIsDifferent() {
            Promoter promoter1 = new Promoter();
            promoter1.setId(1L);
            promoter1.setName("Maria Santos");

            Promoter promoter2 = new Promoter();
            promoter2.setId(2L);
            promoter2.setName("Maria Santos");

            assertThat(promoter1).isNotEqualTo(promoter2);
        }

        @Test
        @DisplayName("Should not be equal when name is different")
        void shouldNotBeEqualWhenNameIsDifferent() {
            Promoter promoter1 = new Promoter();
            promoter1.setId(1L);
            promoter1.setName("Maria Santos");

            Promoter promoter2 = new Promoter();
            promoter2.setId(1L);
            promoter2.setName("Joao Silva");

            assertThat(promoter1).isNotEqualTo(promoter2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            Promoter promoter1 = new Promoter();
            promoter1.setId(1L);

            assertThat(promoter1).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should be equal to itself")
        void shouldBeEqualToItself() {
            Promoter promoter1 = new Promoter();
            promoter1.setId(1L);

            assertThat(promoter1).isEqualTo(promoter1);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate valid toString")
        void shouldGenerateValidToString() {
            promoter.setId(1L);
            promoter.setName("Maria Santos");
            promoter.setEmail("maria@org.org");
            promoter.setOrganization("Test Org");

            String result = promoter.toString();

            assertThat(result).contains("Promoter");
            assertThat(result).contains("1");
            assertThat(result).contains("Maria Santos");
            assertThat(result).contains("maria@org.org");
        }
    }

    @Nested
    @DisplayName("Null Value Tests")
    class NullValueTests {

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValuesGracefully() {
            promoter.setId(null);
            promoter.setName(null);
            promoter.setEmail(null);
            promoter.setOrganization(null);
            promoter.setDescription(null);
            promoter.setPhone(null);
            promoter.setWebsite(null);
            promoter.setAddress(null);
            promoter.setLogoUrl(null);
            promoter.setOrganizationType(null);
            promoter.setAreaOfActivity(null);
            promoter.setFoundedYear(null);
            promoter.setNumberOfEmployees(null);
            promoter.setSocialMedia(null);
            promoter.setProfileCreatedAt(null);
            promoter.setProfileUpdatedAt(null);

            assertThat(promoter.getId()).isNull();
            assertThat(promoter.getName()).isNull();
            assertThat(promoter.getEmail()).isNull();
            assertThat(promoter.getOrganization()).isNull();
            assertThat(promoter.getDescription()).isNull();
            assertThat(promoter.getPhone()).isNull();
            assertThat(promoter.getWebsite()).isNull();
            assertThat(promoter.getAddress()).isNull();
            assertThat(promoter.getLogoUrl()).isNull();
            assertThat(promoter.getOrganizationType()).isNull();
            assertThat(promoter.getAreaOfActivity()).isNull();
            assertThat(promoter.getFoundedYear()).isNull();
            assertThat(promoter.getNumberOfEmployees()).isNull();
            assertThat(promoter.getSocialMedia()).isNull();
            assertThat(promoter.getProfileCreatedAt()).isNull();
            assertThat(promoter.getProfileUpdatedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            promoter.setName("");
            promoter.setEmail("");
            promoter.setOrganization("");

            assertThat(promoter.getName()).isEmpty();
            assertThat(promoter.getEmail()).isEmpty();
            assertThat(promoter.getOrganization()).isEmpty();
        }

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            String longDescription = "A".repeat(1000);
            promoter.setDescription(longDescription);

            assertThat(promoter.getDescription()).hasSize(1000);
        }

        @Test
        @DisplayName("Should handle special characters in fields")
        void shouldHandleSpecialCharactersInFields() {
            promoter.setName("Maria Santos - Organizacao");
            promoter.setEmail("maria+test@org.org");
            promoter.setDescription("Descricao com acentos: e, a, i, o, u");

            assertThat(promoter.getName()).isEqualTo("Maria Santos - Organizacao");
            assertThat(promoter.getEmail()).isEqualTo("maria+test@org.org");
            assertThat(promoter.getDescription()).isEqualTo("Descricao com acentos: e, a, i, o, u");
        }
    }
}
