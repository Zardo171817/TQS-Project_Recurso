package com.example.demo.dto;

import com.example.demo.entity.Volunteer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VolunteerResponseTest {

    @Test
    void whenFromEntity_thenMapAllFields() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(1L);
        volunteer.setName("Test Volunteer");
        volunteer.setEmail("test@test.com");
        volunteer.setPhone("123456789");
        volunteer.setSkills("Java, Python");

        VolunteerResponse response = VolunteerResponse.fromEntity(volunteer);

        assertEquals(1L, response.getId());
        assertEquals("Test Volunteer", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals("123456789", response.getPhone());
        assertEquals("Java, Python", response.getSkills());
    }

    @Test
    void whenFromEntityWithNullOptionalFields_thenMapCorrectly() {
        Volunteer volunteer = new Volunteer();
        volunteer.setId(2L);
        volunteer.setName("Minimal Volunteer");
        volunteer.setEmail("minimal@test.com");
        volunteer.setPhone(null);
        volunteer.setSkills(null);

        VolunteerResponse response = VolunteerResponse.fromEntity(volunteer);

        assertEquals(2L, response.getId());
        assertEquals("Minimal Volunteer", response.getName());
        assertEquals("minimal@test.com", response.getEmail());
        assertNull(response.getPhone());
        assertNull(response.getSkills());
    }

    @Test
    void whenCreateWithNoArgsConstructor_thenFieldsAreNull() {
        VolunteerResponse response = new VolunteerResponse();

        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertNull(response.getPhone());
        assertNull(response.getSkills());
    }

    @Test
    void whenCreateWithAllArgsConstructor_thenFieldsAreSet() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");

        assertEquals(1L, response.getId());
        assertEquals("Test", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals("123", response.getPhone());
        assertEquals("Java", response.getSkills());
    }

    @Test
    void whenSettersUsed_thenFieldsAreUpdated() {
        VolunteerResponse response = new VolunteerResponse();
        response.setId(3L);
        response.setName("Updated Name");
        response.setEmail("updated@test.com");
        response.setPhone("999888777");
        response.setSkills("React, Node.js");

        assertEquals(3L, response.getId());
        assertEquals("Updated Name", response.getName());
        assertEquals("updated@test.com", response.getEmail());
        assertEquals("999888777", response.getPhone());
        assertEquals("React, Node.js", response.getSkills());
    }

    // Tests for equals() - covering all branches
    @Test
    void equals_sameObject_returnsTrue() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertEquals(response, response);
    }

    @Test
    void equals_equalObjects_returnsTrue() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertEquals(response1, response2);
    }

    @Test
    void equals_null_returnsFalse() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertNotEquals(null, response);
    }

    @Test
    void equals_differentClass_returnsFalse() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertNotEquals("String object", response);
    }

    @Test
    void equals_differentId_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(2L, "Test", "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_differentName_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test1", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test2", "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_differentEmail_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test1@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test2@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_differentPhone_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "456", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_differentSkills_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Python");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_nullIdInOne_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(null, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_nullIdInBoth_returnsTrue() {
        VolunteerResponse response1 = new VolunteerResponse(null, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(null, "Test", "test@test.com", "123", "Java");
        assertEquals(response1, response2);
    }

    @Test
    void equals_nullNameInOne_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, null, "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_nullNameInBoth_returnsTrue() {
        VolunteerResponse response1 = new VolunteerResponse(1L, null, "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, null, "test@test.com", "123", "Java");
        assertEquals(response1, response2);
    }

    @Test
    void equals_nullEmailInOne_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", null, "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_nullEmailInBoth_returnsTrue() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", null, "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", null, "123", "Java");
        assertEquals(response1, response2);
    }

    @Test
    void equals_nullPhoneInOne_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", null, "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_nullPhoneInBoth_returnsTrue() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", null, "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", null, "Java");
        assertEquals(response1, response2);
    }

    @Test
    void equals_nullSkillsInOne_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", null);
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_nullSkillsInBoth_returnsTrue() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", null);
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", null);
        assertEquals(response1, response2);
    }

    @Test
    void equals_allFieldsNull_returnsTrue() {
        VolunteerResponse response1 = new VolunteerResponse();
        VolunteerResponse response2 = new VolunteerResponse();
        assertEquals(response1, response2);
    }

    @Test
    void equals_idNullInSecondObject_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(null, "Test", "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_nameNullInSecondObject_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, null, "test@test.com", "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_emailNullInSecondObject_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", null, "123", "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_phoneNullInSecondObject_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", null, "Java");
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_skillsNullInSecondObject_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", null);
        assertNotEquals(response1, response2);
    }

    // Tests for hashCode() - covering all branches
    @Test
    void hashCode_equalObjects_sameHashCode() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void hashCode_differentObjects_differentHashCode() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(2L, "Test2", "test2@test.com", "456", "Python");
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void hashCode_withNullId_works() {
        VolunteerResponse response = new VolunteerResponse(null, "Test", "test@test.com", "123", "Java");
        assertDoesNotThrow(response::hashCode);
    }

    @Test
    void hashCode_withNullName_works() {
        VolunteerResponse response = new VolunteerResponse(1L, null, "test@test.com", "123", "Java");
        assertDoesNotThrow(response::hashCode);
    }

    @Test
    void hashCode_withNullEmail_works() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", null, "123", "Java");
        assertDoesNotThrow(response::hashCode);
    }

    @Test
    void hashCode_withNullPhone_works() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", null, "Java");
        assertDoesNotThrow(response::hashCode);
    }

    @Test
    void hashCode_withNullSkills_works() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", null);
        assertDoesNotThrow(response::hashCode);
    }

    @Test
    void hashCode_allNullFields_works() {
        VolunteerResponse response = new VolunteerResponse();
        assertDoesNotThrow(response::hashCode);
    }

    @Test
    void hashCode_consistency_returnsSameValue() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        int hash1 = response.hashCode();
        int hash2 = response.hashCode();
        assertEquals(hash1, hash2);
    }

    // Tests for toString()
    @Test
    void toString_withAllFields_containsAllValues() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        String str = response.toString();

        assertTrue(str.contains("1"));
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("test@test.com"));
        assertTrue(str.contains("123"));
        assertTrue(str.contains("Java"));
    }

    @Test
    void toString_withNullFields_containsNull() {
        VolunteerResponse response = new VolunteerResponse();
        String str = response.toString();
        assertTrue(str.contains("null"));
    }

    // Tests for canEqual (Lombok generates this for inheritance support)
    @Test
    void canEqual_sameType_returnsTrue() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(2L, "Test2", "test2@test.com", "456", "Python");
        assertTrue(response1.canEqual(response2));
    }

    @Test
    void canEqual_differentType_returnsFalse() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertFalse(response.canEqual("String"));
    }

    @Test
    void canEqual_null_returnsFalse() {
        VolunteerResponse response = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertFalse(response.canEqual(null));
    }

    // Additional edge case tests for equals branches
    @Test
    void equals_bothObjectsWithAllNullFields_areEqual() {
        VolunteerResponse response1 = new VolunteerResponse(null, null, null, null, null);
        VolunteerResponse response2 = new VolunteerResponse(null, null, null, null, null);
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void equals_symmetry_worksCorrectly() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertTrue(response1.equals(response2));
        assertTrue(response2.equals(response1));
    }

    @Test
    void equals_transitivity_worksCorrectly() {
        VolunteerResponse response1 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        VolunteerResponse response3 = new VolunteerResponse(1L, "Test", "test@test.com", "123", "Java");
        assertTrue(response1.equals(response2));
        assertTrue(response2.equals(response3));
        assertTrue(response1.equals(response3));
    }

    @Test
    void equals_withMixedNullFields_returnsFalse() {
        VolunteerResponse response1 = new VolunteerResponse(1L, null, "test@test.com", null, "Java");
        VolunteerResponse response2 = new VolunteerResponse(1L, "Test", null, "123", null);
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_onlyIdDifferent_withAllOtherFieldsNull() {
        VolunteerResponse response1 = new VolunteerResponse(1L, null, null, null, null);
        VolunteerResponse response2 = new VolunteerResponse(2L, null, null, null, null);
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_onlyNameDifferent_withAllOtherFieldsNull() {
        VolunteerResponse response1 = new VolunteerResponse(null, "Name1", null, null, null);
        VolunteerResponse response2 = new VolunteerResponse(null, "Name2", null, null, null);
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_onlyEmailDifferent_withAllOtherFieldsNull() {
        VolunteerResponse response1 = new VolunteerResponse(null, null, "email1@test.com", null, null);
        VolunteerResponse response2 = new VolunteerResponse(null, null, "email2@test.com", null, null);
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_onlyPhoneDifferent_withAllOtherFieldsNull() {
        VolunteerResponse response1 = new VolunteerResponse(null, null, null, "111", null);
        VolunteerResponse response2 = new VolunteerResponse(null, null, null, "222", null);
        assertNotEquals(response1, response2);
    }

    @Test
    void equals_onlySkillsDifferent_withAllOtherFieldsNull() {
        VolunteerResponse response1 = new VolunteerResponse(null, null, null, null, "Java");
        VolunteerResponse response2 = new VolunteerResponse(null, null, null, null, "Python");
        assertNotEquals(response1, response2);
    }
}
