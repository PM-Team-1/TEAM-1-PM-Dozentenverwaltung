package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.*;
import teameins.lecturerassignmentsystem.model.export.CsvCreator;
import teameins.lecturerassignmentsystem.model.export.FileCreator;
import teameins.lecturerassignmentsystem.model.export.JsonCreator;
import teameins.lecturerassignmentsystem.model.export.PdfCreator;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExportService {
    MappingService mappingService;

    public ExportService(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    public byte[] exportFile(FileCreationMode mode, List<LecturerReportEntity> reportEntities) {
        FileCreator fileCreator;
        Lecturer lecturer = new Lecturer(1, Title.DOCTOR, "Meister", "Proper", "", "meister.proper@wc.de", "+123456789", false, Preference.ALLES);
        Course course1 = new Course(1, "Informatik", false, false, "WiSe 24/25");
        Course course2 = new Course(2, "Informatik2", false, false, "WiSe 24/25");

        LecturerCanHoldCourse lecturerCanHoldCourse1 = new LecturerCanHoldCourse(1, AlreadyHeld.PROVADIS, Qualification.IMMEDIATELY, course1, lecturer, true);
        LecturerCanHoldCourse lecturerCanHoldCourse2 = new LecturerCanHoldCourse(2, AlreadyHeld.PROVADIS, Qualification.IMMEDIATELY, course2, lecturer, false);

        LecturerReportEntity lecturerReportEntity = mappingService.mapReport(lecturer, List.of(lecturerCanHoldCourse1, lecturerCanHoldCourse2));

        reportEntities.add(lecturerReportEntity);
        reportEntities.add(lecturerReportEntity);

        try {
            Constructor<?> constructor = mode.getCreatorClass().getDeclaredConstructor(List.class);
            fileCreator = (FileCreator) constructor.newInstance(reportEntities);
        } catch (Exception e) {
            throw new IllegalArgumentException("Kein gültiger Exportmodus angegeben");
        }

        return fileCreator.createFile();
    }
}
