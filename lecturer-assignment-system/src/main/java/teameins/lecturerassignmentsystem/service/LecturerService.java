package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturerService {
    private final LecturerRepository lecturerRepository;
    private final LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;
    private final CourseRepository courseRepository;
    private final MappingService mappingService;

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
        int id = lecturerRepository.save(mappingService.map(lecturerDto)).getId();
        return getLecturerById(id);
    }
    public LecturerDto updateLecturer(LecturerDto lecturerDto) {
        lecturerRepository.findById(lecturerDto.getId())
                .orElseThrow(() -> new LecturerNotFoundException(
                        "Es konnte kein Dozent mit der ID " + lecturerDto.getId() + " gefunden werden."
                ));

        lecturerRepository.save(mappingService.map(lecturerDto));
        return getLecturerById(lecturerDto.getId());
    }

    public LecturerCanHoldCourseDto addCourseToLecturer(LecturerCanHoldCourseDto dto) {
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

        Preference preference = lecturer.getPreference();
        if (preference == Preference.ONLY_MASTER && !course.isMaster()) {
            throw new IllegalArgumentException(
                    "Der Dozent hält nur Master-Vorlesungen, aber die Vorlesung ist eine Bachelor-Vorlesung."
            );
        }
        if (preference == Preference.ONLY_BACHELOR && course.isMaster()) {
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
