package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import java.util.List;

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
