package teameins.lecturerassignmentsystem.model.dto;

import teameins.lecturerassignmentsystem.model.enums.Preference;

import java.util.List;

public class LecturerDto {

    private Integer id;
    private String title;
    private String firstName;
    private String lastName;
    private String secondName;
    private String email;
    private String phone;
    private boolean isExtern;
    private Preference preference;
    private List<LecturerCanHoldCourseDto> canHoldCourses;

    public LecturerDto() {
    }

    public LecturerDto(Integer id, String title, String firstName, String lastName, String secondName, String email, String phone, boolean isExtern, Preference preference, List<LecturerCanHoldCourseDto> canHoldCourses) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public void setExtern(boolean extern) {
        isExtern = extern;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public List<LecturerCanHoldCourseDto> getCanHoldCourses() {
        return canHoldCourses;
    }

    public void setCanHoldCourses(List<LecturerCanHoldCourseDto> canHoldCourses) {
        this.canHoldCourses = canHoldCourses;
    }
}
