package teameins.lecturerassignmentsystem.model.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	private String name;
	private String grantedAuthority;

    public Role() {

    }

    public Role(int id, String name, String grantedAuthority) {
		super();
		this.id = id;
		this.name = name;
		this.grantedAuthority = grantedAuthority;
    }

	public int getId() {
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getGrantedAuthority(){
		return grantedAuthority;
	}

	public void setGrantedAuthority(String grantedAuthority){
		this.grantedAuthority = grantedAuthority;
	}
    
}
