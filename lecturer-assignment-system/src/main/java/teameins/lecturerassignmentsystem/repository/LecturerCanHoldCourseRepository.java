package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerCanHoldCourse;

public interface LecturerCanHoldCourseRepository extends JpaRepository<LecturerCanHoldCourse, Integer> {
    boolean existsByLecturerIdAndCourseId(int lecturerId, int courseId);
}
