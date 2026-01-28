package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teameins.lecturerassignmentsystem.model.LecturerCanHoldCourse;

public interface LecturerCanHoldCourseRepository extends JpaRepository<LecturerCanHoldCourse, Integer> {
}
