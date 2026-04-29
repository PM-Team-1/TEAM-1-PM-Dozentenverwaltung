package teameins.lecturerassignmentsystem.model;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerCanHoldCourseDto;

import static org.junit.jupiter.api.Assertions.*;

class LecturerCanHoldCourseDtoTest {

    @Test
    void testSuccessfulValidate() {
        assertTrue(getLecturerCanHoldCourseDto("P", "S", "mittel").validate());
    }

    @Test
    void testEmptyAlreadyHeldError() {
        assertFalse(getLecturerCanHoldCourseDto("", "S", "mittel").validate());
        assertFalse(getLecturerCanHoldCourseDto(null, "S", "mittel").validate());
        assertFalse(getLecturerCanHoldCourseDto(" ", "S", "mittel").validate());
        assertFalse(getLecturerCanHoldCourseDto("\t", "S", "mittel").validate());
        assertFalse(getLecturerCanHoldCourseDto("\n", "S", "mittel").validate());
    }

    @Test
    void testInvalidAlreadyHeldError() {
        assertFalse(getLecturerCanHoldCourseDto("asdf", "S", "mittel").validate());
    }

    @Test
    void testEmptyQualificationError() {
        assertFalse(getLecturerCanHoldCourseDto("P", "", "mittel").validate());
        assertFalse(getLecturerCanHoldCourseDto("P", null, "mittel").validate());
        assertFalse(getLecturerCanHoldCourseDto("P", " ", "mittel").validate());
        assertFalse(getLecturerCanHoldCourseDto("P", "\t", "mittel").validate());
        assertFalse(getLecturerCanHoldCourseDto("P", "\n", "mittel").validate());
    }

    @Test
    void testInvalidQualificationError() {
        assertFalse(getLecturerCanHoldCourseDto("P", "asdf", "mittel").validate());
    }

    @Test
    void testEmptyAffinityError() {
        assertFalse(getLecturerCanHoldCourseDto("P", "S", "").validate());
        assertFalse(getLecturerCanHoldCourseDto("P", "S", null).validate());
        assertFalse(getLecturerCanHoldCourseDto("P", "S", " ").validate());
        assertFalse(getLecturerCanHoldCourseDto("P", "S", "\t").validate());
        assertFalse(getLecturerCanHoldCourseDto("P", "S", "\n").validate());
    }

    @Test
    void testInvalidAffinityError() {
        assertFalse(getLecturerCanHoldCourseDto("P", "S", "asdf").validate());
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
