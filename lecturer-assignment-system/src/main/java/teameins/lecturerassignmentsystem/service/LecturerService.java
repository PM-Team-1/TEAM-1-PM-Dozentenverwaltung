package teameins.lecturerassignmentsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.repository.CourseRepository;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

@Service
public class LecturerService {
    private final LecturerRepository lecturerRepository;
    private final CourseService courseService;

    public LecturerService(LecturerRepository lecturerRepository, CourseService courseService) {
        this.lecturerRepository = lecturerRepository;
        this.courseService = courseService;
    }

    public Page<Lecturer> getLecturers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lecturerRepository.findAll(pageable);
    }

    public void saveLecturer(LecturerDto lecturerDto) {
        Lecturer newLecturer = new Lecturer(lecturerDto, courseService);
        lecturerRepository.save(newLecturer);
    }

    public void deleteLecturer(int lecturerId) {
        lecturerRepository.deleteById(lecturerId);
    }
}
