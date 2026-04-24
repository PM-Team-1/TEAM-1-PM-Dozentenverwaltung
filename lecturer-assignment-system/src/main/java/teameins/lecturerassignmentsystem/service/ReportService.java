package teameins.lecturerassignmentsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.*;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerCanHoldCourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    public ReportService(MappingService mappingService, LecturerCanHoldCourseRepository lecturerCanHoldCourseRepository,
                         CourseRepository courseRepository, LecturerRepository lecturerRepository) {
        this.mappingService = mappingService;
        this.lecturerCanHoldCourseRepository = lecturerCanHoldCourseRepository;
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
    }

    public List<LecturerReportEntity> getReportByReportMode(ReportMode reportMode) {
        return reportMode.getReportValueSupplier().apply(this);
    }

    /**
     * Report 1 aus der Story
     */
    public List<LecturerReportEntity> getCoursesHeldLocally() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = lecturerCanHoldCourseRepository.findAll();

        List<LecturerReportEntity> result = new ArrayList<LecturerReportEntity>();

        for (Lecturer lecturer : lecturers) {
            List<LecturerCanHoldCourse> list = lecturerCanHoldCourses.stream()
                    .filter(t -> t.getLecturer().getId() == lecturer.getId())
                    .filter(t -> t.getAlreadyHeld().getValue().equals(AlreadyHeld.PROVADIS.getValue()))
                    .toList();

            LecturerReportEntity lecturerEntity = getLreFromLchcList(list, lecturer);
            result.add(lecturerEntity);
        }

        return result;
    }

    /**
     * Report 2 aus der Story
     */
    //TODO soll geprüft werden, ob der Dozent weder Bachelor noch Master gehalten hat?
    public List<LecturerReportEntity> getCoursesNeverHeldLocally() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = lecturerCanHoldCourseRepository.findAll();

        List<LecturerReportEntity> result = new ArrayList<LecturerReportEntity>();

        for (Lecturer lecturer : lecturers) {
            List<LecturerCanHoldCourse> list = lecturerCanHoldCourses.stream()
                    .filter(t -> t.getLecturer().getId() == lecturer.getId())
                    .filter(t -> !(t.getAlreadyHeld().getValue().equals(AlreadyHeld.PROVADIS.getValue())))
                    .toList();

            LecturerReportEntity lecturerEntity = getLreFromLchcList(list, lecturer);
            result.add(lecturerEntity);
        }

        return result;
    }

    /**
     * Report 3 aus Story
     */
    public List<LecturerReportEntity> getCoursesWithNoLecturers() {
        List<Course> courses = courseRepository.findAll();
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = lecturerCanHoldCourseRepository.findAll();

        List<Course> coursesWithNoLecturers = courses.stream()
                .filter(course -> lecturerCanHoldCourses.stream().noneMatch(lchc -> lchc.getCourse().getId() == course.getId()))
                .toList();

        List<LecturerReportEntity> result = getLreListFromCourseList(coursesWithNoLecturers);
        return result;
    }

    /**
     * Report 4 aus der Story
     */
    //TODO soll nur geprüft werden, dass keine Dozenten die Vorlesung an der Provadis schon gehalten haben
    //	und es einen Dozenten gibt, der sie woanders gehalten hat (so ist es momentan)
    //	oder soll auch geprüft werden, dass keine Dozenten die Vorlesung halten können, aber noch nie gehalten haben?
    public List<LecturerReportEntity> getCoursesWithOnlyForeignExperience() {
        List<Course> courses = courseRepository.findAll();
        List<LecturerCanHoldCourse> lecturerCanHoldCourses = lecturerCanHoldCourseRepository.findAll();
        Map<Lecturer, List<LecturerCanHoldCourse>> lecturerToCoursesMap = new HashMap<>();

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
                    if(lchc.getCourse().getId() == course.getId()) {
                        addToMap(lecturerToCoursesMap, lchc.getLecturer(), lchc);
                    }
                }));

        return getLreListFromLecturerToCoursesMap(lecturerToCoursesMap);
    }

    protected LecturerReportEntity getLreFromLchcList(List<LecturerCanHoldCourse> list, Lecturer lecturer) {
        List<CourseReportEntity> coursesByLecturer = new ArrayList<CourseReportEntity>();

        for (LecturerCanHoldCourse entry : list) {
            Course course = entry.getCourse();

            String openStatus = course.isClosed() ? "geschlossen" : "offen"; //TODO anpassen
            String academicDegree = course.isMaster() ? "Master" : "Bachelor";

            CourseReportEntity courseR = new CourseReportEntity(course.getName(), openStatus, academicDegree, course.getSemester(),
                    entry.getAlreadyHeld().getDescription(), entry.getQualification().getDescription(), entry.getAffinity().getValue());
            coursesByLecturer.add(courseR);
        }

        LecturerReportEntity lecturerEntity = new LecturerReportEntity(lecturer.getTitle().getValue(), lecturer.getFirstName(), lecturer.getLastName(), lecturer.getSecondName(),
                lecturer.getEmail(), lecturer.getPhone(), lecturer.isExtern(), lecturer.getTeachingPreference().getDescription(), coursesByLecturer);

        return lecturerEntity;
    }

    protected List<LecturerReportEntity> getLreListFromCourseList(List<Course> courses) {
        List<CourseReportEntity> creList = new ArrayList<CourseReportEntity>();

        for (Course course : courses) {
            String openStatus = course.isClosed() ? "geschlossen" : "offen"; //TODO anpassen
            String academicDegree = course.isMaster() ? "Master" : "Bachelor";

            creList.add(new CourseReportEntity(course.getName(), openStatus, academicDegree, course.getSemester(), "", "", ""));
        }

        LecturerReportEntity lecturerEntity = new LecturerReportEntity("Main Entry", "", "", "", "", "", false, "", creList);
        List<LecturerReportEntity> result = new ArrayList<LecturerReportEntity>();
        result.add(lecturerEntity);
        return result;
    }

    protected List<LecturerReportEntity> getLreListFromLecturerToCoursesMap(Map<Lecturer, List<LecturerCanHoldCourse>> lecturerToCoursesMap) {
        List<LecturerReportEntity> lecturerReportEntities = new ArrayList<>();

        for(Map.Entry<Lecturer, List<LecturerCanHoldCourse>> entry : lecturerToCoursesMap.entrySet()) {
            lecturerReportEntities.add(getLreFromLchcList(entry.getValue(), entry.getKey()));
        }
        return lecturerReportEntities;
    }

    private <L, C> void addToMap(Map<L, List<C>> map, L key, C value){
        if(map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            List<C> list = new ArrayList<>();
            list.add(value);
            map.put(key, list);
        }
    }

}
