package teameins.lecturerassignmentsystem.model.db;

import jakarta.persistence.*;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	private String name;
    private boolean isClosed;
    private boolean isMaster;
    private String semester;
    
    public Course() {
    	
    }

    public Course(int id, String name, boolean isClosed, boolean isMaster, String semester) {
		super();
		this.id = id;
		this.name = name;
		this.isClosed = isClosed;
		this.isMaster = isMaster;
		this.semester = semester;
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
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
	public boolean isMaster() {
		return isMaster;
	}
	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}
	public String getSemester() {
		return semester;
	}
	public void setSemester(String semester) {
		this.semester = semester;
	}
    
}
