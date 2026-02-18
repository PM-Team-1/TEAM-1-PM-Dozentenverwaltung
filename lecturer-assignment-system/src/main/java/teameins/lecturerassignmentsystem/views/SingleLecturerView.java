package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.router.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;

import java.util.List;

@Route("dozenten")
@PageTitle("Dozent")
public class SingleLecturerView extends VerticalLayout implements HasUrlParameter<String> {

    private final transient LecturerService lecturerService;
    private final transient CourseService courseService;
    private transient LecturerDto lecturer;

    private static final String ALL_LECTURERS_VIEW_ROUTE = "dozenten";

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
        } catch (LecturerNotFoundException ex) {
            renderLecturerNotFoundError("Dozent nicht gefunden", ex.getMessage());
        } catch (Exception ex) {
            renderLecturerNotFoundError("Fehler", "Es ist ein unerwarteter Fehler aufgetreten: " + ex.getMessage());
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

        Button back = new Button("Zurück zur Übersicht", e -> UI.getCurrent().navigate(ALL_LECTURERS_VIEW_ROUTE));
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

        H3 heading = new H3("Vorlesungen, die " + lecturer.getFullName() + " halten kann:");
        heading.getStyle().setMarginBottom("var(--lumo-space-m)");

        canHoldgrid.addColumn(row -> row.getCourse().getName()).setHeader("Name")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> row.getCourse().isMaster() ? "Master" : "Bachelor").setHeader("Grad")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> row.getCourse().getSemester()).setHeader("Semester")
                .setSortable(true).setComparator(CourseToLecturerRelation::getSemesterSortable)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> row.getCourse().isClosed() ? "Geschlossen" : "Offen").setHeader("Zugänglichkeit")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> mapQualification(row.getLecturerCanHoldCourse().getQualification())).setHeader("benötigte Vorbereitungszeit")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);

        canHoldgrid.setItems(rows);
        coursesDiv.add(heading, canHoldgrid);
        return coursesDiv;
    }

    private Div renderCoursesLecturerHasHeld(List<CourseToLecturerRelation> rows) {
        Div coursesDiv = new Div();
        coursesDiv.setWidthFull();
        H3 heading = new H3("Bereits Gehaltene Vorlesungen:");
        heading.getStyle().setMarginBottom("var(--lumo-space-m)");

        Grid<CourseToLecturerRelation> alredyHeldGrid = new Grid<>();
        alredyHeldGrid.addClassName("grid-custom");
        alredyHeldGrid.setAllRowsVisible(true);

        alredyHeldGrid.addColumn(row -> row.getCourse().getName()).setHeader("Name")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        alredyHeldGrid.addColumn(row -> row.getCourse().isMaster() ? "Master" : "Bachelor").setHeader("Grad")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        alredyHeldGrid.addColumn(row -> mapAlreadyHeld(row.getLecturerCanHoldCourse().getAlreadyHeld())).setHeader("Gehalten an")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);

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

        Button back = new Button("Zurück zur Übersicht", e -> UI.getCurrent().navigate(ALL_LECTURERS_VIEW_ROUTE));

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

    private String mapQualification(String code) {
        if (code == null || code.isEmpty()) return "-";
        return switch (code.trim().toUpperCase()) {
            case "M" -> "Über vier Wochen";
            case "S" -> "Keine";
            case "4" -> "vier Wochen";
            default -> code;
        };
    }

    private void toggleEditMode() {
        isInEditMode = !isInEditMode;
        removeAll();
        renderSingleLecturer(isInEditMode);
    }

    private void deleteLecturer() {
        Dialog confirmDelete = new Dialog();
        confirmDelete.add(new H3("Möchten Sie " + lecturer.getFullName() + " tatsächlich aus dem Verwaltungssystem löschen?"));
        confirmDelete.addClassName("dialog");

        Div deleteOrCancel = new Div();
        deleteOrCancel.setClassName("toolbar");
        deleteOrCancel.getStyle().setMarginTop("var(--lumo-space-l)");

        Button confirmButton = new Button("Löschen", e -> {
            lecturerService.deleteLecturer(lecturer);
            confirmDelete.close();
            UI.getCurrent().navigate(ALL_LECTURERS_VIEW_ROUTE);
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Abbrechen", e -> confirmDelete.close());

        deleteOrCancel.add(confirmButton, cancelButton);
        confirmDelete.add(deleteOrCancel);
        confirmDelete.open();
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

        public String getSemesterSortable() {
            String semester = this.getCourse().getSemester();
            String yearPart = semester.replaceAll("\\D", "");
            if (yearPart.contains("/")) {
                yearPart = yearPart.split("/")[0];
            }
            String termPart = semester.toLowerCase().contains("winter") ? "1" : "2";
            return yearPart + termPart;
        }
    }
}
