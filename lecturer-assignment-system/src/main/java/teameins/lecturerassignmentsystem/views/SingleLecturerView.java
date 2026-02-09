package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
    private transient LecturerDto lecturer;

    private boolean isInEditMode = false;

    @Autowired
    public SingleLecturerView(LecturerService lecturerService, CourseService courseService) {
        this.lecturerService = lecturerService;
        this.courseService = courseService;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        try {
            int id = Integer.parseInt(parameter);
            lecturer = lecturerService.getLecturerById(id);
            renderSingleLecturer(isInEditMode);
        } catch (NumberFormatException ex) {
            renderLecturerNotFoundError("Ungültige ID", "Die ID " + parameter + " ist ungültig.");
        } catch (RuntimeException ex) {
            renderLecturerNotFoundError("Dozent nicht gefunden", "Es konnte kein Dozent mit der ID " + parameter + " gefunden werden.");
        }
    }

    private void renderSingleLecturer(boolean isInEditMode) {
        H2 heading = new H2(lecturer.getFullName());

        VerticalLayout lecturerInfo = new VerticalLayout();
        lecturerInfo.getStyle().set("flex", "0 0 auto");
        lecturerInfo.getStyle().set("width", "auto");

        Div toolbar = getToolbar();
        VerticalLayout info = getLecturerInfo(isInEditMode);
        lecturerInfo.add(toolbar, info);

        VerticalLayout courses = new VerticalLayout();
        courses.getStyle().set("flex", "1 1 auto");
        courses.setWidthFull();

        List<CourseToLecturerRelation> ctlr = lecturer.getCanHoldCourses().stream()
                .map(lchc -> new CourseToLecturerRelation(lchc, courseService))
                .toList();
        Div coursesLecturerCanHold = renderCoursesLecturerCanHold(ctlr);
        Div coursesLecturerHasHeld = renderCoursesLecturerHasHeld(ctlr);
        coursesLecturerCanHold.getStyle().set("margin-bottom", "var(--lumo-space-l)");
        courses.add(coursesLecturerCanHold, coursesLecturerHasHeld);

        HorizontalLayout singleLecturer = new HorizontalLayout(lecturerInfo, courses);
        singleLecturer.setWidthFull();
        singleLecturer.setSpacing(true);
        singleLecturer.setFlexGrow(0, lecturerInfo);
        singleLecturer.setFlexGrow(1, courses);

        add(heading, singleLecturer);
    }

    private Div getToolbar() {
        Div toolbar = new Div();
        toolbar.addClassName("toolbar");

        Button back = new Button("Zurück zur Übersicht", e -> UI.getCurrent().navigate("dozenten"));
        toolbar.add(back);
        Button delete = new Button("Löschen", e -> deleteLecturer());
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        if (!isInEditMode) {
            Button edit = new Button("Bearbeiten", e -> toggleEditMode());
            edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

            toolbar.add(edit, delete);
        } else {
            Button save = new Button("Speichern", e -> {
                saveEdits();
                toggleEditMode();
            });
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

            Button cancel = new Button("Abbrechen", e -> toggleEditMode());
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

            toolbar.add(save, cancel);
        }

        return toolbar;
    }

    private VerticalLayout getLecturerInfo(boolean edit) {
        VerticalLayout info = new VerticalLayout();
        info.setJustifyContentMode(JustifyContentMode.BETWEEN);

        ComboBox<String> title = new ComboBox<>("Titel");
        title.setItems("Dr.", "Prof.", "Keine Angabe");
        title.setValue(lecturer.getTitle().isEmpty() ? "Keine Angabe" : lecturer.getTitle());
        title.setReadOnly(!edit);
        title.setWidthFull();

        TextField lastName = new TextField("Nachname", lecturer.getLastName(), "Nachname");
        lastName.setReadOnly(!edit);
        lastName.setWidthFull();

        TextField firstName = new TextField("Vorname", lecturer.getFirstName(), "Vorname");
        firstName.setReadOnly(!edit);
        firstName.setWidthFull();

        TextField secondName = new TextField("2. Vorname", lecturer.getSecondName() != null ? lecturer.getSecondName() : "", "2. Vorname");
        secondName.setReadOnly(!edit);
        secondName.setWidthFull();

        ComboBox<String> status = new ComboBox<>("Status");
        status.setItems("Intern", "Extern");
        status.setValue(lecturer.isExtern() ? "Extern" : "Intern");
        status.setReadOnly(!edit);
        status.setWidthFull();

        TextField email = new TextField("E-Mail", lecturer.getEmail(), "E-Mail");
        email.setReadOnly(!edit);
        email.setWidthFull();

        TextField phone = new TextField("Telefonnummer", lecturer.getPhone(), "Telefonnummer");
        phone.setReadOnly(!edit);
        phone.setWidthFull();

        info.add(title, lastName, firstName, secondName, status, email, phone);

        return info;
    }

    private Div renderCoursesLecturerCanHold(List<CourseToLecturerRelation> rows) {
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

    private void toggleEditMode() {
        isInEditMode = !isInEditMode;
        removeAll();
        renderSingleLecturer(isInEditMode);
    }

    private void deleteLecturer() {
        // Implement delete functionality here
    }

    private void saveEdits() {
        // Implement save functionality here
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
