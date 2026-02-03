package teameins.lecturerassignmentsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.repository.LecturerRepository;

@Service
public class LecturerService {
    private final LecturerRepository lecturerRepository;

    public LecturerService(LecturerRepository lecturerRepository) {
        this.lecturerRepository = lecturerRepository;
    }

    public Page<Lecturer> getLecturers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lecturerRepository.findAll(pageable);
    }

    public void saveLecturer(Lecturer lecturer) {
        lecturerRepository.save(lecturer);
    }
}
