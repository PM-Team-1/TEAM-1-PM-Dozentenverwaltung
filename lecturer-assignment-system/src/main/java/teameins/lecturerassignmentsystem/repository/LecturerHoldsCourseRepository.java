package teameins.lecturerassignmentsystem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import teameins.lecturerassignmentsystem.model.db.relation.LecturerHoldsCourse;

import java.util.List;
import java.util.Optional;
public interface LecturerHoldsCourseRepository extends CrudRepository<LecturerHoldsCourse, Integer> {
    @Query("""
    	    SELECT lhc.lecturer.id
    	    FROM LecturerHoldsCourse lhc
    	    WHERE lhc.course.id = :courseId
    	""")
    Optional<Integer> findLecturerIdByCourseId(int courseId);
    Optional<LecturerHoldsCourse> findByCourseId(int courseId);
    @Query("""
    	    SELECT lhc.lecturer.firstName
    	    FROM LecturerHoldsCourse lhc
    	    WHERE lhc.course.id = :courseId
    	""")
    Optional<String> findFirstNameByCourseId(int courseId);
    @Query("""
    	    SELECT lhc.lecturer.lastName
    	    FROM LecturerHoldsCourse lhc
    	    WHERE lhc.course.id = :courseId
    	""")
    Optional<String> findLastNameByCourseId(int courseId);
    @Query("""
    	    SELECT lhc.lecturer.secondName
    	    FROM LecturerHoldsCourse lhc
    	    WHERE lhc.course.id = :courseId
    	""")
    Optional<String> findSecondNameByCourseId(int courseId);
    boolean existsByCourseId(int courseId);
    List<LecturerHoldsCourse> findByLecturerId(int lecturerId);
}
