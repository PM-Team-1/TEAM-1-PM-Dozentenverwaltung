package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import org.aspectj.apache.bcel.classfile.Module;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import teameins.lecturerassignmentsystem.model.enums.FileCreationMode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.service.ExportService;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

@Route("dashboard")
@PageTitle("Dashboard")
@PermitAll
public class DashboardView extends VerticalLayout {

    private final transient ExportService exportService;

    @Autowired
    public DashboardView(ExportService exportService) {
        this.exportService = exportService;

        H2 heading = new H2("Dashboard");
        heading.addClassName("h2-custom");

        add(heading);
        add(getToolbar());
    }

    private Div getToolbar() {
        Div toolbar = new Div();
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");

        return toolbar;
    }
    
}