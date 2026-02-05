package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Title;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MappingService {

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
                lecturer.getPreference().getValue(),
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
                lecturerCanHoldCourse.getLecturer().getId(),
                lecturerCanHoldCourse.getCourse().getId(),
                lecturerCanHoldCourse.getAlreadyHeld().getValue(),
                lecturerCanHoldCourse.getQualification().getValue(),
                lecturerCanHoldCourse.isPriority()
        );
    }

    public Lecturer map(LecturerDto dto) {
        Lecturer entity = new Lecturer();
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
        entity.setPreference(parsePreference(dto.getPreference()));
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
        entity.setPriority(dto.isPriority());
        return entity;
    }

    private Title parseTitle(String titleStr) {
        if (titleStr == null || titleStr.isBlank()) {
            throw new IllegalArgumentException("Title darf nicht leer sein");
        }
        for (Title t : Title.values()) {
            if (t.getValue().equalsIgnoreCase(titleStr)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Ungültiger Title: '" + titleStr + "'");
    }

    private Preference parsePreference(String prefStr) {
        if (prefStr == null || prefStr.isBlank()) {
            throw new IllegalArgumentException("Preference darf nicht leer sein");
        }
        for (Preference p : Preference.values()) {
            if (p.getValue().equalsIgnoreCase(prefStr)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Ungültige Preference: '" + prefStr + "'");
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
            throw new IllegalArgumentException("Qualification darf nicht leer sein");
        }
        for (Qualification q : Qualification.values()) {
            if (q.getValue().equalsIgnoreCase(value)) {
                return q;
            }
        }
        throw new IllegalArgumentException("Ungültige Qualification: '" + value + "'");
    }
}
