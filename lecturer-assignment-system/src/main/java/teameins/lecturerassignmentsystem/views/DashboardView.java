package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.QueryParameters;

import jakarta.annotation.security.PermitAll;

import com.vaadin.flow.component.UI;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;

import java.util.List;
import java.util.Map;

@Route("dashboard")
@PageTitle("Dashboard")
@PermitAll
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final transient CourseService courseService;
    private final transient LecturerService lecturerService;

    private ComboBox<String> semesterComboBox;
    private Grid<CourseDto> courseGrid;
    private Grid<LecturerDto> unassignedLecturersGrid;
    private H3 courseGridTitle;
    private H3 unassignedLecturersTitle;

    @Autowired
    public DashboardView(CourseService courseService, LecturerService lecturerService) {
        this.courseService = courseService;
        this.lecturerService = lecturerService;

        H2 heading = new H2("Dashboard");
        heading.addClassName("h2-custom");

        add(heading);

        setupSemesterSelector();
        setupGrids();

        add(semesterComboBox, courseGridTitle, courseGrid, unassignedLecturersTitle, unassignedLecturersGrid);
    }

    private void setupSemesterSelector() {
        semesterComboBox = new ComboBox<>("Semester auswählen");
        semesterComboBox.setItems(courseService.getAllSemesters());
        semesterComboBox.addValueChangeListener(event -> {
            updateGrids(event.getValue());
            if (event.isFromClient() && event.getValue() != null && !event.getValue().isEmpty()) {
                UI.getCurrent().navigate(DashboardView.class, QueryParameters.simple(Map.of("semester", event.getValue())));
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String semester = event.getLocation().getQueryParameters().getSingleParameter("semester").orElse(null);
        if (semester != null && courseService.getAllSemesters().contains(semester)) {
            semesterComboBox.setValue(semester);
            updateGrids(semester);
        }
    }

    private void setupGrids() {
        courseGridTitle = new H3("Vorlesungen");
        courseGridTitle.setVisible(false);

        courseGrid = new Grid<>(CourseDto.class, false);
        courseGrid.addColumn(CourseDto::getName).setHeader("Vorlesung").setSortable(true);
        courseGrid.addColumn(c -> c.isMaster() ? "Master" : "Bachelor").setHeader("Typ").setSortable(true);
        courseGrid.addComponentColumn(c -> {
            java.util.Optional<LecturerDto> lecturer = lecturerService.getAssignedLecturerForCourse(c.getId());
            if (lecturer.isPresent()) {
                return new Span(lecturer.get().getFullName());
            } else {
                Span unassigned = new Span("Nicht zugewiesen");
                unassigned.getStyle().set("color", "var(--lumo-secondary-text-color)");
                return unassigned;
            }
        }).setHeader("Zugewiesener Dozent");
        courseGrid.addItemDoubleClickListener(
                event -> UI.getCurrent().navigate(SingleCourseView.class, String.valueOf(event.getItem().getId())));
        courseGrid.setVisible(false);
        courseGrid.setAllRowsVisible(true);

        unassignedLecturersTitle = new H3("Unverplante Dozenten");
        unassignedLecturersTitle.setVisible(false);

        unassignedLecturersGrid = new Grid<>(LecturerDto.class, false);
        unassignedLecturersGrid.addColumn(LecturerDto::getTitle).setHeader("Titel");
        unassignedLecturersGrid.addColumn(LecturerDto::getFirstName).setHeader("Vorname");
        unassignedLecturersGrid.addColumn(LecturerDto::getLastName).setHeader("Nachname").setSortable(true);
        unassignedLecturersGrid.addColumn(LecturerDto::getTeachingPreference).setHeader("Lehrpräferenz");
        unassignedLecturersGrid.addItemDoubleClickListener(
                event -> UI.getCurrent().navigate(SingleLecturerView.class, String.valueOf(event.getItem().getId())));
        unassignedLecturersGrid.setAllRowsVisible(true);
        unassignedLecturersGrid.setVisible(false);
    }

    private void updateGrids(String semester) {
        if (semester == null || semester.isEmpty()) {
            courseGridTitle.setVisible(false);
            courseGrid.setVisible(false);
            unassignedLecturersTitle.setVisible(false);
            unassignedLecturersGrid.setVisible(false);
            return;
        }

        List<CourseDto> courses = courseService.getCoursesBySemester(semester);
        courseGrid.setItems(courses);
        courseGridTitle.setText("Vorlesungen im " + semester);
        courseGridTitle.setVisible(true);
        courseGrid.setVisible(true);

        List<LecturerDto> unassignedLecturers = lecturerService.getUnassignedLecturersForSemester(courses);
        unassignedLecturersGrid.setItems(unassignedLecturers);
        unassignedLecturersTitle.setText("Unverplante Dozenten im " + semester);
        unassignedLecturersTitle.setVisible(true);
        unassignedLecturersGrid.setVisible(true);
    }
}