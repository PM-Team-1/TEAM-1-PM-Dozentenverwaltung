package teameins.lecturerassignmentsystem.model.db;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

@Entity
public class LecturerCanHoldCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private AlreadyHeld alreadyHeld;

    @Enumerated(EnumType.STRING)
    private Qualification qualification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;

    @Nullable
    private Boolean priority;
    
    public LecturerCanHoldCourse() {
    	
    }
    
	public LecturerCanHoldCourse(int id, AlreadyHeld alreadyHeld, Qualification qualification, Course course,
			Lecturer lecturer, Boolean priority) {
		super();
		this.id = id;
		this.alreadyHeld = alreadyHeld;
		this.qualification = qualification;
		this.course = course;
		this.lecturer = lecturer;
		this.priority = priority;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AlreadyHeld getAlreadyHeld() {
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

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Lecturer getLecturer() {
		return lecturer;
	}

	public void setLecturer(Lecturer lecturer) {
		this.lecturer = lecturer;
	}

	public Boolean getPriority() {
		return priority;
	}

	public void setPriority(Boolean priority) {
		this.priority = priority;
	}
}
