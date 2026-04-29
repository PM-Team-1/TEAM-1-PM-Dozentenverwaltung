package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerCanHoldCourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonCreatorTest {

    private JsonCreator jsonCreator;
    private List<LecturerReportEntity> reportEntities;

    @BeforeEach
    void setUp() {
        CourseReportEntity course = new CourseReportEntity();
        course.setName("Informatik");
        course.setOpenStatus("offen");
        course.setAcademicDegree("Bachelor");
        course.setSemester("WiSe 24/25");

        LecturerCanHoldCourseReportEntity lchc = new LecturerCanHoldCourseReportEntity();
        lchc.setAlreadyHeld("ja");
        lchc.setQualification("hoch");
        lchc.setAffinity("5");
        lchc.setCourse(course);

        LecturerReportEntity lecturer = new LecturerReportEntity();
        lecturer.setTitle("Dr.");
        lecturer.setFullName("John Doe");
        lecturer.setEmail("john.doe@example.com");
        lecturer.setPhone("123456");
        lecturer.setIsExtern(false);
        lecturer.setPreference("Alles");
        lecturer.setCanHoldCourses(List.of(lchc));

        reportEntities = List.of(lecturer);
        jsonCreator = new JsonCreator(reportEntities, ReportMode.ALL_COURSES_IN_PROVADIS);
    }

    @Test
    void testCreateFile() {
        byte[] result = jsonCreator.createFile();

        assertNotNull(result);
        assertTrue(result.length > 0);

        ObjectMapper mapper = new ObjectMapper();
        assertDoesNotThrow(() -> mapper.readTree(result));
    }

    @Test
    void testCreateFile_CoursesWithNoLecturers() {
        CourseReportEntity course = new CourseReportEntity();
        course.setName("Math");
        course.setOpenStatus("offen");
        course.setAcademicDegree("Master");
        course.setSemester("SoSe 25");
        course.setCanBeHeldBy(List.of());

        List<CourseReportEntity> specialEntities = List.of(course);
        JsonCreator specialCreator = new JsonCreator(specialEntities, ReportMode.ALL_COURSES_WITH_NO_LECTURERS);

        byte[] result = specialCreator.createFile();

        assertNotNull(result);
        assertTrue(result.length > 0);

        ObjectMapper mapper = new ObjectMapper();
        assertDoesNotThrow(() -> mapper.readTree(result));
    }
}
