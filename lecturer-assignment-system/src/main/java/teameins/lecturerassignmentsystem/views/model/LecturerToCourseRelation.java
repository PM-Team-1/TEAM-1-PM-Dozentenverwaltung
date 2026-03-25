package teameins.lecturerassignmentsystem.views.model;

import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.service.LecturerService;

public class LecturerToCourseRelation {

    private final LecturerService lecturerService;
    private final LecturerCanHoldCourseDto lecturerCanHoldCourse;
    private final LecturerDto lecturer;

    public LecturerToCourseRelation(LecturerCanHoldCourseDto lecturerCanHoldCourse, LecturerService lecturerService) {
        this.lecturerService = lecturerService;
        this.lecturerCanHoldCourse = lecturerCanHoldCourse;
        this.lecturer = lecturerService.getLecturerById(lecturerCanHoldCourse.getLecturerId());
    }

	public LecturerService getLecturerService() {
		return lecturerService;
	}

	public LecturerCanHoldCourseDto getLecturerCanHoldCourse() {
		return lecturerCanHoldCourse;
	}

	public LecturerDto getLecturer() {
		return lecturer;
	}
}
