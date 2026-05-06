package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("")
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        UI.getCurrent().navigate("dashboard");
    }
}
