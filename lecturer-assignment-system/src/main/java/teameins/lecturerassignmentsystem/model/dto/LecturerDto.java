package teameins.lecturerassignmentsystem.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturerDto {

    private Integer id;
    private String title;
    private String firstName;
    private String lastName;
    private String secondName;
    private String email;
    private String phone;
    private boolean isExtern;
    private String preference;
    private List<LecturerCanHoldCourseDto> canHoldCourses;
}
