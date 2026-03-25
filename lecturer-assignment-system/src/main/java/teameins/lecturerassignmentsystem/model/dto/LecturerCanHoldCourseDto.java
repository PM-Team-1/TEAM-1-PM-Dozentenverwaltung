package teameins.lecturerassignmentsystem.model.dto;

import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

import java.util.Arrays;
import java.util.Objects;

public class LecturerCanHoldCourseDto {
    private int id;
    private int lecturerId;
    private int courseId;
    private String alreadyHeld;
    private String qualification;
    private Boolean priority;
    
    public LecturerCanHoldCourseDto() {
    	
    }

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

	public Boolean getPriority() {
		return priority;
	}

	public void setPriority(Boolean priority) {
		this.priority = priority;
	}

	public String getAlreadyHeld() {
		return alreadyHeld;
	}

	public String getQualification() {
		return qualification;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alreadyHeld, courseId, id, lecturerId, priority, qualification);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LecturerCanHoldCourseDto)) {
			return false;
		}
		LecturerCanHoldCourseDto other = (LecturerCanHoldCourseDto) obj;
		return Objects.equals(alreadyHeld, other.alreadyHeld) && courseId == other.courseId && id == other.id
				&& lecturerId == other.lecturerId && Objects.equals(priority, other.priority)
				&& Objects.equals(qualification, other.qualification);
	}
	
}
