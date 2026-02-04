package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MappingService {

    private final CourseService courseService;
    private final LecturerService lecturerService;

    public LecturerDto map(Lecturer lecturer) {
        List<LecturerCanHoldCourseDto> canHoldCourses = lecturerService.getCoursesLecturerCanHold(lecturer.getId());

        return new LecturerDto(
                lecturer.getId(),
                lecturer.getTitle().getValue(),
                lecturer.getFirstName(),
                lecturer.getLastName(),
                lecturer.getSecondName(),
                lecturer.getEmail(),
                lecturer.getPhone(),
                lecturer.isExtern(),
                lecturer.getPreference().getValue(),
                canHoldCourses
        );
    }

    public CourseDto map(Course course) {
        List<LecturerCanHoldCourseDto> canBeHeldBy = courseService.getLecturersWhoCanHoldCourse(course.getId());

        return new CourseDto(
                course.getId(),
                course.getName(),
                course.isClosed(),
                course.isMaster(),
                course.getSemester(),
                canBeHeldBy
        );
    }

    public LecturerCanHoldCourseDto map(LecturerCanHoldCourse lecturerCanHoldCourse, CourseDto course, LecturerDto lecturer) {
        return new LecturerCanHoldCourseDto(
                lecturer,
                course,
                lecturerCanHoldCourse.getAlreadyHeld(),
                lecturerCanHoldCourse.getQualification(),
                lecturerCanHoldCourse.isPriority()
        );
    }
}
