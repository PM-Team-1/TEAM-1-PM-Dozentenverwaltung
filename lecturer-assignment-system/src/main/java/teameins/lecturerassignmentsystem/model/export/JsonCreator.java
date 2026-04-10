package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class JsonCreator extends FileCreator{
    public JsonCreator(List<LecturerReportEntity> allValidLecturers) {
        super(allValidLecturers);
    }

    @Override
    public byte[] createFile() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(allValidLecturers);
    }
}
