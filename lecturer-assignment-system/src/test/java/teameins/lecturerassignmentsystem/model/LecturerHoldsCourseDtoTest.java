package teameins.lecturerassignmentsystem.model;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerHoldsCourseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class LecturerHoldsCourseDtoTest {

    @Test
    void testGettersAndSetters() {
        LecturerHoldsCourseDto dto = new LecturerHoldsCourseDto();
        dto.setId(1);
        dto.setLecturerId(2);
        dto.setCourseId(3);

        LecturerHoldsCourseDto expectedDto = getLecturerHoldsCourseDto(1, 2, 3);

        assertEquals(expectedDto.getId(), dto.getId());
        assertEquals(expectedDto.getLecturerId(), dto.getLecturerId());
        assertEquals(expectedDto.getCourseId(), dto.getCourseId());
        
        assertEquals(expectedDto, dto);
        assertEquals(expectedDto.hashCode(), dto.hashCode());
    }
    
    @Test
    void testEqualsAndHashCode() {
        LecturerHoldsCourseDto dto1 = getLecturerHoldsCourseDto(1, 2, 3);
        LecturerHoldsCourseDto dto2 = getLecturerHoldsCourseDto(1, 2, 3);
        LecturerHoldsCourseDto dto3 = getLecturerHoldsCourseDto(2, 2, 3);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    private LecturerHoldsCourseDto getLecturerHoldsCourseDto(int id, int lecturerId, int courseId) {
        return new LecturerHoldsCourseDto(id, lecturerId, courseId);
    }
}
