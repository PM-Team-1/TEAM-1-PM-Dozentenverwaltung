package teameins.lecturerassignmentsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service für die vier Report-Typen des Dozentenverwaltungssystems.
 */
@Service
@Transactional
public class ReportService {

    private final LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository;
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private final MappingService mappingService;

    public ReportService(LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository,
                         CourseRepository courseRepository,
                         LecturerRepository lecturerRepository,
                         MappingService mappingService) {
        this.lecturerCanHoldCourseRepository = lecturerCanHoldCourseRepository;
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
        this.mappingService = mappingService;
    }

    public List<LecturerReportEntity> getReportByReportMode(ReportMode reportMode) {
        if(reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS) {
            return reportMode.getReportValueSupplier().apply(this).stream().map(dto -> mappingService.mapReport((CourseDto) dto)).toList();
        } else {
            return reportMode.getReportValueSupplier().apply(this).stream().map(dto -> mappingService.mapReport((LecturerDto) dto)).toList();
        }
    }

    /**
     * Report 1: Liste aller Dozenten mit allen Vorlesungen, die sie bereits an der Provadis gehalten haben.
     * Nur Zuordnungen mit AlreadyHeld = PROVADIS.
     * Nur Dozenten, die mindestens eine solche Vorlesung haben.
     *
     * @return Liste der Dozenten, deren canHoldCourses nur Provadis-gehaltene Vorlesungen enthält
     */
    public List<LecturerDto> getReportHeldAtProvadis() {
        List<LecturerCanHoldCourse> allAssignments = lecturerCanHoldCourseRepository.findAll();

        // Gruppierung nach Dozent, gefiltert auf PROVADIS
        Map<Integer, List<LecturerCanHoldCourse>> byLecturer = allAssignments.stream()
                .filter(lchc -> AlreadyHeld.PROVADIS.equals(lchc.getAlreadyHeld()))
                .collect(Collectors.groupingBy(lchc -> lchc.getLecturer().getId()));

        return buildLecturerDtosFromGrouping(byLecturer);
    }

    /**
     * Report 2: Liste aller Dozenten mit allen Vorlesungen, die sie halten können,
     * aber noch nie an der Provadis Hochschule gehalten haben (OTHER_SCHOOL oder NOT_YET_HELD).
     * Nur Dozenten, die mindestens eine solche Vorlesung haben.
     *
     * @return Liste der Dozenten, deren canHoldCourses nur nicht-Provadis-Vorlesungen enthält
     */
    public List<LecturerDto> getReportNotYetHeldAtProvadis() {
        List<LecturerCanHoldCourse> allAssignments = lecturerCanHoldCourseRepository.findAll();

        // Gruppierung nach Dozent, gefiltert auf NICHT PROVADIS
        Map<Integer, List<LecturerCanHoldCourse>> byLecturer = allAssignments.stream()
                .filter(lchc -> !AlreadyHeld.PROVADIS.equals(lchc.getAlreadyHeld()))
                .collect(Collectors.groupingBy(lchc -> lchc.getLecturer().getId()));

        return buildLecturerDtosFromGrouping(byLecturer);
    }

    /**
     * Report 3: Liste aller Vorlesungen (inkl. Bachelor und Master),
     * für die kein Dozent bekannt ist (keine Zuordnung in LecturerCanHoldCourse).
     *
     * @return Liste der Vorlesungen ohne zugeordneten Dozenten
     */
    public List<CourseDto> getReportCoursesWithoutLecturer() {
        List<LecturerCanHoldCourse> allAssignments = lecturerCanHoldCourseRepository.findAll();

        // IDs aller Vorlesungen, für die mindestens ein Dozent bekannt ist
        java.util.Set<Integer> courseIdsWithLecturer = allAssignments.stream()
                .map(lchc -> lchc.getCourse().getId())
                .collect(Collectors.toSet());

        // Alle Vorlesungen ohne Zuordnung zurückgeben
        return courseRepository.findAll().stream()
                .filter(course -> !courseIdsWithLecturer.contains(course.getId()))
                .map(course -> mappingService.map(course, new ArrayList<>()))
                .collect(Collectors.toList());
    }

    /**
     * Report 4: Liste aller Vorlesungen (inkl. Bachelor und Master),
     * für die es ausschließlich Dozenten gibt, die diese noch nie an der Provadis,
     * aber an anderen Hochschulen gehalten haben (AlreadyHeld = OTHER_SCHOOL).
     * Vorlesungen ohne jeglichen Dozenten werden NICHT aufgelistet.
     *
     * @return Liste der Vorlesungen, die nur von Dozenten mit OTHER_SCHOOL-Status belegt sind
     */
    public List<LecturerDto> getReportCoursesOnlyHeldElsewhere() {
        List<LecturerCanHoldCourse> allAssignments = lecturerCanHoldCourseRepository.findAll();

        // Gruppierung aller Zuordnungen nach Vorlesungs-ID
        Map<Integer, List<LecturerCanHoldCourse>> byCourse = allAssignments.stream()
                .collect(Collectors.groupingBy(lchc -> lchc.getCourse().getId()));

        Map<Integer, List<LecturerCanHoldCourseDto>> resultMapByLecturer = new HashMap<>();

        List<LecturerDto> result = new ArrayList<>();

        for (Map.Entry<Integer, List<LecturerCanHoldCourse>> entry : byCourse.entrySet()) {
            List<LecturerCanHoldCourse> assignments = entry.getValue();

            // Bedingung: Mindestens ein Dozent mit OTHER_SCHOOL vorhanden
            boolean hasOtherSchool = assignments.stream()
                    .anyMatch(lchc -> AlreadyHeld.OTHER_SCHOOL.equals(lchc.getAlreadyHeld()));

            // Bedingung: Kein einziger Dozent mit PROVADIS
            boolean hasProvadis = assignments.stream()
                    .anyMatch(lchc -> AlreadyHeld.PROVADIS.equals(lchc.getAlreadyHeld()));

            if (hasOtherSchool && !hasProvadis) {
                assignments.forEach(lecturerCanHoldCourse -> {
                    LecturerCanHoldCourseDto lecturerCanHoldCourseDto = mappingService.map(lecturerCanHoldCourse);
                    addToMap(resultMapByLecturer, lecturerCanHoldCourseDto.getLecturerId(), lecturerCanHoldCourseDto);
                });
            }
        }

        resultMapByLecturer.forEach((key, value) -> {
            Lecturer lecturer = lecturerRepository.findById(key).orElseThrow();
            result.add(mappingService.map(lecturer, value));
        });

        return result;
    }

    private <T> void addToMap(Map<Integer, List<T>> map, Integer key, T value){
        if(map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            List<T> list = new ArrayList<>();
            list.add(value);
            map.put(key, list);
        }
    }

    /**
     * Hilfsmethode: Baut aus einer Gruppierung (LecturerId → List<LecturerCanHoldCourse>)
     * eine Liste von LecturerDtos auf, wobei canHoldCourses nur die gefilterten Zuordnungen enthält.
     */
    private List<LecturerDto> buildLecturerDtosFromGrouping(
            Map<Integer, List<LecturerCanHoldCourse>> byLecturer) {

        List<LecturerDto> result = new ArrayList<>();

        for (Map.Entry<Integer, List<LecturerCanHoldCourse>> entry : byLecturer.entrySet()) {
            List<LecturerCanHoldCourse> assignments = entry.getValue();
            if (assignments.isEmpty()) continue;

            List<LecturerCanHoldCourseDto> lchcDtos = assignments.stream()
                    .map(mappingService::map)
                    .collect(Collectors.toList());

            LecturerDto lecturerDto = mappingService.map(assignments.getFirst().getLecturer(), lchcDtos);
            result.add(lecturerDto);
        }

        return result;
    }
}
