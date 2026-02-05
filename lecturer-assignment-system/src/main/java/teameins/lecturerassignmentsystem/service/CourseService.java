package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final MappingService mappingService;

    public CourseDto getCourseById(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(RuntimeException::new);
        List<LecturerCanHoldCourseDto> canBeHeldBy = getLecturersWhoCanHoldCourse(courseId);
        return mappingService.map(course, canBeHeldBy);
    }

    private List<LecturerCanHoldCourseDto> getLecturersWhoCanHoldCourse(int courseId) {
        List<LecturerCanHoldCourse> lecturersWhoCanHoldCourse = courseRepository.findLecturersWhoCanHoldCourse(courseId);
        List<LecturerCanHoldCourseDto> courseCanBeHeldyByLecturerDtoList = new ArrayList<>();
        for (LecturerCanHoldCourse lchc : lecturersWhoCanHoldCourse) {
            courseCanBeHeldyByLecturerDtoList.add(mappingService.map(lchc));
        }
        return courseCanBeHeldyByLecturerDtoList;
    }
}
