package teameins.lecturerassignmentsystem.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValidationException;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.TeachingPreference;
import teameins.lecturerassignmentsystem.service.LecturerService;

import java.util.List;

public class CreateLecturerDialog extends Dialog {

    private final transient LecturerService lecturerService;
    private final Binder<LecturerDto> binder = new Binder<>(LecturerDto.class);

    public CreateLecturerDialog(LecturerService lecturerService) {
        this.lecturerService = lecturerService;

        H2 headline = new H2("Neuen Dozenten anlegen");

        VerticalLayout fieldLayout = createFieldLayout();

        Button saveButton = new Button("Speichern", e -> saveLecturer());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button("Abbrechen", e -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.getStyle().set("margin-top", "var(--lumo-space-l)");

        add(headline, fieldLayout, buttonLayout);
        this.open();
    }

    private VerticalLayout createFieldLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        ComboBox<String> title = new ComboBox<>("Titel");
        title.setItems("Dr.", "Prof.", "Kein Titel");
        title.setValue("Kein Titel");
        title.setWidthFull();

        TextField lastName = new TextField("Nachname");
        lastName.setWidthFull();

        TextField firstName = new TextField("Vorname");
        firstName.setWidthFull();

        TextField secondName = new TextField("2. Vorname");
        secondName.setWidthFull();

        ComboBox<String> status = new ComboBox<>("Status");
        status.setItems("Intern", "Extern");
        status.setValue("Intern");
        status.setWidthFull();

        TextField email = new TextField("E-Mail");
        email.setWidthFull();

        TextField phone = new TextField("Telefonnummer");
        phone.setWidthFull();

        bindTitle(title);
        bindLastName(lastName);
        bindFirstName(firstName);
        bindSecondName(secondName);
        bindStatus(status);
        bindEmail(email);
        bindPhone(phone);

        layout.add(title, lastName, firstName, secondName, status, email, phone);
        return layout;
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
                .bind(dto -> dto.getSecondName() == null ? "" : dto.getSecondName(),
                        (dto, value) -> dto.setSecondName(value == null || value.isBlank() ? null : value));
    }

    private void bindStatus(ComboBox<String> status) {
        binder.forField(status)
                .asRequired("Status auswählen")
                .bind(dto -> dto.isExtern() ? "Extern" : "Intern", (dto, value) -> dto.setExtern("Extern".equals(value)));
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

    private void saveLecturer() {
        try {
            LecturerDto newLecturer = new LecturerDto();
            newLecturer.setCanHoldCourses(List.of());
            newLecturer.setTeachingPreference(TeachingPreference.ALLES.getValue());
            binder.writeBean(newLecturer);
            newLecturer = lecturerService.createLecturer(newLecturer);
            this.close();
            UI.getCurrent().navigate("dozenten/" + newLecturer.getId());
        } catch (ValidationException ex) {
            //validation errors are already shown by the binder, so we can ignore this exception here
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}