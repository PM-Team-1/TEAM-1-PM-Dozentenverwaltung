package teameins.lecturerassignmentsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturerCanHoldCourseDto {
    private int lecturerId;
    private int courseId;
    private String alreadyHeld;
    private String qualification;
    private boolean priority;
}
