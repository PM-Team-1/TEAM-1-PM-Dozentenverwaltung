package teameins.lecturerassignmentsystem.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.enums.Title;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LecturerDto {

    private int id;
    private String title;
    private String firstName;
    private String lastName;
    private String secondName;
    private String email;
    private String phone;
    private boolean isExtern;
    private String preference;
    private List<LecturerCanHoldCourseDto> canHoldCourses;

    public LecturerDto(int id, String title, String firstName, String lastName, String secondName, String email, String phone, boolean isExtern, String preference, List<LecturerCanHoldCourseDto> canHoldCourses) {
        setId(id);
        setTitle(title);
        setFirstName(firstName);
        setLastName(lastName);
        setSecondName(secondName);
        setEmail(email);
        setPhone(phone);
        setExtern(isExtern);
        setPreference(preference);
        setCanHoldCourses(canHoldCourses);
    }

    public static boolean validate(LecturerDto lecturer) {
        return validateTitle(lecturer.getTitle()) &&
               validateFirstName(lecturer.getFirstName()) &&
               validateLastName(lecturer.getLastName()) &&
               validateSecondName(lecturer.getSecondName()) &&
               validateEmail(lecturer.getEmail()) &&
               validatePhone(lecturer.getPhone()) &&
               validatePreference(lecturer.getPreference());
    }

    public boolean validate(){
        return validate(this);
    }

    public String getFullName() {
        String titlePart = this.getTitle() == null ? "" : this.getTitle() + " ";
        String secondNamePart = this.getSecondName() == null ? "" : this.getSecondName() + " ";
        return titlePart + this.getFirstName() + " " + secondNamePart + this.getLastName();
    }

    public static boolean validateTitle(String title) {
        return Title.validate(title);
    }

    public void setTitle(String title) {
        if (validateTitle(title)) {
            this.title = title;
        } else {
            throw new IllegalArgumentException("Invalid title: " + title);
        }
    }

    public static boolean validateFirstName(String firstName) {
        return firstName != null && !firstName.isEmpty();
    }

    public void setFirstName(String firstName) {
        if (validateFirstName(firstName)) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("Invalid first name: " + firstName);
        }
    }

    public static boolean validateLastName(String lastName) {
        return lastName != null && !lastName.isEmpty();
    }

    public void setLastName(String lastName) {
        if (validateLastName(lastName)) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("Invalid last name: " + lastName);
        }
    }

    public static boolean validateSecondName(String secondName) {
        return secondName == null || !secondName.isEmpty();
    }

    public void setSecondName(String secondName) {
        if (validateSecondName(secondName)) {
            this.secondName = secondName;
        } else {
            throw new IllegalArgumentException("Invalid second name: " + secondName);
        }
    }

    public static boolean validateEmail(String email) {
        return email != null && email.contains("@");
    }

    public void setEmail(String email) {
        if (validateEmail(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
    }

    public static boolean validatePhone(String phone) {
        return phone != null && phone.matches("\\+?[0-9]+");
    }

    public void setPhone(String phone) {
        if (validatePhone(phone)) {
            this.phone = phone;
        } else {
            throw new IllegalArgumentException("Invalid phone number: " + phone);
        }
    }

    public static boolean validatePreference(String preference) {
        return Preference.validate(preference);
    }

    public void setPreference(String preference) {
        if (validatePreference(preference)) {
            this.preference = preference;
        } else {
            throw new IllegalArgumentException("Invalid preference: " + preference);
        }
    }
}
