package teameins.lecturerassignmentsystem.model.db;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String firstName;
    private String lastName;
    private String secondName;
    private String email;
    private String phone;
    private boolean isExtern;
    private Preference preference;

    @OneToMany(mappedBy = "lecturer")
    private List<LecturerCanHoldCourse> canHoldCourses;

    public Lecturer() {
    }

    public Lecturer(String title, String firstName, String lastName, String email, String phone) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondName = null;
        this.email = email;
        this.phone = phone;
        this.isExtern = false;
        this.preference = Preference.ALLES;
    }

    public Lecturer(int id, String title, String firstName, String lastName, String secondName, String email, String phone, boolean isExtern, Preference preference, List<LecturerCanHoldCourse> canHoldCourses) {
        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondName = secondName;
        this.email = email;
        this.phone = phone;
        this.isExtern = isExtern;
        this.preference = preference;
        this.canHoldCourses = canHoldCourses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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

    public List<LecturerCanHoldCourse> getCanHoldCourses() {
        return canHoldCourses;
    }

    public void setCanHoldCourses(List<LecturerCanHoldCourse> canHoldCourses) {
        this.canHoldCourses = canHoldCourses;
    }
}
