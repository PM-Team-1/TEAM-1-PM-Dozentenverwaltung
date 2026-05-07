package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
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
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.DataView;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;
import teameins.lecturerassignmentsystem.model.exception.LecturerNotFoundException;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;
import teameins.lecturerassignmentsystem.views.components.AddCourseToLecturerDialog;
import teameins.lecturerassignmentsystem.views.components.ValidationErrorDialog;
import teameins.lecturerassignmentsystem.views.model.CourseToLecturerRelation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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

        List<CourseToLecturerRelation> ctlr = lecturer.getCanHoldCourses() == null
                ? List.of()
                : lecturer.getCanHoldCourses().stream()
                .map(lchc -> new CourseToLecturerRelation(lchc, courseService))
                .toList();

        Button addCourseButton = new Button("Vorlesung hinzufügen", e -> new AddCourseToLecturerDialog(lecturer, courseService, lecturerService));
        addCourseButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Div coursesLecturerCanHold = renderCoursesLecturerCanHold(ctlr);
        coursesLecturerCanHold.getStyle().set("margin-bottom", "var(--lumo-space-l)");

        addCourseButton.getStyle().set("margin-bottom", "var(--lumo-space-l)");
        courses.add(coursesLecturerCanHold, addCourseButton);

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

        if (!isInEditMode) {
            Button edit = new Button("Bearbeiten", e -> toggleEditLecturerMode());
            edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            toolbar.add(edit);

            Button delete = new Button("Löschen", e -> deleteLecturer());
            delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            toolbar.add(delete);

        } else {
            Button save = new Button("Speichern", e -> {
                if (saveEdits()) {
                    toggleEditLecturerMode();
                }
            });
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

            Button cancel = new Button("Abbrechen", e -> toggleEditLecturerMode());
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

            toolbar.add(save, cancel);
        }

        return toolbar;
    }

    private VerticalLayout getLecturerInfo(boolean edit) {
        VerticalLayout info = new VerticalLayout();
        info.setJustifyContentMode(JustifyContentMode.BETWEEN);

        ComboBox<String> title = new ComboBox<>("Titel");
        title.setItems("Dr.", "Prof.", "Kein Titel");
        title.setValue(
                lecturer.getTitle() == null || lecturer.getTitle().isBlank()
                        ? "Kein Titel"
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
        bindTitle(title);
        bindLastName(lastName);
        bindFirstName(firstName);
        bindSecondName(secondName);
        bindStatus(status);
        bindEmail(email);
        bindPhone(phone);

        binder.readBean(lecturer);

        info.add(title, lastName, firstName, secondName, status, email, phone);

        return info;
    }

    private void bindTitle(ComboBox<String> title) {
        binder.forField(title)
                .withValidator(
                        value -> "Kein Titel".equals(value) || (value != null && !value.isBlank()),
                        "Gültigen Titel angeben"
                )
                .bind(
                        dto -> dto.getTitle() == null || dto.getTitle().isBlank() ? "Kein Titel" : dto.getTitle(),
                        (dto, value) -> dto.setTitle("Kein Titel".equals(value) ? "" : value)
                );
    }

    private void bindLastName(TextField lastName) {
        binder.forField(lastName)
                .withValidator((value, context) -> {
                    String validationResult = LecturerDto.validateLastName(value);
                    return validationResult.isEmpty() ? ValidationResult.ok() : ValidationResult.error(validationResult);
                })
                .bind(LecturerDto::getLastName, LecturerDto::setLastName);
    }

    private void bindFirstName(TextField firstName) {
        binder.forField(firstName)
                .withValidator((value, context) -> {
                    String validationResult = LecturerDto.validateFirstName(value);
                    return validationResult.isEmpty() ? ValidationResult.ok() : ValidationResult.error(validationResult);
                })
                .bind(LecturerDto::getFirstName, LecturerDto::setFirstName);
    }

    private void bindSecondName(TextField secondName) {
        binder.forField(secondName)
                .bind(
                        dto -> dto.getSecondName() == null ? "" : dto.getSecondName(),
                        (dto, value) -> dto.setSecondName(value == null || value.isBlank() ? null : value)
                );
    }

    private void bindStatus(ComboBox<String> status) {
        binder.forField(status)
                .asRequired("Status auswählen")
                .bind(
                        dto -> dto.isExtern() ? "Extern" : "Intern",
                        (dto, value) -> dto.setExtern("Extern".equals(value))
                );
    }

    private void bindEmail(TextField email) {
        binder.forField(email)
                .withValidator((value, context) -> {
                    String validationResult = LecturerDto.validateEmail(value);
                    return validationResult.isEmpty() ? ValidationResult.ok() : ValidationResult.error(validationResult);
                })
                .bind(LecturerDto::getEmail, LecturerDto::setEmail);
    }

    private void bindPhone(TextField phone) {
        binder.forField(phone)
                .withValidator((value, context) -> {
                    String validationResult = LecturerDto.validatePhone(value);
                    return validationResult.isEmpty() ? ValidationResult.ok() : ValidationResult.error(validationResult);
                })
                .bind(LecturerDto::getPhone, LecturerDto::setPhone);
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

        Div filterBar = getCourseFilters(canHoldgrid, rows);

        canHoldgrid.addColumn(row -> row.getCourse().getName())
                .setHeader("Name")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> row.getCourse().isMaster() ? "Master" : "Bachelor")
                .setHeader("Grad")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> row.getCourse().getSemester())
                .setHeader("Semester")
                .setSortable(true).setComparator(CourseToLecturerRelation::getSemesterSortable)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> row.getCourse().isClosed() ? "Geschlossen" : "Offen")
                .setHeader("Zugänglichkeit")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> mapQualification(row.getLecturerCanHoldCourse().getQualification()))
                .setHeader("benötigte Vorbereitungszeit")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> row.getLecturerCanHoldCourse().getAffinity())
                .setKey("priority")
                .setHeader("Priorität")
                .setComparator(row -> row.getPriorityScore(lecturer.getTeachingPreference()))
                .setSortable(false)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addColumn(row -> mapAlreadyHeld(row.getLecturerCanHoldCourse().getAlreadyHeld()))
                .setHeader("Gehalten an")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        canHoldgrid.addComponentColumn(row -> {
                Button detailsButton = new Button("Kurs zuweisen");

                detailsButton.addClickListener(event -> {
                    //TODO überprüfen ob es Beziehung gibt sonst erstellen
                });

                return detailsButton;
        }).setHeader("");


        canHoldgrid.sort(List.of(new GridSortOrder<>(canHoldgrid.getColumnByKey("priority"), SortDirection.DESCENDING)));

        canHoldgrid.setItems(rows);
        coursesDiv.add(heading, filterBar, canHoldgrid);
        return coursesDiv;
    }

    private Div getCourseFilters(Grid<CourseToLecturerRelation> grid, List<CourseToLecturerRelation> rows) {
        Div filterBar = new Div();
        filterBar.setWidthFull();
        filterBar.addClassName("toolbar");

        TextField semesterSearchField = new TextField("Semester");
        semesterSearchField.setPlaceholder("Semester");
        semesterSearchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        semesterSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        semesterSearchField.addValueChangeListener(e -> addSearchFunctionality(
                grid,
                semesterSearchField,
                rows,
                (dto, s) -> matchesSearchTerm(dto.getCourse().getSemester(), s))
                .refreshAll());
        filterBar.add(semesterSearchField);

        addComboboxFilter(
                grid,
                rows,
                filterBar,
                "benötigte Vorbereitungszeit",
                Qualification.class,
                Qualification::getValue,
                Qualification::mapQualification,
                dto -> dto.getLecturerCanHoldCourse().getQualification()
        );

        addComboboxFilter(
                grid,
                rows,
                filterBar,
                "Priorität",
                Affinity.class,
                Affinity::getValue,
                Affinity::getValue,
                dto -> dto.getLecturerCanHoldCourse().getAffinity()
        );

        addComboboxFilter(
                grid,
                rows,
                filterBar,
                "Gehalten an",
                AlreadyHeld.class,
                AlreadyHeld::getValue,
                AlreadyHeld::mapAlreadyHeld,
                dto -> dto.getLecturerCanHoldCourse().getAlreadyHeld()
        );

        return filterBar;
    }

    public <D, E extends Enum<E>> void addComboboxFilter(
            Grid<D> grid,
            List<D> rows,
            Div filterBar,
            String label,
            Class<E> enumClass,
            Function<E, String> valueSupplier,
            Function<E, String> itemLabelSupplier,
            Function<D, String> searchTermSupplier)
    {
        ComboBox<E> comboBox = new ComboBox<>(label);
        comboBox.setItems(enumClass.getEnumConstants());
        comboBox.setPlaceholder(label);
        comboBox.setClearButtonVisible(true);
        comboBox.setValue(null);
        comboBox.setItemLabelGenerator(itemLabelSupplier::apply);
        comboBox.addValueChangeListener(e -> addSearchFunctionality(
                grid,
                comboBox,
                rows,
                (dto, s) -> searchTermSupplier.apply(dto).equals(valueSupplier.apply(s)))
                .refreshAll());

        filterBar.add(comboBox);
    }

    public <D, S extends AbstractSinglePropertyField<?, P>, P> DataView<D> addSearchFunctionality (Grid<D> grid, S searchField, List<D> rows, BiFunction<D, P, Boolean> filter) {
        GridListDataView<D> dataView = grid.getListDataView();
        if(dataView == null) {
            dataView = grid.setItems(rows);
        }
        dataView.addFilter(dto -> {
            P searchTerm = searchField.getValue();

            if(searchTerm == null) return true;

            return filter.apply(dto, searchTerm);
        });

        return dataView;
    }

    public boolean matchesSearchTerm(String value, String searchTerm) {
        return value != null && value.toLowerCase().contains(searchTerm.toLowerCase());
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
            lecturer = lecturerService.updateLecturer(lecturer);
            return true;

        } catch (ValidationException ex) {
            Dialog errorDialog = new ValidationErrorDialog(ex);
            errorDialog.open();
            return false;
        } catch (Exception ex) {
            Dialog errorDialog = new Dialog();
            errorDialog.add(new H3("Unerwarteter Fehler"));
            errorDialog.add(new Paragraph("Die Änderungen konnten nicht gespeichert werden: " + ex.getMessage()));
            Button closeButton = new Button("Schließen", e -> errorDialog.close());
            errorDialog.add(closeButton);
            errorDialog.open();
            return false;
        }
    }
}