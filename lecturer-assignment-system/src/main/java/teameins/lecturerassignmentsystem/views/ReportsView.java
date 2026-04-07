package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;

@Route("reports")
@PageTitle("Reports")
public class ReportsView extends VerticalLayout {

    private final transient CourseService courseService;
    private final transient LecturerService lecturerService;

    private final Div reportContent = new Div();

    @Autowired
    public ReportsView(CourseService courseService, LecturerService lecturerService) {
        this.courseService = courseService;
        this.lecturerService = lecturerService;

        H2 heading = new H2("Reports");
        heading.addClassName("h2-custom");

        Div toolbar = buildToolbar();

        reportContent.setWidthFull();
        showPlaceholder();

        add(heading, toolbar, reportContent);
    }

    private Div buildToolbar() {
        Div toolbar = new Div();
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");

        ComboBox<String> reportSelector = new ComboBox<>("Report auswählen");
        reportSelector.setItems(
                "Dozenten-Übersicht",
                "Vorlesungen-Übersicht",
                "Dozenten-Vorlesungs-Zuordnung"
        );
        reportSelector.setPlaceholder("Bitte Report wählen...");
        reportSelector.setWidth("300px");
        reportSelector.setClearButtonVisible(true);

        Button exportButton = new Button("Exportieren", VaadinIcon.DOWNLOAD.create());
        exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        exportButton.setEnabled(false);

        reportSelector.addValueChangeListener(event -> {
            String selected = event.getValue();
            if (selected == null) {
                exportButton.setEnabled(false);
                showPlaceholder();
            } else {
                exportButton.setEnabled(true);
                showReport(selected);
            }
        });

        exportButton.addClickListener(event -> {
            String selected = reportSelector.getValue();
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

    private void showReport(String reportName) {
        reportContent.removeAll();

        H3 reportTitle = new H3(reportName);

        switch (reportName) {
            case "Dozenten-Übersicht" -> reportContent.add(reportTitle, buildLecturersReport());
            case "Vorlesungen-Übersicht" -> reportContent.add(reportTitle, buildCoursesReport());
            case "Dozenten-Vorlesungs-Zuordnung" -> reportContent.add(reportTitle, buildAssignmentReport());
            default -> showPlaceholder();
        }
    }

    private com.vaadin.flow.component.grid.Grid<teameins.lecturerassignmentsystem.model.dto.LecturerDto> buildLecturersReport() {
        var grid = new com.vaadin.flow.component.grid.Grid<>(teameins.lecturerassignmentsystem.model.dto.LecturerDto.class, false);
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerDto::getTitle).setHeader("Titel");
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerDto::getFirstName).setHeader("Vorname");
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerDto::getLastName).setHeader("Nachname");
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerDto::getEmail).setHeader("E-Mail");
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerDto::getPhone).setHeader("Telefon");
        grid.addColumn(dto -> dto.isExtern() ? "Extern" : "Intern").setHeader("Typ");
        grid.setItems(lecturerService.listLecturers());
        grid.setWidthFull();
        return grid;
    }

    private com.vaadin.flow.component.grid.Grid<teameins.lecturerassignmentsystem.model.dto.CourseDto> buildCoursesReport() {
        var grid = new com.vaadin.flow.component.grid.Grid<>(teameins.lecturerassignmentsystem.model.dto.CourseDto.class, false);
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.CourseDto::getName).setHeader("Name");
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.CourseDto::getSemester).setHeader("Semester");
        grid.addColumn(dto -> dto.isMaster() ? "Master" : "Bachelor").setHeader("Studiengang");
        grid.addColumn(dto -> dto.isClosed() ? "Geschlossen" : "Offen").setHeader("Status");
        grid.setItems(courseService.listCourses());
        grid.setWidthFull();
        return grid;
    }

    private com.vaadin.flow.component.grid.Grid<teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto> buildAssignmentReport() {
        var grid = new com.vaadin.flow.component.grid.Grid<>(teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto.class, false);
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto::getLecturerId).setHeader("Dozenten-ID");
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto::getCourseId).setHeader("Vorlesungs-ID");
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto::getQualification).setHeader("Qualifikation");
        grid.addColumn(teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto::getAlreadyHeld).setHeader("Bereits gehalten");
        grid.addColumn(dto -> dto.getPriority() != null && dto.getPriority() ? "Ja" : "Nein").setHeader("Priorität");

        var assignments = courseService.listCourses().stream()
                .flatMap(course -> course.getCanBeHeldBy().stream())
                .toList();
        grid.setItems(assignments);
        grid.setWidthFull();
        return grid;
    }
}
