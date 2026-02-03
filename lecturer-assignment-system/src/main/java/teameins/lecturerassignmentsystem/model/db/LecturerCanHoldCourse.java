package teameins.lecturerassignmentsystem.model.db;

import jakarta.persistence.*;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.service.CourseService;

@Entity
public class LecturerCanHoldCourse {
    @Transient
    CourseRepository courseRepository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "lecturer_fk", nullable = false)
    private Lecturer lecturer;

    @ManyToOne
    @JoinColumn(name = "course_fk", nullable = false)
    private Course course;

    private AlreadyHeld alreadyHeld;
    private Qualification qualification;
    private boolean priority;

    public LecturerCanHoldCourse() {
    }

    public LecturerCanHoldCourse(LecturerCanHoldCourseDto dto, Lecturer lecturer, CourseService courseService) throws EntityNotFoundException{
        this.lecturer = lecturer;
        this.course = courseService.getCourseById(dto.getCourse().getId());
        this.alreadyHeld = dto.getAlreadyHeld();
        this.qualification = dto.getQualification();
        this.priority = dto.isPriority();
    }

    public LecturerCanHoldCourse(LecturerCanHoldCourseDto dto, Lecturer lecturer, Course course) {
        this.lecturer = lecturer;
        this.course = course;
        this.alreadyHeld = dto.getAlreadyHeld();
        this.qualification = dto.getQualification();
        this.priority = dto.isPriority();
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
