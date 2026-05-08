package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerHoldsCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerHoldsCourseDto;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
import teameins.lecturerassignmentsystem.model.exception.InvalidCourseException;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerHoldsCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final MappingService mappingService;
    private final LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;
    private final LecturerHoldsCourseRepository lecturerHoldsCourseRepository;
    private final LecturerRepository lecturerRepository;

    public CourseService(CourseRepository courseRepository, MappingService mappingService,
			LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository,
            LecturerHoldsCourseRepository lecturerHoldsCourseRepository,
            LecturerRepository lecturerRepository) {
		super();
		this.courseRepository = courseRepository;
		this.mappingService = mappingService;
		this.lecturerCanHoldCourseRepository = lecturerCanHoldCourseRepository;
        this.lecturerHoldsCourseRepository = lecturerHoldsCourseRepository;
        this.lecturerRepository = lecturerRepository;
	}

	public CourseDto getCourseDtoById(int courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Es konnte keine Vorlesung mit der ID " + courseId + " gefunden werden."));
        List<LecturerCanHoldCourseDto> canBeHeldBy = getLecturersWhoCanHoldCourse(courseId);
        LecturerHoldsCourseDto heldBy = lecturerHoldsCourseRepository.findByCourseId(courseId)
                .map(mappingService::map)
                .orElse(null);
        return mappingService.map(course, canBeHeldBy, heldBy);
    }

    public List<CourseDto> listCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDto> courseDtos = new ArrayList<>();
        for (Course course : courses) {
            List<LecturerCanHoldCourseDto> canBeHeldBy = getLecturersWhoCanHoldCourse(course.getId());
            LecturerHoldsCourseDto heldBy = lecturerHoldsCourseRepository.findByCourseId(course.getId())
                    .map(mappingService::map)
                    .orElse(null);
            courseDtos.add(mappingService.map(course, canBeHeldBy, heldBy));
        }
        return courseDtos;
    }

    public List<String> getAllSemesters() {
        List<String> semesters = new ArrayList<>(courseRepository.findAllDistinctSemesters());
        semesters.sort((s1, s2) -> {
            CourseDto c1 = new CourseDto();
            c1.setSemester(s1);
            CourseDto c2 = new CourseDto();
            c2.setSemester(s2);
            return c1.getSemesterSortable().compareTo(c2.getSemesterSortable());
        });
        return semesters;
    }

    public List<CourseDto> getCoursesBySemester(String semester) {
        List<Course> courses = courseRepository.findBySemester(semester);
        List<CourseDto> courseDtos = new ArrayList<>();
        for (Course course : courses) {
            List<LecturerCanHoldCourseDto> canBeHeldBy = getLecturersWhoCanHoldCourse(course.getId());
            LecturerHoldsCourseDto heldBy = lecturerHoldsCourseRepository.findByCourseId(course.getId())
                    .map(mappingService::map)
                    .orElse(null);
            courseDtos.add(mappingService.map(course, canBeHeldBy, heldBy));
        }
        courseDtos.sort((c1, c2) -> Boolean.compare(c1.isMaster(), c2.isMaster()));
        return courseDtos;
    }

    public CourseDto createCourse(CourseDto courseDto) {
        if (!courseDto.validate()) {
            throw new InvalidCourseException("Die Vorlesung ist ungültig.");
        }
        int id = courseRepository.save(mappingService.map(courseDto)).getId();
        return getCourseDtoById(id);
    }

    public CourseDto updateCourse(CourseDto courseDto) {
        if (!courseDto.validate()) {
            throw new InvalidCourseException("Die Vorlesung ist ungültig.");
        }
        courseRepository.findById(courseDto.getId()).
                orElseThrow(() -> new CourseNotFoundException("Es konnte keine Vorlesung mit der ID " + courseDto.getId() + " gefunden werden."));
        courseRepository.save(mappingService.map(courseDto));
        return getCourseDtoById(courseDto.getId());
    }

    public void deleteCourse(CourseDto courseDto) {
        for (LecturerCanHoldCourseDto lchc : courseDto.getCanBeHeldBy()) {
            lecturerCanHoldCourseRepository.deleteById(lchc.getId());
        }

        Optional<LecturerHoldsCourse> existingAssignment = lecturerHoldsCourseRepository
                .findByCourseId(courseDto.getId());
        existingAssignment.ifPresent(assignment -> lecturerHoldsCourseRepository.deleteById(assignment.getId()));

        courseRepository.deleteById(courseDto.getId());
    }

    private List<LecturerCanHoldCourseDto> getLecturersWhoCanHoldCourse(int courseId) {
        List<LecturerCanHoldCourse> lecturersWhoCanHoldCourse = courseRepository
                .findLecturersWhoCanHoldCourse(courseId);
        List<LecturerCanHoldCourseDto> courseCanBeHeldyByLecturerDtoList = new ArrayList<>();
        for (LecturerCanHoldCourse lchc : lecturersWhoCanHoldCourse) {
            courseCanBeHeldyByLecturerDtoList.add(mappingService.map(lchc));
        }
        return courseCanBeHeldyByLecturerDtoList;
    }

    public CourseDto assignLecturerToCourse(int courseId, int lecturerId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Vorlesung nicht gefunden."));
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new LecturerNotFoundException("Dozent nicht gefunden."));

        if (lecturerHoldsCourseRepository.existsByCourseId(courseId)) {
            throw new InvalidCourseException("Dieser Vorlesung ist bereits ein Dozent zugeordnet.");
        }

        LecturerHoldsCourse assignment = new LecturerHoldsCourse(0, course, lecturer);
        assignment = lecturerHoldsCourseRepository.save(assignment);
        mappingService.map(assignment);
        return getCourseDtoById(courseId);
    }

    public LecturerHoldsCourseDto updateLecturerForCourse(int courseId, int newLecturerId) {
        LecturerHoldsCourse assignment = lecturerHoldsCourseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new InvalidCourseException("Dieser Vorlesung ist noch kein Dozent zugeordnet."));
        
        Lecturer newLecturer = lecturerRepository.findById(newLecturerId)
                .orElseThrow(() -> new LecturerNotFoundException("Neuer Dozent nicht gefunden."));

        assignment.setLecturer(newLecturer);
        assignment = lecturerHoldsCourseRepository.save(assignment);
        return mappingService.map(assignment);
    }

    public CourseDto removeLecturerFromCourse(int courseId) {
        LecturerHoldsCourse assignment = lecturerHoldsCourseRepository.findByCourseId(courseId)
                .orElseThrow(() -> new InvalidCourseException("Dieser Vorlesung ist noch kein Dozent zugeordnet."));
        
        lecturerHoldsCourseRepository.deleteById(assignment.getId());
        return getCourseDtoById(courseId);
    }
}
