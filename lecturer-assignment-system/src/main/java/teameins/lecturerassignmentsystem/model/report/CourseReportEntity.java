package teameins.lecturerassignmentsystem.model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teameins.lecturerassignmentsystem.model.annotations.CsvHeaderName;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseReportEntity {

    @JsonProperty("Vorlesungsname")
    private String name;
    @JsonProperty("Status")
    private String openStatus;
    @JsonProperty("Akademischer Grad")
    private String academicDegree;
    @JsonProperty("Semester")
    private String semester;
    @JsonProperty("Kann von Dozenten gehalten werden")
    private List<LecturerCanHoldCourseReportEntity> canBeHeldBy;

    public CourseReportEntity(String name, String openStatus, String academicDegree, String semester) {
        this.name = name;
        this.openStatus = openStatus;
        this.academicDegree = academicDegree;
        this.semester = semester;
    }
}
