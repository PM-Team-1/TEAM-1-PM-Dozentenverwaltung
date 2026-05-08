package teameins.lecturerassignmentsystem.service;

import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.model.enums.Title;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerCanHoldCourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;
import teameins.lecturerassignmentsystem.model.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MappingService {
    CourseRepository courseRepository;
    LecturerRepository lecturerRepository;
	
	public MappingService(CourseRepository courseRepository, LecturerRepository lecturerRepository) {
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
	}

    public MappingService() {}

    public LecturerDto map(Lecturer lecturer, List<LecturerCanHoldCourseDto> canHoldCourses) {
        return new LecturerDto(
                lecturer.getId(),
                lecturer.getTitle().getValue(),
                lecturer.getFirstName(),
                lecturer.getLastName(),
                lecturer.getSecondName(),
                lecturer.getEmail(),
                lecturer.getPhone(),
                lecturer.isExtern(),
                lecturer.getTeachingPreference().getValue(),
                canHoldCourses
        );
    }

    public CourseDto map(Course course, List<LecturerCanHoldCourseDto> canBeHeldBy) {
        return new CourseDto(
                course.getId(),
                course.getName(),
                course.isClosed(),
                course.isMaster(),
                course.getSemester(),
                canBeHeldBy
        );
    }

    public LecturerCanHoldCourseDto map(LecturerCanHoldCourse lecturerCanHoldCourse) {
        return new LecturerCanHoldCourseDto(
                lecturerCanHoldCourse.getId(),
                lecturerCanHoldCourse.getLecturer().getId(),
                lecturerCanHoldCourse.getCourse().getId(),
                lecturerCanHoldCourse.getAlreadyHeld().getValue(),
                lecturerCanHoldCourse.getQualification().getValue(),
                lecturerCanHoldCourse.getAffinity().getValue()
        );
    }

    public teameins.lecturerassignmentsystem.model.dto.LecturerHoldsCourseDto map(teameins.lecturerassignmentsystem.model.db.LecturerHoldsCourse assignment) {
        return new teameins.lecturerassignmentsystem.model.dto.LecturerHoldsCourseDto(
                assignment.getId(),
                assignment.getLecturer().getId(),
                assignment.getCourse().getId()
        );
    }

    public teameins.lecturerassignmentsystem.model.db.LecturerHoldsCourse map(teameins.lecturerassignmentsystem.model.dto.LecturerHoldsCourseDto assignmentDto) {
        Lecturer lecturer = lecturerRepository.findById(assignmentDto.getLecturerId()).orElseThrow(() -> new LecturerNotFoundException("Es konnte kein Dozent mit der ID " + assignmentDto.getLecturerId() + " gefunden werden."));
        Course course = courseRepository.findById(assignmentDto.getCourseId()).orElseThrow(() -> new CourseNotFoundException("Es konnte keine Vorlesung mit der ID " + assignmentDto.getCourseId() + " gefunden werden."));
        teameins.lecturerassignmentsystem.model.db.LecturerHoldsCourse holdsCourse = new teameins.lecturerassignmentsystem.model.db.LecturerHoldsCourse();
        holdsCourse.setCourse(course);
        holdsCourse.setLecturer(lecturer);
        return holdsCourse;
    }

    public Lecturer map(LecturerDto dto) {
        Lecturer entity = new Lecturer();
        entity.setId(dto.getId());
        if (dto.getId() > 0) {
            entity.setId(dto.getId());
        }
        entity.setTitle(parseTitle(dto.getTitle()));
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setSecondName(dto.getSecondName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setExtern(dto.isExtern());
        entity.setTeachingPreference(parseTeachingPreference(dto.getTeachingPreference()));
        return entity;
    }

    public Course map(CourseDto dto) {
        Course entity = new Course();
        if (dto.getId() > 0) {
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setClosed(dto.isClosed());
        entity.setMaster(dto.isMaster());
        entity.setSemester(dto.getSemester());
        return entity;
    }

    public LecturerCanHoldCourse map(LecturerCanHoldCourseDto dto, Lecturer lecturer, Course course) {
        LecturerCanHoldCourse entity = new LecturerCanHoldCourse();
        entity.setLecturer(lecturer);
        entity.setCourse(course);
        entity.setAlreadyHeld(parseAlreadyHeld(dto.getAlreadyHeld()));
        entity.setQualification(parseQualification(dto.getQualification()));
        entity.setAffinity(parseAffinity(dto.getAffinity()));
        return entity;
    }

    public LecturerReportEntity mapReport(Lecturer lecturer, List<LecturerCanHoldCourse> canHoldCourses) {
        LecturerReportEntity lecturerReportEntity = new LecturerReportEntity();
        lecturerReportEntity.setTitle(lecturer.getTitle().getValue());
        lecturerReportEntity.setFullName(lecturer.getFullName());
        lecturerReportEntity.setEmail(lecturer.getEmail());
        lecturerReportEntity.setPhone(lecturer.getPhone());
        lecturerReportEntity.setIsExtern(lecturer.isExtern());
        lecturerReportEntity.setPreference(lecturer.getTeachingPreference().getDescription());
        lecturerReportEntity.setCanHoldCourses(
                canHoldCourses.stream().map(canHoldCourse ->
                        mapReport(canHoldCourse, canHoldCourse.getCourse())).toList()
        );
        return lecturerReportEntity;
    }

    public LecturerCanHoldCourseReportEntity mapReport(LecturerCanHoldCourse canHoldCourse, Course course) {
        LecturerCanHoldCourseReportEntity lecturerCanHoldCourseReportEntity = new LecturerCanHoldCourseReportEntity();
        lecturerCanHoldCourseReportEntity.setAffinity(canHoldCourse.getAffinity().getValue());
        lecturerCanHoldCourseReportEntity.setQualification(canHoldCourse.getQualification().getDescription());
        lecturerCanHoldCourseReportEntity.setAlreadyHeld(canHoldCourse.getAlreadyHeld().getDescription());
        lecturerCanHoldCourseReportEntity.setCourse(mapReport(course));
        return lecturerCanHoldCourseReportEntity;
    }

    public CourseReportEntity mapReport(Course course) {
        CourseReportEntity courseReportEntity = new CourseReportEntity();
        courseReportEntity.setName(course.getName());
        courseReportEntity.setOpenStatus(course.isClosed() ? "geschlossen" : "offen");
        courseReportEntity.setAcademicDegree(course.isMaster() ? "Master" : "Bachelor");
        courseReportEntity.setSemester(course.getSemester());
        return courseReportEntity;
    }

    public CourseReportEntity mapReport(Course course, List<LecturerCanHoldCourse> canHoldCourses) {
        CourseReportEntity courseReportEntity = new CourseReportEntity();
        courseReportEntity.setName(course.getName());
        courseReportEntity.setOpenStatus(course.isClosed() ? "geschlossen" : "offen");
        courseReportEntity.setAcademicDegree(course.isMaster() ? "Master" : "Bachelor");
        courseReportEntity.setSemester(course.getSemester());
        courseReportEntity.setCanBeHeldBy(
                canHoldCourses.stream().map(canHoldCourse ->
                        mapReport(canHoldCourse, canHoldCourse.getLecturer())).toList()
        );
        return courseReportEntity;
    }

    public LecturerCanHoldCourseReportEntity mapReport(LecturerCanHoldCourse canHoldCourse, Lecturer lecturer) {
        LecturerCanHoldCourseReportEntity lecturerCanHoldCourseReportEntity = new LecturerCanHoldCourseReportEntity();
        lecturerCanHoldCourseReportEntity.setAffinity(canHoldCourse.getAffinity().getValue());
        lecturerCanHoldCourseReportEntity.setQualification(canHoldCourse.getQualification().getDescription());
        lecturerCanHoldCourseReportEntity.setAlreadyHeld(canHoldCourse.getAlreadyHeld().getDescription());
        lecturerCanHoldCourseReportEntity.setLecturer(mapReport(lecturer));
        return lecturerCanHoldCourseReportEntity;
    }

    public LecturerReportEntity mapReport(Lecturer lecturer) {
        LecturerReportEntity lecturerReportEntity = new LecturerReportEntity();
        lecturerReportEntity.setTitle(lecturer.getTitle().getValue());
        lecturerReportEntity.setFullName(lecturer.getFullName());
        lecturerReportEntity.setEmail(lecturer.getEmail());
        lecturerReportEntity.setPhone(lecturer.getPhone());
        lecturerReportEntity.setIsExtern(lecturer.isExtern());
        lecturerReportEntity.setPreference(lecturer.getTeachingPreference().getDescription());
        return lecturerReportEntity;
    }

    private Title parseTitle(String titleStr) {
        if (titleStr == null) {
            throw new IllegalArgumentException("Titel darf nicht leer sein");
        }
        for (Title t : Title.values()) {
            if (t.getValue().equalsIgnoreCase(titleStr)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Ungültiger Titel: '" + titleStr + "'");
    }

    private TeachingPreference parseTeachingPreference(String prefStr) {
        if (prefStr == null || prefStr.isBlank()) {
            throw new IllegalArgumentException("Lehrpräferenz darf nicht leer sein");
        }
        for (TeachingPreference p : TeachingPreference.values()) {
            if (p.getValue().equalsIgnoreCase(prefStr)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Ungültige Lehrpräferenz: '" + prefStr + "'");
    }

    private AlreadyHeld parseAlreadyHeld(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AlreadyHeld darf nicht leer sein");
        }
        for (AlreadyHeld a : AlreadyHeld.values()) {
            if (a.getValue().equalsIgnoreCase(value)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Ungültiges AlreadyHeld: '" + value + "'");
    }

    private Qualification parseQualification(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Qualifikation darf nicht leer sein");
        }
        for (Qualification q : Qualification.values()) {
            if (q.getValue().equalsIgnoreCase(value)) {
                return q;
            }
        }
        throw new IllegalArgumentException("Ungültige Qualifikation: '" + value + "'");
    }

    private Affinity parseAffinity(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Affinität darf nicht leer sein");
        }
        for (Affinity a : Affinity.values()) {
            if (a.getValue().equalsIgnoreCase(value)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Ungültige Affinität: '" + value + "'");
    }
}
