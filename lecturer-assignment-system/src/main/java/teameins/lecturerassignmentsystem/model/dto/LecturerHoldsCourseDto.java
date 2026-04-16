package teameins.lecturerassignmentsystem.model.dto;

import java.util.Objects;

public class LecturerHoldsCourseDto {
    private int id;
    private int lecturerId;
    private int courseId;

    public LecturerHoldsCourseDto() {
    }

    public LecturerHoldsCourseDto(int id, int lecturerId, int courseId) {
        this.id = id;
        this.lecturerId = lecturerId;
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(int lecturerId) {
        this.lecturerId = lecturerId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lecturerId, courseId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LecturerHoldsCourseDto other)) {
            return false;
        }
        return this.id == other.id && this.courseId == other.courseId && this.lecturerId == other.lecturerId;
    }
}
