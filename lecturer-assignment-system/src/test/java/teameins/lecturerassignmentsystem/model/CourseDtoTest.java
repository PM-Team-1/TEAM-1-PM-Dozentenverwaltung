package teameins.lecturerassignmentsystem.model;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CourseDtoTest {

    @Test
    void testSuccessfulValidate() {
        assertTrue(getCourseDto("Informatik", "WiSe 24/25").validate());
    }

    @Test
    void testEmptyNameError() {
        assertFalse(getCourseDto("", "WiSe 24/25").validate());
        assertFalse(getCourseDto(null, "WiSe 24/25").validate());
    }

    @Test
    void testEmptySemesterError() {
        assertFalse(getCourseDto("Informatik", "").validate());
        assertFalse(getCourseDto("Informatik", null).validate());
    }

    @Test
    void testWrongSemesterError() {
        assertFalse(getCourseDto("Informatik", "blablabla").validate());
    }

    @Test
    void testSemesterSortable() {
        CourseDto courseDto = getCourseDto("Informatik", "WiSe 24/25");
        assertEquals("24/25 2", courseDto.getSemesterSortable());

        courseDto.setSemester("SoSe 24/25");
        assertEquals("24/25 1", courseDto.getSemesterSortable());

        courseDto.setSemester("24/25");
        assertEquals("24/25", courseDto.getSemesterSortable());

        courseDto.setSemester("");
        assertEquals("", courseDto.getSemesterSortable());

        courseDto.setSemester(null);
        assertEquals("", courseDto.getSemesterSortable());
    }

    @Test
    void testSettersAndGetters() {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(1);
        courseDto.setName("Informatik");
        courseDto.setClosed(false);
        courseDto.setMaster(false);
        courseDto.setSemester("WiSe 24/25");
        courseDto.setCanBeHeldBy(new ArrayList<>());

        CourseDto expectedCourseDto = getCourseDto("Informatik", "WiSe 24/25");
        assertEquals(expectedCourseDto.getId(), courseDto.getId());
        assertEquals(expectedCourseDto.getName(), courseDto.getName());
        assertEquals(expectedCourseDto.isClosed(), courseDto.isClosed());
        assertEquals(expectedCourseDto.isMaster(), courseDto.isMaster());
        assertEquals(expectedCourseDto.getSemester(), courseDto.getSemester());
        assertEquals(expectedCourseDto.getCanBeHeldBy(), courseDto.getCanBeHeldBy());
        assertEquals(expectedCourseDto, courseDto);
        assertEquals(expectedCourseDto.hashCode(), courseDto.hashCode());
    }

    CourseDto getCourseDto(String name, String semester){
        return new CourseDto(
                1,
                name,
                false,
                false, semester,
                new ArrayList<>());
    }
}
