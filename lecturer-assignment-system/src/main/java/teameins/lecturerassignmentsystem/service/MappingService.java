package teameins.lecturerassignmentsystem.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.Role;
import teameins.lecturerassignmentsystem.model.db.User;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerHoldsCourse;
import teameins.lecturerassignmentsystem.model.db.relation.UserHasRole;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.RoleDto;
import teameins.lecturerassignmentsystem.model.dto.UserDto;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerHoldsCourseDto;
import teameins.lecturerassignmentsystem.model.dto.relation.UserHasRoleDto;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.model.enums.Title;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

import java.util.List;

@Service
public class MappingService {

    private final PasswordEncoder passwordEncoder;

    public MappingService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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

    public UserDto map(User user, List<UserHasRoleDto> hasRoles) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                null,
                user.isEnabled(),
                hasRoles
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

    public RoleDto map(Role role, List<UserHasRoleDto> usersWithRole) {
        return new RoleDto(
                role.getId(),
                role.getName(),
                role.getGrantedAuthority(),
                usersWithRole
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

    public LecturerHoldsCourseDto map(LecturerHoldsCourse assignment) {
        return new LecturerHoldsCourseDto(
                assignment.getId(),
                assignment.getLecturer().getId(),
                assignment.getCourse().getId()
        );
    }

    public UserHasRoleDto map(UserHasRole uhr) {
        return new UserHasRoleDto(
                uhr.getId(),
                uhr.getUser().getId(),
                uhr.getRole().getId()
        );
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

    public Role map(RoleDto roleDto) {
        Role entity = new Role();
        if (roleDto.getId() > 0) {
            entity.setId(roleDto.getId());
        }
        entity.setName(roleDto.getName());
        entity.setGrantedAuthority(roleDto.getGrantedAuthority());
        return entity;
    }

    public User map(UserDto dto) {
        User entity = new User();
        if (dto.getId() > 0) {
            entity.setId(dto.getId());
        }
        entity.setUsername(dto.getUsername());
        entity.setPasswordHash(hashPassword(dto.getPassword()));
        entity.setEnabled(dto.isEnabled());
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

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
