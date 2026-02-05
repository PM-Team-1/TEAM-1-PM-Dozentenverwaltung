package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {


    @Query("select lchc from LecturerCanHoldCourse lchc where lchc.course.id = :courseId")
    List<LecturerCanHoldCourse> findLecturersWhoCanHoldCourse(@Param("courseId") int courseId);
}
