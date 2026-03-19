package teameins.lecturerassignmentsystem.model;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;

import static org.junit.jupiter.api.Assertions.*;

public class LecturerCanHoldCourseDtoTest {

    @Test
    void testSuccessfulValidate() {
        assertTrue(getLecturerCanHoldCourseDto("P", "S").validate());
    }

    @Test
    void testEmptyAlreadyHeldError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerCanHoldCourseDto("", "S").validate());
        assertThrows(IllegalArgumentException.class, () -> getLecturerCanHoldCourseDto(null, "S").validate());
    }

    @Test
    void testEmptyQualificationError() {
        assertThrows(IllegalArgumentException.class, () -> getLecturerCanHoldCourseDto("P", "").validate());
        assertThrows(IllegalArgumentException.class, () -> getLecturerCanHoldCourseDto("P", null).validate());
    }

    @Test
    void testGettersAndSetters() {
        LecturerCanHoldCourseDto lecturerCanHoldCourseDto = new LecturerCanHoldCourseDto();
        lecturerCanHoldCourseDto.setId(1);
        lecturerCanHoldCourseDto.setLecturerId(1);
        lecturerCanHoldCourseDto.setCourseId(1);
        lecturerCanHoldCourseDto.setAlreadyHeld("P");
        lecturerCanHoldCourseDto.setQualification("S");
        lecturerCanHoldCourseDto.setPriority(false);

        LecturerCanHoldCourseDto expectedLecturerCanHoldCourseDto = getLecturerCanHoldCourseDto("P", "S");

        assertEquals(expectedLecturerCanHoldCourseDto.getId(), lecturerCanHoldCourseDto.getId());
        assertEquals(expectedLecturerCanHoldCourseDto.getLecturerId(), lecturerCanHoldCourseDto.getLecturerId());
        assertEquals(expectedLecturerCanHoldCourseDto.getCourseId(), lecturerCanHoldCourseDto.getCourseId());
        assertEquals(expectedLecturerCanHoldCourseDto.getAlreadyHeld(), lecturerCanHoldCourseDto.getAlreadyHeld());
        assertEquals(expectedLecturerCanHoldCourseDto.getQualification(), lecturerCanHoldCourseDto.getQualification());
        assertEquals(expectedLecturerCanHoldCourseDto.getPriority(), lecturerCanHoldCourseDto.getPriority());
        assertEquals(expectedLecturerCanHoldCourseDto, lecturerCanHoldCourseDto);
        assertEquals(expectedLecturerCanHoldCourseDto.hashCode(), lecturerCanHoldCourseDto.hashCode());
    }

    LecturerCanHoldCourseDto getLecturerCanHoldCourseDto(String alreadyHeld, String qualification){
        return new LecturerCanHoldCourseDto(1, 1, 1, alreadyHeld, qualification, false);
    }
}
