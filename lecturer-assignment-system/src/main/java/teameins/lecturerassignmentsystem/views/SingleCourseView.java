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
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.exception.CourseNotFoundException;
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
public class SingleCourseView extends VerticalLayout implements HasUrlParameter<String> {

    private final transient LecturerService lecturerService;
    private final transient CourseService courseService;
    private transient CourseDto course;
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
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        try {
            int id = Integer.parseInt(parameter);
            course = courseService.getCourseById(id);
            renderSingleCourse(isInEditMode);
        } catch (NumberFormatException ex) {
            renderCourseNotFoundError("Ungültige ID", "Die ID " + parameter + " ist ungültig.");
        } catch (CourseNotFoundException ex) {
            renderCourseNotFoundError("Vorlesung nicht gefunden", ex.getMessage());
        } catch (Exception ex) {
            renderCourseNotFoundError("Fehler", "Es ist ein unerwarteter Fehler aufgetreten: " + ex.getMessage());
        }
    }

    private void renderSingleCourse(boolean isInEditMode) {
        H2 heading = new H2(course.getName());

        VerticalLayout courseInfo = new VerticalLayout();
        courseInfo.getStyle().set("flex", "0 0 auto");
        courseInfo.getStyle().set("width", "auto");

        Div toolbar = getToolbar();
        VerticalLayout info = getCourseInfo(isInEditMode);
        courseInfo.add(toolbar, info);

        VerticalLayout lecturers = new VerticalLayout();
        lecturers.getStyle().set("flex", "1 1 auto");
        lecturers.setWidthFull();

        H3 lecturersWhoCanHoldCourseHeading = new H3("Mögliche Dozenten für diese Vorlesung:");
        lecturersWhoCanHoldCourseHeading.getStyle().setMarginBottom("var(--lumo-space-m)");

        List<LecturerToCourseRelation> ltcr = course.getCanBeHeldBy().stream()
                .map(lchc -> new LecturerToCourseRelation(lchc, lecturerService))
                .toList();

        Div noLecturersMessage = getNoLecturersMessage();

        if (ltcr.isEmpty()) {
            noLecturersMessage.setVisible(true);
            lecturers.add(lecturersWhoCanHoldCourseHeading, noLecturersMessage);
        } else {
            Grid<LecturerToCourseRelation> lecturersWhoCanHoldCourse = renderLecturersWhoCanHoldCourse(ltcr);
            Div filterBar = getFilterBar(lecturersWhoCanHoldCourse, noLecturersMessage);
            lecturers.add(lecturersWhoCanHoldCourseHeading, filterBar, lecturersWhoCanHoldCourse, noLecturersMessage);
        }

        HorizontalLayout singleCourse = new HorizontalLayout(courseInfo, lecturers);
        singleCourse.setSpacing(true);
        singleCourse.setWidthFull();
        singleCourse.setFlexGrow(0, courseInfo);
        singleCourse.setFlexGrow(1, lecturers);

        add(heading, singleCourse);
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

    private VerticalLayout getCourseInfo(boolean edit) {
        VerticalLayout info = new VerticalLayout();
        info.setJustifyContentMode(JustifyContentMode.BETWEEN);

        TextField name = new TextField("Name", "Name");
        name.setReadOnly(!edit);
        name.setWidthFull();
        binder.forField(name)
                .withValidator((value, context) -> {
                    String msg = CourseDto.validateNameMessage(value);
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
                    String msg = CourseDto.validateSemesterMessage(value);
                    return msg.isEmpty() ? ValidationResult.ok() : ValidationResult.error(msg);
                })
                .bind(CourseDto::getSemester, CourseDto::setSemester);

        binder.readBean(course);

        info.add(name, grad, accessibility, semester);

        return info;
    }

    private Grid<LecturerToCourseRelation> renderLecturersWhoCanHoldCourse(List<LecturerToCourseRelation> rows) {
        Grid<LecturerToCourseRelation> lecturersWhoCanHoldGrid = new Grid<>();
        lecturersWhoCanHoldGrid.addClassName("grid-custom");
        lecturersWhoCanHoldGrid.setAllRowsVisible(true);

        lecturersWhoCanHoldGrid.addColumn(row -> row.getLecturer().getFullName()).setHeader("Name")
                .setSortable(true).setComparator(row -> row.getLecturer().getLastName())
                .setAutoWidth(true).setFlexGrow(1);
        lecturersWhoCanHoldGrid.addColumn(row -> mapQualification(row.getLecturerCanHoldCourse().getQualification())).setHeader("benötigte Vorbereitungszeit")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        Grid.Column<LecturerToCourseRelation> prefColumn = lecturersWhoCanHoldGrid.addColumn(this::mapPreference)
                .setHeader("Präferenz")
                .setSortable(true)
                .setComparator(row -> priorityScore(row.getLecturerCanHoldCourse().getPriority()))
                .setAutoWidth(true).setFlexGrow(1);

        lecturersWhoCanHoldGrid.addColumn(row -> mapAlreadyHeld(row.getLecturerCanHoldCourse().getAlreadyHeld())).setHeader("Bereits gehalten an")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);

        lecturersWhoCanHoldGrid.setItems(rows);

        lecturersWhoCanHoldGrid.sort(List.of(new GridSortOrder<>(prefColumn, SortDirection.ASCENDING)));

        return lecturersWhoCanHoldGrid;
    }

    private void toggleEditMode() {
        isInEditMode = !isInEditMode;
        removeAll();
        renderSingleCourse(isInEditMode);
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

    private String mapPreference(LecturerToCourseRelation ltcr){
        Boolean priority = ltcr.getLecturerCanHoldCourse().getPriority();
        if (priority == null) {
            return "-";
        }
        if (priority) {
            return "Hält gerne im " + (course.isMaster() ? MASTER : BACHELOR);
        } else {
            return "Hält lieber im " + (course.isMaster() ? BACHELOR : MASTER);
        }
    }

    private int priorityScore(Boolean priority) {
        if (priority == null) return 1;
        return priority ? 0 : 2;
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

        Paragraph subtitle = new Paragraph("Weisen Sie Dozenten diese Vorlesung zu, damit sie hier angezeigt werden.");
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

            return matchesQualification && matchesAlreadyHeld;
        });
        return dataView;
    }

}
