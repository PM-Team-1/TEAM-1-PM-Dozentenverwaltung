package teameins.lecturerassignmentsystem.model.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LecturerDtoTest {

    @Test
    void testSuccessfulValidate() {
        assertTrue(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
    }

    @Test
    void testWrongTitleError() {
        assertFalse(getLecturerDto("Blödmann", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto(null, "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto(" ", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("\t", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("\n", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
    }

    @Test
    void testEmptyFirstNameError() {
        assertFalse(getLecturerDto("Dr.", "", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", null, "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", " ", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "\t", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "\n", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
    }

    @Test
    void testEmptyLastNameError() {
        assertFalse(getLecturerDto("Dr.", "Kollege", "", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", null, "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", " ", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "\t", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "\n", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
    }

    @Test
    void testEmptyEmailError() {
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", null, "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", " ", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "\t", "+123456789", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "\n", "+123456789", "A").validate());
    }

    @Test
    void testWrongEmailError() {
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "not-an-email", "+123456789", "A").validate());
        assertTrue(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "a@b", "+123456789", "A").validate());
    }

    @Test
    void testEmptyPhoneError() {
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", null, "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", " ", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "\t", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "\n", "A").validate());
    }

    @Test
    void testWrongPhoneError() {
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "abc", "A").validate());
        assertTrue(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "123-456", "A").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "++123", "A").validate());
    }

    @Test
    void testEmptyTeachingPreferenceError() {
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", null).validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", " ").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "\t").validate());
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "\n").validate());
    }

    @Test
    void testWrongTeachingPreferenceError() {
        assertFalse(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "X").validate());
    }

    @Test
    void testGettersAndSetters() {
        LecturerDto lecturerDto = new LecturerDto();
        lecturerDto.setId(1);
        lecturerDto.setTitle("Dr.");
        lecturerDto.setFirstName("Kollege");
        lecturerDto.setLastName("Schnürrschuh");
        lecturerDto.setSecondName("");
        lecturerDto.setEmail("kollege.schnürrschuh@deichmann.de");
        lecturerDto.setPhone("+123456789");
        lecturerDto.setExtern(false);
        lecturerDto.setTeachingPreference("A");
        lecturerDto.setCanHoldCourses(new ArrayList<>());

        LecturerDto expectedLecturerDto = getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A");

        assertEquals(expectedLecturerDto.getId(), lecturerDto.getId());
        assertEquals(expectedLecturerDto.getTitle(), lecturerDto.getTitle());
        assertEquals(expectedLecturerDto.getFirstName(), lecturerDto.getFirstName());
        assertEquals(expectedLecturerDto.getLastName(), lecturerDto.getLastName());
        assertEquals(expectedLecturerDto.getSecondName(), lecturerDto.getSecondName());
        assertEquals(expectedLecturerDto.getFullName(), lecturerDto.getFullName());
        assertEquals(expectedLecturerDto.getEmail(), lecturerDto.getEmail());
        assertEquals(expectedLecturerDto.getPhone(), lecturerDto.getPhone());
        assertEquals(expectedLecturerDto.isExtern(), lecturerDto.isExtern());
        assertEquals(expectedLecturerDto.getTeachingPreference(), lecturerDto.getTeachingPreference());
        assertEquals(expectedLecturerDto.getCanHoldCourses(), lecturerDto.getCanHoldCourses());
        assertEquals(expectedLecturerDto, lecturerDto);
        assertEquals(expectedLecturerDto.hashCode(), lecturerDto.hashCode());
    }

    LecturerDto getLecturerDto(String title, String firstName, String lastName, String email, String phone, String teachingPreference) {
        return new LecturerDto(1, title, firstName, lastName, "", email, phone, false, teachingPreference, new ArrayList<>());
    }
}
