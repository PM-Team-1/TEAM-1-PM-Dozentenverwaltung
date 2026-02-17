package teameins.lecturerassignmentsystem.model.dto;

import lombok.AllArgsConstructor;
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

    public CourseDto(int id, String name, boolean isClosed, boolean isMaster, String semester, List<LecturerCanHoldCourseDto> canBeHeldBy) {
        setId(id);
        setName(name);
        setClosed(isClosed);
        setMaster(isMaster);
        setSemester(semester);
        setCanBeHeldBy(canBeHeldBy);
    }

    public static boolean validate(CourseDto course) {
        return validateName(course.getName()) &&
               validateSemester(course.getSemester());
    }

    public boolean validate(){
        return validate(this);
    }

    public static boolean validateName(String name) {
        return name != null && !name.isEmpty();
    }

    public void setName(String name) {
        if (validateName(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Invalid course name: " + name);
        }
    }

    public static boolean validateSemester(String semester) {
        return semester != null && !semester.isEmpty();
    }

    public void setSemester(String semester) {
        if (validateSemester(semester)) {
            this.semester = semester;
        } else {
            throw new IllegalArgumentException("Invalid semester: " + semester);
        }
    }
}
