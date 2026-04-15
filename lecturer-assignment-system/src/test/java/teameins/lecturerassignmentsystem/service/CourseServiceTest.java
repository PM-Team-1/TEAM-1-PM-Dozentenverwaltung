package teameins.lecturerassignmentsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
import teameins.lecturerassignmentsystem.model.exception.InvalidCourseException;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.model.enums.Qualification;
import teameins.lecturerassignmentsystem.model.enums.Title;
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

    @Spy
    private MappingService mappingService;

    @Mock
    private LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseDto courseDto;

    @BeforeEach
    void setUp() {
        course = new Course(1, "Programmierung 1", false, false, "WiSe 24/25");

        courseDto = new CourseDto();
        courseDto.setId(1);
        courseDto.setName("Programmierung 1");
        courseDto.setClosed(false);
        courseDto.setMaster(false);
        courseDto.setSemester("WiSe 24/25");
        courseDto.setCanBeHeldBy(new ArrayList<>());
    }

    // -------------------------------------------------------------------------
    // getCourseById
    // -------------------------------------------------------------------------

    @Test
    void getCourseById_kursExistiert_gibtCourseDtoZurueck() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());

        CourseDto result = courseService.getCourseById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Programmierung 1", result.getName());
        assertEquals("WiSe 24/25", result.getSemester());
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
        Course course2 = new Course(2, "Mathematik", false, true, "SoSe 25");

        when(courseRepository.findAll()).thenReturn(List.of(course, course2));
        when(courseRepository.findLecturersWhoCanHoldCourse(anyInt())).thenReturn(new ArrayList<>());

        List<CourseDto> result = courseService.listCourses();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        verify(courseRepository).findAll();
        verify(mappingService, times(2)).map(any(Course.class), anyList());
    }

    @Test
    void listCourses_kursHatZugeordneteDozenten_gibtKurseMitDozentenZurueck() {
        Lecturer lecturer = new Lecturer();
        lecturer.setId(7);
        lecturer.setTitle(Title.PROFESSOR);
        lecturer.setFirstName("Max");
        lecturer.setLastName("Mustermann");
        lecturer.setSecondName(null);
        lecturer.setEmail("max.mustermann@hs.de");
        lecturer.setPhone("0123456789");
        lecturer.setExtern(false);
        lecturer.setTeachingPreference(TeachingPreference.ALLES);

        LecturerCanHoldCourse lchc = new LecturerCanHoldCourse();
        lchc.setId(10);
        lchc.setLecturer(lecturer);
        lchc.setCourse(course);
        lchc.setAlreadyHeld(AlreadyHeld.NOT_YET_HELD);
        lchc.setQualification(Qualification.IMMEDIATELY);
        lchc.setAffinity(Affinity.MEDIUM);

        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(List.of(lchc));

        List<CourseDto> result = courseService.listCourses();

        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getCanBeHeldBy().size());
        assertEquals(10, result.getFirst().getCanBeHeldBy().getFirst().getId());
    }

    // -------------------------------------------------------------------------
    // createCourse
    // -------------------------------------------------------------------------

    @Test
    void createCourse_gueltigerKurs_gibtErstelltenKursZurueck() {
        CourseDto neuerKursDto = new CourseDto();
        neuerKursDto.setId(0);
        neuerKursDto.setName("Algorithmen");
        neuerKursDto.setClosed(false);
        neuerKursDto.setMaster(false);
        neuerKursDto.setSemester("WiSe 24/25");
        neuerKursDto.setCanBeHeldBy(new ArrayList<>());

        Course gespeicherterKurs = new Course(5, "Algorithmen", false, false, "WiSe 24/25");

        when(courseRepository.save(any(Course.class))).thenReturn(gespeicherterKurs);
        when(courseRepository.findById(5)).thenReturn(Optional.of(gespeicherterKurs));
        when(courseRepository.findLecturersWhoCanHoldCourse(5)).thenReturn(new ArrayList<>());

        CourseDto result = courseService.createCourse(neuerKursDto);

        assertNotNull(result);
        assertEquals(5, result.getId());
        assertEquals("Algorithmen", result.getName());
        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(captor.capture());
        assertEquals("Algorithmen", captor.getValue().getName());
        assertEquals("WiSe 24/25", captor.getValue().getSemester());
    }

    @Test
    void createCourse_speichertKursInDatenbank() {
        Course mappedCourse = new Course(3, "Datenbanken", false, false, "WiSe 24/25");
        CourseDto input = new CourseDto();
        input.setId(1);
        input.setName("Programmierung 1");
        input.setClosed(false);
        input.setMaster(false);
        input.setSemester("WiSe 24/25");
        input.setCanBeHeldBy(new ArrayList<>());

        when(courseRepository.save(any(Course.class))).thenReturn(mappedCourse);
        when(courseRepository.findById(3)).thenReturn(Optional.of(mappedCourse));
        when(courseRepository.findLecturersWhoCanHoldCourse(3)).thenReturn(new ArrayList<>());

        courseService.createCourse(input);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void createCourse_ungueltigerKurs_wirftInvalidCourseException_und_mapWirdNichtAufgerufen() {
        CourseDto invalid = new CourseDto();
        invalid.setId(0);
        invalid.setName("");
        invalid.setClosed(false);
        invalid.setMaster(false);
        invalid.setSemester("WiSe 24/25");
        invalid.setCanBeHeldBy(new ArrayList<>());

        assertThrows(InvalidCourseException.class, () -> courseService.createCourse(invalid));
        verify(mappingService, never()).map(any(CourseDto.class));
        verify(courseRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // updateCourse
    // -------------------------------------------------------------------------

    @Test
    void updateCourse_kursExistiert_gibtAktualisiertenkursZurueck() {
        CourseDto aktualisiertesDto = new CourseDto();
        aktualisiertesDto.setId(1);
        aktualisiertesDto.setName("Programmierung 1 - Aktualisiert");
        aktualisiertesDto.setClosed(false);
        aktualisiertesDto.setMaster(false);
        aktualisiertesDto.setSemester("WiSe 24/25");
        aktualisiertesDto.setCanBeHeldBy(new ArrayList<>());

        Course aktualisiertKurs = new Course(1, "Programmierung 1 - Aktualisiert", false, false, "WiSe 24/25");

        // findById wird zweimal aufgerufen: einmal für die Existenzprüfung, einmal in getCourseById
        when(courseRepository.findById(1)).thenReturn(Optional.of(course)).thenReturn(Optional.of(aktualisiertKurs));
        when(courseRepository.save(any(Course.class))).thenReturn(aktualisiertKurs);
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());

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
        nichtExistierenderKurs.setClosed(false);
        nichtExistierenderKurs.setMaster(false);
        nichtExistierenderKurs.setSemester("WiSe 24/25");
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
        Course mappedEntity = new Course(1, "Programmierung 1", false, false, "WiSe 24/25");
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(mappedEntity);
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());

        courseService.updateCourse(courseDto);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourse_ungueltigerKurs_wirftInvalidCourseException_und_mapWirdNichtAufgerufen() {
        CourseDto invalid = new CourseDto();
        invalid.setId(1);
        invalid.setName(null);
        invalid.setClosed(false);
        invalid.setMaster(false);
        invalid.setSemester("WiSe 24/25");
        invalid.setCanBeHeldBy(new ArrayList<>());

        assertThrows(InvalidCourseException.class, () -> courseService.updateCourse(invalid));
        verify(mappingService, never()).map(any(CourseDto.class));
        verify(courseRepository, never()).save(any());
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
