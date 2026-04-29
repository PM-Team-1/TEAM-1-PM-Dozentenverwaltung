package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerCanHoldCourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvCreator extends FileCreator {

    public CsvCreator(List<?> reportEntities, ReportMode reportMode) {
        super(reportEntities, reportMode);
    }

    @Override
    public byte[] createFile() {
        List<String[]> rows = new ArrayList<>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        } catch (IOException e) {
            log.error("BOM Bytes couldn't be attached.");
        }

        List<Field> lecturerFields = getNonTransientFieldsExcludingList(LecturerReportEntity.class);
        List<Field> courseFields = getNonTransientFieldsExcludingList(CourseReportEntity.class);
        List<Field> lchcFields = getNonTransientFieldsExcludingList(LecturerCanHoldCourseReportEntity.class);

        // Header row + data rows (Reihenfolge je nach Report-Typ)
        rows.add(buildHeaderRow(lecturerFields, courseFields, lchcFields));
        rows.addAll(buildRows(lecturerFields, courseFields, lchcFields));

        try (CSVWriter writer = new CSVWriter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
                ';', '"', '"', "\n")) {
            writer.writeAll(rows);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
    }

    private String[] buildHeaderRow(List<Field> lecturerFields, List<Field> courseFields, List<Field> lchcFields) {
        List<String> headers = new ArrayList<>();
        if (reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS) {
            // Report 3: nur Course-Felder
            headers.addAll(toHeaders(courseFields));
        } else if (reportMode.isCourseBased()) {
            // Course-based: Course -> Lecturer -> LCHC
            headers.addAll(toHeaders(courseFields));
            headers.addAll(toHeaders(lecturerFields));
            headers.addAll(toHeaders(lchcFields));
        } else {
            // Lecturer-based: Lecturer -> Course -> LCHC
            headers.addAll(toHeaders(lecturerFields));
            headers.addAll(toHeaders(courseFields));
            headers.addAll(toHeaders(lchcFields));
        }
        return headers.toArray(new String[0]);
    }

    private List<String[]> buildRows(List<Field> lecturerFields, List<Field> courseFields, List<Field> lchcFields) {
        List<String[]> rows = new ArrayList<>();

        if (reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS) {
            // Report 3: nur Course-Felder, eine Zeile pro Course
            for (CourseReportEntity course : asCourseList()) {
                List<String> row = new ArrayList<>();
                appendValues(row, course, courseFields);
                rows.add(row.toArray(new String[0]));
            }
        } else if (reportMode.isCourseBased()) {
            // Course-based: course -> lecturer -> lchc
            for (CourseReportEntity course : asCourseList()) {
                List<LecturerCanHoldCourseReportEntity> lchcList = course.getCanBeHeldBy();
                if (lchcList == null || lchcList.isEmpty()) {
                    rows.add(buildRow(course, courseFields, null, lecturerFields, null, lchcFields));
                } else {
                    for (LecturerCanHoldCourseReportEntity lchc : lchcList) {
                        rows.add(buildRow(course, courseFields, lchc.getLecturer(), lecturerFields, lchc, lchcFields));
                    }
                }
            }
        } else {
            // Lecturer-based: lecturer -> course -> lchc
            for (LecturerReportEntity lecturer : asLecturerList()) {
                List<LecturerCanHoldCourseReportEntity> lchcList = lecturer.getCanHoldCourses();
                if (lchcList == null || lchcList.isEmpty()) {
                    rows.add(buildRow(lecturer, lecturerFields, null, courseFields, null, lchcFields));
                } else {
                    for (LecturerCanHoldCourseReportEntity lchc : lchcList) {
                        rows.add(buildRow(lecturer, lecturerFields, lchc.getCourse(), courseFields, lchc, lchcFields));
                    }
                }
            }
        }

        return rows;
    }

    private String[] buildRow(Object first, List<Field> firstFields,
                              Object second, List<Field> secondFields,
                              Object lchc, List<Field> lchcFields) {
        List<String> row = new ArrayList<>();
        appendValues(row, first, firstFields);
        appendValues(row, second, secondFields);
        appendValues(row, lchc, lchcFields);
        return row.toArray(new String[0]);
    }

    private void appendValues(List<String> row, Object obj, List<Field> fields) {
        for (Field f : fields) {
            if (obj == null) {
                row.add("");
                continue;
            }
            try {
                Object val = f.get(obj);
                row.add(val != null ? val.toString() : "");
            } catch (IllegalAccessException e) {
                row.add("");
            }
        }
    }

    private static List<Field> getNonTransientFieldsExcludingList(Class<?> clazz) {
        List<Field> all = getNonTransientFields(clazz);
        List<Field> filtered = new ArrayList<>();
        for (Field f : all) {
            if (f.getType().equals(List.class)) continue;
            filtered.add(f);
        }
        return filtered;
    }

    private static List<String> toHeaders(List<Field> fields) {
        List<String> headers = new ArrayList<>();
        for (Field f : fields) {
            JsonProperty jp = f.getAnnotation(JsonProperty.class);
            headers.add(jp != null ? jp.value() : f.getName());
        }
        return headers;
    }
}
