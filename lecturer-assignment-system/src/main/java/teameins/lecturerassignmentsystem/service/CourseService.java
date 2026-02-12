package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.exception.InvalidCourseException;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final MappingService mappingService;
    private final LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;

    public CourseDto getCourseById(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(RuntimeException::new);
        List<LecturerCanHoldCourseDto> canBeHeldBy = getLecturersWhoCanHoldCourse(courseId);
        return mappingService.map(course, canBeHeldBy);
    }

    public List<CourseDto> listCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDto> courseDtos = new ArrayList<>();
        for (Course course : courses) {
            List<LecturerCanHoldCourseDto> canBeHeldBy = getLecturersWhoCanHoldCourse(course.getId());
            courseDtos.add(mappingService.map(course, canBeHeldBy));
        }
        return courseDtos;
    }

    public CourseDto createCourse(CourseDto courseDto) {
        String validationResult = validateCourse(courseDto);
        if (validationResult != null) {
            throw new InvalidCourseException(validationResult);
        }
        int id = courseRepository.save(mappingService.map(courseDto)).getId();
        return getCourseById(id);
    }

    public CourseDto updateCourse(CourseDto courseDto) {
        String validationResult = validateCourse(courseDto);
        if (validationResult != null) {
            throw new InvalidCourseException(validationResult);
        }
        courseRepository.save(mappingService.map(courseDto));
        return getCourseById(courseDto.getId());
    }

    public void deleteCourseById(CourseDto courseDto) {
        for (LecturerCanHoldCourseDto lchc : courseDto.getCanBeHeldBy()) {
            lecturerCanHoldCourseRepository.deleteById(lchc.getId());
        }
        courseRepository.deleteById(courseDto.getId());

    }

    private List<LecturerCanHoldCourseDto> getLecturersWhoCanHoldCourse(int courseId) {
        List<LecturerCanHoldCourse> lecturersWhoCanHoldCourse = courseRepository.findLecturersWhoCanHoldCourse(courseId);
        List<LecturerCanHoldCourseDto> courseCanBeHeldyByLecturerDtoList = new ArrayList<>();
        for (LecturerCanHoldCourse lchc : lecturersWhoCanHoldCourse) {
            courseCanBeHeldyByLecturerDtoList.add(mappingService.map(lchc));
        }
        return courseCanBeHeldyByLecturerDtoList;
    }

    private String validateCourse(CourseDto courseDto) {
        //TODO: Validation eines Kurses implementieren, wenn der Kurs invalide ist, soll der Grund zurückgegeben werden, sonst null
        return null;
    }
}
