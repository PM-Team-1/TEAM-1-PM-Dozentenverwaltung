package teameins.lecturerassignmentsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public ReportService(MappingService mappingService, LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository,
                         CourseRepository courseRepository, LecturerRepository lecturerRepository) {
        this.mappingService = mappingService;
        this.lecturerCanHoldCourseRepository = lecturerCanHoldCourseRepository;
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
    }

    public List<?> getReportByReportMode(ReportMode reportMode) {
        return reportMode.getReportValueSupplier().apply(this);
    }

    /**
     * Report 1 aus der Story
     */
    public List<LecturerReportEntity> getCoursesHeldLocally() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = lecturerCanHoldCourseRepository.findAll();

        List<LecturerReportEntity> result = new ArrayList<>();

        for (Lecturer lecturer : lecturers) {
            List<LecturerCanHoldCourse> list = lecturerCanHoldCourses.stream()
                    .filter(t -> t.getLecturer().getId() == lecturer.getId())
                    .filter(t -> t.getAlreadyHeld().getValue().equals(AlreadyHeld.PROVADIS.getValue()))
                    .toList();

            result.add(mappingService.mapReport(lecturer, list));
        }

        return result;
    }

    /**
     * Report 2 aus der Story
     */
    public List<LecturerReportEntity> getCoursesNeverHeldLocally() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = lecturerCanHoldCourseRepository.findAll();

        List<LecturerReportEntity> result = new ArrayList<>();

        for (Lecturer lecturer : lecturers) {
            List<LecturerCanHoldCourse> list = lecturerCanHoldCourses.stream()
                    .filter(t -> t.getLecturer().getId() == lecturer.getId())
                    .filter(t -> !(t.getAlreadyHeld().getValue().equals(AlreadyHeld.PROVADIS.getValue())))
                    .toList();

            result.add(mappingService.mapReport(lecturer, list));
        }

        return result;
    }

    /**
     * Report 3 aus Story
     */
    public List<CourseReportEntity> getCoursesWithNoLecturers() {
        List<Course> courses = courseRepository.findAll();
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = lecturerCanHoldCourseRepository.findAll();

        List<Course> coursesWithNoLecturers = courses.stream()
                .filter(course -> lecturerCanHoldCourses.stream().noneMatch(lchc -> lchc.getCourse().getId() == course.getId()))
                .toList();

        List<CourseReportEntity> result = new ArrayList<>();
        for (Course course : coursesWithNoLecturers) {
            result.add(mappingService.mapReport(course, List.of()));
        }
        return result;
    }

    /**
     * Report 4 aus der Story
     */
    public List<CourseReportEntity> getCoursesWithOnlyForeignExperience() {
        List<Course> courses = courseRepository.findAll();
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = lecturerCanHoldCourseRepository.findAll();
        Map<Course, List<LecturerCanHoldCourse>> courseToLecturersMap = new HashMap<>();

        List<LecturerCanHoldCourse> haveHeldLocally = lecturerCanHoldCourses.stream()
                .filter(t -> t.getAlreadyHeld().getValue().equals(AlreadyHeld.PROVADIS.getValue()))
                .toList();
        List<LecturerCanHoldCourse> haveHeldElsewhere = lecturerCanHoldCourses.stream()
                .filter(t -> t.getAlreadyHeld().getValue().equals(AlreadyHeld.OTHER_SCHOOL.getValue()))
                .toList();

        List<Course> noLocallyExperiencedLecturers = courses.stream()
                .filter(course -> haveHeldLocally.stream().noneMatch(lchc -> lchc.getCourse().getId() == course.getId()))
                .toList();
        noLocallyExperiencedLecturers
                .forEach(course -> haveHeldElsewhere.forEach(lchc -> {
                    if (lchc.getCourse().getId() == course.getId()) {
                        addToMap(courseToLecturersMap, lchc.getCourse(), lchc);
                    }
                }));

        List<CourseReportEntity> result = new ArrayList<>();
        for (Map.Entry<Course, List<LecturerCanHoldCourse>> entry : courseToLecturersMap.entrySet()) {
            result.add(mappingService.mapReport(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private <L, C> void addToMap(Map<L, List<C>> map, L key, C value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            List<C> list = new ArrayList<>();
            list.add(value);
            map.put(key, list);
        }
    }
}
