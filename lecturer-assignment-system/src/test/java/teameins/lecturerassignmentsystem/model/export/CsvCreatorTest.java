package teameins.lecturerassignmentsystem.model.export;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvCreatorTest {

    private CsvCreator csvCreator;
    private List<LecturerReportEntity> reportEntities;

    @BeforeEach
    void setUp() {
        CourseReportEntity course = new CourseReportEntity("Informatik", "offen", "Bachelor", "WiSe 24/25", "ja", "hoch", "5");
        LecturerReportEntity lecturer = new LecturerReportEntity("Dr.", "John", "Doe", "", "john.doe@example.com", "123456", false, "Alles", List.of(course));
        reportEntities = List.of(lecturer);
        csvCreator = new CsvCreator(reportEntities, ReportMode.ALL_COURSES_IN_PROVADIS);
    }

    @Test
    void testCreateFile() {
        byte[] result = csvCreator.createFile();

        assertNotNull(result);
        assertTrue(result.length > 0);
        // Optional: Prüfe, ob es mit BOM beginnt
        assertEquals((byte) 0xEF, result[0]);
        assertEquals((byte) 0xBB, result[1]);
        assertEquals((byte) 0xBF, result[2]);
    }

    @Test
    void testCreateFile_EmptyList() {
        CsvCreator emptyCreator = new CsvCreator(List.of(), ReportMode.ALL_COURSES_IN_PROVADIS);
        byte[] result = emptyCreator.createFile();

        assertNotNull(result);
        // Sollte nur Header haben
        assertTrue(result.length > 0);
    }
}
