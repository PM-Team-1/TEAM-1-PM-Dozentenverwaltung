package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerHoldsCourse;

import java.util.List;
import java.util.Optional;
public interface LecturerHoldsCourseRepository extends CrudRepository<LecturerHoldsCourse, Integer> {
    @Query("select lhc from teameins.lecturerassignmentsystem.model.db.relation.LecturerHoldsCourse lhc join fetch lhc.lecturer where lhc.course.id = :courseId")
    Optional<LecturerHoldsCourse> findByCourseId(@Param("courseId") int courseId);
    boolean existsByCourseId(int courseId);
    List<LecturerHoldsCourse> findByLecturerId(int lecturerId);
}
