package teameins.lecturerassignmentsystem.model.dto;

import teameins.lecturerassignmentsystem.model.dto.relation.LecturerCanHoldCourseDto;
import lombok.ToString;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import lombok.ToString;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.model.enums.Title;

import java.util.List;
import java.util.Objects;

@ToString
public class LecturerDto {

    private int id;
    private String title;
    private String firstName;
    private String lastName;
    private String secondName;
    private String email;
    private String phone;
    private boolean isExtern;
    private String teachingPreference;
    private List<LecturerCanHoldCourseDto> canHoldCourses;
    
    public LecturerDto() {
    	
    }

    public LecturerDto(int id, String title, String firstName, String lastName, String secondName, String email, String phone, boolean isExtern, String teachingPreference, List<LecturerCanHoldCourseDto> canHoldCourses) throws IllegalArgumentException {
        setId(id);
        setTitle(title);
        setFirstName(firstName);
        setLastName(lastName);
        setSecondName(secondName);
        setEmail(email);
        setPhone(phone);
        setExtern(isExtern);
        setTeachingPreference(teachingPreference);
        setCanHoldCourses(canHoldCourses);
    }

    public static boolean validate(LecturerDto lecturer) {
        return validateTitle(lecturer.getTitle()).isEmpty() &&
               validateFirstName(lecturer.getFirstName()).isEmpty() &&
               validateLastName(lecturer.getLastName()).isEmpty() &&
               validateEmail(lecturer.getEmail()).isEmpty() &&
               validatePhone(lecturer.getPhone()).isEmpty() &&
               validateTeachingPreference(lecturer.getTeachingPreference()).isEmpty();
    }

    public boolean validate(){
        return validate(this);
    }

    public String getFullName() {
        String titlePart = this.getTitle() == null ? "" : this.getTitle() + " ";
        String secondNamePart = this.getSecondName() == null ? "" : this.getSecondName() + " ";
        return titlePart + this.getFirstName() + " " + secondNamePart + this.getLastName();
    }

    public static String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            return "Der Titel darf nicht leer sein.";
        }
        if (!Title.validate(title)) {
            return "Der Titel ist ungültig.";
        }
        return "";
    }

    public void setTitle(String title) throws IllegalArgumentException {
        this.title = title;
    }

    public static String validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            return "Der Vorname darf nicht leer sein.";
        }
        return "";
    }

    public void setFirstName(String firstName) throws IllegalArgumentException {
        this.firstName = firstName;
    }

    public static String validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return "Der Nachname darf nicht leer sein.";
        }
        return "";
    }

    public void setLastName(String lastName) throws IllegalArgumentException {
        this.lastName = lastName;
    }

    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return "Die E-Mail Adresse darf nicht leer sein.";
        }
        if (!email.contains("@")) {
            return "Die E-Mail Adresse muss ein @ enthalten.";
        }
        return "";
    }

    public void setEmail(String email) throws IllegalArgumentException {
        this.email = email;
    }

    public static String validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return "Die Telefonnummer darf nicht leer sein.";
        }
        if (!phone.matches("\\+?\\d(?:[\\s\\-/]?\\d){5,14}")) {
            return "Die Telefonnummer darf nur Ziffern und optional ein führendes + enthalten.";
        }
        return "";
    }

    public void setPhone(String phone) throws IllegalArgumentException {
        this.phone = phone;
    }

    public static String validateTeachingPreference(String teachingPreference) {
        if (teachingPreference == null || teachingPreference.isBlank()) {
            return "Die Lehrpräferenz darf nicht leer sein.";
        }
        if (!TeachingPreference.validate(teachingPreference)) {
            return "Die Lehrpräferenz ist ungültig.";
        }
        return "";
    }

    public void setTeachingPreference(String teachingPreference) throws IllegalArgumentException {
        this.teachingPreference = teachingPreference;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public boolean isExtern() {
		return isExtern;
	}

	public void setExtern(boolean isExtern) {
		this.isExtern = isExtern;
	}

	public List<LecturerCanHoldCourseDto> getCanHoldCourses() {
		return canHoldCourses;
	}

	public void setCanHoldCourses(List<LecturerCanHoldCourseDto> canHoldCourses) {
		this.canHoldCourses = canHoldCourses;
	}

	public String getTitle() {
		return title;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getTeachingPreference() {
		return teachingPreference;
	}

	@Override
	public int hashCode() {
		return Objects.hash(canHoldCourses, email, firstName, id, isExtern, lastName, phone, teachingPreference, secondName,
				title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LecturerDto other)) {
			return false;
		}
        return Objects.equals(canHoldCourses, other.canHoldCourses) && Objects.equals(email, other.email)
				&& Objects.equals(firstName, other.firstName) && id == other.id && isExtern == other.isExtern
				&& Objects.equals(lastName, other.lastName) && Objects.equals(phone, other.phone)
				&& Objects.equals(teachingPreference, other.teachingPreference) && Objects.equals(secondName, other.secondName)
				&& Objects.equals(title, other.title);
	}
	
}
