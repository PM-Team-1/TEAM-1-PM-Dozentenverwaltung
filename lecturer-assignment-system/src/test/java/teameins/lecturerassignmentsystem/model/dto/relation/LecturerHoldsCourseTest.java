package teameins.lecturerassignmentsystem.model.dto.relation;

import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerHoldsCourse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LecturerHoldsCourseTest {

    @Test
    void testGettersAndSetters() {
        Course course = new Course();
        course.setId(1);
        
        Lecturer lecturer = new Lecturer();
        lecturer.setId(1);

        LecturerHoldsCourse lecturerHoldsCourse = new LecturerHoldsCourse();
        lecturerHoldsCourse.setId(10);
        lecturerHoldsCourse.setCourse(course);
        lecturerHoldsCourse.setLecturer(lecturer);

        assertEquals(10, lecturerHoldsCourse.getId());
        assertEquals(course, lecturerHoldsCourse.getCourse());
        assertEquals(lecturer, lecturerHoldsCourse.getLecturer());

        LecturerHoldsCourse anotherLecturerHoldsCourse = new LecturerHoldsCourse(11, course, lecturer);
        assertEquals(11, anotherLecturerHoldsCourse.getId());
        assertEquals(course, anotherLecturerHoldsCourse.getCourse());
        assertEquals(lecturer, anotherLecturerHoldsCourse.getLecturer());
    }
}
