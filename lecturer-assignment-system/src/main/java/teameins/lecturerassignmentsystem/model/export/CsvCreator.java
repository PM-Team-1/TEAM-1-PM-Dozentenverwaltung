package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.CSVWriter;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class CsvCreator extends FileCreator{
    public CsvCreator(List<LecturerReportEntity> allValidLecturers) {
        super(allValidLecturers);
    }

    @Override
    public byte[] createFile() {
        List<String[]> rows =  new ArrayList<>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Field[] fields = LecturerReportEntity.class.getDeclaredFields();

        List<String> headerValues = getHeaders(fields);
        rows.add(headerValues.toArray(new String[0]));

        List<String[]> rowsValues = getRowsValues(fields);
        rows.addAll(rowsValues);

        try (CSVWriter writer = new CSVWriter(
                new OutputStreamWriter(outputStream),
                ';',
                '"',
                '"',
                "\n")) {
            writer.writeAll(rows);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
    }

    private List<String> getHeaders(Field[] fields) {
        List<String> headerValues = new ArrayList<>();

        for (Field field : fields) {
            if(!field.getType().equals(List.class)) {
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);

                if (jsonProperty != null) {
                    headerValues.add(jsonProperty.value());
                } else {
                    headerValues.add(field.getName());
                }
            } else {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> listTypeClass = (Class<?>) listType.getActualTypeArguments()[0];
                Field[] listTypeFields = listTypeClass.getDeclaredFields();

                headerValues.addAll(getHeaders(listTypeFields));
            }
        }

        return headerValues;
    }

    private List<String[]> getRowsValues(Field[] fields) {
        List<String[]> rows = new ArrayList<>();
        for (LecturerReportEntity lecturer : allValidLecturers) {
            for (CourseReportEntity course : lecturer.getCanHoldCourses()) {
                List<String> row = new ArrayList<>();
                row.add(lecturer.getTitle());
                row.add(lecturer.getFirstName());
                row.add(lecturer.getLastName());
                row.add(lecturer.getSecondName());
                row.add(lecturer.getEmail());
                row.add(lecturer.getPhone());
                row.add(String.valueOf(lecturer.isExtern()));
                row.add(lecturer.getPreference());
                row.add(course.getName());
                row.add(course.getOpenStatus());
                row.add(course.getAcademicDegree());
                row.add(course.getSemester());
                row.add(course.getAlreadyHeld());
                row.add(course.getQualification());
                row.add(String.valueOf(course.getPriority()));

                rows.add(row.toArray(new String[0]));
            }
        }

        return rows;
    }
}
