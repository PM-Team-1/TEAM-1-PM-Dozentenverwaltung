package teameins.lecturerassignmentsystem.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import teameins.lecturerassignmentsystem.service.ReportService;

import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum ReportMode {
    ALL_COURSES_IN_PROVADIS(
            "Alle Dozenten, mit Kursen die er schon an der Provadis gehalten hat.",
            "all_courses_in_provadis",
            "Liste aller Dozenten und aller Vorlesungen, die der Dozierende an der Provadis schon gehalten hat (inkl. Bachelor, Master)",
            ReportService::getCoursesHeldLocally),
    ALL_COURSES_NOT_IN_PROVADIS(
            "Alle Dozenten, mit Kursen die er noch nie an der Provadis gehalten hat.",
            "all_courses_not_in_provadis",
            "Liste aller Dozierenden und aller Vorlesungen, die der Dozierende halten kann, aber noch nie an der Provadis Hochschule gehalten hat (inkl. Bachelor, Master)",
            ReportService::getCoursesNeverHeldLocally),
    ALL_COURSES_WITH_NO_LECTURERS(
            "Alle Kurse zu denen kein Dozent bekannt ist.",
            "all_courses_with_no_lecturer",
            "Liste aller Vorlesungen (inkl. Master, Bachelor), für die kein Dozent bekannt ist",
            ReportService::getCoursesWithNoLecturers),
    ALL_COURSES_WITH_ONLY_LECTURERS_NOT_HELD_IN_PROVADIS(
            "Alle Kurse, zu denen es nur Dozenten gibt die diesen noch nie an der Provadis, aber an anderen Schulen gehalten haben.",
            "all_courses_with_only_lecturers_not_held_in_provadis",
            "Liste aller Vorlesungen (inkl. Master, Bachelor), für die es nur Dozenten gibt, die diese noch nie an der Provadis Hochschule aber an anderen Hochschulen gehalten haben",
            ReportService::getCoursesWithOnlyForeignExperience);

    private final String header;
    private final String filename;
    private final String description;
    private final Function<ReportService, ? extends List<?>> reportValueSupplier;

    /**
     * @return true wenn der Report eine Liste von CourseReportEntity zurückgibt,
     *         false wenn er eine Liste von LecturerReportEntity zurückgibt.
     */
    public boolean isCourseBased() {
        return this == ALL_COURSES_WITH_NO_LECTURERS
                || this == ALL_COURSES_WITH_ONLY_LECTURERS_NOT_HELD_IN_PROVADIS;
    }
}
