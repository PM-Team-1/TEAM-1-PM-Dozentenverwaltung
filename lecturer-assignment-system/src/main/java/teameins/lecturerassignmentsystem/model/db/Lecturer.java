package teameins.lecturerassignmentsystem.model.db;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.enums.Title;


@Entity
public class Lecturer {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private Title title;

    private String firstName;
    private String lastName;

    @Nullable
    private String secondName;
    private String email;
    private String phone;
    private boolean isExtern;

    @Enumerated(EnumType.STRING)
    private Preference preference;
    
    public Lecturer() {
    	
    }
    
    public Lecturer(int id, Title title, String firstName, String lastName, String secondName, String email,
			String phone, boolean isExtern, Preference preference) {
		super();
		this.id = id;
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.secondName = secondName;
		this.email = email;
		this.phone = phone;
		this.isExtern = isExtern;
		this.preference = preference;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isExtern() {
		return isExtern;
	}

	public void setExtern(boolean isExtern) {
		this.isExtern = isExtern;
	}

	public Preference getPreference() {
		return preference;
	}

	public void setPreference(Preference preference) {
		this.preference = preference;
	}
}
