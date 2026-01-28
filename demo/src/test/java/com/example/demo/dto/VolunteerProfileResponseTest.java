package com.example.demo.dto;

import com.example.demo.entity.Volunteer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VolunteerProfileResponse Tests")
class VolunteerProfileResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("No-args constructor creates instance")
        void noArgsConstructorCreatesInstance() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            assertNotNull(response);
        }

        @Test
        @DisplayName("All-args constructor sets all fields")
        void allArgsConstructorSetsAllFields() {
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            VolunteerProfileResponse response = new VolunteerProfileResponse(
                    1L, "John Doe", "john@email.com", "+351912345678",
                    "Java, Python", "Education", "Weekends", "My bio",
                    100, createdAt, updatedAt
            );

            assertEquals(1L, response.getId());
            assertEquals("John Doe", response.getName());
            assertEquals("john@email.com", response.getEmail());
            assertEquals("+351912345678", response.getPhone());
            assertEquals("Java, Python", response.getSkills());
            assertEquals("Education", response.getInterests());
            assertEquals("Weekends", response.getAvailability());
            assertEquals("My bio", response.getBio());
            assertEquals(100, response.getTotalPoints());
            assertEquals(createdAt, response.getProfileCreatedAt());
            assertEquals(updatedAt, response.getProfileUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("setId and getId work correctly")
        void setAndGetId() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setId(1L);
            assertEquals(1L, response.getId());
        }

        @Test
        @DisplayName("setName and getName work correctly")
        void setAndGetName() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setName("Maria");
            assertEquals("Maria", response.getName());
        }

        @Test
        @DisplayName("setEmail and getEmail work correctly")
        void setAndGetEmail() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setEmail("maria@email.com");
            assertEquals("maria@email.com", response.getEmail());
        }

        @Test
        @DisplayName("setPhone and getPhone work correctly")
        void setAndGetPhone() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setPhone("+351912345678");
            assertEquals("+351912345678", response.getPhone());
        }

        @Test
        @DisplayName("setSkills and getSkills work correctly")
        void setAndGetSkills() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setSkills("Programming");
            assertEquals("Programming", response.getSkills());
        }

        @Test
        @DisplayName("setInterests and getInterests work correctly")
        void setAndGetInterests() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setInterests("Environment");
            assertEquals("Environment", response.getInterests());
        }

        @Test
        @DisplayName("setAvailability and getAvailability work correctly")
        void setAndGetAvailability() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setAvailability("Mornings");
            assertEquals("Mornings", response.getAvailability());
        }

        @Test
        @DisplayName("setBio and getBio work correctly")
        void setAndGetBio() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setBio("My biography");
            assertEquals("My biography", response.getBio());
        }

        @Test
        @DisplayName("setTotalPoints and getTotalPoints work correctly")
        void setAndGetTotalPoints() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setTotalPoints(250);
            assertEquals(250, response.getTotalPoints());
        }

        @Test
        @DisplayName("setProfileCreatedAt and getProfileCreatedAt work correctly")
        void setAndGetProfileCreatedAt() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            LocalDateTime now = LocalDateTime.now();
            response.setProfileCreatedAt(now);
            assertEquals(now, response.getProfileCreatedAt());
        }

        @Test
        @DisplayName("setProfileUpdatedAt and getProfileUpdatedAt work correctly")
        void setAndGetProfileUpdatedAt() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            LocalDateTime now = LocalDateTime.now();
            response.setProfileUpdatedAt(now);
            assertEquals(now, response.getProfileUpdatedAt());
        }
    }

    @Nested
    @DisplayName("fromEntity Tests")
    class FromEntityTests {

        @Test
        @DisplayName("fromEntity maps all fields correctly")
        void fromEntityMapsAllFields() {
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            Volunteer volunteer = new Volunteer();
            volunteer.setId(1L);
            volunteer.setName("John Doe");
            volunteer.setEmail("john@email.com");
            volunteer.setPhone("+351912345678");
            volunteer.setSkills("Java, Python");
            volunteer.setInterests("Education, Environment");
            volunteer.setAvailability("Weekends");
            volunteer.setBio("I love volunteering");
            volunteer.setTotalPoints(100);
            volunteer.setProfileCreatedAt(createdAt);
            volunteer.setProfileUpdatedAt(updatedAt);

            VolunteerProfileResponse response = VolunteerProfileResponse.fromEntity(volunteer);

            assertEquals(1L, response.getId());
            assertEquals("John Doe", response.getName());
            assertEquals("john@email.com", response.getEmail());
            assertEquals("+351912345678", response.getPhone());
            assertEquals("Java, Python", response.getSkills());
            assertEquals("Education, Environment", response.getInterests());
            assertEquals("Weekends", response.getAvailability());
            assertEquals("I love volunteering", response.getBio());
            assertEquals(100, response.getTotalPoints());
            assertEquals(createdAt, response.getProfileCreatedAt());
            assertEquals(updatedAt, response.getProfileUpdatedAt());
        }

        @Test
        @DisplayName("fromEntity handles null optional fields")
        void fromEntityHandlesNullFields() {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(1L);
            volunteer.setName("John Doe");
            volunteer.setEmail("john@email.com");
            volunteer.setTotalPoints(0);

            VolunteerProfileResponse response = VolunteerProfileResponse.fromEntity(volunteer);

            assertEquals(1L, response.getId());
            assertEquals("John Doe", response.getName());
            assertEquals("john@email.com", response.getEmail());
            assertNull(response.getPhone());
            assertNull(response.getSkills());
            assertNull(response.getInterests());
            assertNull(response.getAvailability());
            assertNull(response.getBio());
            assertEquals(0, response.getTotalPoints());
            assertNull(response.getProfileCreatedAt());
            assertNull(response.getProfileUpdatedAt());
        }

        @Test
        @DisplayName("fromEntity creates new instance")
        void fromEntityCreatesNewInstance() {
            Volunteer volunteer = new Volunteer();
            volunteer.setId(1L);
            volunteer.setName("John");
            volunteer.setEmail("john@email.com");
            volunteer.setTotalPoints(0);

            VolunteerProfileResponse response1 = VolunteerProfileResponse.fromEntity(volunteer);
            VolunteerProfileResponse response2 = VolunteerProfileResponse.fromEntity(volunteer);

            assertNotSame(response1, response2);
            assertEquals(response1, response2);
        }
    }

    @Nested
    @DisplayName("Equals, HashCode and ToString Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("equals returns true for same values")
        void equalsReturnsTrueForSameValues() {
            VolunteerProfileResponse r1 = new VolunteerProfileResponse();
            r1.setId(1L);
            r1.setName("John");
            r1.setEmail("john@email.com");

            VolunteerProfileResponse r2 = new VolunteerProfileResponse();
            r2.setId(1L);
            r2.setName("John");
            r2.setEmail("john@email.com");

            assertEquals(r1, r2);
        }

        @Test
        @DisplayName("equals returns false for different values")
        void equalsReturnsFalseForDifferentValues() {
            VolunteerProfileResponse r1 = new VolunteerProfileResponse();
            r1.setId(1L);

            VolunteerProfileResponse r2 = new VolunteerProfileResponse();
            r2.setId(2L);

            assertNotEquals(r1, r2);
        }

        @Test
        @DisplayName("hashCode is consistent for equal objects")
        void hashCodeIsConsistent() {
            VolunteerProfileResponse r1 = new VolunteerProfileResponse();
            r1.setId(1L);
            r1.setName("John");

            VolunteerProfileResponse r2 = new VolunteerProfileResponse();
            r2.setId(1L);
            r2.setName("John");

            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("toString contains field values")
        void toStringContainsValues() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setId(1L);
            response.setName("John");
            response.setEmail("john@email.com");

            String toString = response.toString();
            assertTrue(toString.contains("John"));
            assertTrue(toString.contains("john@email.com"));
        }
    }

    @Nested
    @DisplayName("Null Value Tests")
    class NullValueTests {

        @Test
        @DisplayName("Response accepts null for all optional fields")
        void acceptsNullForOptionalFields() {
            VolunteerProfileResponse response = new VolunteerProfileResponse();
            response.setId(1L);
            response.setName("John");
            response.setEmail("john@email.com");
            response.setPhone(null);
            response.setSkills(null);
            response.setInterests(null);
            response.setAvailability(null);
            response.setBio(null);
            response.setTotalPoints(0);
            response.setProfileCreatedAt(null);
            response.setProfileUpdatedAt(null);

            assertNull(response.getPhone());
            assertNull(response.getSkills());
            assertNull(response.getInterests());
            assertNull(response.getAvailability());
            assertNull(response.getBio());
            assertNull(response.getProfileCreatedAt());
            assertNull(response.getProfileUpdatedAt());
        }
    }
}
