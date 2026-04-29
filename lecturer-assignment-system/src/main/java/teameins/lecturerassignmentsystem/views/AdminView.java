package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Route("admin")
@PageTitle("Admin")
@RolesAllowed("ROLE_ADMIN")
public class AdminView extends VerticalLayout {

    public AdminView() {
        H2 heading = new H2("Admin");
        heading.addClassName("h2-custom");

        add(heading);
    }
    
}