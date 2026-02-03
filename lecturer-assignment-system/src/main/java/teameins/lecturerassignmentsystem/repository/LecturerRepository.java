package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teameins.lecturerassignmentsystem.model.db.Lecturer;

public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {

    Page<Lecturer> findAll(Pageable pageable);
}
