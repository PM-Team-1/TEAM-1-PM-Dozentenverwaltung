package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import teameins.lecturerassignmentsystem.model.dto.RoleDto;
import teameins.lecturerassignmentsystem.model.dto.UserDto;
import teameins.lecturerassignmentsystem.service.AccountService;

import java.util.ArrayList;

@Route("admin")
@PageTitle("Admin")
@RolesAllowed("ROLE_ADMIN")
public class AdminView extends VerticalLayout {
    private final transient AccountService accountService;
    private Grid<UserDto> userGrid;
    private Grid<RoleDto> roleGrid;

    public AdminView(AccountService accountService) {
        this.accountService = accountService;
        setSpacing(true);
        setPadding(true);

        H2 mainHeading = new H2("Admin Panel");
        mainHeading.addClassName("h2-custom");
        add(mainHeading);

        add(createUserManagementSection());
        add(createRoleManagementSection());
        add(createRoleAssignmentSection());
    }

    private VerticalLayout createUserManagementSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);

        H3 heading = new H3("Benutzerverwaltung");
        section.add(heading);

        userGrid = new Grid<>(UserDto.class, false);
        userGrid.addColumn(UserDto::getId).setHeader("ID");
        userGrid.addColumn(UserDto::getUsername).setHeader("Benutzername");
        userGrid.addColumn(u -> u.isEnabled() ? "Ja" : "Nein").setHeader("Aktiv");
        userGrid.setItems(accountService.listUsers());
        userGrid.setHeight("300px");

        Button addUserBtn = new Button("Benutzer anlegen", e -> openUserDialog(null));
        Button editUserBtn = new Button("Bearbeiten", e -> {
            UserDto selected = userGrid.asSingleSelect().getValue();
            if (selected != null) {
                openUserDialog(selected);
            }
        });
        Button deleteUserBtn = new Button("Löschen", e -> {
            UserDto selected = userGrid.asSingleSelect().getValue();
            if (selected != null) {
                accountService.deleteUser(selected);
                refreshUserGrid();
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(addUserBtn, editUserBtn, deleteUserBtn);
        section.add(userGrid, buttonLayout);

        return section;
    }

    private VerticalLayout createRoleManagementSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);

        H3 heading = new H3("Rollenverwaltung");
        section.add(heading);

        roleGrid = new Grid<>(RoleDto.class, false);
        roleGrid.addColumn(RoleDto::getId).setHeader("ID");
        roleGrid.addColumn(RoleDto::getName).setHeader("Rollenname");
        roleGrid.addColumn(RoleDto::getGrantedAuthority).setHeader("Authority");
        roleGrid.setHeight("300px");

        refreshRoleGrid();

        Button addRoleBtn = new Button("Rolle anlegen", e -> openRoleDialog(null));
        Button editRoleBtn = new Button("Bearbeiten", e -> {
            RoleDto selected = roleGrid.asSingleSelect().getValue();
            if (selected != null) {
                openRoleDialog(selected);
            }
        });
        Button deleteRoleBtn = new Button("Löschen", e -> {
            RoleDto selected = roleGrid.asSingleSelect().getValue();
            if (selected != null) {
                accountService.deleteRole(selected);
                refreshRoleGrid();
            }
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(addRoleBtn, editRoleBtn, deleteRoleBtn);
        section.add(roleGrid, buttonLayout);

        return section;
    }

    private VerticalLayout createRoleAssignmentSection() {
        VerticalLayout section = new VerticalLayout();
        section.setSpacing(true);

        H3 heading = new H3("Rollen zuweisen");
        section.add(heading);

        FormLayout form = new FormLayout();

        Select<UserDto> userSelect = new Select<>();
        userSelect.setLabel("Benutzer");
        userSelect.setItems(accountService.listUsers());
        userSelect.setItemLabelGenerator(UserDto::getUsername);

        Select<RoleDto> roleSelect = new Select<>();
        roleSelect.setLabel("Rolle");
        roleSelect.setItems(accountService.listRoles());
        roleSelect.setItemLabelGenerator(RoleDto::getName);

        Button assignBtn = new Button("Rolle zuweisen", e -> {
            UserDto user = userSelect.getValue();
            RoleDto role = roleSelect.getValue();
            if (user != null && role != null) {
                try {
                    accountService.assignRoleToUser(user.getId(), role.getId());
                    userSelect.clear();
                    roleSelect.clear();
                    refreshUserGrid();
                    refreshRoleGrid();
                } catch (Exception ex) {
                }
            }
        });

        form.add(userSelect, roleSelect);
        section.add(form, assignBtn);

        return section;
    }

    private void openUserDialog(UserDto user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(user == null ? "Benutzer anlegen" : "Benutzer bearbeiten");

        FormLayout form = new FormLayout();

        TextField usernameField = new TextField("Benutzername");
        usernameField.setValue(user != null ? user.getUsername() : "");

        PasswordField passwordField = new PasswordField("Passwort");
        passwordField.setValue(user != null ? user.getPassword() : "");

        Checkbox enabledCheckbox = new Checkbox("Aktiv");
        enabledCheckbox.setValue(user != null && user.isEnabled());

        form.add(usernameField, passwordField, enabledCheckbox);

        Button saveBtn = new Button("Speichern", e -> {
            UserDto dto = new UserDto();
            if (user != null) {
                dto.setId(user.getId());
            }
            dto.setUsername(usernameField.getValue());
            dto.setPassword(passwordField.getValue());
            dto.setEnabled(enabledCheckbox.getValue());
            dto.setRoles(user != null ? user.getRoles() : new ArrayList<>());

            try {
                if (user == null) {
                    accountService.createUser(dto);
                } else {
                    accountService.updateUser(dto);
                }
                refreshUserGrid();
                dialog.close();
            } catch (Exception ex) {
            }
        });

        Button cancelBtn = new Button("Abbrechen", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
        dialog.add(form, buttons);
        dialog.open();
    }

    private void openRoleDialog(RoleDto role) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(role == null ? "Rolle anlegen" : "Rolle bearbeiten");

        FormLayout form = new FormLayout();

        TextField nameField = new TextField("Rollenname");
        nameField.setValue(role != null ? role.getName() : "");

        TextField authorityField = new TextField("Authority");
        authorityField.setValue(role != null && role.getGrantedAuthority() != null ? role.getGrantedAuthority() : "");

        form.add(nameField, authorityField);

        Button saveBtn = new Button("Speichern", e -> {
            RoleDto dto = new RoleDto();
            if (role != null) {
                dto.setId(role.getId());
            }
            dto.setName(nameField.getValue());
            dto.setGrantedAuthority(authorityField.getValue());
            dto.setUsersWithRole(role != null ? role.getUsersWithRole() : new ArrayList<>());

            try {
                accountService.createRole(dto);
                refreshRoleGrid();
                dialog.close();
            } catch (Exception ex) {
            }
        });

        Button cancelBtn = new Button("Abbrechen", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
        dialog.add(form, buttons);
        dialog.open();
    }

    private void refreshUserGrid() {
        userGrid.setItems(accountService.listUsers());
    }

    private void refreshRoleGrid() {
        roleGrid.setItems(accountService.listRoles());
    }
}