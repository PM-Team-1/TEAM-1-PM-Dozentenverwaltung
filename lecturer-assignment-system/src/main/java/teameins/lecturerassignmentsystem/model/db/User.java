package teameins.lecturerassignmentsystem.model.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	private String username;
    private String passwordHash;
    private boolean enabled;

    public User() {

    }

    public User(int id, String username, String passwordHash, boolean enabled) {
		super();
		this.id = id;
		this.username = username;
		this.passwordHash = passwordHash;
		this.enabled = enabled;
	}

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public void setPasswordHash(String passwordHash){
		this.passwordHash = passwordHash;
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
    
}
