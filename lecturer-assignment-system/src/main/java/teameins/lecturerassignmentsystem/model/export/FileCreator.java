package teameins.lecturerassignmentsystem.model.export;

import teameins.lecturerassignmentsystem.model.enums.FileCreationMode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerCanHoldCourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class FileCreator {

    protected final List<?> reportEntities;
    protected final ReportMode reportMode;

    protected FileCreator(List<?> reportEntities, ReportMode reportMode) {
        this.reportEntities = reportEntities;
        this.reportMode = reportMode;
    }

    public static FileCreator createCreator(FileCreationMode fileCreationMode, List<?> reportEntities, ReportMode reportMode) {
        try {
            Constructor<?> constructor = fileCreationMode.getCreatorClass().getDeclaredConstructor(List.class, ReportMode.class);
            return (FileCreator) constructor.newInstance(reportEntities, reportMode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Kein gültiger Exportmodus angegeben");
        }
    }

    public abstract byte[] createFile();

    /**
     * Liste der nicht-transient Felder einer Klasse (für CSV/PDF-Spaltenermittlung).
     */
    protected static List<Field> getNonTransientFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            if (Modifier.isTransient(f.getModifiers())) continue;
            if (f.isSynthetic()) continue;
            f.setAccessible(true);
            result.add(f);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected List<LecturerReportEntity> asLecturerList() {
        return (List<LecturerReportEntity>) reportEntities;
    }

    @SuppressWarnings("unchecked")
    protected List<CourseReportEntity> asCourseList() {
        return (List<CourseReportEntity>) reportEntities;
    }

    /**
     * Holt aus dem Report-Entity die LCHC-Liste (canHoldCourses bzw. canBeHeldBy).
     */
    protected static List<LecturerCanHoldCourseReportEntity> getLchcList(Object entity) {
        if (entity instanceof LecturerReportEntity l) {
            return l.getCanHoldCourses();
        }
        if (entity instanceof CourseReportEntity c) {
            return c.getCanBeHeldBy();
        }
        return List.of();
    }
}
