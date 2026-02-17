package teameins.lecturerassignmentsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

@Getter
@Setter
@NoArgsConstructor
public class LecturerCanHoldCourseDto {
    private int id;
    private int lecturerId;
    private int courseId;
    private String alreadyHeld;
    private String qualification;
    private boolean priority;

    public LecturerCanHoldCourseDto(int id, int lecturerId, int courseId, String alreadyHeld, String qualification, boolean priority) {
        setId(id);
        setLecturerId(lecturerId);
        setCourseId(courseId);
        setAlreadyHeld(alreadyHeld);
        setQualification(qualification);
        setPriority(priority);
    }

    public static boolean validate(LecturerCanHoldCourseDto lecturerCanHoldCourse) {
        return validateAlreadyHeld(lecturerCanHoldCourse.getAlreadyHeld()) &&
               validateQualification(lecturerCanHoldCourse.getQualification());
    }

    public boolean validate(){
        return validate(this);
    }

    public static boolean validateQualification(String qualification) {
        return Qualification.validate(qualification);
    }

    public void setQualification(String qualification) {
        if (validateQualification(qualification)) {
            this.qualification = qualification;
        } else {
            throw new IllegalArgumentException("Invalid qualification: " + qualification);
        }
    }

    public static boolean validateAlreadyHeld(String alreadyHeld) {
        return AlreadyHeld.validate(alreadyHeld);
    }

    public void setAlreadyHeld(String alreadyHeld) {
        if (validateAlreadyHeld(alreadyHeld)) {
            this.alreadyHeld = alreadyHeld;
        } else {
            throw new IllegalArgumentException("Invalid already held value: " + alreadyHeld);
        }
    }
}
