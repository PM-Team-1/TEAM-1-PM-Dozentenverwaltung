package teameins.lecturerassignmentsystem.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.enums.Qualification;
import teameins.lecturerassignmentsystem.model.enums.Title;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LecturerServiceTest {

    @Mock
    LecturerRepository lecturerRepository;
    @Mock
    LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;
    @Mock
    CourseRepository courseRepository;

    @InjectMocks
    LecturerService lecturerService;

    @Spy
    MappingService mappingService;

    @Test
    void getLecturerByIdTest(){
        int lecturerId = 1;
        Mockito.doReturn(Optional.of(getLecturerById(lecturerId))).when(lecturerRepository).findById(lecturerId);
        Mockito.doReturn(listAllCoursesLecturerCanHold(lecturerId, List.of(1,2,3,4,5,6,7,8,9))).when(lecturerRepository).findCoursesLecturerCanHold(lecturerId);

        Lecturer lecturer = getLecturerById(lecturerId);
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = listAllCoursesLecturerCanHold(lecturerId, List.of(1,2,3,4,5,6,7,8,9));
        LecturerDto lecturerDto = mappingService.map(
                lecturer,
                lecturerCanHoldCourses.stream().map(mappingService::map).toList()
        );

        assertEquals(lecturerDto, lecturerService.getLecturerById(lecturerId));
    }

    @Test
    void getLecturerByIdTestError(){
        int lecturerId = 100;
        Mockito.doReturn(Optional.empty()).when(lecturerRepository).findById(lecturerId);

        assertThrows(LecturerNotFoundException.class, () -> lecturerService.getLecturerById(lecturerId));
    }

    @Test
    void listAllLecturersTest(){
        List<Integer> lecturerIds = List.of(1,2,3,4,5,6,7,8,9);
        Mockito.doReturn(listAllLecturers(lecturerIds)).when(lecturerRepository).findAll();

        List<Lecturer> lecturers = listAllLecturers(lecturerIds);
        lecturers.forEach(lecturer -> Mockito.doReturn(
                listAllCoursesLecturerCanHold(lecturer.getId(), List.of(1,2,3,4,5,6,7,8,9)))
                .when(lecturerRepository).findCoursesLecturerCanHold(lecturer.getId())
        );

        List<LecturerDto> lecturerDtos = lecturers.stream().map(lecturer -> mappingService.map(lecturer, listAllCoursesLecturerCanHold(
                lecturer.getId(),
                List.of(1,2,3,4,5,6,7,8,9)
        ).stream().map(mappingService::map).toList())).toList();

        assertEquals(lecturerDtos, lecturerService.listLecturers());
    }

    @Test
    void createLecturerTest(){
        int lecturerId = 10;
        Mockito.doReturn(getLecturerById(lecturerId)).when(lecturerRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(getLecturerById(lecturerId))).when(lecturerRepository).findById(lecturerId);
        Mockito.doReturn(listAllCoursesLecturerCanHold(lecturerId, List.of(1,2,3,4,5,6,7,8,9))).when(lecturerRepository).findCoursesLecturerCanHold(lecturerId);

        Lecturer lecturer = getLecturerById(lecturerId);
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = listAllCoursesLecturerCanHold(lecturerId, List.of(1,2,3,4,5,6,7,8,9));
        LecturerDto lecturerDto = mappingService.map(
                lecturer,
                lecturerCanHoldCourses.stream().map(mappingService::map).toList()
        );

        assertEquals(lecturerDto, lecturerService.createLecturer(lecturerDto));
    }

    @Test
    void updateLecturerTest() {
        int lecturerId = 10;
        Mockito.doReturn(getLecturerById(lecturerId)).when(lecturerRepository).save(Mockito.any());
        Mockito.doReturn(Optional.of(getLecturerById(lecturerId))).when(lecturerRepository).findById(lecturerId);
        Mockito.doReturn(listAllCoursesLecturerCanHold(lecturerId, List.of(1,2,3,4,5,6,7,8,9))).when(lecturerRepository).findCoursesLecturerCanHold(lecturerId);

        Lecturer lecturer = getLecturerById(lecturerId);
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = listAllCoursesLecturerCanHold(lecturerId, List.of(1,2,3,4,5,6,7,8,9));
        LecturerDto lecturerDto = mappingService.map(
                lecturer,
                lecturerCanHoldCourses.stream().map(mappingService::map).toList()
        );

        assertEquals(lecturerDto, lecturerService.updateLecturer(lecturerDto));
    }

    @Test
    void updateLecturerTestError() {
        int lecturerId = 100;
        Mockito.doReturn(Optional.empty()).when(lecturerRepository).findById(lecturerId);

        Lecturer lecturer = getLecturerById(lecturerId);
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = listAllCoursesLecturerCanHold(lecturerId, List.of(1,2,3,4,5,6,7,8,9));
        LecturerDto lecturerDto = mappingService.map(
                lecturer,
                lecturerCanHoldCourses.stream().map(mappingService::map).toList()
        );

        assertThrows(LecturerNotFoundException.class, () -> lecturerService.updateLecturer(lecturerDto));
    }

    @Test
    void addCourseToLecturerTest(){
        int lecturerId = 1;
        int courseId = 1;
        int lecturerCanHoldCourseId = 1;
        Mockito.doReturn(Optional.of(getLecturerById(lecturerId))).when(lecturerRepository).findById(lecturerId);
        Mockito.doReturn(Optional.of(getMasterCourseById(courseId))).when(courseRepository).findById(courseId);
        Mockito.doReturn(false).when(lecturerCanHoldCourseRepository).existsByLecturerIdAndCourseId(Mockito.anyInt(), Mockito.anyInt());
        Mockito.doReturn(getLecturerCanHoldCourseById(lecturerCanHoldCourseId)).when(lecturerCanHoldCourseRepository).save(Mockito.any());

        LecturerCanHoldCourse lecturerCanHoldCourse = getLecturerCanHoldCourseByLecturerAndCourseId(lecturerId, courseId, lecturerCanHoldCourseId);

        LecturerCanHoldCourseDto lecturerCanHoldCourseDto = mappingService.map(lecturerCanHoldCourse);

        assertEquals(lecturerCanHoldCourseDto, lecturerService.addCourseToLecturer(lecturerCanHoldCourseDto));
    }

    @Test
    void addCourseToLecturerTestLecturerError(){
        int lecturerId = 1;
        int courseId = 1;
        int lecturerCanHoldCourseId = 1;
        Mockito.doReturn(Optional.empty()).when(lecturerRepository).findById(lecturerId);

        LecturerCanHoldCourse lecturerCanHoldCourse = getLecturerCanHoldCourseByLecturerAndCourseId(lecturerId, courseId, lecturerCanHoldCourseId);

        LecturerCanHoldCourseDto lecturerCanHoldCourseDto = mappingService.map(lecturerCanHoldCourse);

        assertThrows(LecturerNotFoundException.class, () -> lecturerService.addCourseToLecturer(lecturerCanHoldCourseDto));
    }

    @Test
    void addCourseToLecturerTestCourseError(){
        int lecturerId = 1;
        int courseId = 1;
        int lecturerCanHoldCourseId = 1;
        Mockito.doReturn(Optional.of(getLecturerById(lecturerId))).when(lecturerRepository).findById(lecturerId);
        Mockito.doReturn(Optional.empty()).when(courseRepository).findById(courseId);

        LecturerCanHoldCourse lecturerCanHoldCourse = getLecturerCanHoldCourseByLecturerAndCourseId(lecturerId, courseId, lecturerCanHoldCourseId);

        LecturerCanHoldCourseDto lecturerCanHoldCourseDto = mappingService.map(lecturerCanHoldCourse);

        assertThrows(CourseNotFoundException.class, () -> lecturerService.addCourseToLecturer(lecturerCanHoldCourseDto));
    }

    @Test
    void addCourseToLecturerTestRelationshipError(){
        int lecturerId = 1;
        int courseId = 1;
        int lecturerCanHoldCourseId = 1;
        Mockito.doReturn(Optional.of(getLecturerById(lecturerId))).when(lecturerRepository).findById(lecturerId);
        Mockito.doReturn(Optional.of(getMasterCourseById(courseId))).when(courseRepository).findById(courseId);
        Mockito.doReturn(true).when(lecturerCanHoldCourseRepository).existsByLecturerIdAndCourseId(Mockito.anyInt(), Mockito.anyInt());

        LecturerCanHoldCourse lecturerCanHoldCourse = getLecturerCanHoldCourseByLecturerAndCourseId(lecturerId, courseId, lecturerCanHoldCourseId);

        LecturerCanHoldCourseDto lecturerCanHoldCourseDto = mappingService.map(lecturerCanHoldCourse);

        assertThrows(IllegalArgumentException.class, () -> lecturerService.addCourseToLecturer(lecturerCanHoldCourseDto));
    }

    @Test
    void addCourseToLecturerTestPreferenceOnlyMasterError(){
        int lecturerId = 1;
        int courseId = 1;
        int lecturerCanHoldCourseId = 1;
        Mockito.doReturn(Optional.of(getOnlyMasterLecturerById(lecturerId))).when(lecturerRepository).findById(lecturerId);
        Mockito.doReturn(Optional.of(getBachelorCourseById(courseId))).when(courseRepository).findById(courseId);
        Mockito.doReturn(false).when(lecturerCanHoldCourseRepository).existsByLecturerIdAndCourseId(Mockito.anyInt(), Mockito.anyInt());

        LecturerCanHoldCourse lecturerCanHoldCourse = getLecturerCanHoldCourseByLecturerAndCourseId(lecturerId, courseId, lecturerCanHoldCourseId);

        LecturerCanHoldCourseDto lecturerCanHoldCourseDto = mappingService.map(lecturerCanHoldCourse);

        assertThrows(IllegalArgumentException.class, () -> lecturerService.addCourseToLecturer(lecturerCanHoldCourseDto));
    }

    @Test
    void addCourseToLecturerTestPreferenceOnlyBachelorError(){
        int lecturerId = 1;
        int courseId = 1;
        int lecturerCanHoldCourseId = 1;
        Mockito.doReturn(Optional.of(getOnlyBachelorLecturerById(lecturerId))).when(lecturerRepository).findById(lecturerId);
        Mockito.doReturn(Optional.of(getMasterCourseById(courseId))).when(courseRepository).findById(courseId);
        Mockito.doReturn(false).when(lecturerCanHoldCourseRepository).existsByLecturerIdAndCourseId(Mockito.anyInt(), Mockito.anyInt());

        LecturerCanHoldCourse lecturerCanHoldCourse = getLecturerCanHoldCourseByLecturerAndCourseId(lecturerId, courseId, lecturerCanHoldCourseId);

        LecturerCanHoldCourseDto lecturerCanHoldCourseDto = mappingService.map(lecturerCanHoldCourse);

        assertThrows(IllegalArgumentException.class, () -> lecturerService.addCourseToLecturer(lecturerCanHoldCourseDto));
    }

    @Test
    void deleteLecturerTest(){
        int lecturerId = 1;
        Mockito.doNothing().when(lecturerCanHoldCourseRepository).deleteById(Mockito.anyInt());
        Mockito.doNothing().when(lecturerRepository).deleteById(Mockito.anyInt());

        Lecturer lecturer = getLecturerById(lecturerId);
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = listAllCoursesLecturerCanHold(lecturerId, List.of(1,2,3,4,5,6,7,8,9));
        LecturerDto lecturerDto = mappingService.map(
                lecturer,
                lecturerCanHoldCourses.stream().map(mappingService::map).toList()
        );

        assertDoesNotThrow(() -> lecturerService.deleteLecturer(lecturerDto));
    }

    private Lecturer getLecturerById(int id){
        return new Lecturer(
                id,
                Title.PROFESSOR,
                "Test",
                "Fall",
                null,
                "test@testfall.com",
                "+123456789",
                false,
                Preference.ALLES
        );
    }

    private Lecturer getOnlyMasterLecturerById(int id){
        return new Lecturer(
                id,
                Title.PROFESSOR,
                "Test",
                "Fall",
                null,
                "test@testfall.com",
                "+123456789",
                false,
                Preference.ONLY_MASTER
        );
    }

    private Lecturer getOnlyBachelorLecturerById(int id){
        return new Lecturer(
                id,
                Title.PROFESSOR,
                "Test",
                "Fall",
                null,
                "test@testfall.com",
                "+123456789",
                false,
                Preference.ONLY_BACHELOR
        );
    }

    private List<Lecturer> listAllLecturers(List<Integer> ids){
        return ids.stream().map(this::getLecturerById).toList();
    }

    private Course getMasterCourseById(int id){
        return new Course(
                id,
                "Testcourse",
                false,
                true,
                "WS 24/25"
        );
    }

    private Course getBachelorCourseById(int id){
        return new Course(
                id,
                "Testcourse",
                false,
                false,
                "WS 24/25"
        );
    }

    private LecturerCanHoldCourse getLecturerCanHoldCourseById(int id){
        return new LecturerCanHoldCourse(
                id,
                AlreadyHeld.PROVADIS,
                Qualification.IMMEDIATELY,
                getMasterCourseById(id),
                getLecturerById(id),
                false
        );
    }

    private LecturerCanHoldCourse getLecturerCanHoldCourseByLecturerId(int lecturerId, int id){
        return new LecturerCanHoldCourse(
                id,
                AlreadyHeld.PROVADIS,
                Qualification.IMMEDIATELY,
                getMasterCourseById(id),
                getLecturerById(lecturerId),
                false
        );
    }

    private LecturerCanHoldCourse getLecturerCanHoldCourseByLecturerAndCourseId(int lecturerId, int courseId, int id){
        return new LecturerCanHoldCourse(
                id,
                AlreadyHeld.PROVADIS,
                Qualification.IMMEDIATELY,
                getMasterCourseById(courseId),
                getLecturerById(lecturerId),
                false
        );
    }

    private List<LecturerCanHoldCourse> listAllCoursesLecturerCanHold(int lecturerId, List<Integer> ids){
        return ids.stream().map(id -> getLecturerCanHoldCourseByLecturerId(lecturerId, id)).toList();
    }
}
