package teameins.lecturerassignmentsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import teameins.lecturerassignmentsystem.model.db.Course;
import teameins.lecturerassignmentsystem.repository.CourseRepository;

@Service
public class CourseService {
    CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course getCourseById(int courseId) throws EntityNotFoundException{
        return courseRepository
                .findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with ID " + courseId + " not found!"));
    }
}
