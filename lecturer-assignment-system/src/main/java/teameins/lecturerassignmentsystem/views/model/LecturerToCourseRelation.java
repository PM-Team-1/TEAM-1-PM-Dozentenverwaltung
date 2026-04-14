package teameins.lecturerassignmentsystem.views.model;

import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.service.LecturerService;

public class LecturerToCourseRelation {

    private final LecturerCanHoldCourseDto lecturerCanHoldCourse;
    private final LecturerDto lecturer;

    public LecturerToCourseRelation(LecturerCanHoldCourseDto lecturerCanHoldCourse, LecturerService lecturerService) {
        this.lecturerCanHoldCourse = lecturerCanHoldCourse;
        this.lecturer = lecturerService.getLecturerById(lecturerCanHoldCourse.getLecturerId());
    }

	public LecturerCanHoldCourseDto getLecturerCanHoldCourse() {
		return lecturerCanHoldCourse;
	}

	public LecturerDto getLecturer() {
		return lecturer;
	}

	public double getPreferenceScore(boolean isMaster){
		int affinityScore = Affinity.mapAffinityScore(lecturerCanHoldCourse.getAffinity());
		String degree = isMaster ? "M" : "B";
		double degreeScore;
		if (lecturer.getTeachingPreference().equals("A")) {
			degreeScore = 0.0;
		} else if (lecturer.getTeachingPreference().contains(degree)) {
			degreeScore = 0.5;
		} else {
			degreeScore = -0.5;
		}
		return affinityScore + degreeScore;
	}
}
