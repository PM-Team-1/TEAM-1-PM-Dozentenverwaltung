package teameins.lecturerassignmentsystem.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.db.Lecturer;
import teameins.lecturerassignmentsystem.model.db.LecturerCanHoldCourse;
import teameins.lecturerassignmentsystem.model.enums.ReportMode;
import teameins.lecturerassignmentsystem.model.report.CourseReportEntity;
import teameins.lecturerassignmentsystem.model.report.LecturerReportEntity;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;
import teameins.lecturerassignmentsystem.service.MappingService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route("reports")
@PageTitle("Reports")
public class ReportsView extends VerticalLayout {

    private final transient CourseService courseService;
    private final transient LecturerService lecturerService;
    private final transient MappingService mappingService;

    private final Div reportContent = new Div();

    @Autowired
    public ReportsView(CourseService courseService, LecturerService lecturerService, MappingService mappingService) {
        this.courseService = courseService;
        this.lecturerService = lecturerService;
        this.mappingService = mappingService;

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

        Button exportButton = new Button("Exportieren", VaadinIcon.DOWNLOAD.create());
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportButton.setEnabled(false);

        reportSelector.addValueChangeListener(event -> {
            ReportMode selected = event.getValue();
            if (selected == null) {
                exportButton.setEnabled(false);
                showPlaceholder();
            } else {
                exportButton.setEnabled(true);
                showReport(selected);
            }
        });

        exportButton.addClickListener(event -> {
            ReportMode selected = reportSelector.getValue();
            if (selected != null) {
                // TODO: Export-Logik implementieren (z.B. CSV/PDF-Export)
            }
        });

        HorizontalLayout controls = new HorizontalLayout(reportSelector, exportButton);
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
        List<Lecturer> lecturers = lecturerService.listLecturers();
        List<LecturerReportEntity> lecturerReportEntities = lecturers
                .stream()
                .map(lecturer -> mappingService.mapReport(lecturer, lecturerService.getCoursesLecturerCanHold(lecturer.getId())))
                .toList();
        List<LecturerGridRow> lecturerGridRows = lecturerReportEntities
                .stream()
                .map(this::transformReportEntityToGridRow)
                .flatMap(List::stream)
                .toList();

        addGridColumns(grid, reportMode);
        grid.setItems(lecturerGridRows);
        grid.setWidthFull();
        return grid;
    }

    private void getReportEntites(ReportMode reportMode) {

    }

    private void addGridColumns(Grid<LecturerGridRow> grid, ReportMode reportMode) {
        Field[] fields = LecturerGridRow.class.getDeclaredFields();

        for(Field field : fields) {
            Class<?> type = field.getType();
            if(reportMode == ReportMode.ALL_COURSES_WITH_NO_LECTURERS && type == LecturerReportEntity.class) {
                continue;
            }
            Field[] itemFields = type.getDeclaredFields();
            field.setAccessible(true);

            for(Field itemField : itemFields) {
                Class<?> itemType = itemField.getType();
                itemField.setAccessible(true);
                if(itemType != List.class) {
                    JsonProperty jsonProperty = itemField.getAnnotation(JsonProperty.class);
                    String headerValue = jsonProperty != null ? jsonProperty.value() : itemField.getName();

                    grid.addColumn(item -> {
                        try {
                            if(field.get(item) != null) {
                                return itemField.get(field.get(item));
                            }
                            return "";
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }).setHeader(headerValue);
                }
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

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private class LecturerGridRow {
        LecturerReportEntity lecturerReportEntity;
        CourseReportEntity courseReportEntity;
    }
}
