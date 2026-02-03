package teameins.lecturerassignmentsystem.model.db;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private boolean isClosed;
    private boolean isMaster;
    private String semester;

    @OneToMany(mappedBy = "course")
    private List<LecturerCanHoldCourse> canBeHeldBy;

    public Course() {
    }

    public Course(String name, boolean isMaster, String semester) {
        this.name = name;
        this.isClosed = false;
        this.isMaster = isMaster;
        this.semester = semester;
    }

    public Course(int id, String name, boolean isClosed, boolean isMaster, String semester, List<LecturerCanHoldCourse> canBeHeldBy) {
        this.id = id;
        this.name = name;
        this.isClosed = isClosed;
        this.isMaster = isMaster;
        this.semester = semester;
        this.canBeHeldBy = canBeHeldBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public List<LecturerCanHoldCourse> getCanBeHeldBy() {
        return canBeHeldBy;
    }

    public void setCanBeHeldBy(List<LecturerCanHoldCourse> canBeHeldBy) {
        this.canBeHeldBy = canBeHeldBy;
    }
}
