package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturerService {
    private final LecturerRepository lecturerRepository;
    private final LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;
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
    }abcder

    public void deleteLecturer(LecturerDto lecturer) {
        List<LecturerCanHoldCourseDto> canHoldCourses = lecturer.getCanHoldCourses();
        for (LecturerCanHoldCourseDto lchc: canHoldCourses) {
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
