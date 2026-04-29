package teameins.lecturerassignmentsystem.views.model;

import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.service.CourseService;

public class CourseToLecturerRelation {

    private final LecturerCanHoldCourseDto lecturerCanHoldCourse;
    private final CourseDto course;

    public CourseToLecturerRelation(LecturerCanHoldCourseDto lecturerCanHoldCourse, CourseService courseService) {
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

    public double getPriorityScore(String teachingPreference){
        int affinityScore = Affinity.mapAffinityScore(lecturerCanHoldCourse.getAffinity());
        String degree = course.isMaster() ? "M" : "B";
        double degreeScore;
        if (teachingPreference.equals("A")) {
            degreeScore = 0.0;
        } else if (teachingPreference.contains(degree)) {
            degreeScore = 0.4;
        } else {
            degreeScore = -0.4;
        }
        return affinityScore + degreeScore;
    }

	public LecturerCanHoldCourseDto getLecturerCanHoldCourse() {
		return lecturerCanHoldCourse;
	}

	public CourseDto getCourse() {
		return course;
	}
}
