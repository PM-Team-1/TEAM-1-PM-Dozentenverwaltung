package teameins.lecturerassignmentsystem.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.enums.FileCreationMode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import teameins.lecturerassignmentsystem.service.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route("reports")
@PageTitle("Reports")
public class ReportsView extends VerticalLayout {

    private final transient ReportService reportService;
    private final transient ExportService exportService;

    private List<LecturerReportEntity> selectedReportEntities;

    private final Div reportContent = new Div();

    @Autowired
    public ReportsView(ReportService reportService, ExportService exportService) {
        this.reportService = reportService;
        this.exportService = exportService;

        this.selectedReportEntities = new ArrayList<>();

        H2 heading = new H2("Reports");
        heading.addClassName("h2-custom");

        Div toolbar = getToolbar();

        reportContent.setWidthFull();
        showPlaceholder();

        add(heading, toolbar, reportContent);
    }

    private Div getToolbar() {
        Div toolbar = new Div();
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");

        ComboBox<ReportMode> reportSelector = new ComboBox<>("Report auswählen");
        reportSelector.setItems(ReportMode.values());
        reportSelector.setItemLabelGenerator(ReportMode::getHeader);
        reportSelector.setPlaceholder("Bitte Report wählen...");
        reportSelector.setWidth("300px");
        reportSelector.setClearButtonVisible(true);

        ComboBox<FileCreationMode> fileFormatSelector = new ComboBox<>("Dateiformat auswählen");
        fileFormatSelector.setItems(FileCreationMode.values());
        fileFormatSelector.setItemLabelGenerator(Enum::name);
        fileFormatSelector.setPlaceholder("Bitte Dateiformat wählen...");
        fileFormatSelector.setWidth("300px");
        fileFormatSelector.setClearButtonVisible(true);

        Button exportButton = new Button("Exportieren", VaadinIcon.DOWNLOAD.create());
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportButton.setEnabled(false);

        reportSelector.addValueChangeListener(event -> {
            ReportMode selected = event.getValue();
            if (selected == null) {
                exportButton.setEnabled(false);
                showPlaceholder();
            } else {
                if(fileFormatSelector.getValue() != null) exportButton.setEnabled(true);
                showReport(selected);
            }
        });

        fileFormatSelector.addValueChangeListener(event -> {
            FileCreationMode selected = event.getValue();
            if (selected == null) {
                exportButton.setEnabled(false);
            } else {
                if(fileFormatSelector.getValue() != null) exportButton.setEnabled(true);
            }
        });

        exportButton.addClickListener(e -> {
            ReportMode selectedReportMode = reportSelector.getValue();
            FileCreationMode selectedFileCreationMode = fileFormatSelector.getValue();
            byte[] byteArray = exportService.exportFile(
                    selectedFileCreationMode, selectedReportMode, selectedReportEntities);

            if (byteArray.length == 0) {
                Notification.show("Keine Daten für den Export vorhanden.",
                        3000, Notification.Position.MIDDLE);
                return; // <-- kein Download, kein Anchor
            }

            Anchor tmpAnchor = new Anchor(
                    DownloadHandler.fromInputStream(event -> new DownloadResponse(
                            new ByteArrayInputStream(byteArray),
                            selectedReportMode.getFilename() + selectedFileCreationMode.getFileEnd(),
                            selectedFileCreationMode.getContentType(),
                            byteArray.length
                    )),
                    ""
            );
            tmpAnchor.getElement().setAttribute("download", true);
            UI.getCurrent().add(tmpAnchor);
            tmpAnchor.getElement().executeJs("this.click(); this.remove()");
        });

        HorizontalLayout controls = new HorizontalLayout(reportSelector, fileFormatSelector, exportButton);
        controls.setAlignItems(Alignment.END);
        toolbar.add(controls);

        return toolbar;
    }

    private void showPlaceholder() {
        reportContent.removeAll();
        Div placeholder = new Div();
        placeholder.addClassName("report-placeholder");
        Paragraph hint = new Paragraph("Bitte wählen Sie einen Report aus der Liste aus.");
        hint.getStyle().set("color", "var(--lumo-secondary-text-color)");
        placeholder.add(hint);
        reportContent.add(placeholder);
    }

    private void showReport(ReportMode reportMode) {
        reportContent.removeAll();
        H3 reportTitle = new H3(reportMode.getHeader());
        reportContent.add(reportTitle, buildReport(reportMode));
    }

    private Grid<LecturerGridRow> buildReport(ReportMode reportMode) {
        Grid<LecturerGridRow> grid = new Grid<>(LecturerGridRow.class);

        selectedReportEntities = reportService.getReportByReportMode(reportMode);
        List<LecturerGridRow> lecturerGridRows = selectedReportEntities
                .stream()
                .map(this::transformReportEntityToGridRow)
                .flatMap(List::stream)
                .toList();

        addGridColumns(grid, reportMode);
        grid.setItems(lecturerGridRows);
        grid.setWidthFull();
        return grid;
    }

    private void addGridColumns(Grid<LecturerGridRow> grid, ReportMode reportMode) {
        RecordComponent[] components = LecturerGridRow.class.getRecordComponents();
        grid.removeAllColumns();

        for (RecordComponent component : components) {
            Class<?> type = component.getType();

            if (reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS
                    && type == LecturerReportEntity.class) {
                continue;
            }

            Field[] itemFields = type.getDeclaredFields();

            for (Field itemField : itemFields) {
                if (itemField.getType() == List.class) continue;
                itemField.setAccessible(true);

                JsonProperty jsonProperty = itemField.getAnnotation(JsonProperty.class);
                String headerValue = jsonProperty != null ? jsonProperty.value() : itemField.getName();

                // Accessor-Methode des Records nutzen (sauberer als field.get())
                Method accessor = component.getAccessor();

                grid.addColumn(item -> {
                    try {
                        Object parent = accessor.invoke(item);
                        return parent != null ? itemField.get(parent) : "";
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).setHeader(headerValue).setAutoWidth(true);
            }
        }
    }

    private List<LecturerGridRow> transformReportEntityToGridRow(LecturerReportEntity reportEntity) {
        List<LecturerGridRow> lecturerGridRows = new ArrayList<>();
        List<CourseReportEntity> courseReportEntities = reportEntity.getCanHoldCourses();

        if(courseReportEntities != null) {
            for(CourseReportEntity courseReportEntity : courseReportEntities) {
                lecturerGridRows.add(new LecturerGridRow(reportEntity, courseReportEntity));
            }
        } else {
            lecturerGridRows.add(new LecturerGridRow(reportEntity, null));
        }

        return lecturerGridRows;
    }

    public record LecturerGridRow(
                LecturerReportEntity lecturerReportEntity,
                CourseReportEntity courseReportEntity) {
    }
}
