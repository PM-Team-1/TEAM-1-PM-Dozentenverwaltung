package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;


@Route("")
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new AppLayoutBasic());

    }
}
