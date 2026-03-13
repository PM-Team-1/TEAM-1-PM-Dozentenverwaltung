package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Route("admin")
@PageTitle("Admin")
public class AdminView extends VerticalLayout {

    @Autowired
    public AdminView() {
        H2 heading = new H2("Admin");
        heading.addClassName("h2-custom");

        add(heading);
    }
    
}