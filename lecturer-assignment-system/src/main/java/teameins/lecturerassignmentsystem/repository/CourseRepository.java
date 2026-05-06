package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerCanHoldCourse;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {


    @Query("select lchc from LecturerCanHoldCourse lchc where lchc.course.id = :courseId")
    List<LecturerCanHoldCourse> findLecturersWhoCanHoldCourse(@Param("courseId") int courseId);

    @Query("SELECT DISTINCT c.semester FROM Course c WHERE c.semester IS NOT NULL AND c.semester <> ''")
    List<String> findAllDistinctSemesters();

    List<Course> findBySemester(String semester);
}
