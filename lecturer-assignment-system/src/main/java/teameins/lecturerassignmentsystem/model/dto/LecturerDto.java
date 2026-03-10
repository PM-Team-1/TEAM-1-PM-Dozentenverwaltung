package teameins.lecturerassignmentsystem.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public LecturerDto(int id, String title, String firstName, String lastName, String secondName, String email, String phone, boolean isExtern, String preference, List<LecturerCanHoldCourseDto> canHoldCourses) throws IllegalArgumentException {
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

    public void setTitle(String title) throws IllegalArgumentException {
        if (validateTitle(title)) {
            this.title = title;
        } else {
            throw new IllegalArgumentException("Der Titel ist ungültig: " + title + ". Gültige Werte sind: " + Arrays.toString(Title.getValidValues()));
        }
    }

    public static boolean validateFirstName(String firstName) {
        return firstName != null && !firstName.isEmpty();
    }

    public void setFirstName(String firstName) throws IllegalArgumentException {
        if (validateFirstName(firstName)) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("Der Vorname darf nicht leer sein.");
        }
    }

    public static boolean validateLastName(String lastName) {
        return lastName != null && !lastName.isEmpty();
    }

    public void setLastName(String lastName) throws IllegalArgumentException {
        if (validateLastName(lastName)) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("Der Nachname darf nicht leer sein.");
        }
    }

    public static boolean validateEmail(String email) {
        return email != null && email.contains("@");
    }

    public void setEmail(String email) throws IllegalArgumentException {
        if (validateEmail(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Die E-Mail Adresse muss ein @ enthalten.");
        }
    }

    public static boolean validatePhone(String phone) {
        return phone != null && phone.matches("\\+?[0-9]+(-[0-9]+)*");
    }

    public void setPhone(String phone) throws IllegalArgumentException {
        if (validatePhone(phone)) {
            this.phone = phone;
        } else {
            throw new IllegalArgumentException("Die Telefonnummer darf nur Ziffern und optional ein führendes + enthalten.");
        }
    }

    public static boolean validatePreference(String preference) {
        if (preference == null || preference.isBlank()) {
            return true;
        }
        return Preference.validate(preference);
    }

    public void setPreference(String preference) throws IllegalArgumentException {
        if (preference == null || preference.isBlank()) {
            this.preference = null;
            return;
        }

        if (validatePreference(preference)) {
            this.preference = preference;
        } else {
            throw new IllegalArgumentException(
                    "Die Präferenz ist ungültig: " + preference +
                            ". Gültige Werte sind: " + Arrays.toString(Preference.getValidValues())
            );
        }
    }
}
