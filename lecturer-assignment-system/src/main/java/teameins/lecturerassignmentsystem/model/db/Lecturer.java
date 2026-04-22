package teameins.lecturerassignmentsystem.model.db;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.model.enums.Title;

import java.util.Objects;


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
    private TeachingPreference teachingPreference;

    public Lecturer() {

    }

    public Lecturer(int id, Title title, String firstName, String lastName, @Nullable String secondName, String email,
                    String phone, boolean isExtern, TeachingPreference teachingPreference) {
		super();
		this.id = id;
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.secondName = secondName;
		this.email = email;
		this.phone = phone;
		this.isExtern = isExtern;
		this.teachingPreference = teachingPreference;
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

	public @Nullable String getSecondName() {
		return secondName;
	}

	public void setSecondName(@Nullable String secondName) {
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

	public TeachingPreference getTeachingPreference() {
		return teachingPreference;
	}

	public void setTeachingPreference(TeachingPreference teachingPreference) {
		this.teachingPreference = teachingPreference;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Lecturer lecturer = (Lecturer) o;
		return getId() == lecturer.getId() && isExtern() == lecturer.isExtern() && getTitle() == lecturer.getTitle() && Objects.equals(getFirstName(), lecturer.getFirstName()) && Objects.equals(getLastName(), lecturer.getLastName()) && Objects.equals(getSecondName(), lecturer.getSecondName()) && Objects.equals(getEmail(), lecturer.getEmail()) && Objects.equals(getPhone(), lecturer.getPhone()) && getTeachingPreference() == lecturer.getTeachingPreference();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getTitle(), getFirstName(), getLastName(), getSecondName(), getEmail(), getPhone(), isExtern(), getTeachingPreference());
	}
}
