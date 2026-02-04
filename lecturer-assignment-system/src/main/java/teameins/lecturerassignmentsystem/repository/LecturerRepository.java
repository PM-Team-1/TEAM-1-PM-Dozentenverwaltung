package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;

import java.util.List;

public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {

    @Query("select lchc from LecturerCanHoldCourse lchc where lchc.lecturer.id = :lecturerId")
    List<LecturerCanHoldCourse> findCoursesLecturerCanHold(@Param("lecturerId") int lecturerId);
}
