package teameins.lecturerassignmentsystem.model.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teameins.lecturerassignmentsystem.model.db.Lecturer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LecturerCanHoldCourseReportEntity {

    @JsonProperty("Wo bereits gehalten")
    private String alreadyHeld;
    @JsonProperty("Qualifikation")
    private String qualification;
    @JsonProperty("Affinität")
    private String affinity;

    @JsonIgnore
    private transient LecturerReportEntity lecturer;
    @JsonIgnore
    private transient CourseReportEntity course;
}
