package teameins.lecturerassignmentsystem.views.model;


import lombok.Getter;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.service.CourseService;

@Getter
public class CourseToLecturerRelation {

    private final CourseService courseService;
    private final LecturerCanHoldCourseDto lecturerCanHoldCourse;
    private final CourseDto course;

    public CourseToLecturerRelation(LecturerCanHoldCourseDto lecturerCanHoldCourse, CourseService courseService) {
        this.courseService = courseService;
        this.lecturerCanHoldCourse = lecturerCanHoldCourse;
        this.course = courseService.getCourseById(lecturerCanHoldCourse.getCourseId());
    }
}
