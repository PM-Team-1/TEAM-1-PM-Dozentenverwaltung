package teameins.lecturerassignmentsystem.model;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.enums.Title;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LecturerDtoTest {

    @Test
    void testSuccessfulValidate() {
        assertTrue(getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
    }

    @Test
    void testWrongTitleError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Blödmann", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto(null, "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
    }

    @Test
    void testEmptyFirstNameError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", null, "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
    }

    @Test
    void testEmptyLastNameError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "Kollege", "", "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "Kollege", null, "kollege.schnürrschuh@deichmann.de", "+123456789", "A").validate());
    }

    @Test
    void testEmptyEmailError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "", "+123456789", "A").validate());
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "Kollege", "Schnürrschuh", null, "+123456789", "A").validate());
    }

    @Test
    void testEmptyPhoneError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "", "A").validate());
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", null, "A").validate());
    }

    @Test
    void testEmptyPreferenceError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", "").validate());
        assertThrows(IllegalArgumentException.class, () -> getLecturerDto("Dr.", "Kollege", "Schnürrschuh", "kollege.schnürrschuh@deichmann.de", "+123456789", null).validate());
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
        lecturerDto.setPreference("A");
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
        assertEquals(expectedLecturerDto.getPreference(), lecturerDto.getPreference());
        assertEquals(expectedLecturerDto.getCanHoldCourses(), lecturerDto.getCanHoldCourses());
        assertEquals(expectedLecturerDto, lecturerDto);
        assertEquals(expectedLecturerDto.hashCode(), lecturerDto.hashCode());
    }

    LecturerDto getLecturerDto(String title, String firstName, String lastName, String email, String phone, String preference) {
        return new LecturerDto(1, title, firstName, lastName, "", email, phone, false, preference, new ArrayList<>());
    }
}
