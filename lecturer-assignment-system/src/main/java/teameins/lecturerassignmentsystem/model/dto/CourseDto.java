package teameins.lecturerassignmentsystem.model.dto;

import java.util.List;

public class CourseDto {

    private Integer id;
    private String name;
    private boolean isClosed;
    private boolean isMaster;
    private String semester;
    private List<LecturerCanHoldCourseDto> canBeHeldBy;

    public CourseDto() {
    }

    public CourseDto(Integer id, String name, boolean isClosed, boolean isMaster, String semester, List<LecturerCanHoldCourseDto> canBeHeldBy) {
        this.id = id;
        this.name = name;
        this.isClosed = isClosed;
        this.isMaster = isMaster;
        this.semester = semester;
        this.canBeHeldBy = canBeHeldBy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<LecturerCanHoldCourseDto> getCanBeHeldBy() {
        return canBeHeldBy;
    }

    public void setCanBeHeldBy(List<LecturerCanHoldCourseDto> canBeHeldBy) {
        this.canBeHeldBy = canBeHeldBy;
    }
}
