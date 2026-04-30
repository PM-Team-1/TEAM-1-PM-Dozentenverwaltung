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
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.enums.FileCreationMode;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerCanHoldCourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import teameins.lecturerassignmentsystem.service.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Route("reports")
@PageTitle("Reports")
public class ReportsView extends VerticalLayout {

    private final transient ReportService reportService;
    private final transient ExportService exportService;

    private List<?> selectedReportEntities;

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
                if (fileFormatSelector.getValue() != null) exportButton.setEnabled(true);
                showReport(selected);
            }
        });

        fileFormatSelector.addValueChangeListener(event -> {
            FileCreationMode selected = event.getValue();
            if (selected == null) {
                exportButton.setEnabled(false);
            } else {
                if (reportSelector.getValue() != null) exportButton.setEnabled(true);
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
                return;
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

    private Grid<ReportGridRow> buildReport(ReportMode reportMode) {
        selectedReportEntities = reportService.getReportByReportMode(reportMode);
        List<ReportGridRow> rows = buildGridRows(reportMode, selectedReportEntities);

        Grid<ReportGridRow> grid = new Grid<>(ReportGridRow.class, false);
        addGridColumns(grid, reportMode);
        grid.setItems(rows);
        grid.setWidthFull();
        return grid;
    }

    @SuppressWarnings("unchecked")
    private List<ReportGridRow> buildGridRows(ReportMode reportMode, List<?> entities) {
        List<ReportGridRow> rows = new ArrayList<>();
        if (reportMode.isCourseBased()) {
            for (CourseReportEntity course : (List<CourseReportEntity>) entities) {
                List<LecturerCanHoldCourseReportEntity> lchcs = course.getCanBeHeldBy();
                if (lchcs == null || lchcs.isEmpty()) {
                    rows.add(new ReportGridRow(null, null, course));
                } else {
                    for (LecturerCanHoldCourseReportEntity lchc : lchcs) {
                        rows.add(new ReportGridRow(lchc.getLecturer(), lchc, course));
                    }
                }
            }
        } else {
            for (LecturerReportEntity lecturer : (List<LecturerReportEntity>) entities) {
                List<LecturerCanHoldCourseReportEntity> lchcs = lecturer.getCanHoldCourses();
                if (lchcs == null || lchcs.isEmpty()) {
                    rows.add(new ReportGridRow(lecturer, null, null));
                } else {
                    for (LecturerCanHoldCourseReportEntity lchc : lchcs) {
                        rows.add(new ReportGridRow(lecturer, lchc, lchc.getCourse()));
                    }
                }
            }
        }
        return rows;
    }

    private void addGridColumns(Grid<ReportGridRow> grid, ReportMode reportMode) {
        grid.removeAllColumns();
        if (reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS) {
            // Report 3: nur Course-Felder
            addColumnsFor(grid, CourseReportEntity.class, ReportGridRow::course);
        } else if (reportMode.isCourseBased()) {
            // Course-based: Course -> Lecturer -> LCHC
            addColumnsFor(grid, CourseReportEntity.class, ReportGridRow::course);
            addColumnsFor(grid, LecturerReportEntity.class, ReportGridRow::lecturer);
            addColumnsFor(grid, LecturerCanHoldCourseReportEntity.class, ReportGridRow::lchc);
        } else {
            // Lecturer-based: Lecturer -> Course -> LCHC
            addColumnsFor(grid, LecturerReportEntity.class, ReportGridRow::lecturer);
            addColumnsFor(grid, CourseReportEntity.class, ReportGridRow::course);
            addColumnsFor(grid, LecturerCanHoldCourseReportEntity.class, ReportGridRow::lchc);
        }
    }

    private <T> void addColumnsFor(Grid<ReportGridRow> grid, Class<T> clazz, Function<ReportGridRow, ?> accessor) {
        for (Field field : clazz.getDeclaredFields()) {
            int mods = field.getModifiers();
            if (Modifier.isStatic(mods) || Modifier.isTransient(mods) || field.isSynthetic()) continue;
            if (field.getType() == List.class) continue;
            field.setAccessible(true);

            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            String header = jsonProperty != null ? jsonProperty.value() : field.getName();

            grid.addColumn(item -> {
                Object parent = accessor.apply(item);
                if (parent == null) return "";
                try {
                    Object val = field.get(parent);
                    return val != null ? val.toString() : "";
                } catch (IllegalAccessException e) {
                    return "";
                }
            }).setHeader(header).setAutoWidth(true);
        }
    }

    public record ReportGridRow(
            LecturerReportEntity lecturer,
            LecturerCanHoldCourseReportEntity lchc,
            CourseReportEntity course) {
    }
}
