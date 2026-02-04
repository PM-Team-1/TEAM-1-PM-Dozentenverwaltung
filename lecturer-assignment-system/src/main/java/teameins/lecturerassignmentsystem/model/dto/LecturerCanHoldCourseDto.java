package teameins.lecturerassignmentsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturerCanHoldCourseDto {
    private LecturerDto lecturer;
    private CourseDto course;
    private AlreadyHeld alreadyHeld;
    private Qualification qualification;
    private boolean priority;
}
