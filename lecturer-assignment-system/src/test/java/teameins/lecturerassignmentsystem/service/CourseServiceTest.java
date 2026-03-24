package teameins.lecturerassignmentsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private MappingService mappingService;

    @Mock
    private LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseDto courseDto;

    @BeforeEach
    void setUp() {
        course = new Course(1, "Programmierung 1", false, false, "WS2024/2025");

        courseDto = new CourseDto();
        courseDto.setId(1);
        courseDto.setName("Programmierung 1");
        courseDto.setClosed(false);
        courseDto.setMaster(false);
        courseDto.setSemester("WS2024/2025");
        courseDto.setCanBeHeldBy(new ArrayList<>());
    }

    // -------------------------------------------------------------------------
    // getCourseById
    // -------------------------------------------------------------------------

    @Test
    void getCourseById_kursExistiert_gibtCourseDtoZurueck() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());
        when(mappingService.map(course, new ArrayList<>())).thenReturn(courseDto);

        CourseDto result = courseService.getCourseById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Programmierung 1", result.getName());
        verify(courseRepository).findById(1);
    }

    @Test
    void getCourseById_kursExistiertNicht_wirftCourseNotFoundException() {
        when(courseRepository.findById(99)).thenReturn(Optional.empty());

        CourseNotFoundException exception = assertThrows(
                CourseNotFoundException.class,
                () -> courseService.getCourseById(99)
        );

        assertTrue(exception.getMessage().contains("99"));
        verify(courseRepository).findById(99);
    }

    @Test
    void getCourseById_rufsMappingServiceAuf() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());
        when(mappingService.map(course, new ArrayList<>())).thenReturn(courseDto);

        courseService.getCourseById(1);

        verify(mappingService).map(eq(course), anyList());
    }

    // -------------------------------------------------------------------------
    // listCourses
    // -------------------------------------------------------------------------

    @Test
    void listCourses_keinKursVorhanden_gibtLeereListeZurueck() {
        when(courseRepository.findAll()).thenReturn(new ArrayList<>());

        List<CourseDto> result = courseService.listCourses();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(courseRepository).findAll();
    }

    @Test
    void listCourses_mehrereKurseVorhanden_gibtAlleKurseZurueck() {
        Course course2 = new Course(2, "Mathematik", false, true, "SS2025");
        CourseDto courseDto2 = new CourseDto();
        courseDto2.setId(2);
        courseDto2.setName("Mathematik");
        courseDto2.setCanBeHeldBy(new ArrayList<>());

        when(courseRepository.findAll()).thenReturn(List.of(course, course2));
        when(courseRepository.findLecturersWhoCanHoldCourse(anyInt())).thenReturn(new ArrayList<>());
        when(mappingService.map(eq(course), anyList())).thenReturn(courseDto);
        when(mappingService.map(eq(course2), anyList())).thenReturn(courseDto2);

        List<CourseDto> result = courseService.listCourses();

        assertEquals(2, result.size());
        verify(courseRepository).findAll();
        verify(mappingService, times(2)).map(any(Course.class), anyList());
    }

    @Test
    void listCourses_kursHatZugeordneteDozenten_gibtKurseMitDozentenZurueck() {
        LecturerCanHoldCourse lchc = new LecturerCanHoldCourse();
        LecturerCanHoldCourseDto lchcDto = new LecturerCanHoldCourseDto();

        CourseDto courseDtoWithLecturers = new CourseDto();
        courseDtoWithLecturers.setId(1);
        courseDtoWithLecturers.setCanBeHeldBy(List.of(lchcDto));

        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(List.of(lchc));
        when(mappingService.map(lchc)).thenReturn(lchcDto);
        when(mappingService.map(eq(course), anyList())).thenReturn(courseDtoWithLecturers);

        List<CourseDto> result = courseService.listCourses();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCanBeHeldBy().size());
    }

    // -------------------------------------------------------------------------
    // createCourse
    // -------------------------------------------------------------------------

    @Test
    void createCourse_gueltigerKurs_gibtErstelltenKursZurueck() {
        CourseDto neuerKursDto = new CourseDto();
        neuerKursDto.setId(0);
        neuerKursDto.setName("Algorithmen");
        neuerKursDto.setSemester("WS2024/2025");
        neuerKursDto.setCanBeHeldBy(new ArrayList<>());

        Course gespeicherterKurs = new Course(5, "Algorithmen", false, false, "WS2024/2025");

        CourseDto erwartetesErgebnis = new CourseDto();
        erwartetesErgebnis.setId(5);
        erwartetesErgebnis.setName("Algorithmen");
        erwartetesErgebnis.setCanBeHeldBy(new ArrayList<>());

        when(mappingService.map(neuerKursDto)).thenReturn(gespeicherterKurs);
        when(courseRepository.save(gespeicherterKurs)).thenReturn(gespeicherterKurs);
        when(courseRepository.findById(5)).thenReturn(Optional.of(gespeicherterKurs));
        when(courseRepository.findLecturersWhoCanHoldCourse(5)).thenReturn(new ArrayList<>());
        when(mappingService.map(eq(gespeicherterKurs), anyList())).thenReturn(erwartetesErgebnis);

        CourseDto result = courseService.createCourse(neuerKursDto);

        assertNotNull(result);
        assertEquals(5, result.getId());
        assertEquals("Algorithmen", result.getName());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_speichertKursInDatenbank() {
        Course mappedCourse = new Course(3, "Datenbanken", false, false, "SS2025");

        when(mappingService.map(courseDto)).thenReturn(mappedCourse);
        when(courseRepository.save(mappedCourse)).thenReturn(mappedCourse);
        when(courseRepository.findById(3)).thenReturn(Optional.of(mappedCourse));
        when(courseRepository.findLecturersWhoCanHoldCourse(3)).thenReturn(new ArrayList<>());
        when(mappingService.map(eq(mappedCourse), anyList())).thenReturn(courseDto);

        courseService.createCourse(courseDto);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    // -------------------------------------------------------------------------
    // updateCourse
    // -------------------------------------------------------------------------

    @Test
    void updateCourse_kursExistiert_gibtAktualisiertenkursZurueck() {
        CourseDto aktualisiertesDto = new CourseDto();
        aktualisiertesDto.setId(1);
        aktualisiertesDto.setName("Programmierung 1 - Aktualisiert");
        aktualisiertesDto.setSemester("WS2024/2025");
        aktualisiertesDto.setCanBeHeldBy(new ArrayList<>());

        Course aktualisiertKurs = new Course(1, "Programmierung 1 - Aktualisiert", false, false, "WS2024/2025");

        // findById wird zweimal aufgerufen: einmal für die Existenzprüfung, einmal in getCourseById
        when(courseRepository.findById(1)).thenReturn(Optional.of(course)).thenReturn(Optional.of(aktualisiertKurs));
        when(mappingService.map(aktualisiertesDto)).thenReturn(aktualisiertKurs);
        when(courseRepository.save(aktualisiertKurs)).thenReturn(aktualisiertKurs);
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());
        when(mappingService.map(any(Course.class), anyList())).thenReturn(aktualisiertesDto);

        CourseDto result = courseService.updateCourse(aktualisiertesDto);

        assertNotNull(result);
        assertEquals("Programmierung 1 - Aktualisiert", result.getName());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void updateCourse_kursExistiertNicht_wirftCourseNotFoundException() {
        CourseDto nichtExistierenderKurs = new CourseDto();
        nichtExistierenderKurs.setId(99);
        nichtExistierenderKurs.setName("Unbekannt");
        nichtExistierenderKurs.setCanBeHeldBy(new ArrayList<>());

        when(courseRepository.findById(99)).thenReturn(Optional.empty());

        CourseNotFoundException exception = assertThrows(
                CourseNotFoundException.class,
                () -> courseService.updateCourse(nichtExistierenderKurs)
        );

        assertTrue(exception.getMessage().contains("99"));
        verify(courseRepository, never()).save(any());
    }

    @Test
    void updateCourse_speichertAenderungenInDatenbank() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(mappingService.map(courseDto)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());
        when(mappingService.map(eq(course), anyList())).thenReturn(courseDto);

        courseService.updateCourse(courseDto);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    // -------------------------------------------------------------------------
    // deleteCourse
    // -------------------------------------------------------------------------

    @Test
    void deleteCourse_kursOhneDozenten_loeschtKursAusDatenbank() {
        courseDto.setCanBeHeldBy(new ArrayList<>());

        courseService.deleteCourse(courseDto);

        verify(courseRepository).deleteById(1);
        verify(lecturerCanHoldCourseRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteCourse_kursWithDozenten_loeschtZuordnungenUndKurs() {
        LecturerCanHoldCourseDto lchcDto1 = new LecturerCanHoldCourseDto();
        lchcDto1.setId(10);
        LecturerCanHoldCourseDto lchcDto2 = new LecturerCanHoldCourseDto();
        lchcDto2.setId(11);

        courseDto.setCanBeHeldBy(List.of(lchcDto1, lchcDto2));

        courseService.deleteCourse(courseDto);

        verify(lecturerCanHoldCourseRepository).deleteById(10);
        verify(lecturerCanHoldCourseRepository).deleteById(11);
        verify(courseRepository).deleteById(1);
    }

    @Test
    void deleteCourse_loeschtZuerst_Zuordnungen_DannKurs() {
        LecturerCanHoldCourseDto lchcDto = new LecturerCanHoldCourseDto();
        lchcDto.setId(10);
        courseDto.setCanBeHeldBy(List.of(lchcDto));

        var inOrder = inOrder(lecturerCanHoldCourseRepository, courseRepository);

        courseService.deleteCourse(courseDto);

        inOrder.verify(lecturerCanHoldCourseRepository).deleteById(10);
        inOrder.verify(courseRepository).deleteById(1);
    }
}
