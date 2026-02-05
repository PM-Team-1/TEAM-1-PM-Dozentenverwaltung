package teameins.lecturerassignmentsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    private Integer id;
    private String name;
    private boolean isClosed;
    private boolean isMaster;
    private String semester;
    private List<LecturerCanHoldCourseDto> canBeHeldBy;
}
