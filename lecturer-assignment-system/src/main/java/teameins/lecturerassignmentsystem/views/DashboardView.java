package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import teameins.lecturerassignmentsystem.model.enums.FileCreationMode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.service.ExportService;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

@Route("dashboard")
@PageTitle("Dashboard")
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

        Anchor pdfAnchor = new Anchor(DownloadHandler.fromInputStream(event -> {
            FileCreationMode creationMode = FileCreationMode.PDF;
            return getDownloadResponse(creationMode);
        }), "PDF herunterladen");

        Anchor csvAnchor = new Anchor(DownloadHandler.fromInputStream(event -> {
            FileCreationMode creationMode = FileCreationMode.CSV;
            return getDownloadResponse(creationMode);
        }), "CSV herunterladen");

        Anchor jsonAnchor = new Anchor(DownloadHandler.fromInputStream(event -> {
            FileCreationMode creationMode = FileCreationMode.JSON;
            return getDownloadResponse(creationMode);
        }), "JSON herunterladen");

        toolbar.add(pdfAnchor, csvAnchor, jsonAnchor);

        return toolbar;
    }

    private DownloadResponse getDownloadResponse(FileCreationMode creationMode) {
        ReportMode reportMode = ReportMode.ALL_COURSES_IN_PROVADIS;
        byte[] fileBytes = exportService.exportFile(creationMode, reportMode, new ArrayList<>());
        return new DownloadResponse(
                new ByteArrayInputStream(fileBytes),
                reportMode.getFilename() + creationMode.getFileEnd(),
                creationMode.getContentType(),
                fileBytes.length);
    }
    
}