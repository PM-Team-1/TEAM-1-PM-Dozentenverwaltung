package teameins.lecturerassignmentsystem.model.db;

import jakarta.persistence.*;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
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

	@Enumerated(EnumType.STRING)
    private Affinity affinity;
    
    public LecturerCanHoldCourse() {
    	
    }
    
	public LecturerCanHoldCourse(int id, AlreadyHeld alreadyHeld, Qualification qualification, Course course,
			Lecturer lecturer, Affinity affinity) {
		super();
		this.id = id;
		this.alreadyHeld = alreadyHeld;
		this.qualification = qualification;
		this.course = course;
		this.lecturer = lecturer;
		this.affinity = affinity;
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

	public Affinity getAffinity() {
		return affinity;
	}

	public void setAffinity(Affinity affinity) {
		this.affinity = affinity;
	}
}
