package teameins.lecturerassignmentsystem.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Title;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class LecturerService {
    private final LecturerRepository lecturerRepository;
    private final MappingService mappingService;

    public LecturerDto getLecturerById(int lecturerId) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId).orElseThrow(RuntimeException::new);
        List<LecturerCanHoldCourseDto> canHoldCourses = getCoursesLecturerCanHold(lecturerId);
        return mappingService.map(lecturer, canHoldCourses);
    }

    public List<LecturerDto> listLecturers() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<LecturerDto> lecturerDtos = new ArrayList<>();
        for (Lecturer lecturer : lecturers) {
            List<LecturerCanHoldCourseDto> canHold = getCoursesLecturerCanHold(lecturer.getId());
            lecturerDtos.add(mappingService.map(lecturer, canHold));
        }
        return lecturerDtos;
    }

    public void createLecturer(String title, String firstName, String lastName, String secondName, String email, String phone, boolean isExtern, String preference) {
        Title parsedTitle = Arrays.stream(Title.values())
                .filter(t -> t.name().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ungültiger Titel: " + title + ". Erlaubte Werte: " + Arrays.toString(Title.values())));
        Preference parsedPreference = Arrays.stream(Preference.values())
                .filter(p -> p.name().equalsIgnoreCase(preference))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ungültige Präferenz: " + preference + ". Erlaubte Werte: " + Arrays.toString(Preference.values())));

        Lecturer lecturer = new Lecturer();
        lecturer.setTitle(parsedTitle);
        lecturer.setFirstName(firstName);
        lecturer.setLastName(lastName);
        lecturer.setSecondName(secondName);
        lecturer.setEmail(email);
        lecturer.setPhone(phone);
        lecturer.setExtern(isExtern);
        lecturer.setPreference(parsedPreference);

        lecturerRepository.save(lecturer);
    }

    public List<LecturerCanHoldCourseDto> getCoursesLecturerCanHold(int lecturerId) {
        List<LecturerCanHoldCourse> coursesLecturerCanHold = lecturerRepository.findCoursesLecturerCanHold(lecturerId);
        List<LecturerCanHoldCourseDto> lecturerCanHoldCourseDtoList = new ArrayList<>();
        for (LecturerCanHoldCourse lchc : coursesLecturerCanHold) {
            lecturerCanHoldCourseDtoList.add(mappingService.map(lchc));
        }
        return lecturerCanHoldCourseDtoList;
    }

    public void deleteLecturer(int lecturerId) {
        lecturerRepository.deleteById(lecturerId);
    }
}
