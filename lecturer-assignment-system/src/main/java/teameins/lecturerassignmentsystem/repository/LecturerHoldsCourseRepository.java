package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.repository.CrudRepository;
import teameins.lecturerassignmentsystem.model.db.LecturerHoldsCourse;

import java.util.Optional;

public interface LecturerHoldsCourseRepository extends CrudRepository<LecturerHoldsCourse, Integer> {
    Optional<LecturerHoldsCourse> findByCourseId(int courseId);
    boolean existsByCourseId(int courseId);
}
