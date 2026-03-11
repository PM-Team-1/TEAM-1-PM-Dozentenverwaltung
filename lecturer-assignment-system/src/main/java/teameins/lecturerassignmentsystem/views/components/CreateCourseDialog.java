package teameins.lecturerassignmentsystem.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.service.CourseService;

public class CreateCourseDialog extends Dialog {
    private transient CourseDto course;
    private final transient CourseService courseService;
    private final transient Binder<CourseDto> binder;

    private static final String BACHELOR = "Bachelor";
    private static final String MASTER = "Master";
    private static final String ACCESSIBILITY_GESCHLOSSEN = "Geschlossen";
    private static final String ACCESSIBILITY_OFFEN = "Offen";

    public CreateCourseDialog(CourseService courseService) {
        this.courseService = courseService;
        this.binder = new Binder<>(CourseDto.class);
        this.course = new CourseDto();

        H2 heading = new H2("Neue Vorlesung anlegen");

        VerticalLayout createCourse = new VerticalLayout();
        createCourse.getStyle().set("flex", "0 0 auto");
        createCourse.getStyle().set("width", "auto");

        Div toolbar = getToolbar();
        VerticalLayout info = getCourseFields();
        createCourse.add(info, toolbar);
        add(heading, createCourse);
        this.open();
    }

    private VerticalLayout getCourseFields() {
        VerticalLayout fields = new VerticalLayout();
        fields.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        TextField name = new TextField("Name", "Name");
        name.setWidthFull();
        binder.forField(name)
                .withValidator((value, context) -> {
                    String msg = CourseDto.validateNameMessage(value);
                    return msg.isEmpty() ? ValidationResult.ok() : ValidationResult.error(msg);
                })
                .bind(CourseDto::getName, CourseDto::setName);

        ComboBox<String> grad = new ComboBox<>("Grad");
        grad.setItems(BACHELOR, MASTER);
        grad.setWidthFull();
        binder.bind(grad,
                courseDto -> courseDto.isMaster() ? MASTER : BACHELOR,
                (courseDto, value) -> courseDto.setMaster(value.equals(MASTER)));

        ComboBox<String> accessibility = new ComboBox<>("Zugänglichkeit");
        accessibility.setItems(ACCESSIBILITY_GESCHLOSSEN, ACCESSIBILITY_OFFEN);
        accessibility.setWidthFull();
        binder.bind(accessibility,
                courseDto -> courseDto.isClosed() ? ACCESSIBILITY_GESCHLOSSEN : ACCESSIBILITY_OFFEN,
                (courseDto, value) -> courseDto.setClosed(value.equals(ACCESSIBILITY_GESCHLOSSEN)));

        TextField semester = new TextField("Semester", "Semester");
        semester.setWidthFull();
        binder.forField(semester)
                .withValidator((value, context) -> {
                    String msg = CourseDto.validateSemesterMessage(value);
                    return msg.isEmpty() ? ValidationResult.ok() : ValidationResult.error(msg);
                })
                .bind(CourseDto::getSemester, CourseDto::setSemester);

        binder.readBean(course);

        fields.add(name, grad, accessibility, semester);

        return fields;
    }

    private Div getToolbar() {
        Div toolbar = new Div();
        toolbar.addClassName("toolbar");

        Button save = new Button("Speichern", e -> createCourse());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        Button cancel = new Button("Abbrechen", e -> this.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        toolbar.add(save, cancel);
        return toolbar;
    }

    private void createCourse() {
        try {
            binder.writeBean(course);
            course = courseService.createCourse(course);
            this.close();
            UI.getCurrent().navigate("vorlesungen/" + course.getId());
        } catch (ValidationException e) {
            Dialog errorDialog = new ValidationErrorDialog(e);
            errorDialog.open();
        }
    }
}
