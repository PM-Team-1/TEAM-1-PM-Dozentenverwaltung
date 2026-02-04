package teameins.lecturerassignmentsystem.model.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LecturerCanHoldCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private AlreadyHeld alreadyHeld;

    @Enumerated(EnumType.STRING)
    private Qualification qualification;

    private int courseId;
    private int lecturerId;

    private boolean priority;
}
