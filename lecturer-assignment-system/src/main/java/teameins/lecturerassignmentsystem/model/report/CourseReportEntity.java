package teameins.lecturerassignmentsystem.model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import teameins.lecturerassignmentsystem.model.annotations.CsvHeaderName;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;

import java.util.List;

public class CourseReportEntity {

    @JsonProperty("Vorlesungsname")
    private String name;
    @JsonProperty("Status")
    private String openStatus;
    @JsonProperty("Akademischer Grad")
    private String academicDegree;
    @JsonProperty("Semester")
    private String semester;
    @JsonProperty("Wo bereits gehalten")
    private String alreadyHeld;
    @JsonProperty("Qualifikation")
    private String qualification;
    @JsonProperty("Affinität")
    private String affinity;

    public CourseReportEntity() {}

    public CourseReportEntity(String name, String openStatus, String academicDegree, String semester, String alreadyHeld, String qualification, String affinity) {
        this.name = name;
        this.openStatus = openStatus;
        this.academicDegree = academicDegree;
        this.semester = semester;
        this.alreadyHeld = alreadyHeld;
        this.qualification = qualification;
        this.affinity = affinity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }

    public String getAcademicDegree() {
        return academicDegree;
    }

    public void setAcademicDegree(String academicDegree) {
        this.academicDegree = academicDegree;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAlreadyHeld() {
        return alreadyHeld;
    }

    public void setAlreadyHeld(String alreadyHeld) {
        this.alreadyHeld = alreadyHeld;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getAffinity() {
        return affinity;
    }

    public void setAffinity(String affinity) {
        this.affinity = affinity;
    }
}
