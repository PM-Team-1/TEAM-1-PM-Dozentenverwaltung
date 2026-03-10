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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Preference;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;

import java.util.List;

import static teameins.lecturerassignmentsystem.model.enums.AlreadyHeld.mapAlreadyHeld;
import static teameins.lecturerassignmentsystem.model.enums.Qualification.mapQualification;

@Route("dozenten")
@PageTitle("Dozent")
public class SingleLecturerView extends VerticalLayout implements HasUrlParameter<String> {

    private final transient LecturerService lecturerService;
    private final transient CourseService courseService;
    private transient LecturerDto lecturer;

    private static final String ALL_LECTURERS_VIEW_ROUTE = "dozenten";

    private boolean isInEditMode = false;

    private final Binder<LecturerDto> binder = new Binder<>(LecturerDto.class);

    @Autowired
    public SingleLecturerView(LecturerService lecturerService, CourseService courseService) {
        this.lecturerService = lecturerService;
        this.courseService = courseService;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        try {
            if ("neu".equalsIgnoreCase(parameter)) {
                lecturer = new LecturerDto();
                lecturer.setExtern(false);
                lecturer.setCanHoldCourses(List.of());
                lecturer.setPreference(Preference.ALLES.getValue()); // Default
                isInEditMode = true;
                removeAll();
                renderSingleLecturer(true);
                return;
            }

            int id = Integer.parseInt(parameter);
            lecturer = lecturerService.getLecturerById(id);
            isInEditMode = false;
            removeAll();
            renderSingleLecturer(false);
        } catch (NumberFormatException ex) {
            renderLecturerNotFoundError("Ungültige ID", "Die ID " + parameter + " ist ungültig.");
        } catch (LecturerNotFoundException ex) {
            renderLecturerNotFoundError("Dozent nicht gefunden", ex.getMessage());
        } catch (Exception ex) {
            renderLecturerNotFoundError("Fehler", "Es ist ein unerwarteter Fehler aufgetreten: " + ex.getMessage());
        }
    }

    private void renderSingleLecturer(boolean isInEditMode) {
        boolean isNewLecturer =
                lecturer.getId() == 0
                        && (lecturer.getFirstName() == null || lecturer.getFirstName().isBlank())
                        && (lecturer.getLastName() == null || lecturer.getLastName().isBlank());

        H2 heading = new H2(isNewLecturer ? "Neuen Dozenten anlegen" : lecturer.getFullName());

        VerticalLayout lecturerInfo = new VerticalLayout();
        lecturerInfo.getStyle().set("flex", "0 0 auto");
        lecturerInfo.getStyle().set("width", "auto");

        Div toolbar = getToolbar();
        VerticalLayout info = getLecturerInfo(isInEditMode);
        lecturerInfo.add(toolbar, info);

        VerticalLayout courses = new VerticalLayout();
        courses.getStyle().set("flex", "1 1 auto");
        courses.setWidthFull();

        List<CourseToLecturerRelation> ctlr = lecturer.getCanHoldCourses() == null
                ? List.of()
                : lecturer.getCanHoldCourses().stream()
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

        boolean isNewLecturer = lecturer.getId() == 0;

        if (!isInEditMode) {
            Button edit = new Button("Bearbeiten", e -> toggleEditLecturerMode());
            edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            toolbar.add(edit);

            if (!isNewLecturer) {
                Button delete = new Button("Löschen", e -> deleteLecturer());
                delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
                toolbar.add(delete);
            }
        } else {
            Button save = new Button("Speichern", e -> {
                if (saveEdits()) {
                    toggleEditLecturerMode();
                }
            });
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

            Button cancel = new Button("Abbrechen", e -> {
                if (isNewLecturer) {
                    UI.getCurrent().navigate(ALL_LECTURERS_VIEW_ROUTE);
                } else {
                    toggleEditLecturerMode();
                }
            });
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
        title.setValue(
                lecturer.getTitle() == null || lecturer.getTitle().isBlank()
                        ? "Keine Angabe"
                        : lecturer.getTitle()
        );
        title.setReadOnly(!edit);
        title.setWidthFull();

        TextField lastName = new TextField(
                "Nachname",
                lecturer.getLastName() != null ? lecturer.getLastName() : "",
                "Nachname"
        );
        lastName.setReadOnly(!edit);
        lastName.setWidthFull();

        TextField firstName = new TextField(
                "Vorname",
                lecturer.getFirstName() != null ? lecturer.getFirstName() : "",
                "Vorname"
        );
        firstName.setReadOnly(!edit);
        firstName.setWidthFull();

        TextField secondName = new TextField(
                "2. Vorname",
                lecturer.getSecondName() != null ? lecturer.getSecondName() : "",
                "2. Vorname"
        );
        secondName.setReadOnly(!edit);
        secondName.setWidthFull();

        ComboBox<String> status = new ComboBox<>("Status");
        status.setItems("Intern", "Extern");
        status.setValue(lecturer.isExtern() ? "Extern" : "Intern");
        status.setReadOnly(!edit);
        status.setWidthFull();

        TextField email = new TextField(
                "E-Mail",
                lecturer.getEmail() != null ? lecturer.getEmail() : "",
                "E-Mail"
        );
        email.setReadOnly(!edit);
        email.setWidthFull();

        TextField phone = new TextField(
                "Telefonnummer",
                lecturer.getPhone() != null ? lecturer.getPhone() : "",
                "Telefonnummer"
        );
        phone.setReadOnly(!edit);
        phone.setWidthFull();

        binder.removeBean();

        binder.forField(title)
                .asRequired("Titel auswählen")
                .withValidator(
                        value -> "Keine Angabe".equals(value) || LecturerDto.validateTitle(value),
                        "Gültigen Titel angeben"
                )
                .bind(
                        dto -> dto.getTitle() == null || dto.getTitle().isBlank() ? "Keine Angabe" : dto.getTitle(),
                        (dto, value) -> dto.setTitle("Keine Angabe".equals(value) ? "" : value)
                );

        binder.forField(lastName)
                .asRequired("Nachname darf nicht leer sein")
                .withValidator(LecturerDto::validateLastName, "Nachname darf nicht leer sein")
                .bind(LecturerDto::getLastName, LecturerDto::setLastName);

        binder.forField(firstName)
                .asRequired("Vorname darf nicht leer sein")
                .withValidator(LecturerDto::validateFirstName, "Vorname darf nicht leer sein")
                .bind(LecturerDto::getFirstName, LecturerDto::setFirstName);

        binder.forField(secondName)
                .bind(
                        dto -> dto.getSecondName() == null ? "" : dto.getSecondName(),
                        (dto, value) -> dto.setSecondName(value == null || value.isBlank() ? null : value)
                );

        binder.forField(status)
                .asRequired("Status auswählen")
                .bind(
                        dto -> dto.isExtern() ? "Extern" : "Intern",
                        (dto, value) -> dto.setExtern("Extern".equals(value))
                );

        binder.forField(email)
                .asRequired("E-Mail darf nicht leer sein")
                .withValidator(LecturerDto::validateEmail, "Die E-Mail Adresse muss ein @ enthalten")
                .bind(LecturerDto::getEmail, LecturerDto::setEmail);

        binder.forField(phone)
                .asRequired("Telefonnummer darf nicht leer sein")
                .withValidator(
                        LecturerDto::validatePhone,
                        "Die Telefonnummer darf nur Ziffern und optional ein führendes + enthalten"
                )
                .bind(LecturerDto::getPhone, LecturerDto::setPhone);

        binder.readBean(lecturer);

        info.add(title, lastName, firstName, secondName, status, email, phone);
        return info;
    }

    private Div renderCoursesLecturerCanHold(List<CourseToLecturerRelation> rows) {
        Div coursesDiv = new Div();
        coursesDiv.setWidthFull();
        Grid<CourseToLecturerRelation> canHoldgrid = new Grid<>();
        canHoldgrid.addClassName("grid-custom");
        canHoldgrid.setAllRowsVisible(true);

        String lecturerName = lecturer.getFullName() == null || lecturer.getFullName().isBlank()
                ? "dieser Dozent"
                : lecturer.getFullName();

        H3 heading = new H3("Vorlesungen, die " + lecturerName + " halten kann:");
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
        H3 heading = new H3("Bereits gehaltene Vorlesungen:");
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

    private void toggleEditLecturerMode() {
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

    private boolean saveEdits() {
        try {
            binder.writeBean(lecturer);

            if (lecturer.getId() == 0) {
                lecturer = lecturerService.createLecturer(lecturer);
            } else {
                lecturer = lecturerService.updateLecturer(lecturer);
            }

            return true;
        } catch (Exception ex) {
            Dialog errorDialog = new Dialog();
            errorDialog.add(new H3("Validierungsfehler"));
            errorDialog.add(new Paragraph("Die Änderungen konnten nicht gespeichert werden: " + ex.getMessage()));
            Button closeButton = new Button("Schließen", e -> errorDialog.close());
            errorDialog.add(closeButton);
            errorDialog.open();
            return false;
        }
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