package teameins.lecturerassignmentsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
@StyleSheet("styles.css")
public class LecturerAssignmentSystemApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(LecturerAssignmentSystemApplication.class, args);
    }
}
