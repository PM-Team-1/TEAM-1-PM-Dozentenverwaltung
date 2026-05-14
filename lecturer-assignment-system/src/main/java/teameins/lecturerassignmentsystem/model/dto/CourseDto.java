package teameins.lecturerassignmentsystem.model.dto;

import teameins.lecturerassignmentsystem.model.dto.relation.LecturerCanHoldCourseDto;

import lombok.ToString;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerHoldsCourseDto;

import java.util.List;
import java.util.Objects;

@ToString
public class CourseDto {

    private int id;
    private String name;
    private boolean isClosed;
    private boolean isMaster;
    private String semester;
    private List<LecturerCanHoldCourseDto> canBeHeldBy;
    private LecturerHoldsCourseDto heldBy;
    
    public CourseDto() {
    	
    }

    public CourseDto(int id, String name, boolean isClosed, boolean isMaster, String semester, List<LecturerCanHoldCourseDto> canBeHeldBy, LecturerHoldsCourseDto heldBy) throws IllegalArgumentException {
        setId(id);
        setName(name);
        setClosed(isClosed);
        setMaster(isMaster);
        setSemester(semester);
        setCanBeHeldBy(canBeHeldBy);
        setHeldBy(heldBy);
    }

    public static boolean validate(CourseDto course) {
        return validateName(course.getName()).isEmpty() &&
               validateSemester(course.getSemester()).isEmpty();
    }

    public boolean validate(){
        return validate(this);
    }

    public static String validateName(String name) {
        if (name == null || name.isBlank()) {
            return "Der Name der Vorlesung darf nicht leer sein.";
        }
        return "";
    }

    public void setName(String name) throws IllegalArgumentException {
        this.name = name;
    }

    public static String validateSemester(String semester) {
        if (semester == null || semester.isBlank()) {
            return "Das Semester der Vorlesung darf nicht leer sein.";
        }
        if (!semester.strip().matches("(WiSe \\d{2,4}/\\d{2,4}|SoSe \\d{2,4})")) {
            return "Das Semester muss im erwarteten Format sein (z. B. WiSe 25/26 oder SoSe 27).";
        }
        return "";
    }

    public void setSemester(String semester) throws IllegalArgumentException {
        this.semester = semester;
    }

    public String getSemesterSortable() {
        if (semester == null || semester.isEmpty()) {
            return "";
        }
        String[] parts = semester.split(" ");
        if (parts.length != 2) {
            return semester;
        }
        String term = parts[0];
        String yearPart = parts[1];
        return yearPart + " " + (term.contains("SoSe") ? 1 : 2);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public List<LecturerCanHoldCourseDto> getCanBeHeldBy() {
		return canBeHeldBy;
	}

	public void setCanBeHeldBy(List<LecturerCanHoldCourseDto> canBeHeldBy) {
		this.canBeHeldBy = canBeHeldBy;
	}

    public LecturerHoldsCourseDto getHeldBy() {
        return heldBy;
    }

    public void setHeldBy(LecturerHoldsCourseDto heldBy) {
        this.heldBy = heldBy;
    }

    public String getName() {
		return name;
	}

	public String getSemester() {
		return semester;
	}

	@Override
	public int hashCode() {
		return Objects.hash(canBeHeldBy, id, isClosed, isMaster, name, semester);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CourseDto other)) {
			return false;
		}
        return Objects.equals(canBeHeldBy, other.canBeHeldBy) && id == other.id && isClosed == other.isClosed
				&& isMaster == other.isMaster && Objects.equals(name, other.name)
				&& Objects.equals(semester, other.semester);
	}
	
}
