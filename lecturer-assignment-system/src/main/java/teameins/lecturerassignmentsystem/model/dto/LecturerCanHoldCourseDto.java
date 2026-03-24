package teameins.lecturerassignmentsystem.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class LecturerCanHoldCourseDto {
    private int id;
    private int lecturerId;
    private int courseId;
    private String alreadyHeld;
    private String qualification;
    private Boolean priority;

    public LecturerCanHoldCourseDto(int id, int lecturerId, int courseId, String alreadyHeld, String qualification, Boolean priority) throws IllegalArgumentException {
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

    public void setQualification(String qualification) throws IllegalArgumentException{
        if (validateQualification(qualification)) {
            this.qualification = qualification;
        } else {
            throw new IllegalArgumentException("Die Qualifikation ist ungültig: " + qualification + ". Gültige Werte sind: " + Arrays.toString(Qualification.getValidValues()));
        }
    }

    public static boolean validateAlreadyHeld(String alreadyHeld) {
        return AlreadyHeld.validate(alreadyHeld);
    }

    public void setAlreadyHeld(String alreadyHeld) throws IllegalArgumentException {
        if (validateAlreadyHeld(alreadyHeld)) {
            this.alreadyHeld = alreadyHeld;
        } else {
            throw new IllegalArgumentException("Die Angabe, ob die Vorlesung bereits gehalten wurde, ist ungültig: " + alreadyHeld + ". Gültige Werte sind: " + Arrays.toString(AlreadyHeld.getValidValues()));
        }
    }
}
