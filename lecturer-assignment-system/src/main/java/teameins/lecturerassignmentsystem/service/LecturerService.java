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
    private final CourseRepository courseRepository;

    public LecturerService(LecturerRepository lecturerRepository, CourseRepository courseRepository) {
        this.lecturerRepository = lecturerRepository;
        this.courseRepository = courseRepository;
    }

    public Page<Lecturer> getLecturers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lecturerRepository.findAll(pageable);
    }

    public void saveLecturer(LecturerDto lecturerDto) {
        Lecturer newLecturer = new Lecturer(lecturerDto, courseRepository);
        lecturerRepository.save(newLecturer);
    }
}
