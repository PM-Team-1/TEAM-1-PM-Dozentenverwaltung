package teameins.lecturerassignmentsystem.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.model.dto.relation.LecturerCanHoldCourseDto;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Affinity;
import teameins.lecturerassignmentsystem.model.enums.AlreadyHeld;
import teameins.lecturerassignmentsystem.model.enums.Qualification;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.model.exception.NoCoursesFoundException;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.service.LecturerService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AddCourseToLecturerDialog extends Dialog {
    private final transient LecturerDto lecturer;
    private final transient Dialog dialog;
    private final transient CourseService courseService;
    private final transient LecturerService lecturerService;
    private final transient Binder<LecturerCanHoldCourseDto> binder;

    public AddCourseToLecturerDialog(LecturerDto lecturer, CourseService courseService, LecturerService lecturerService) {
        this.lecturer = lecturer;
        this.courseService = courseService;
        this.lecturerService = lecturerService;
        this.binder = new Binder<>(LecturerCanHoldCourseDto.class);

        this.dialog = new Dialog();
        renderDialog();
    }

    private void renderDialog(){
        dialog.setHeaderTitle("Vorlesung hinzufügen");
        dialog.addClassName("dialog");
        dialog.setWidth("500px");

        try {
            List<CourseDto> availableCourses = getAvailableCourses();

            ComboBox<CourseDto> courseComboBox = getCourseSelection(availableCourses);
            ComboBox<String> alreadyHeldComboBox = getAlreadyHeldSelection();
            ComboBox<String> qualificationComboBox = getQualificationSelection();
            ComboBox<String> affinityComboBox = getAffinitySelection();
            Button cancelButton = getCloseButton("Abbrechen");
            Button saveButton = getSaveButton();

            VerticalLayout formLayout = new VerticalLayout(courseComboBox, alreadyHeldComboBox, qualificationComboBox, affinityComboBox);
            formLayout.setPadding(false);
            formLayout.setSpacing(true);
            dialog.add(formLayout);

            dialog.getFooter().add(cancelButton, saveButton);
            dialog.open();

        } catch (NoCoursesFoundException ex) {
            dialog.add(new Paragraph(ex.getMessage()));
            Button closeButton = getCloseButton("Schließen");
            dialog.getFooter().add(closeButton);
            dialog.open();
        }
    }

    private List<CourseDto> getAvailableCourses(){
        // Determine which courses the lecturer already holds
        Set<Integer> alreadyAssignedCourseIds = lecturer.getCanHoldCourses().stream()
                .map(LecturerCanHoldCourseDto::getCourseId)
                .collect(Collectors.toSet());

        // Determine which courses the lecturer can not hold by teaching preference
        Set<Integer> wrongDegreeCourseIds = courseService.listCourses().stream()
                .filter(c -> (lecturer.getTeachingPreference().equals(TeachingPreference.ONLY_BACHELOR.getValue()) && c.isMaster())
                        || (lecturer.getTeachingPreference().equals(TeachingPreference.ONLY_MASTER.getValue()) && !c.isMaster()))
                .map(CourseDto::getId)
                .collect(Collectors.toSet());

        // Load all courses and filter out already assigned ones
        List<CourseDto> availableCourses = courseService.listCourses().stream()
                .filter(c -> !(alreadyAssignedCourseIds.contains(c.getId()) || wrongDegreeCourseIds.contains(c.getId())))
                .toList();

        if (!availableCourses.isEmpty()) {
            return availableCourses;
        } else {
            throw new NoCoursesFoundException("Es sind keine weiteren Vorlesungen verfügbar, die diesem Dozenten zugewiesen werden können.");
        }
    }

    private ComboBox<CourseDto> getCourseSelection(List<CourseDto> availableCourses){
        ComboBox<CourseDto> courseComboBox = new ComboBox<>("Vorlesung");
        courseComboBox.setItems(availableCourses);
        courseComboBox.setItemLabelGenerator(c -> c.getName() + " (" + (c.isMaster() ? "Master" : "Bachelor") + ", " + c.getSemester() + ")");
        courseComboBox.setWidthFull();
        courseComboBox.setRequired(true);
        courseComboBox.setPlaceholder("Vorlesung auswählen...");

        binder.forField(courseComboBox)
                .withValidator(Objects::nonNull, "Bitte wählen Sie eine Vorlesung aus.")
                .bind(dto -> null, (dto, course) -> dto.setCourseId(course.getId()));

        return courseComboBox;
    }

    private ComboBox<String> getAlreadyHeldSelection(){
        ComboBox<String> alreadyHeldComboBox = new ComboBox<>("Bereits gehalten?");
        alreadyHeldComboBox.setItems(
                AlreadyHeld.NOT_YET_HELD.getValue(),
                AlreadyHeld.PROVADIS.getValue(),
                AlreadyHeld.OTHER_SCHOOL.getValue()
        );
        alreadyHeldComboBox.setItemLabelGenerator(code -> switch (code) {
            case "N" -> "Noch nicht gehalten";
            case "P" -> "Provadis";
            case "A" -> "Andere Hochschule";
            default -> code;
        });
        alreadyHeldComboBox.setValue(AlreadyHeld.NOT_YET_HELD.getValue());
        alreadyHeldComboBox.setWidthFull();
        alreadyHeldComboBox.setRequired(true);

        binder.forField(alreadyHeldComboBox)
                .bind(LecturerCanHoldCourseDto::getAlreadyHeld, LecturerCanHoldCourseDto::setAlreadyHeld);

        return alreadyHeldComboBox;
    }

    private ComboBox<String> getQualificationSelection(){
        ComboBox<String> qualificationComboBox = new ComboBox<>("Vorbereitungszeit");
        qualificationComboBox.setItems(
                Qualification.IMMEDIATELY.getValue(),
                Qualification.FOUR_WEEKS.getValue(),
                Qualification.OVER_FOUR_WEEKS.getValue()
        );
        qualificationComboBox.setItemLabelGenerator(code -> switch (code) {
            case "S" -> "Keine (sofort einsetzbar)";
            case "4" -> "Vier Wochen";
            case "M" -> "Über vier Wochen";
            default -> code;
        });
        qualificationComboBox.setValue(Qualification.IMMEDIATELY.getValue());
        qualificationComboBox.setWidthFull();
        qualificationComboBox.setRequired(true);

        binder.forField(qualificationComboBox)
                .bind(LecturerCanHoldCourseDto::getQualification, LecturerCanHoldCourseDto::setQualification);

        return qualificationComboBox;
    }

    private ComboBox<String> getAffinitySelection(){
        ComboBox<String> affinityComboBox = new ComboBox<>("Priorität");
        affinityComboBox.setItems(
                Affinity.LOW.getValue(),
                Affinity.MEDIUM.getValue(),
                Affinity.HIGH.getValue()
        );
        affinityComboBox.setValue(Affinity.MEDIUM.getValue());
        affinityComboBox.setWidthFull();
        affinityComboBox.setRequired(true);

        binder.forField(affinityComboBox)
                .bind(LecturerCanHoldCourseDto::getAffinity, LecturerCanHoldCourseDto::setAffinity);

        return affinityComboBox;
    }


    private Button getCloseButton(String label){
        return new Button(label, e -> dialog.close());
    }

    private Button getSaveButton(){
        Button saveButton = new Button("Hinzufügen", e -> addCourseToLecturer());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }

    private void addCourseToLecturer(){
        LecturerCanHoldCourseDto dto = new LecturerCanHoldCourseDto();
        dto.setLecturerId(lecturer.getId());
        try {
            binder.writeBean(dto);
            lecturerService.addCourseToLecturer(dto);
            dialog.close();
            removeAll();
            UI.getCurrent().navigate("dozenten/" + lecturer.getId());
            Notification.show("Vorlesung erfolgreich hinzugefügt.", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException ex) {
            //validation errors are already shown by the binder, so we can ignore this exception here
        } catch (IllegalArgumentException ex) {
            Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
