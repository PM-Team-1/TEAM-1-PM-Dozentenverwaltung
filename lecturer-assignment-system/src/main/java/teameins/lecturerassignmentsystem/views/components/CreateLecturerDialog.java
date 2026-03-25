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
import com.vaadin.flow.data.binder.ValidationException;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.model.enums.Preference;
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

        binder.forField(title)
                .withValidator(
                        value -> "Kein Titel".equals(value) || (value != null && !value.isBlank()),
                        "Gültigen Titel angeben"
                )
                .bind(
                        dto -> dto.getTitle() == null || dto.getTitle().isBlank() ? "Kein Titel" : dto.getTitle(),

                        (dto, value) -> dto.setTitle("Kein Titel".equals(value) ? "" : value)
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
                .bind(dto -> dto.getSecondName() == null ? "" : dto.getSecondName(),
                        (dto, value) -> dto.setSecondName(value == null || value.isBlank() ? null : value));

        binder.forField(status)
                .asRequired("Status auswählen")
                .bind(dto -> dto.isExtern() ? "Extern" : "Intern", (dto, value) -> dto.setExtern("Extern".equals(value)));

        binder.forField(email)
                .asRequired("E-Mail darf nicht leer sein")
                .withValidator(LecturerDto::validateEmail, "Die E-Mail Adresse muss ein @ enthalten")
                .bind(LecturerDto::getEmail, LecturerDto::setEmail);

        binder.forField(phone)
                .asRequired("Telefonnummer darf nicht leer sein")
                .withValidator(LecturerDto::validatePhone, "Die Telefonnummer darf nur Ziffern und optional ein führendes + enthalten")
                .bind(LecturerDto::getPhone, LecturerDto::setPhone);

        layout.add(title, lastName, firstName, secondName, status, email, phone);
        return layout;
    }

    private void saveLecturer() {
        try {
            LecturerDto newLecturer = new LecturerDto();
            newLecturer.setCanHoldCourses(List.of());
            newLecturer.setPreference(Preference.ALLES.getValue());
            binder.writeBean(newLecturer);
            newLecturer = lecturerService.createLecturer(newLecturer);
            this.close();
            UI.getCurrent().navigate("dozenten/" + newLecturer.getId());
        } catch (ValidationException ex) {
            // Normaler Fehler Felder werden rot nix machen
        } catch (Exception ex) {
            // Special kram
            ex.printStackTrace();
        }
    }
}