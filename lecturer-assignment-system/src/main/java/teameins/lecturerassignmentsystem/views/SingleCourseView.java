package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
import teameins.lecturerassignmentsystem.model.exception.LecturerAssignmentException;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;

import java.util.List;

import com.vaadin.flow.data.provider.SortDirection;
import teameins.lecturerassignmentsystem.views.components.ValidationErrorDialog;
import teameins.lecturerassignmentsystem.views.model.LecturerToCourseRelation;

import static teameins.lecturerassignmentsystem.model.enums.AlreadyHeld.mapAlreadyHeld;
import static teameins.lecturerassignmentsystem.model.enums.Qualification.mapQualification;


@Route("vorlesungen")
@PageTitle("Dozent")
@PermitAll
public class SingleCourseView extends VerticalLayout implements HasUrlParameter<String> {

    private final transient LecturerService lecturerService;
    private final transient CourseService courseService;
    private transient CourseDto course;
    private final transient VerticalLayout courseInfo;
    private final transient VerticalLayout assignedLecturerInfo;
    private final transient VerticalLayout lecturersWhoCanHoldInfo;
    private final transient Binder<CourseDto> binder;

    private static final String ALL_COURSES_VIEW_ROUTE = "vorlesungen";
    private static final String BACHELOR = "Bachelor";
    private static final String MASTER = "Master";
    private static final String ACCESSIBILITY_GESCHLOSSEN = "Geschlossen";
    private static final String ACCESSIBILITY_OFFEN = "Offen";
    private static final String TOOLBAR_CLASS_NAME = "toolbar";
    private static final String FILTER_SOFORT = "Sofort";
    private static final String FILTER_IN_VIER_WOCHEN = "in vier Wochen";
    private static final String FILTER_EGAL = "Egal";
    private static final String FILTER_PROVADIS = "Provadis";
    private static final String FILTER_ANDERE_HOCHSCHULE = "Andere Hochschule";

    private boolean isInEditMode = false;

    @Autowired
    public SingleCourseView(LecturerService lecturerService, CourseService courseService) {
        this.lecturerService = lecturerService;
        this.courseService = courseService;
        this.binder = new Binder<>(CourseDto.class);
        this.courseInfo = new VerticalLayout();
        this.assignedLecturerInfo = new VerticalLayout();
        this.lecturersWhoCanHoldInfo = new VerticalLayout();
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        try {
            int id = Integer.parseInt(parameter);
            course = courseService.getCourseDtoById(id);
            renderCourseInfo(false);
            renderAssignedLecturerInfo();
            renderLecturersWhoCanHoldCourseInfo();
            renderSingleCourse();
        } catch (NumberFormatException ex) {
            renderCourseNotFoundError("Ungültige ID", "Die ID " + parameter + " ist ungültig.");
        } catch (CourseNotFoundException ex) {
            renderCourseNotFoundError("Vorlesung nicht gefunden", ex.getMessage());
        } catch (Exception ex) {
            renderCourseNotFoundError("Fehler", "Ein unerwarteter Fehler ist aufgetreten." + ex.getMessage());
        }
    }

    private void renderSingleCourse() {
        VerticalLayout singleCourse = new VerticalLayout(courseInfo, assignedLecturerInfo, lecturersWhoCanHoldInfo);
        singleCourse.setSpacing(false);
        singleCourse.setWidthFull();

        removeAll();
        add(singleCourse);
    }

    private void renderCourseInfo(boolean isInEditMode){
        courseInfo.getStyle().set("flex", "0 0 auto");
        courseInfo.setWidthFull();

        Div toolbar = getToolbar();
        Div info = getCourseInfo(isInEditMode);
        courseInfo.add(toolbar, info);
    }

    private Div getToolbar() {
        Div toolbar = new Div();
        toolbar.addClassName(TOOLBAR_CLASS_NAME);

        Button back = new Button("Zurück zur Übersicht", e -> UI.getCurrent().navigate(ALL_COURSES_VIEW_ROUTE));
        toolbar.add(back);
        Button delete = new Button("Löschen", e -> deleteCourse());
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        if (!isInEditMode) {
            Button edit = new Button("Bearbeiten", e -> toggleEditMode());
            edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            toolbar.add(edit, delete);
        } else {
            Button save = new Button("Speichern", e -> saveEdits());
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

            Button cancel = new Button("Abbrechen", e -> toggleEditMode());
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);

            toolbar.add(save, cancel);
        }

        return toolbar;
    }
    
    private void renderAssignedLecturerInfo(){
        assignedLecturerInfo.setSpacing(false);
        assignedLecturerInfo.add(new Paragraph("Zugewiesener Dozent:"));

        HorizontalLayout layout = new HorizontalLayout();
        TextField assignedLecturer = new TextField();
        assignedLecturer.setReadOnly(true);

        if (course.getHeldBy() == null){
            String value = "Kein Dozent zugewiesen";
            assignedLecturer.setValue(value);
            assignedLecturer.setWidth((value.length() + 2) + "ch");
            layout.add(assignedLecturer);
        } else {
            LecturerDto lecturer = lecturerService.getLecturerDtoById(course.getHeldBy().getLecturerId());
            String value = lecturer.getFullName();
            assignedLecturer.setValue(value);
            assignedLecturer.setWidth((value.length() + 2) + "ch");
            Button removeAssignment = new Button("Zuweisung entfernen", e -> {
                course = courseService.removeLecturerFromCourse(course.getId());
                assignedLecturerInfo.removeAll();
                renderAssignedLecturerInfo();
            });
            layout.add(assignedLecturer, removeAssignment);
        }
        assignedLecturerInfo.add(layout);
    }

    private void renderLecturersWhoCanHoldCourseInfo(){
        H3 lecturersWhoCanHoldCourseHeading = new H3("Mögliche Dozenten für diese Vorlesung:");
        lecturersWhoCanHoldCourseHeading.getStyle().setMarginBottom("var(--lumo-space-s)");
        lecturersWhoCanHoldInfo.add(lecturersWhoCanHoldCourseHeading);

        List<LecturerToCourseRelation> ltcr = course.getCanBeHeldBy().stream()
                .map(lchc -> new LecturerToCourseRelation(lchc, lecturerService))
                .toList();

        Div noLecturersMessage = getNoLecturersMessage();

        if (ltcr.isEmpty()) {
            noLecturersMessage.setVisible(true);
            lecturersWhoCanHoldInfo.add(noLecturersMessage);
        } else {
            Grid<LecturerToCourseRelation> lecturersWhoCanHoldCourse = getLecturersWhoCanHoldCourseGrid(ltcr);
            Div filterBar = getFilterBar(lecturersWhoCanHoldCourse, noLecturersMessage);
            lecturersWhoCanHoldInfo.add(filterBar, lecturersWhoCanHoldCourse, noLecturersMessage);
        }
    }

    private Div getCourseInfo(boolean edit) {
        Div info = new Div();
        info.setWidthFull();

        TextField name = new TextField("Name", "Name");
        name.setReadOnly(!edit);
        name.setWidthFull();
        binder.forField(name)
                .withValidator((value, context) -> {
                    String msg = CourseDto.validateName(value);
                    return msg.isEmpty() ? ValidationResult.ok() : ValidationResult.error(msg);
                })
                .bind(CourseDto::getName, CourseDto::setName);

        ComboBox<String> grad = new ComboBox<>("Grad");
        grad.setItems(BACHELOR, MASTER);
        grad.setReadOnly(!edit);
        grad.setWidthFull();
        binder.bind(grad,
                courseDto -> courseDto.isMaster() ? MASTER : BACHELOR,
                (courseDto, value) -> courseDto.setMaster(value.equals(MASTER)));

        ComboBox<String> accessibility = new ComboBox<>("Zugänglichkeit");
        accessibility.setItems(ACCESSIBILITY_GESCHLOSSEN, ACCESSIBILITY_OFFEN);
        accessibility.setReadOnly(!edit);
        accessibility.setWidthFull();
        binder.bind(accessibility,
                courseDto -> courseDto.isClosed() ? ACCESSIBILITY_GESCHLOSSEN : ACCESSIBILITY_OFFEN,
                (courseDto, value) -> courseDto.setClosed(value.equals(ACCESSIBILITY_GESCHLOSSEN)));

        TextField semester = new TextField("Semester", "Semester");
        semester.setReadOnly(!edit);
        semester.setWidthFull();
        binder.forField(semester)
                .withValidator((value, context) -> {
                    String msg = CourseDto.validateSemester(value);
                    return msg.isEmpty() ? ValidationResult.ok() : ValidationResult.error(msg);
                })
                .bind(CourseDto::getSemester, CourseDto::setSemester);

        binder.readBean(course);

        HorizontalLayout layout = new HorizontalLayout(name, grad, accessibility, semester);
        layout.setWidthFull();
        info.add(layout);

        return info;
    }

    private Grid<LecturerToCourseRelation> getLecturersWhoCanHoldCourseGrid(List<LecturerToCourseRelation> rows) {
        Grid<LecturerToCourseRelation> lecturersWhoCanHoldGrid = new Grid<>();
        lecturersWhoCanHoldGrid.addClassName("grid-custom");
        lecturersWhoCanHoldGrid.setAllRowsVisible(true);

        lecturersWhoCanHoldGrid.addColumn(row -> row.getLecturer().getFullName()).setHeader("Name")
                .setSortable(true).setComparator(row -> row.getLecturer().getLastName())
                .setAutoWidth(true).setFlexGrow(1);
        lecturersWhoCanHoldGrid.addColumn(row -> mapQualification(row.getLecturerCanHoldCourse().getQualification())).setHeader("benötigte Vorbereitungszeit")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        lecturersWhoCanHoldGrid.addColumn(row -> mapAlreadyHeld(row.getLecturerCanHoldCourse().getAlreadyHeld())).setHeader("Bereits gehalten an")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        lecturersWhoCanHoldGrid.addColumn(row -> row.getLecturerCanHoldCourse().getAffinity())
                .setKey("priority")
                .setHeader("Priorität")
                .setComparator(row -> row.getPriorityScore(course.isMaster()))
                .setSortable(false)
                .setAutoWidth(true).setFlexGrow(1);
        lecturersWhoCanHoldGrid.addComponentColumn(row -> new Button("Dieser Vorlesung zuweisen", e -> assignLecturerToCourse(row)))
                .setAutoWidth(true).setFlexGrow(0);

        lecturersWhoCanHoldGrid.setItems(rows);

        lecturersWhoCanHoldGrid.sort(List.of(new GridSortOrder<>(lecturersWhoCanHoldGrid.getColumnByKey("priority"), SortDirection.DESCENDING)));

        return lecturersWhoCanHoldGrid;
    }

    private void toggleEditMode() {
        isInEditMode = !isInEditMode;
        courseInfo.removeAll();
        renderCourseInfo(isInEditMode);
        renderSingleCourse();
    }

    private void deleteCourse() {
        Dialog confirmDelete = new Dialog();
        confirmDelete.add(new H3("Möchten Sie die "
                + (course.isMaster() ? MASTER : BACHELOR)
                +"-Vorlesung " +  course.getName()
                + " " + course.getSemester()
                +" tatsächlich aus dem Verwaltungssystem löschen?"));
        confirmDelete.addClassName("dialog");

        Div deleteOrCancel = new Div();
        deleteOrCancel.setClassName(TOOLBAR_CLASS_NAME);
        deleteOrCancel.getStyle().setMarginTop("var(--lumo-space-l)");

        Button confirmButton = new Button("Löschen", e -> {
            courseService.deleteCourse(course);
            confirmDelete.close();
            UI.getCurrent().navigate(ALL_COURSES_VIEW_ROUTE);
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Abbrechen", e -> confirmDelete.close());

        deleteOrCancel.add(confirmButton, cancelButton);
        confirmDelete.add(deleteOrCancel);
        confirmDelete.open();
    }

    private void saveEdits() {
        try {
            binder.writeBean(course);
            course = courseService.updateCourse(course);
            toggleEditMode();
        } catch (ValidationException e) {
            Dialog errorDialog = new ValidationErrorDialog(e);
            errorDialog.open();
        }
    }

    private void renderCourseNotFoundError(String heading, String details) {
        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(Alignment.CENTER);
        Icon warn = new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
        warn.setClassName("warn");
        H2 title = new H2(heading);
        header.add(warn, title);

        Button back = new Button("Zurück zur Übersicht", e -> UI.getCurrent().navigate(ALL_COURSES_VIEW_ROUTE));

        Paragraph desc = new Paragraph(details);

        add(header, desc, back);
    }


    private Div getFilterBar(Grid<LecturerToCourseRelation> lecturersWhoCanHoldCourse, Component noLecturersMessage) {
        Div filterBar = new Div();
        filterBar.addClassName(TOOLBAR_CLASS_NAME);

        ComboBox<String> qualificationFilter = new ComboBox<>("Soll halten");
        qualificationFilter.setItems(FILTER_SOFORT, FILTER_IN_VIER_WOCHEN, FILTER_EGAL);
        qualificationFilter.setValue(FILTER_SOFORT);
        qualificationFilter.setWidth("240px");

        ComboBox<String> alreadyHeldFilter = new ComboBox<>("Soll bereits gehalten haben an");
        alreadyHeldFilter.setItems(FILTER_PROVADIS, FILTER_ANDERE_HOCHSCHULE, FILTER_EGAL);
        alreadyHeldFilter.setValue(FILTER_PROVADIS);
        alreadyHeldFilter.setWidthFull();

        filterBar.add(qualificationFilter, alreadyHeldFilter);

        GridListDataView<LecturerToCourseRelation> dataView = addFilterFunctionality(lecturersWhoCanHoldCourse, qualificationFilter, alreadyHeldFilter);

        dataView.refreshAll();

        Runnable updateEmptyState = () -> {
            boolean isEmpty = dataView.getItemCount() == 0;
            lecturersWhoCanHoldCourse.setVisible(!isEmpty);
            noLecturersMessage.setVisible(isEmpty);
        };
        updateEmptyState.run();

        qualificationFilter.addValueChangeListener(e -> {
            dataView.refreshAll();
            updateEmptyState.run();
        });
        alreadyHeldFilter.addValueChangeListener(e -> {
            dataView.refreshAll();
            updateEmptyState.run();
        });
        dataView.addItemCountChangeListener(e -> updateEmptyState.run());
        return filterBar;
    }

    private Div getNoLecturersMessage() {
        Div noLecturersMessage = new Div();

        noLecturersMessage.addClassName("empty-state");

        Icon infoIcon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE);
        infoIcon.addClassName("empty-state__icon");

        Paragraph headline = new Paragraph("Keine Dozenten für die Vorlesung gefunden");
        headline.addClassName("empty-state__headline");

        Paragraph subtitle = new Paragraph("Passen Sie Ihre Filtereinstellungen an, um die Suche zu erweitern.");
        subtitle.addClassName("empty-state__subtitle");

        Div textWrapper = new Div();
        textWrapper.add(headline, subtitle);

        noLecturersMessage.add(infoIcon, textWrapper);
        noLecturersMessage.setVisible(false);
        return  noLecturersMessage;
    }

    private GridListDataView<LecturerToCourseRelation> addFilterFunctionality(Grid<LecturerToCourseRelation> ltcrGrid, ComboBox<String> qualification, ComboBox<String> alreadyHeld) {
        GridListDataView<LecturerToCourseRelation> dataView = ltcrGrid.getListDataView();
        dataView.addFilter(ltcr -> {
            boolean matchesQualification = isMatchesQualification(qualification, ltcr);
            boolean matchesAlreadyHeld = isMatchesAlreadyHeld(alreadyHeld, ltcr);
            return matchesQualification && matchesAlreadyHeld;
        });
        return dataView;
    }

    private static boolean isMatchesQualification(ComboBox<String> qualification, LecturerToCourseRelation ltcr) {
        boolean matchesQualification = false;
        if (qualification.getValue() == null || qualification.getValue().isEmpty()){
            matchesQualification = true;
        } else if (qualification.getValue().equals(FILTER_EGAL)){
            matchesQualification = true;
        } else if (qualification.getValue().equals(FILTER_IN_VIER_WOCHEN)){
            matchesQualification = !ltcr.getLecturerCanHoldCourse().getQualification().equals("M");
        } else if (qualification.getValue().equals(FILTER_SOFORT)){
            matchesQualification = ltcr.getLecturerCanHoldCourse().getQualification().equals("S");
        }
        return matchesQualification;
    }

    private static boolean isMatchesAlreadyHeld(ComboBox<String> alreadyHeld, LecturerToCourseRelation ltcr) {
        boolean matchesAlreadyHeld = false;
        if (alreadyHeld.getValue() == null || alreadyHeld.getValue().isEmpty()){
            matchesAlreadyHeld = true;
        } else if (alreadyHeld.getValue().equals(FILTER_EGAL)){
            matchesAlreadyHeld = true;
        } else if (alreadyHeld.getValue().equals(FILTER_ANDERE_HOCHSCHULE)){
            matchesAlreadyHeld = !ltcr.getLecturerCanHoldCourse().getAlreadyHeld().equals("N");
        } else if (alreadyHeld.getValue().equals(FILTER_PROVADIS)){
            matchesAlreadyHeld = ltcr.getLecturerCanHoldCourse().getAlreadyHeld().equals("P");
        }
        return matchesAlreadyHeld;
    }

    private void assignLecturerToCourse(LecturerToCourseRelation ltcr){
        try{
            course = courseService.assignLecturerToCourse(course.getId(), ltcr.getLecturer().getId());
            assignedLecturerInfo.removeAll();
            renderAssignedLecturerInfo();
            renderSingleCourse();
        } catch (LecturerAssignmentException e) {
            openErrorDialog(e.getMessage());
        } catch (Exception e){
            openErrorDialog("Ein unerwarteter Fehler ist aufgetreten.");
        }
    }

    private void openErrorDialog(String errorMessage){
        Dialog error = new Dialog();
        error.add(new H3("Ein Fehler ist aufgetreten"));
        Div errorMessagesDiv = new Div();
        errorMessagesDiv.getStyle().setMarginTop("var(--lumo-space-l)");
        errorMessagesDiv.add(new Paragraph(errorMessage));
        error.add(errorMessagesDiv);
        Button closeButton = new Button("Schließen", e -> error.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        error.add(closeButton);
        error.open();
    }

}
