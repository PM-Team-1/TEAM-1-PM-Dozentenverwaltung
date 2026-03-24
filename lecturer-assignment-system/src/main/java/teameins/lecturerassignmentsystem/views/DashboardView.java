package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Route("dashboard")
@PageTitle("Dashboard")
public class DashboardView extends VerticalLayout {

    @Autowired
    public DashboardView() {
        H2 heading = new H2("Dashboard");
        heading.addClassName("h2-custom");

        add(heading);
    }
    
}