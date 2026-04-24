package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class JsonCreator extends FileCreator{
    public JsonCreator(List<LecturerReportEntity> allValidLecturers, ReportMode reportMode) {
        super(allValidLecturers, reportMode);
    }

    @Override
    public byte[] createFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if(reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS){
                List<CourseReportEntity> courseReportEntities = allValidLecturers.getFirst().getCanHoldCourses();
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(courseReportEntities);
            } else {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(allValidLecturers);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
