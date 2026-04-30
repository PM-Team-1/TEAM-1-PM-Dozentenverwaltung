package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login", autoLayout = false)
@PageTitle("Login")
@AnonymousAllowed
public class LoginView extends Main implements BeforeEnterObserver {

    private final LoginForm login;

    public LoginView() {
        this.login = new LoginForm();
        LoginI18n loginI18n = LoginI18n.createDefault();

        LoginI18n.Form loginForm = loginI18n.getForm();
        loginForm.setTitle("Anmeldung");
        loginForm.setUsername("Benutzername");
        loginForm.setPassword("Passwort");
        loginForm.setSubmit("Anmelden");
        loginI18n.setForm(loginForm);

        LoginI18n.ErrorMessage error = loginI18n.getErrorMessage();
        error.setTitle("Ungültige Anmeldedaten");
        error.setMessage(
                "Benutzername oder Passwort sind ungültig, bitte überprüfen Sie Ihre Angaben");
        error.setUsername("Benutzername angeben");
        error.setPassword("Passwort angeben");
        loginI18n.setErrorMessage(error);

        login.setI18n(loginI18n);
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        Dialog layout = new Dialog();
        layout.add(login);
        layout.open();
        layout.setCloseOnEsc(false);
        layout.setCloseOnOutsideClick(false);

        add(layout);
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
