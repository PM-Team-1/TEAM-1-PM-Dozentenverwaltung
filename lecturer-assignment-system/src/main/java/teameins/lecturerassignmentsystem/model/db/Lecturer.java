package teameins.lecturerassignmentsystem.model.db;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.enums.Title;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private Title title;

    private String firstName;
    private String lastName;

    @Nullable
    private String secondName;
    private String email;
    private String phone;
    private boolean isExtern;

    @Enumerated(EnumType.STRING)
    private Preference preference;
}
