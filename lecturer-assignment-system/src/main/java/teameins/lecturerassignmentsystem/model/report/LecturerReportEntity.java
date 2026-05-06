package teameins.lecturerassignmentsystem.model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LecturerReportEntity {

    @JsonProperty("Titel")
    private String title;
    @JsonProperty("Name")
    private String fullName;
    @JsonProperty("E-Mail Adresse")
    private String email;
    @JsonProperty("Telefonnummer")
    private String phone;
    @JsonProperty("Externer")
    private Boolean isExtern;
    @JsonProperty("Präferenz")
    private String preference;
    @JsonProperty("Kann Kurse halten")
    private List<LecturerCanHoldCourseReportEntity> canHoldCourses;

    public LecturerReportEntity(String title, String fullName, String email, String phone, Boolean isExtern, String preference) {
        this.title = title;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.isExtern = isExtern;
        this.preference = preference;
    }

}
