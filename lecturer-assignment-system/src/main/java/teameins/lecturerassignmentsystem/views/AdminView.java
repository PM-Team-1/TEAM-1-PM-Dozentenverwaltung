package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import teameins.lecturerassignmentsystem.model.dto.RoleDto;
import teameins.lecturerassignmentsystem.model.dto.UserDto;
import teameins.lecturerassignmentsystem.model.dto.relation.UserHasRoleDto;
import teameins.lecturerassignmentsystem.service.AccountService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("admin")
@PageTitle("Admin")
@RolesAllowed("ROLE_ADMIN")
public class AdminView extends VerticalLayout {
    private final transient AccountService accountService;
    private Grid<UserDto> userGrid;
    private Grid<RoleDto> roleGrid;
    private Map<Integer, String> roleNamesById = new HashMap<>();

    public AdminView(AccountService accountService) {
        this.accountService = accountService;
        setSpacing(true);
        setPadding(true);

        H2 mainHeading = new H2("Admin Panel");
        mainHeading.addClassName("h2-custom");
        add(mainHeading);

        add(createUserManagementSection());
        add(createRoleManagementSection());
    }

    private VerticalLayout createUserManagementSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout();
        H3 heading = new H3("Benutzerverwaltung");
        Button addUserBtn = iconButton(VaadinIcon.PLUS, ButtonVariant.LUMO_ICON, e -> openUserDialog(null), "Benutzer anlegen");
        header.add(heading, addUserBtn);

        userGrid = new Grid<>(UserDto.class, false);
        userGrid.addColumn(UserDto::getUsername).setHeader("Benutzername");
        userGrid.addComponentColumn(user -> {
            Span roles = new Span(getUserRolesText(user));
            roles.getStyle().set("white-space", "normal");

            Button assignButton = iconButton(VaadinIcon.PLUS, ButtonVariant.LUMO_ICON, e -> openRoleAssignmentDialog(user), "Rolle zuweisen");

            HorizontalLayout layout = new HorizontalLayout(roles, assignButton);
            layout.setAlignItems(Alignment.CENTER);
            layout.setPadding(false);
            layout.setSpacing(true);
            return layout;
        }).setHeader("Rollen");
        userGrid.addComponentColumn(user -> {
            Button editButton = iconButton(VaadinIcon.EDIT, ButtonVariant.LUMO_ICON, e -> openUserDialog(user), "Bearbeiten");
            Button deleteButton = iconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, e -> confirmDeleteUser(user), "Löschen");
            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setPadding(false);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Aktionen");
        userGrid.setHeight("300px");

        refreshRoleNames();
        refreshUserGrid();

        section.add(header, userGrid);
        return section;
    }

    private VerticalLayout createRoleManagementSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout();
        H3 heading = new H3("Rollenverwaltung");
        Button addRoleBtn = iconButton(VaadinIcon.PLUS, ButtonVariant.LUMO_ICON, e -> openRoleDialog(), "Rolle anlegen");
        header.add(heading, addRoleBtn);

        roleGrid = new Grid<>(RoleDto.class, false);
        roleGrid.addColumn(RoleDto::getName).setHeader("Rollenname");
        roleGrid.addColumn(RoleDto::getGrantedAuthority).setHeader("Authority");
        roleGrid.addComponentColumn(role -> {
            Button deleteButton = iconButton(VaadinIcon.TRASH, ButtonVariant.LUMO_ERROR, e -> confirmDeleteRole(role), "Löschen");
            HorizontalLayout actions = new HorizontalLayout(deleteButton);
            actions.setPadding(false);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Aktionen");
        roleGrid.setHeight("300px");

        refreshRoleGrid();

        section.add(header, roleGrid);
        return section;
    }

    private Button iconButton(VaadinIcon icon, ButtonVariant variant, ComponentEventListener<com.vaadin.flow.component.ClickEvent<Button>> listener, String ariaLabel) {
        Button button = new Button(new Icon(icon));
        button.addClickListener(listener);
        button.addThemeVariants(ButtonVariant.LUMO_ICON, variant);
        button.setAriaLabel(ariaLabel);
        return button;
    }

    private String getUserRolesText(UserDto user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return "-";
        }

        List<String> roles = new ArrayList<>();
        for (UserHasRoleDto role : user.getRoles()) {
            String roleName = roleNamesById.get(role.getRoleId());
            if (roleName != null) {
                roles.add(roleName);
            }
        }
        return roles.isEmpty() ? "-" : String.join(", ", roles);
    }

    private void openRoleAssignmentDialog(UserDto user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Rolle zuweisen");

        FormLayout form = new FormLayout();
        com.vaadin.flow.component.select.Select<RoleDto> roleSelect = new com.vaadin.flow.component.select.Select<>();
        roleSelect.setLabel("Rolle");
        roleSelect.setItems(accountService.listRoles());
        roleSelect.setItemLabelGenerator(RoleDto::getName);
        form.add(roleSelect);

        Button assignButton = new Button("Zuweisen", e -> {
            RoleDto role = roleSelect.getValue();
            if (role != null) {
                accountService.assignRoleToUser(user.getId(), role.getId());
                refreshRoleNames();
                refreshUserGrid();
                dialog.close();
            }
        });
        assignButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());

        dialog.add(form, new HorizontalLayout(assignButton, cancelButton));
        dialog.open();
    }

    private void confirmDeleteUser(UserDto user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Benutzer löschen");

        Button deleteButton = new Button("Löschen", e -> {
            accountService.deleteUser(user);
            refreshUserGrid();
            dialog.close();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());

        dialog.add(new H3("Möchten Sie den Benutzer " + safe(user.getUsername()) + " wirklich löschen?"), new HorizontalLayout(deleteButton, cancelButton));
        dialog.open();
    }

    private void confirmDeleteRole(RoleDto role) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Rolle löschen");

        Button deleteButton = new Button("Löschen", e -> {
            accountService.deleteRole(role);
            refreshRoleNames();
            refreshRoleGrid();
            refreshUserGrid();
            dialog.close();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());

        dialog.add(new H3("Möchten Sie die Rolle " + safe(role.getName()) + " wirklich löschen?"), new HorizontalLayout(deleteButton, cancelButton));
        dialog.open();
    }

    private void openUserDialog(UserDto user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(user == null ? "Benutzer anlegen" : "Benutzer bearbeiten");

        FormLayout form = new FormLayout();

        TextField usernameField = new TextField("Benutzername");
        usernameField.setValue(safe(user != null ? user.getUsername() : null));

        PasswordField passwordField = new PasswordField("Passwort");
        passwordField.setValue(safe(user != null ? user.getPassword() : null));


        form.add(usernameField, passwordField);

        Button saveButton = new Button("Speichern", e -> {
            UserDto dto = new UserDto();
            if (user != null) {
                dto.setId(user.getId());
            }
            dto.setUsername(usernameField.getValue());
            dto.setPassword(passwordField.getValue());
            dto.setEnabled(true);
            dto.setRoles(user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<>());

            if (user == null) {
                accountService.createUser(dto);
            } else {
                accountService.updateUser(dto);
            }
            refreshUserGrid();
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());

        dialog.add(form, new HorizontalLayout(saveButton, cancelButton));
        dialog.open();
    }

    private void openRoleDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Rolle anlegen");

        FormLayout form = new FormLayout();

        TextField nameField = new TextField("Rollenname");
        TextField authorityField = new TextField("Authority");

        form.add(nameField, authorityField);

        Button saveButton = new Button("Speichern", e -> {
            RoleDto dto = new RoleDto();
            dto.setName(nameField.getValue());
            dto.setGrantedAuthority(authorityField.getValue());
            dto.setUsersWithRole(new ArrayList<>());

            accountService.createRole(dto);
            refreshRoleNames();
            refreshRoleGrid();
            refreshUserGrid();
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());

        dialog.add(form, new HorizontalLayout(saveButton, cancelButton));
        dialog.open();
    }

    private void refreshRoleNames() {
        roleNamesById = new HashMap<>();
        for (RoleDto role : accountService.listRoles()) {
            roleNamesById.put(role.getId(), role.getName());
        }
    }

    private void refreshUserGrid() {
        userGrid.setItems(accountService.listUsers());
    }

    private void refreshRoleGrid() {
        roleGrid.setItems(accountService.listRoles());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}