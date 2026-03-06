package teameins.lecturerassignmentsystem.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CourseDto {

    private int id;
    private String name;
    private boolean isClosed;
    private boolean isMaster;
    private String semester;
    private List<LecturerCanHoldCourseDto> canBeHeldBy;

    public CourseDto(int id, String name, boolean isClosed, boolean isMaster, String semester, List<LecturerCanHoldCourseDto> canBeHeldBy) throws IllegalArgumentException {
        setId(id);
        setName(name);
        setClosed(isClosed);
        setMaster(isMaster);
        setSemester(semester);
        setCanBeHeldBy(canBeHeldBy);
    }

    public static boolean validate(CourseDto course) {
        return validateNameMessage(course.getName()).isEmpty() &&
               validateSemesterMessage(course.getSemester()).isEmpty();
    }

    public boolean validate(){
        return validate(this);
    }

    public static String validateNameMessage(String name) {
        if (name == null || name.isEmpty()) {
            return "Der Name der Vorlesung darf nicht leer sein.";
        }
        return "";
    }

    public void setName(String name) throws IllegalArgumentException {
        this.name = name;
    }

    public static String validateSemesterMessage(String semester) {
        if (semester == null || semester.isEmpty()) {
            return "Das Semester der Vorlesung darf nicht leer sein.";
        }
        if (!semester.matches("(WiSe \\d{2,4}/\\d{2,4}|SoSe \\d{2,4}).*")) {
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
}
