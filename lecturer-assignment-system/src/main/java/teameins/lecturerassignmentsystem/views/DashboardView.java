package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;

import java.util.List;
import java.util.Optional;

@Route("dashboard")
@PageTitle("Dashboard")
public class DashboardView extends VerticalLayout {

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
        add(getToolbar());
        
        setupSemesterSelector();
        setupGrids();
        
        add(semesterComboBox, courseGridTitle, courseGrid, unassignedLecturersTitle, unassignedLecturersGrid);
    }

    private Div getToolbar() {
        Div toolbar = new Div();
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void setupSemesterSelector() {
        semesterComboBox = new ComboBox<>("Semester auswählen");
        semesterComboBox.setItems(courseService.getAllSemesters());
        semesterComboBox.addValueChangeListener(event -> updateGrids(event.getValue()));
    }

    private void setupGrids() {
        courseGridTitle = new H3("Vorlesungen");
        courseGridTitle.setVisible(false);
        
        courseGrid = new Grid<>(CourseDto.class, false);
        courseGrid.addColumn(CourseDto::getName).setHeader("Vorlesung").setSortable(true);
        courseGrid.addColumn(c -> c.isMaster() ? "Master" : "Bachelor").setHeader("Typ").setSortable(true);
        courseGrid.addColumn(c -> {
            Optional<LecturerDto> lecturer = lecturerService.getAssignedLecturerForCourse(c.getId());
            return lecturer.map(LecturerDto::getFullName).orElse("Nicht zugewiesen");
        }).setHeader("Zugewiesener Dozent");
        courseGrid.addItemClickListener(event -> 
            UI.getCurrent().navigate(SingleCourseView.class, String.valueOf(event.getItem().getId()))
        );
        courseGrid.setVisible(false);

        unassignedLecturersTitle = new H3("Unverplante Dozenten");
        unassignedLecturersTitle.setVisible(false);

        unassignedLecturersGrid = new Grid<>(LecturerDto.class, false);
        unassignedLecturersGrid.addColumn(LecturerDto::getTitle).setHeader("Titel");
        unassignedLecturersGrid.addColumn(LecturerDto::getFirstName).setHeader("Vorname");
        unassignedLecturersGrid.addColumn(LecturerDto::getLastName).setHeader("Nachname").setSortable(true);
        unassignedLecturersGrid.addColumn(LecturerDto::getTeachingPreference).setHeader("Lehrpräferenz");
        unassignedLecturersGrid.addItemClickListener(event -> 
            UI.getCurrent().navigate(SingleLecturerView.class, String.valueOf(event.getItem().getId()))
        );
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