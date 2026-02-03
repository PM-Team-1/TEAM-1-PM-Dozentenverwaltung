package teameins.lecturerassignmentsystem.model.dto;

import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

public class LecturerCanHoldCourseDto {
    private LecturerDto lecturer;
    private CourseDto course;
    private AlreadyHeld alreadyHeld;
    private Qualification qualification;
    private boolean priority;

    public LecturerCanHoldCourseDto() {
    }

    public LecturerCanHoldCourseDto(LecturerDto lecturer, CourseDto course, AlreadyHeld alreadyHeld, Qualification qualification, boolean priority) {
        this.lecturer = lecturer;
        this.course = course;
        this.alreadyHeld = alreadyHeld;
        this.qualification = qualification;
        this.priority = priority;
    }

    public LecturerDto getLecturer() {
        return lecturer;
    }

    public void setLecturer(LecturerDto lecturer) {
        this.lecturer = lecturer;
    }

    public CourseDto getCourse() {
        return course;
    }

    public void setCourse(CourseDto course) {
        this.course = course;
    }

    public AlreadyHeld getAlreadyHeld() {
        return alreadyHeld;
    }

    public void setAlreadyHeld(AlreadyHeld alreadyHeld) {
        this.alreadyHeld = alreadyHeld;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }
}
