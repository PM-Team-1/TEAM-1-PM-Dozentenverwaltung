package teameins.lecturerassignmentsystem.views.model;

import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.service.CourseService;

public class CourseToLecturerRelation {

    private final CourseService courseService;
    private final LecturerCanHoldCourseDto lecturerCanHoldCourse;
    private final CourseDto course;

    public CourseToLecturerRelation(LecturerCanHoldCourseDto lecturerCanHoldCourse, CourseService courseService) {
        this.courseService = courseService;
        this.lecturerCanHoldCourse = lecturerCanHoldCourse;
        this.course = courseService.getCourseById(lecturerCanHoldCourse.getCourseId());
    }
    public String getSemesterSortable() {
        String semester = this.getCourse().getSemester();
        if (semester == null) {
            return "";
        }

        String yearPart = semester.replaceAll("\\D", "");
        if (yearPart.contains("/")) {
            yearPart = yearPart.split("/")[0];
        }
        String termPart = semester.toLowerCase().contains("winter") ? "1" : "2";
        return yearPart + termPart;
    }

	public CourseService getCourseService() {
		return courseService;
	}

	public LecturerCanHoldCourseDto getLecturerCanHoldCourse() {
		return lecturerCanHoldCourse;
	}

	public CourseDto getCourse() {
		return course;
	}
}
