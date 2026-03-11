package teameins.lecturerassignmentsystem.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;


public class ValidationErrorDialog extends Dialog {

    public ValidationErrorDialog(ValidationException e) {
        add(new H3("Ungültige Eingaben"));
        Div errorMessagesDiv = new Div();
        errorMessagesDiv.getStyle().setMarginTop("var(--lumo-space-l)");
        for (ValidationResult error : e.getValidationErrors()) {
            if (error.isError()) {
                Paragraph errorMessage = new Paragraph(error.getErrorMessage());
                errorMessagesDiv.add(errorMessage);
            }
        }
        add(errorMessagesDiv);
        Button closeButton = new Button("Schließen", error -> this.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(closeButton);
    }
}
