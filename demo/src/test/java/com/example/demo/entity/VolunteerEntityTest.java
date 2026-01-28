package com.example.demo.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
        @DisplayName("No-args constructor creates instance with null fields")
        void noArgsConstructorCreatesInstance() {
            Volunteer v = new Volunteer();
            assertNotNull(v);
            assertNull(v.getId());
            assertNull(v.getName());
            assertNull(v.getEmail());
        }

        @Test
        @DisplayName("All-args constructor sets all fields correctly")
        void allArgsConstructorSetsAllFields() {
            LocalDateTime now = LocalDateTime.now();
            Volunteer v = new Volunteer(
                    1L, "John Doe", "john@email.com", "+351912345678",
                    "Java, Python", "Education, Environment", "Weekends",
                    "I love helping others", 100, now, now
            );

            assertEquals(1L, v.getId());
            assertEquals("John Doe", v.getName());
            assertEquals("john@email.com", v.getEmail());
            assertEquals("+351912345678", v.getPhone());
            assertEquals("Java, Python", v.getSkills());
            assertEquals("Education, Environment", v.getInterests());
            assertEquals("Weekends", v.getAvailability());
            assertEquals("I love helping others", v.getBio());
            assertEquals(100, v.getTotalPoints());
            assertEquals(now, v.getProfileCreatedAt());
            assertEquals(now, v.getProfileUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("setId and getId work correctly")
        void setAndGetId() {
            volunteer.setId(1L);
            assertEquals(1L, volunteer.getId());
        }

        @Test
        @DisplayName("setName and getName work correctly")
        void setAndGetName() {
            volunteer.setName("Maria Silva");
            assertEquals("Maria Silva", volunteer.getName());
        }

        @Test
        @DisplayName("setEmail and getEmail work correctly")
        void setAndGetEmail() {
            volunteer.setEmail("maria@email.com");
            assertEquals("maria@email.com", volunteer.getEmail());
        }

        @Test
        @DisplayName("setPhone and getPhone work correctly")
        void setAndGetPhone() {
            volunteer.setPhone("+351912345678");
            assertEquals("+351912345678", volunteer.getPhone());
        }

        @Test
        @DisplayName("setSkills and getSkills work correctly")
        void setAndGetSkills() {
            volunteer.setSkills("Communication, Leadership");
            assertEquals("Communication, Leadership", volunteer.getSkills());
        }

        @Test
        @DisplayName("setInterests and getInterests work correctly")
        void setAndGetInterests() {
            volunteer.setInterests("Environment, Animals");
            assertEquals("Environment, Animals", volunteer.getInterests());
        }

        @Test
        @DisplayName("setAvailability and getAvailability work correctly")
        void setAndGetAvailability() {
            volunteer.setAvailability("Monday mornings, Weekends");
            assertEquals("Monday mornings, Weekends", volunteer.getAvailability());
        }

        @Test
        @DisplayName("setBio and getBio work correctly")
        void setAndGetBio() {
            String bio = "Passionate about helping the community";
            volunteer.setBio(bio);
            assertEquals(bio, volunteer.getBio());
        }

        @Test
        @DisplayName("setTotalPoints and getTotalPoints work correctly")
        void setAndGetTotalPoints() {
            volunteer.setTotalPoints(250);
            assertEquals(250, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("setProfileCreatedAt and getProfileCreatedAt work correctly")
        void setAndGetProfileCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            volunteer.setProfileCreatedAt(now);
            assertEquals(now, volunteer.getProfileCreatedAt());
        }

        @Test
        @DisplayName("setProfileUpdatedAt and getProfileUpdatedAt work correctly")
        void setAndGetProfileUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            volunteer.setProfileUpdatedAt(now);
            assertEquals(now, volunteer.getProfileUpdatedAt());
        }
    }

    @Nested
    @DisplayName("PrePersist Tests")
    class PrePersistTests {

        @Test
        @DisplayName("onCreate sets totalPoints to 0 if null")
        void onCreateSetsTotalPointsIfNull() {
            volunteer.setTotalPoints(null);
            volunteer.onCreate();
            assertEquals(0, volunteer.getTotalPoints());
        }

        @Test
        @DisplayName("onCreate does not change totalPoints if already set")
        void onCreateKeepsTotalPointsIfSet() {
            volunteer.setTotalPoints(100);
            volunteer.onCreate();
            assertEquals(100, volunteer.getTotalPoints());
        }
    }

    @Nested
    @DisplayName("Equals, HashCode and ToString Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("equals returns true for same object")
        void equalsReturnsTrueForSameObject() {
            volunteer.setId(1L);
            volunteer.setName("John");
            volunteer.setEmail("john@email.com");
            assertEquals(volunteer, volunteer);
        }

        @Test
        @DisplayName("equals returns true for objects with same values")
        void equalsReturnsTrueForSameValues() {
            Volunteer v1 = new Volunteer();
            v1.setId(1L);
            v1.setName("John");
            v1.setEmail("john@email.com");

            Volunteer v2 = new Volunteer();
            v2.setId(1L);
            v2.setName("John");
            v2.setEmail("john@email.com");

            assertEquals(v1, v2);
        }

        @Test
        @DisplayName("equals returns false for different objects")
        void equalsReturnsFalseForDifferentObjects() {
            Volunteer v1 = new Volunteer();
            v1.setId(1L);

            Volunteer v2 = new Volunteer();
            v2.setId(2L);

            assertNotEquals(v1, v2);
        }

        @Test
        @DisplayName("hashCode is consistent for equal objects")
        void hashCodeIsConsistent() {
            Volunteer v1 = new Volunteer();
            v1.setId(1L);
            v1.setName("John");

            Volunteer v2 = new Volunteer();
            v2.setId(1L);
            v2.setName("John");

            assertEquals(v1.hashCode(), v2.hashCode());
        }

        @Test
        @DisplayName("toString contains class name and field values")
        void toStringContainsFieldValues() {
            volunteer.setId(1L);
            volunteer.setName("John Doe");
            volunteer.setEmail("john@email.com");

            String toString = volunteer.toString();
            assertTrue(toString.contains("Volunteer"));
            assertTrue(toString.contains("John Doe"));
            assertTrue(toString.contains("john@email.com"));
        }
    }

    @Nested
    @DisplayName("Null Value Tests")
    class NullValueTests {

        @Test
        @DisplayName("Volunteer accepts null for optional fields")
        void acceptsNullForOptionalFields() {
            volunteer.setId(1L);
            volunteer.setName("John");
            volunteer.setEmail("john@email.com");
            volunteer.setPhone(null);
            volunteer.setSkills(null);
            volunteer.setInterests(null);
            volunteer.setAvailability(null);
            volunteer.setBio(null);
            volunteer.setTotalPoints(0);

            assertNull(volunteer.getPhone());
            assertNull(volunteer.getSkills());
            assertNull(volunteer.getInterests());
            assertNull(volunteer.getAvailability());
            assertNull(volunteer.getBio());
        }
    }

    @Nested
    @DisplayName("Field Length Tests")
    class FieldLengthTests {

        @Test
        @DisplayName("Skills can hold 500 characters")
        void skillsCanHold500Characters() {
            String longSkills = "A".repeat(500);
            volunteer.setSkills(longSkills);
            assertEquals(500, volunteer.getSkills().length());
        }

        @Test
        @DisplayName("Interests can hold 500 characters")
        void interestsCanHold500Characters() {
            String longInterests = "B".repeat(500);
            volunteer.setInterests(longInterests);
            assertEquals(500, volunteer.getInterests().length());
        }

        @Test
        @DisplayName("Availability can hold 500 characters")
        void availabilityCanHold500Characters() {
            String longAvailability = "C".repeat(500);
            volunteer.setAvailability(longAvailability);
            assertEquals(500, volunteer.getAvailability().length());
        }

        @Test
        @DisplayName("Bio can hold 1000 characters")
        void bioCanHold1000Characters() {
            String longBio = "D".repeat(1000);
            volunteer.setBio(longBio);
            assertEquals(1000, volunteer.getBio().length());
        }
    }
}
