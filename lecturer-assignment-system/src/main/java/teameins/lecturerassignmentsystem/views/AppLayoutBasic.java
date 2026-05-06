package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

@Layout
@PermitAll
public class AppLayoutBasic extends AppLayout {


    public AppLayoutBasic(AuthenticationContext authenticationContext) {

        SideNav nav = getSideNav();
        Scroller scroller = new Scroller(nav);

        Button logoutButton = new Button(new Icon(VaadinIcon.SIGN_OUT), buttonClickEvent -> openLogoutDialog(authenticationContext));
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        logoutButton.setAriaLabel("Abmelden");

        addToDrawer(scroller, logoutButton);

        DrawerToggle toggle = new DrawerToggle();
        HorizontalLayout topBar = getTopbar();

        addToNavbar(toggle, topBar);
    }

    private HorizontalLayout getTopbar(){

        Image provadisLogo = new Image("images/provadis.svg", "Provadis");
        provadisLogo.setHeight("60px");

        H1 title = new H1("Dozentenverwaltungssystem");
        title.addClassName("h1-custom");

        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.setPadding(true);
        topBar.setSpacing(true);
        HorizontalLayout center = new HorizontalLayout(title);
        center.setWidthFull();
        center.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        topBar.add(provadisLogo, center);
        topBar.expand(center);
        return topBar;
    }

    private void openLogoutDialog(AuthenticationContext authenticationContext){
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Abmelden");
        dialog.add(new Paragraph("Möchten Sie sich wirklich abmelden?"));

        dialog.getFooter().add(new Button("Abbrechen", buttonClickEvent -> dialog.close()));
        Button logout = new Button("Abmelden", buttonClickEvent -> {
            authenticationContext.logout();
            dialog.close();
        });
        logout.addThemeVariants(ButtonVariant.LUMO_ERROR);
        dialog.getFooter().add(logout);
        dialog.open();
    }

    private SideNav getSideNav() {
        SideNav nav = new SideNav();
        nav.addClassName("sidebar-custom");
        nav.addItem(new SideNavItem("Dashboard", "/dashboard", VaadinIcon.MODAL_LIST.create()));
        nav.addItem(new SideNavItem("Vorlesungen", "/vorlesungen/", VaadinIcon.CALENDAR.create()));
        nav.addItem(new SideNavItem("Dozenten", "/dozenten/", VaadinIcon.USERS.create()));
        nav.addItem(new SideNavItem("Reports", "/reports", VaadinIcon.BAR_CHART.create()));
        nav.addItem(new SideNavItem("Admin", "/admin", VaadinIcon.WRENCH.create()));
        return nav;
    }
}
