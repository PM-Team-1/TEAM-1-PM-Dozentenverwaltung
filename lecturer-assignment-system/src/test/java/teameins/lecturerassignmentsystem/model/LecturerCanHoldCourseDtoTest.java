package teameins.lecturerassignmentsystem.model;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;

import static org.junit.jupiter.api.Assertions.*;

class LecturerCanHoldCourseDtoTest {

    @Test
    void testSuccessfulValidate() {
        assertTrue(getLecturerCanHoldCourseDto("P", "S", "mittel").validate());
    }

    @Test
    void testEmptyAlreadyHeldError() {
        LecturerCanHoldCourseDto emptyString = getLecturerCanHoldCourseDto("", "S", "mittel");
        LecturerCanHoldCourseDto nullString = getLecturerCanHoldCourseDto(null, "S", "mittel");
        assertThrows(IllegalArgumentException.class, emptyString::validate);
        assertThrows(IllegalArgumentException.class, nullString::validate);
    }

    @Test
    void testInvalidAlreadyHeldError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerCanHoldCourseDto("asdf", "S", "mittel"));
    }

    @Test
    void testEmptyQualificationError() {
        LecturerCanHoldCourseDto emptyString = getLecturerCanHoldCourseDto("P", "", "mittel");
        LecturerCanHoldCourseDto nullString = getLecturerCanHoldCourseDto("P", null, "mittel");
        assertThrows(IllegalArgumentException.class, emptyString::validate);
        assertThrows(IllegalArgumentException.class, nullString::validate);
    }

    @Test
    void testInvalidQualificationError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerCanHoldCourseDto("P", "asdf", "mittel"));
    }

    @Test
    void testEmptyAffinityError() {
        LecturerCanHoldCourseDto emptyString = getLecturerCanHoldCourseDto("P", "S", "");
        LecturerCanHoldCourseDto nullString = getLecturerCanHoldCourseDto("P", "S", null);
        assertThrows(IllegalArgumentException.class, emptyString::validate);
        assertThrows(IllegalArgumentException.class, nullString::validate);
    }

    @Test
    void testInvalidAffinityError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerCanHoldCourseDto("P", "S", "asdf"));
    }

    @Test
    void testGettersAndSetters() {
        LecturerCanHoldCourseDto lecturerCanHoldCourseDto = new LecturerCanHoldCourseDto();
        lecturerCanHoldCourseDto.setId(1);
        lecturerCanHoldCourseDto.setLecturerId(1);
        lecturerCanHoldCourseDto.setCourseId(1);
        lecturerCanHoldCourseDto.setAlreadyHeld("P");
        lecturerCanHoldCourseDto.setQualification("S");
        lecturerCanHoldCourseDto.setAffinity("niedrig");

        LecturerCanHoldCourseDto expectedLecturerCanHoldCourseDto = getLecturerCanHoldCourseDto("P", "S", "niedrig");

        assertEquals(expectedLecturerCanHoldCourseDto.getId(), lecturerCanHoldCourseDto.getId());
        assertEquals(expectedLecturerCanHoldCourseDto.getLecturerId(), lecturerCanHoldCourseDto.getLecturerId());
        assertEquals(expectedLecturerCanHoldCourseDto.getCourseId(), lecturerCanHoldCourseDto.getCourseId());
        assertEquals(expectedLecturerCanHoldCourseDto.getAlreadyHeld(), lecturerCanHoldCourseDto.getAlreadyHeld());
        assertEquals(expectedLecturerCanHoldCourseDto.getQualification(), lecturerCanHoldCourseDto.getQualification());
        assertEquals(expectedLecturerCanHoldCourseDto.getAffinity(), lecturerCanHoldCourseDto.getAffinity());
        assertEquals(expectedLecturerCanHoldCourseDto, lecturerCanHoldCourseDto);
        assertEquals(expectedLecturerCanHoldCourseDto.hashCode(), lecturerCanHoldCourseDto.hashCode());
    }

    LecturerCanHoldCourseDto getLecturerCanHoldCourseDto(String alreadyHeld, String qualification, String affinity){
        return new LecturerCanHoldCourseDto(1, 1, 1, alreadyHeld, qualification, affinity);
    }
}
