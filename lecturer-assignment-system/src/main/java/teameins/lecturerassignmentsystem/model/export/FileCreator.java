package teameins.lecturerassignmentsystem.model.export;

import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.FileCreationMode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

public abstract class FileCreator {

    List<LecturerReportEntity> allValidLecturers;
    ReportMode reportMode;

    FileCreator(List<LecturerReportEntity> allValidLecturers, ReportMode reportMode) {
        this.allValidLecturers = allValidLecturers;
        this.reportMode = reportMode;
    }

    public static FileCreator createCreator(FileCreationMode fileCreationMode, List<LecturerReportEntity> reportEntities, ReportMode reportMode) {
        try {
            Constructor<?> constructor = fileCreationMode.getCreatorClass().getDeclaredConstructor(List.class, ReportMode.class);
            return (FileCreator) constructor.newInstance(reportEntities, reportMode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Kein gültiger Exportmodus angegeben");
        }
    }

    public abstract byte[] createFile();
}
