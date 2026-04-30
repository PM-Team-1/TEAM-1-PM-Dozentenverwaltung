package teameins.lecturerassignmentsystem.model.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerCanHoldCourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.lang.reflect.Field;
import java.util.List;

public class JsonCreator extends FileCreator {

    private static final String LECTURER_INNER_LABEL = "Kann Kurse halten";
    private static final String COURSE_INNER_LABEL = "Kann von Dozenten gehalten werden";

    public JsonCreator(List<?> reportEntities, ReportMode reportMode) {
        super(reportEntities, reportMode);
    }

    @Override
    public byte[] createFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode root = mapper.createArrayNode();

            if (reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS) {
                // Report 3: nur die Course-Felder, keine innere Liste
                for (CourseReportEntity course : asCourseList()) {
                    root.add(buildOuterNode(mapper, course, CourseReportEntity.class));
                }
            } else if (reportMode.isCourseBased()) {
                // Course-based: outer = Course, inner-Eintrag = Lecturer-Felder + LCHC-Felder
                for (CourseReportEntity course : asCourseList()) {
                    ObjectNode node = buildOuterNode(mapper, course, CourseReportEntity.class);
                    ArrayNode inner = mapper.createArrayNode();
                    if (course.getCanBeHeldBy() != null) {
                        for (LecturerCanHoldCourseReportEntity lchc : course.getCanBeHeldBy()) {
                            inner.add(buildMergedNode(mapper, lchc.getLecturer(), LecturerReportEntity.class, lchc));
                        }
                    }
                    node.set(COURSE_INNER_LABEL, inner);
                    root.add(node);
                }
            } else {
                // Lecturer-based: outer = Lecturer, inner-Eintrag = Course-Felder + LCHC-Felder
                for (LecturerReportEntity lecturer : asLecturerList()) {
                    ObjectNode node = buildOuterNode(mapper, lecturer, LecturerReportEntity.class);
                    ArrayNode inner = mapper.createArrayNode();
                    if (lecturer.getCanHoldCourses() != null) {
                        for (LecturerCanHoldCourseReportEntity lchc : lecturer.getCanHoldCourses()) {
                            inner.add(buildMergedNode(mapper, lchc.getCourse(), CourseReportEntity.class, lchc));
                        }
                    }
                    node.set(LECTURER_INNER_LABEL, inner);
                    root.add(node);
                }
            }

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Erzeugt einen ObjectNode mit allen nicht-transient Skalar-Feldern (keine Listen) eines Outer-Entities.
     */
    private ObjectNode buildOuterNode(ObjectMapper mapper, Object obj, Class<?> clazz) {
        ObjectNode node = mapper.createObjectNode();
        appendFields(node, obj, clazz);
        return node;
    }

    /**
     * Erzeugt einen ObjectNode aus den Feldern des inneren Entities + den LCHC-Feldern.
     */
    private ObjectNode buildMergedNode(ObjectMapper mapper, Object innerEntity, Class<?> innerClass,
                                       LecturerCanHoldCourseReportEntity lchc) {
        ObjectNode node = mapper.createObjectNode();
        appendFields(node, innerEntity, innerClass);
        appendFields(node, lchc, LecturerCanHoldCourseReportEntity.class);
        return node;
    }

    private void appendFields(ObjectNode node, Object obj, Class<?> clazz) {
        if (obj == null) return;
        for (Field f : getNonTransientFields(clazz)) {
            if (f.getType().equals(List.class)) continue;
            JsonProperty jp = f.getAnnotation(JsonProperty.class);
            String name = jp != null ? jp.value() : f.getName();
            try {
                Object val = f.get(obj);
                node.putPOJO(name, val);
            } catch (IllegalAccessException ignored) {
            }
        }
    }
}
