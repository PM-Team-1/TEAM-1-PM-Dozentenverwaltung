package teameins.lecturerassignmentsystem.model.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private LecturerReportEntity lecturer;
    @JsonIgnore
    private CourseReportEntity course;
}
