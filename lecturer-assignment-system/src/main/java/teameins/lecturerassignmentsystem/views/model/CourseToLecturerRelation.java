package teameins.lecturerassignmentsystem.views.model;

import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.repository.LecturerHoldsCourseRepository;
import teameins.lecturerassignmentsystem.service.CourseService;
import java.util.Objects;

public class CourseToLecturerRelation {

    private final LecturerCanHoldCourseDto lecturerCanHoldCourse;
    private final CourseDto course;
    private final LecturerHoldsCourseRepository lhcRepository;

    public CourseToLecturerRelation(LecturerCanHoldCourseDto lecturerCanHoldCourse, CourseService courseService, LecturerHoldsCourseRepository lhcRepository) {
        this.lecturerCanHoldCourse = lecturerCanHoldCourse;
        this.course = courseService.getCourseDtoById(lecturerCanHoldCourse.getCourseId());
        this.lhcRepository = lhcRepository;
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
	public String getLecturerName() {
		String lecturerName = lhcRepository
        		.findFirstNameByCourseId(course.getId())
                .orElse("")
                + " " + Objects.toString(lhcRepository
                .findSecondNameByCourseId(course.getId())
                .orElse(""), "")
                + " " + lhcRepository
                .findLastNameByCourseId(course.getId())
                .orElse("");
        lecturerName = lecturerName.strip();
        if (lecturerName.isBlank()) {
        	lecturerName = "nicht zugewiesen";
        }
		return lecturerName;
	}
	public int getLecturerId() {
		int lecturerId = lhcRepository.findLecturerIdByCourseId(course.getId())
				.orElse(-1);
		return lecturerId;
	}
}
