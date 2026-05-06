package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    public HomeView() {
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.forwardTo(DashboardView.class);
    }
}
