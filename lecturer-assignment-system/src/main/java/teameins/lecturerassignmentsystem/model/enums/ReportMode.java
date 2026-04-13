package teameins.lecturerassignmentsystem.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportMode {
    ALL_COURSES_IN_PROVADIS("all_courses_in_provadis", "Liste aller Dozenten und aller Vorlesungen, die der Dozierende an der Provadis schon gehalten hat (inkl. Bachelor, Master)"),
    ALL_COURSES_NOT_IN_PROVADIS("all_courses_not_in_provadis", "Liste aller Dozierenden und aller Vorlesungen, die der Dozierende halten kann, aber noch nie an der Provadis Hochschule gehalten hat (inkl. Bachelor, Master)"),
    ALL_COURSES_WITH_NO_LECTURERS("all_courses_with_no_lecturer", "Liste aller Vorlesungen (inkl. Master, Bachelor), für die kein Dozent bekannt ist"),
    ALL_COURSES_WITH_ONLY_LECTURERS_NOT_HELD_IN_PROVADIS("all_courses_with_only_lecturers_not_held_in_provadis", "Liste aller Vorlesungen (inkl. Master, Bachelor), für die es nur Dozenten gibt, die diese noch nie an der Provadis Hochschule aber an anderen Hochschulen gehalten haben");

    private final String filename;
    private final String description;
}
