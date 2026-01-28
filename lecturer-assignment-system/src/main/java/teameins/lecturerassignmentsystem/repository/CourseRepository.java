package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teameins.lecturerassignmentsystem.model.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
}
