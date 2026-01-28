package teameins.lecturerassignmentsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LecturerCanHoldCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private AlreadyHeld alreadyHeld;
    private Qualification qualification;
    private boolean priority;

    public LecturerCanHoldCourse() {
    }

    public LecturerCanHoldCourse(Lecturer lecturer, Course course, AlreadyHeld alreadyHeld, Qualification qualification, boolean priority) {
        this.lecturer = lecturer;
        this.course = course;
        this.alreadyHeld = alreadyHeld;
        this.qualification = qualification;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public AlreadyHeld isAlreadyHeld() {
        return alreadyHeld;
    }

    public void setAlreadyHeld(AlreadyHeld alreadyHeld) {
        this.alreadyHeld = alreadyHeld;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }

    public boolean isPriority(){
        return priority;
    }

    public void setPriority(boolean priority){
        this.priority = priority;
    }
}
