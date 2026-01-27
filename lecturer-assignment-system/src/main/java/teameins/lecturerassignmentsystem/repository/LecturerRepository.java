package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teameins.lecturerassignmentsystem.model.Dozent;

public interface LecturerRepository extends JpaRepository<Dozent, Integer> {
}
