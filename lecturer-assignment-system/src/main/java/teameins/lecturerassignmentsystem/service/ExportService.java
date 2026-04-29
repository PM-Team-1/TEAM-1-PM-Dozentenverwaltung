package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.enums.FileCreationMode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.export.FileCreator;

import java.util.List;

@Service
public class ExportService {

    public byte[] exportFile(FileCreationMode fileCreationMode, ReportMode reportMode, List<?> reportEntities) {
        if (reportEntities == null || reportEntities.isEmpty()) {
            return new byte[]{};
        }

        FileCreator fileCreator = FileCreator.createCreator(fileCreationMode, reportEntities, reportMode);
        return fileCreator.createFile();
    }
}
