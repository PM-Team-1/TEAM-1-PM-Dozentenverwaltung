package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
import teameins.lecturerassignmentsystem.model.exception.InvalidLecturerException;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerHoldsCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;
import teameins.lecturerassignmentsystem.model.db.LecturerHoldsCourse;

import java.util.ArrayList;
import java.util.List;

@Service
public class LecturerService {
    private final LecturerRepository lecturerRepository;
    private final LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;
    private final LecturerHoldsCourseRepository lecturerHoldsCourseRepository;
    private final CourseRepository courseRepository;
    private final MappingService mappingService;

    public LecturerService(LecturerRepository lecturerRepository,
			LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository, 
            LecturerHoldsCourseRepository lecturerHoldsCourseRepository,
            CourseRepository courseRepository,
			MappingService mappingService) {
		super();
		this.lecturerRepository = lecturerRepository;
		this.lecturerCanHoldCourseRepository = lecturerCanHoldCourseRepository;
        this.lecturerHoldsCourseRepository = lecturerHoldsCourseRepository;
		this.courseRepository = courseRepository;
		this.mappingService = mappingService;
	}

	public LecturerDto getLecturerById(int lecturerId) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new LecturerNotFoundException("Es konnte kein Dozent mit der ID " + lecturerId + " gefunden werden."));
        List<LecturerCanHoldCourseDto> canHoldCourses = getCoursesLecturerCanHold(lecturerId);
        return mappingService.map(lecturer, canHoldCourses);
    }

    public List<LecturerDto> listLecturers() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<LecturerDto> lecturerDtos = new ArrayList<>();
        for (Lecturer lecturer : lecturers) {
            List<LecturerCanHoldCourseDto> canHold = getCoursesLecturerCanHold(lecturer.getId());
            lecturerDtos.add(mappingService.map(lecturer, canHold));
        }
        return lecturerDtos;
    }

    public LecturerDto createLecturer(LecturerDto lecturerDto) {
            if (!lecturerDto.validate()) {
                throw new InvalidLecturerException("Der Dozent ist ungültig.");
            }
        int id = lecturerRepository.save(mappingService.map(lecturerDto)).getId();
        return getLecturerById(id);
    }
    public LecturerDto updateLecturer(LecturerDto lecturerDto) {
            if (!lecturerDto.validate()) {
                throw new InvalidLecturerException("Der Dozent ist ungültig.");
            }
        lecturerRepository.findById(lecturerDto.getId())
                .orElseThrow(() -> new LecturerNotFoundException(
                        "Es konnte kein Dozent mit der ID " + lecturerDto.getId() + " gefunden werden."
                ));

        lecturerRepository.save(mappingService.map(lecturerDto));
        return getLecturerById(lecturerDto.getId());
    }

    public LecturerCanHoldCourseDto addCourseToLecturer(LecturerCanHoldCourseDto dto) {
        if (!dto.validate()) {
            throw new IllegalArgumentException("Die Beziehung ist ungültig.");
        }
        Lecturer lecturer = lecturerRepository.findById(dto.getLecturerId())
                .orElseThrow(() -> new LecturerNotFoundException(
                        "Es konnte kein Dozent mit der ID " + dto.getLecturerId() + " gefunden werden."
                ));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(
                        "Es konnte keine Vorlesung mit der ID " + dto.getCourseId() + " gefunden werden."
                ));

        if (lecturerCanHoldCourseRepository.existsByLecturerIdAndCourseId(
                dto.getLecturerId(), dto.getCourseId())) {
            throw new IllegalArgumentException(
                    "Die Beziehung zwischen Dozent (ID " + dto.getLecturerId()
                            + ") und Vorlesung (ID " + dto.getCourseId() + ") existiert bereits."
            );
        }

        TeachingPreference teachingPreference = lecturer.getTeachingPreference();
        if (teachingPreference == TeachingPreference.ONLY_MASTER && !course.isMaster()) {
            throw new IllegalArgumentException(
                    "Der Dozent hält nur Master-Vorlesungen, aber die Vorlesung ist eine Bachelor-Vorlesung."
            );
        }
        if (teachingPreference == TeachingPreference.ONLY_BACHELOR && course.isMaster()) {
            throw new IllegalArgumentException(
                    "Der Dozent hält nur Bachelor-Vorlesungen, aber die Vorlesung ist eine Master-Vorlesung."
            );
        }

        LecturerCanHoldCourse entity = mappingService.map(dto, lecturer, course);
        LecturerCanHoldCourse saved = lecturerCanHoldCourseRepository.save(entity);
        return mappingService.map(saved);
    }
    public void deleteLecturer(LecturerDto lecturer) {
        List<LecturerCanHoldCourseDto> canHoldCourses = lecturer.getCanHoldCourses();
        for (LecturerCanHoldCourseDto lchc : canHoldCourses) {
            lecturerCanHoldCourseRepository.deleteById(lchc.getId());
        }
        
        List<LecturerHoldsCourse> holdsCourses = lecturerHoldsCourseRepository.findByLecturerId(lecturer.getId());
        for (LecturerHoldsCourse lhc : holdsCourses) {
            lecturerHoldsCourseRepository.deleteById(lhc.getId());
        }

        lecturerRepository.deleteById(lecturer.getId());
    }

    private List<LecturerCanHoldCourseDto> getCoursesLecturerCanHold(int lecturerId) {
        List<LecturerCanHoldCourse> coursesLecturerCanHold = lecturerRepository.findCoursesLecturerCanHold(lecturerId);
        List<LecturerCanHoldCourseDto> lecturerCanHoldCourseDtoList = new ArrayList<>();
        for (LecturerCanHoldCourse lchc : coursesLecturerCanHold) {
            lecturerCanHoldCourseDtoList.add(mappingService.map(lchc));
        }
        return lecturerCanHoldCourseDtoList;
    }
}
