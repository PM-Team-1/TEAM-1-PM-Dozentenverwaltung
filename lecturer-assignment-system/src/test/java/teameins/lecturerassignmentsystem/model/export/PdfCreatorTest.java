package teameins.lecturerassignmentsystem.model.export;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerCanHoldCourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfCreatorTest {

    private PdfCreator pdfCreator;
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
        pdfCreator = new PdfCreator(reportEntities, ReportMode.ALL_COURSES_IN_PROVADIS);
    }

    @Test
    void testCreateFile() {
        byte[] result = pdfCreator.createFile();

        assertNotNull(result);
        assertTrue(result.length > 0);

        // Prüfe, ob es ein gültiges PDF ist
        assertDoesNotThrow(() -> {
            try (PDDocument document = Loader.loadPDF(result)) {
                assertTrue(document.getNumberOfPages() > 0);
            }
        });
    }

    @Test
    void testCreateFile_EmptyList() {
        PdfCreator emptyCreator = new PdfCreator(List.of(), ReportMode.ALL_COURSES_IN_PROVADIS);
        byte[] result = emptyCreator.createFile();

        assertNotNull(result);
        assertTrue(result.length > 0);

        // Sollte trotzdem ein gültiges PDF erzeugen
        assertDoesNotThrow(() -> {
            try (PDDocument document = Loader.loadPDF(result)) {
                assertTrue(document.getNumberOfPages() > 0);
            }
        });
    }
}
