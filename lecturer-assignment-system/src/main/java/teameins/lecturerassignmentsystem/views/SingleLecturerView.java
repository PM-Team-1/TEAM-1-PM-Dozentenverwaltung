package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;

import java.util.List;

@Route("dozenten")
@PageTitle("Dozent")
public class SingleLecturerView extends VerticalLayout implements HasUrlParameter<String> {

    private final transient LecturerService lecturerService;
    private final transient CourseService courseService;

    @Autowired
    public SingleLecturerView(LecturerService lecturerService, CourseService courseService) {
        this.lecturerService = lecturerService;
        this.courseService = courseService;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        try {
            int id = Integer.parseInt(parameter);
            renderSingleLecturer(id);
        } catch (NumberFormatException ex) {
            renderLecturerNotFoundError("Ungültige ID", "Die ID " + parameter + " ist ungültig.");
        } catch (RuntimeException ex) {
            renderLecturerNotFoundError("Dozent nicht gefunden", "Es konnte kein Dozent mit der ID " + parameter + " gefunden werden.");
        }
    }

    private void renderSingleLecturer(int id) {
        LecturerDto lecturer = lecturerService.getLecturerById(id);

        H2 heading = new H2(lecturer.getFullName());

        VerticalLayout lecturerInfo = new VerticalLayout();
        lecturerInfo.setPadding(false);
        Div toolbar = getToolbar(lecturer);
        VerticalLayout info = getLecturerInfo(lecturer);
        lecturerInfo.add(toolbar, info);

        VerticalLayout courses = new VerticalLayout();
        courses.setPadding(false);
        List<CourseToLecturerRelation> ctlr = lecturer.getCanHoldCourses().stream()
                .map(lchc -> new CourseToLecturerRelation(lchc, courseService))
                .toList();
        Div coursesLecturerCanHold = renderCoursesLecturerCanHold(lecturer, ctlr);
        Div coursesLecturerHasHeld = renderCoursesLecturerHasHeld(ctlr);
        coursesLecturerCanHold.getStyle().set("margin-bottom", "var(--lumo-space-l)");
        courses.add(coursesLecturerCanHold, coursesLecturerHasHeld);

        HorizontalLayout singleLecturer = new HorizontalLayout(lecturerInfo, courses);
        singleLecturer.setWidthFull();

        add(heading, singleLecturer);
    }

    private Div getToolbar(LecturerDto lecturer) {
        Div toolbar = new Div();
        toolbar.addClassName("toolbar");

        Button back = new Button("Zurück zur Übersicht", e -> UI.getCurrent().navigate("dozenten"));
        Button edit = new Button("Bearbeiten", e -> {
            // Implement edit functionality here
        });
        edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        Button delete = new Button("Löschen", e -> {
            // Implement delete functionality here
        });
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        toolbar.add(back, edit, delete);

        return toolbar;
    }

    private VerticalLayout getLecturerInfo(LecturerDto lecturer) {
        VerticalLayout info = new VerticalLayout();
        info.setWidthFull();
        info.setJustifyContentMode(JustifyContentMode.BETWEEN);

        TextField title = new TextField("Titel", lecturer.getTitle());
        TextField firstName = new TextField("Vorname", lecturer.getFirstName());
        TextField lastName = new TextField("Nachname", lecturer.getLastName());
        TextField secondName = new TextField("2. Vorname", lecturer.getSecondName());
        TextField status = new TextField("Status", lecturer.isExtern() ? "Extern" : "Intern");
        TextField email = new TextField("E-Mail", lecturer.getEmail());
        TextField phone = new TextField("Telefonnummer", lecturer.getPhone());

        info.add(title, firstName, lastName, secondName, status, email, phone);

        return info;
    }

    private Div renderCoursesLecturerCanHold(LecturerDto lecturer, List<CourseToLecturerRelation> rows) {
        Div coursesDiv = new Div();
        coursesDiv.setWidthFull();
        Grid<CourseToLecturerRelation> canHoldgrid = new Grid<>();
        canHoldgrid.addClassName("grid-custom");
        canHoldgrid.setAllRowsVisible(true);

        H3 heading = new H3("Vorlesungen, die " + lecturer.getFullName() +  " halten kann:");

        canHoldgrid.addColumn(row -> row.getCourse().getName()).setHeader("Name");
        canHoldgrid.addColumn(row -> row.getCourse().isMaster() ? "Master" : "Bachelor").setHeader("Grad");
        canHoldgrid.addColumn(row -> row.getCourse().getSemester()).setHeader("Semester");
        canHoldgrid.addColumn(row -> row.getCourse().isClosed() ? "Geschlossen" : "Offen").setHeader("Zugänglichkeit");
        canHoldgrid.addColumn(row -> row.getLecturerCanHoldCourse().getQualification()).setHeader("Qualifikation");

        canHoldgrid.setItems(rows);
        coursesDiv.add(heading, canHoldgrid);
        return coursesDiv;
    }

    private Div renderCoursesLecturerHasHeld(List<CourseToLecturerRelation> rows) {
        Div coursesDiv = new Div();
        coursesDiv.setWidthFull();
        H3 heading = new H3("Bereits Gehaltene Vorlesungen:");
        Grid<CourseToLecturerRelation> alredyHeldGrid = new Grid<>();
        alredyHeldGrid.addClassName("grid-custom");
        alredyHeldGrid.setAllRowsVisible(true);

        alredyHeldGrid.addColumn(row -> row.getCourse().getName()).setHeader("Name");
        alredyHeldGrid.addColumn(row -> row.getCourse().isMaster() ? "Master" : "Bachelor").setHeader("Grad");
        alredyHeldGrid.addColumn(row -> mapAlreadyHeld(row.getLecturerCanHoldCourse().getAlreadyHeld()))
            .setHeader("Gehalten an");

        alredyHeldGrid.setItems(rows);
        GridListDataView<CourseToLecturerRelation> dataView = alredyHeldGrid.getListDataView();
        dataView.addFilter(row -> {
            String alreadyHeld = row.getLecturerCanHoldCourse().getAlreadyHeld();
            return alreadyHeld != null && !alreadyHeld.equals(AlreadyHeld.NOT_YET_HELD.getValue());
        });

        coursesDiv.add(heading, alredyHeldGrid);
        return coursesDiv;
    }

    private void renderLecturerNotFoundError(String heading, String details) {
        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(Alignment.CENTER);
        Icon warn = new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
        warn.setClassName("warn");
        H2 title = new H2(heading);
        header.add(warn, title);

        Paragraph desc = new Paragraph(details);

        Button back = new Button("Zurück zur Übersicht", e -> UI.getCurrent().navigate("dozenten"));

        add(header, desc, back);
    }

    private String mapAlreadyHeld(String code) {
        if (code == null || code.isEmpty()) return "-";
        return switch (code.trim().toUpperCase()) {
            case "P" -> "Provadis";
            case "A" -> "Andere Hochschule";
            default -> code;
        };
    }

    @Getter
    private static class CourseToLecturerRelation {

        private final CourseService courseService;
        private final LecturerCanHoldCourseDto lecturerCanHoldCourse;
        private final CourseDto course;

        public CourseToLecturerRelation(LecturerCanHoldCourseDto lecturerCanHoldCourse, CourseService courseService) {
            this.courseService = courseService;
            this.lecturerCanHoldCourse = lecturerCanHoldCourse;
            this.course = courseService.getCourseById(lecturerCanHoldCourse.getCourseId());
        }
    }
}
