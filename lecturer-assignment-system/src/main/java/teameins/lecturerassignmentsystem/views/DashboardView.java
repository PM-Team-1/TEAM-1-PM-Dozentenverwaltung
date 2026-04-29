package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Route("dashboard")
@PageTitle("Dashboard")
@PermitAll
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        H2 heading = new H2("Dashboard");
        heading.addClassName("h2-custom");

        add(heading);
    }
    
}