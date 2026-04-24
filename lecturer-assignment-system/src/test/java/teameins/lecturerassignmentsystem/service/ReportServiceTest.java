package teameins.lecturerassignmentsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.enums.*;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private LecturerRepository lecturerRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;

    @Mock
    private MappingService mappingService;

    @InjectMocks
    private ReportService reportService;

    private Lecturer lecturer;
    private Course course;
    private LecturerCanHoldCourse lchc;

    @BeforeEach
    void setUp() {
        lecturer = new Lecturer(1, Title.DOCTOR, "John", "Doe", "", "john.doe@example.com", "123456", false, TeachingPreference.ALLES);
        course = new Course(1, "Informatik", false, false, "WiSe 24/25");
        lchc = new LecturerCanHoldCourse(1, AlreadyHeld.PROVADIS, Qualification.IMMEDIATELY, course, lecturer, Affinity.HIGH);
    }

    @Test
    void testGetCoursesHeldLocally() {
        when(lecturerRepository.findAll()).thenReturn(List.of(lecturer));
        when(lecturerCanHoldCourseRepository.findAll()).thenReturn(List.of(lchc));

        List<LecturerReportEntity> result = reportService.getCoursesHeldLocally();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(lecturerRepository, times(1)).findAll();
        verify(lecturerCanHoldCourseRepository, times(1)).findAll();
    }

    @Test
    void testGetCoursesNeverHeldLocally() {
        LecturerCanHoldCourse lchcOther = new LecturerCanHoldCourse(2, AlreadyHeld.OTHER_SCHOOL, Qualification.IMMEDIATELY, course, lecturer, Affinity.MEDIUM);
        when(lecturerRepository.findAll()).thenReturn(List.of(lecturer));
        when(lecturerCanHoldCourseRepository.findAll()).thenReturn(List.of(lchcOther));

        List<LecturerReportEntity> result = reportService.getCoursesNeverHeldLocally();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetCoursesWithNoLecturers() {
        Course courseNoLecturer = new Course(2, "Math", false, false, "SoSe 25");
        when(courseRepository.findAll()).thenReturn(List.of(course, courseNoLecturer));
        when(lecturerCanHoldCourseRepository.findAll()).thenReturn(List.of(lchc)); // Nur für course

        List<LecturerReportEntity> result = reportService.getCoursesWithNoLecturers();

        assertNotNull(result);
        assertEquals(1, result.size()); // Nur courseNoLecturer
    }

    @Test
    void testGetCoursesWithOnlyForeignExperience() {
        LecturerCanHoldCourse lchcForeign = new LecturerCanHoldCourse(2, AlreadyHeld.OTHER_SCHOOL, Qualification.IMMEDIATELY, course, lecturer, Affinity.LOW);
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(lecturerCanHoldCourseRepository.findAll()).thenReturn(List.of(lchcForeign)); // Kein PROVADIS

        List<LecturerReportEntity> result = reportService.getCoursesWithOnlyForeignExperience();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetReportByReportMode() {
        when(lecturerRepository.findAll()).thenReturn(List.of(lecturer));
        when(lecturerCanHoldCourseRepository.findAll()).thenReturn(List.of(lchc));

        List<LecturerReportEntity> result = reportService.getReportByReportMode(ReportMode.ALL_COURSES_IN_PROVADIS);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // Zusätzliche Tests für Edge Cases, z. B. leere Listen
    @Test
    void testGetCoursesHeldLocally_EmptyList() {
        when(lecturerRepository.findAll()).thenReturn(List.of());
        when(lecturerCanHoldCourseRepository.findAll()).thenReturn(List.of());

        List<LecturerReportEntity> result = reportService.getCoursesHeldLocally();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
