package teameins.lecturerassignmentsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerHoldsCourseDto;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerHoldsCourse;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
import teameins.lecturerassignmentsystem.model.exception.InvalidCourseException;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.model.enums.Qualification;
import teameins.lecturerassignmentsystem.model.enums.Title;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerHoldsCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

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
    private LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;

    @Mock
    private LecturerHoldsCourseRepository lecturerHoldsCourseRepository;

    @Mock
    private LecturerRepository lecturerRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private CourseService courseService;

    @Spy
    private MappingService mappingService = new MappingService(passwordEncoder);

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
    // getCourseDtoById
    // -------------------------------------------------------------------------

    @Test
    void getCourseById_kursExistiert_gibtCourseDtoDtoZurueck() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());

        CourseDto result = courseService.getCourseDtoById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Programmierung 1", result.getName());
        assertEquals("WiSe 24/25", result.getSemester());
        verify(courseRepository).findById(1);
    }

    @Test
    void getCourseById_kursExistiertNicht_wirftCourseDtoNotFoundException() {
        when(courseRepository.findById(99)).thenReturn(Optional.empty());

        CourseNotFoundException exception = assertThrows(
                CourseNotFoundException.class,
                () -> courseService.getCourseDtoById(99)
        );

        assertTrue(exception.getMessage().contains("99"));
        verify(courseRepository).findById(99);
    }

    @Test
    void getCourseDtoById_rufsMappingServiceAuf() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());

        courseService.getCourseDtoById(1);

        verify(mappingService).map(eq(course), anyList(), any());
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
        verify(mappingService, times(2)).map(any(Course.class), anyList(), any());
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

        // findById wird zweimal aufgerufen: einmal für die Existenzprüfung, einmal in getCourseDtoById
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

    // -------------------------------------------------------------------------
    // assignLecturerToCourse
    // -------------------------------------------------------------------------

    @Test
    void assignLecturerToCourse_success() {
        Lecturer lecturer = new Lecturer();
        lecturer.setId(2);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(lecturerRepository.findById(2)).thenReturn(Optional.of(lecturer));
        when(lecturerHoldsCourseRepository.existsByCourseId(1)).thenReturn(false);
        LecturerHoldsCourse assignment = new LecturerHoldsCourse(10, course, lecturer);
        when(lecturerHoldsCourseRepository.save(any(LecturerHoldsCourse.class))).thenReturn(assignment);
        LecturerHoldsCourseDto dto = new LecturerHoldsCourseDto(10, 2, 1);
        when(mappingService.map(assignment)).thenReturn(dto);
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());
        when(lecturerHoldsCourseRepository.findByCourseId(1)).thenReturn(Optional.of(assignment));

        CourseDto result = courseService.assignLecturerToCourse(1, 2);

        assertNotNull(result);
        assertEquals(10, result.getHeldBy().getId());
        verify(lecturerHoldsCourseRepository).save(any());
    }

    @Test
    void assignLecturerToCourse_courseNotFound() {
        when(courseRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(CourseNotFoundException.class, () -> courseService.assignLecturerToCourse(99, 2));
    }

    @Test
    void assignLecturerToCourse_lecturerNotFound() {
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(lecturerRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(LecturerNotFoundException.class, () -> courseService.assignLecturerToCourse(1, 99));
    }

    @Test
    void assignLecturerToCourse_alreadyAssigned() {
        Lecturer lecturer = new Lecturer();
        lecturer.setId(2);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(lecturerRepository.findById(2)).thenReturn(Optional.of(lecturer));
        when(lecturerHoldsCourseRepository.existsByCourseId(1)).thenReturn(true);

        assertThrows(InvalidCourseException.class, () -> courseService.assignLecturerToCourse(1, 2));
    }

    // -------------------------------------------------------------------------
    // updateLecturerForCourse
    // -------------------------------------------------------------------------

    @Test
    void updateLecturerForCourse_success() {
        Lecturer newLecturer = new Lecturer();
        newLecturer.setId(3);
        LecturerHoldsCourse assignment = new LecturerHoldsCourse(10, course, new Lecturer());
        when(lecturerHoldsCourseRepository.findByCourseId(1)).thenReturn(Optional.of(assignment));
        when(lecturerRepository.findById(3)).thenReturn(Optional.of(newLecturer));
        when(lecturerHoldsCourseRepository.save(any(LecturerHoldsCourse.class))).thenReturn(assignment);
        LecturerHoldsCourseDto dto = new LecturerHoldsCourseDto(10, 3, 1);
        when(mappingService.map(assignment)).thenReturn(dto);

        LecturerHoldsCourseDto result = courseService.updateLecturerForCourse(1, 3);

        assertEquals(3, assignment.getLecturer().getId());
        assertNotNull(result);
    }

    @Test
    void updateLecturerForCourse_notAssigned() {
        when(lecturerHoldsCourseRepository.findByCourseId(1)).thenReturn(Optional.empty());
        assertThrows(InvalidCourseException.class, () -> courseService.updateLecturerForCourse(1, 2));
    }

    // -------------------------------------------------------------------------
    // removeLecturerFromCourse
    // -------------------------------------------------------------------------

    @Test
    void removeLecturerFromCourse_success() {
        LecturerHoldsCourse assignment = new LecturerHoldsCourse(10, course, new Lecturer());
        doReturn(Optional.of(assignment), Optional.empty()).when(lecturerHoldsCourseRepository).findByCourseId(1);
        doReturn(Optional.of(course)).when(courseRepository).findById(1);
        doReturn(new ArrayList<>()).when(courseRepository).findLecturersWhoCanHoldCourse(1);

        CourseDto result = courseService.removeLecturerFromCourse(1);

        assertNotNull(result);
        verify(lecturerHoldsCourseRepository).deleteById(10);
    }

    @Test
    void removeLecturerFromCourse_notAssigned() {
        when(lecturerHoldsCourseRepository.findByCourseId(1)).thenReturn(Optional.empty());
        assertThrows(InvalidCourseException.class, () -> courseService.removeLecturerFromCourse(1));
    }

    @Test
    void getAllSemesters_returnsSortedList() {
        when(courseRepository.findAllDistinctSemesters()).thenReturn(List.of("B", "A"));

        List<String> result = courseService.getAllSemesters();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A", result.get(0));
        assertEquals("B", result.get(1));
    }

    @Test
    void getCoursesBySemester_returnsCoursesAndSortedByMasterFlag() {
        Course bachelorCourse = new Course(1, "BachCourse", false, false, "WiSe 24/25");
        Course masterCourse = new Course(2, "MasterCourse", false, true, "WiSe 24/25");

        when(courseRepository.findBySemester("WiSe 24/25")).thenReturn(List.of(masterCourse, bachelorCourse));
        when(courseRepository.findLecturersWhoCanHoldCourse(1)).thenReturn(new ArrayList<>());
        when(courseRepository.findLecturersWhoCanHoldCourse(2)).thenReturn(new ArrayList<>());
        when(lecturerHoldsCourseRepository.findByCourseId(1)).thenReturn(Optional.empty());
        when(lecturerHoldsCourseRepository.findByCourseId(2)).thenReturn(Optional.empty());

        List<CourseDto> result = courseService.getCoursesBySemester("WiSe 24/25");

        assertNotNull(result);
        assertEquals(2, result.size());
        // method sorts so that non-master (bachelor) comes first
        assertFalse(result.get(0).isMaster());
        assertTrue(result.get(1).isMaster());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
    }
}
