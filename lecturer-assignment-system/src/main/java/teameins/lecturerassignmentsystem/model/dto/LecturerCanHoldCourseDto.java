package teameins.lecturerassignmentsystem.model.dto;

import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;
import java.util.Objects;

public class LecturerCanHoldCourseDto {
    private int id;
    private int lecturerId;
    private int courseId;
    private String alreadyHeld;
    private String qualification;
    private String affinity;
    
    public LecturerCanHoldCourseDto() {
    	
    }

    public LecturerCanHoldCourseDto(int id, int lecturerId, int courseId, String alreadyHeld, String qualification, String affinity) throws IllegalArgumentException {
        setId(id);
        setLecturerId(lecturerId);
        setCourseId(courseId);
        setAlreadyHeld(alreadyHeld);
        setQualification(qualification);
        setAffinity(affinity);
    }

    public static boolean validate(LecturerCanHoldCourseDto lecturerCanHoldCourse) {
		return validateAlreadyHeld(lecturerCanHoldCourse.getAlreadyHeld()).isEmpty() &&
			   validateQualification(lecturerCanHoldCourse.getQualification()).isEmpty() &&
		 validateAffinity(lecturerCanHoldCourse.getAffinity()).isEmpty();
    }

    public boolean validate(){
        return validate(this);
    }

	public static String validateQualification(String qualification) {
		if (qualification == null || qualification.isBlank()) {
			return "Die Qualifikation darf nicht leer sein.";
		}
		if (!Qualification.validate(qualification)) {
			return "Die Qualifikation ist ungültig.";
		}
		return "";
    }

    public void setQualification(String qualification) throws IllegalArgumentException{
		this.qualification = qualification;
    }

  public static String validateAlreadyHeld(String alreadyHeld) {
	if (alreadyHeld == null || alreadyHeld.isBlank()) {
	  return "Die Angabe, ob die Vorlesung bereits gehalten wurde, darf nicht leer sein.";
	}
	if (!AlreadyHeld.validate(alreadyHeld)) {
	  return "Die Angabe, ob die Vorlesung bereits gehalten wurde, ist ungültig.";
	}
	return "";
	}

    public void setAlreadyHeld(String alreadyHeld) throws IllegalArgumentException {
		this.alreadyHeld = alreadyHeld;
    }

  public static String validateAffinity(String affinity) {
	if (affinity == null || affinity.isBlank()) {
	  return "Die Affinity darf nicht leer sein.";
	}
	if (!Affinity.validate(affinity)) {
	  return "Die Affinity ist ungültig.";
	}
	return "";
	}


	public void setAffinity(String affinity) {
		this.affinity = affinity;
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

	public String getAlreadyHeld() {
		return alreadyHeld;
	}

	public String getQualification() {
		return qualification;
	}

	public String getAffinity() {
		return affinity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alreadyHeld, courseId, id, lecturerId, affinity, qualification);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LecturerCanHoldCourseDto other)) {
			return false;
		}
        return Objects.equals(alreadyHeld, other.alreadyHeld) && courseId == other.courseId && id == other.id
				&& lecturerId == other.lecturerId && Objects.equals(affinity, other.affinity)
				&& Objects.equals(qualification, other.qualification);
	}
	
}
