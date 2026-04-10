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
            byte[] fileBytes = exportService.exportFile(creationMode, new ArrayList<>());
            event.setFileName(creationMode.getFileName());
            return new DownloadResponse(new ByteArrayInputStream(fileBytes), creationMode.getFileName(), creationMode.getContentType(), fileBytes.length);
        }), "PDF herunterladen");

        Anchor csvAnchor = new Anchor(DownloadHandler.fromInputStream(event -> {
            FileCreationMode creationMode = FileCreationMode.CSV;
            byte[] fileBytes = exportService.exportFile(creationMode, new ArrayList<>());
            event.setFileName(creationMode.getFileName());
            return new DownloadResponse(new ByteArrayInputStream(fileBytes), creationMode.getFileName(), creationMode.getContentType(), fileBytes.length);
        }), "CSV herunterladen");

        Anchor jsonAnchor = new Anchor(DownloadHandler.fromInputStream(event -> {
            FileCreationMode creationMode = FileCreationMode.JSON;
            byte[] fileBytes = exportService.exportFile(creationMode, new ArrayList<>());
            event.setFileName(creationMode.getFileName());
            return new DownloadResponse(new ByteArrayInputStream(fileBytes), creationMode.getFileName(), creationMode.getContentType(), fileBytes.length);
        }), "JSON herunterladen");

        toolbar.add(pdfAnchor, csvAnchor, jsonAnchor);

        return toolbar;
    }
    
}