package teameins.lecturerassignmentsystem.model.export;

import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class FileCreator {

    List<LecturerReportEntity> allValidLecturers;

    FileCreator(List<LecturerReportEntity> allValidLecturers) {
        this.allValidLecturers = allValidLecturers;
    }

    public abstract byte[] createFile();
}
