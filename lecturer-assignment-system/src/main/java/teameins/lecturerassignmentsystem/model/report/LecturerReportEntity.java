package teameins.lecturerassignmentsystem.model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;

import java.util.List;

public class LecturerReportEntity {

    @JsonProperty("Titel")
    private String title;
    @JsonProperty("Vorname")
    private String firstName;
    @JsonProperty("Nachname")
    private String lastName;
    @JsonProperty("Zweitname")
    private String secondName;
    @JsonProperty("E-Mail Adresse")
    private String email;
    @JsonProperty("Telefonnummer")
    private String phone;
    @JsonProperty("Externer")
    private Boolean isExtern;
    @JsonProperty("Präferenz")
    private String preference;
    @JsonProperty("Kann Kurse halten")
    private List<CourseReportEntity> canHoldCourses;

    public LecturerReportEntity() {}

    public LecturerReportEntity(String title, String firstName, String lastName, String secondName, String email, String phone, Boolean isExtern, String preference, List<CourseReportEntity> canHoldCourses) {
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

    public Boolean isExtern() {
        return isExtern;
    }

    public void setExtern(Boolean extern) {
        isExtern = extern;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public List<CourseReportEntity> getCanHoldCourses() {
        return canHoldCourses;
    }

    public void setCanHoldCourses(List<CourseReportEntity> canHoldCourses) {
        this.canHoldCourses = canHoldCourses;
    }
}
