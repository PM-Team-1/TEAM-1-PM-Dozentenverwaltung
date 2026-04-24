package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonCreatorTest {

    private JsonCreator jsonCreator;
    private List<LecturerReportEntity> reportEntities;

    @BeforeEach
    void setUp() {
        CourseReportEntity course = new CourseReportEntity("Informatik", "offen", "Bachelor", "WiSe 24/25", "ja", "hoch", "5");
        LecturerReportEntity lecturer = new LecturerReportEntity("Dr.", "John", "Doe", "", "john.doe@example.com", "123456", false, "Alles", List.of(course));
        reportEntities = List.of(lecturer);
        jsonCreator = new JsonCreator(reportEntities, ReportMode.ALL_COURSES_IN_PROVADIS);
    }

    @Test
    void testCreateFile() {
        byte[] result = jsonCreator.createFile();

        assertNotNull(result);
        assertTrue(result.length > 0);

        // Prüfe, ob es gültiges JSON ist
        ObjectMapper mapper = new ObjectMapper();
        assertDoesNotThrow(() -> mapper.readTree(result));
    }

    @Test
    void testCreateFile_CoursesWithNoLecturers() {
        CourseReportEntity course = new CourseReportEntity("Math", "offen", "Master", "SoSe 25", "nein", "mittel", "3");
        LecturerReportEntity lecturer = new LecturerReportEntity(null, null, null, null, null, null, null, null, List.of(course));
        List<LecturerReportEntity> specialEntities = List.of(lecturer);
        JsonCreator specialCreator = new JsonCreator(specialEntities, ReportMode.ALL_COURSES_WITH_NO_LECTURERS);

        byte[] result = specialCreator.createFile();

        assertNotNull(result);
        assertTrue(result.length > 0);

        // Sollte nur die Kurse serialisieren
        ObjectMapper mapper = new ObjectMapper();
        assertDoesNotThrow(() -> mapper.readTree(result));
    }
}
