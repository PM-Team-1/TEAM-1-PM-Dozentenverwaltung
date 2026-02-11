package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;

@Layout
public class AppLayoutBasic extends AppLayout {

    public AppLayoutBasic() {

        Image provadis_logo = new Image("images/provadis.svg", "Provadis");
        provadis_logo.setHeight("40px");

        H1 title = new H1("Dozentenverwaltungssystem");
        title.addClassName("h1-custom");

        SideNav nav = getSideNav();

        Scroller scroller = new Scroller(nav);

        addToDrawer(scroller);
        addToNavbar(logo);
        addToNavbar(title);
    }

    private SideNav getSideNav() {
        SideNav nav = new SideNav();
        nav.addClassName("sidebar-custom");
        nav.addItem(new SideNavItem("Dozenten", "/dozenten", VaadinIcon.USERS.create()));
        return nav;
    }
}
