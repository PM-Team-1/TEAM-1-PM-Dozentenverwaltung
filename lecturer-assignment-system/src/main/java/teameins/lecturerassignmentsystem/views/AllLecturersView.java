package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataView;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.LecturerDto;
import teameins.lecturerassignmentsystem.service.LecturerService;

@Route("dozenten")
@PageTitle("Dozenten")
public class AllLecturersView extends VerticalLayout {

    private final transient LecturerService lecturerService;

    @Autowired
    public AllLecturersView(LecturerService lecturerService) {
        this.lecturerService = lecturerService;

        H2 heading = new H2("Dozenten");
        heading.addClassName("h2-custom");

        Grid<LecturerDto> lecturerGrid = getLecturerGrid();

        Div toolbar = getToolbar(lecturerGrid);

        add(heading);
        add(toolbar);
        add(lecturerGrid);
    }

    private Div getToolbar(Grid<LecturerDto> lecturerGrid) {
        Div toolbar = new Div();
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");

        Button addLecturerButton = new Button("Dozenten hinzufügen");
        addLecturerButton.addClickListener(e -> {
            // Implement create lecturer functionality here
        });

        TextField searchField = new TextField();
        searchField.setPlaceholder("Suche");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        DataView<LecturerDto> dataView = addSearchFunctionality(lecturerGrid, searchField);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        toolbar.add(addLecturerButton);
        toolbar.add(searchField);
        return toolbar;
    }

    private Grid<LecturerDto> getLecturerGrid() {
        Grid<LecturerDto> grid = new Grid<>(LecturerDto.class, false);
        grid.addClassName("grid-custom");
        grid.setAllRowsVisible(true);

        grid.addColumn(LecturerDto::getFullName).setHeader("Name").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(lecturer -> lecturer.isExtern() ? "Extern" : "Intern").setHeader("Status").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(LecturerDto::getEmail).setHeader("E-Mail").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(LecturerDto::getPhone).setHeader("Telefon").setAutoWidth(true).setFlexGrow(1);
        grid.addComponentColumn(lecturer -> {
            Button lecturerButton = new Button("Details");
            lecturerButton.addClickListener(e -> {
                int id = lecturer.getId();
                UI.getCurrent().navigate("dozenten/" + id);
            });
            return lecturerButton;
        }).setWidth("150px").setFlexGrow(0);

        grid.addItemDoubleClickListener(event -> {
            LecturerDto item = event.getItem();
            if (item != null && item.getId() != 0) {
                UI.getCurrent().navigate("dozenten/" + item.getId());
            }
        });

        grid.setItems(lecturerService.listLecturers());
        return grid;
    }

    private DataView<LecturerDto> addSearchFunctionality(Grid<LecturerDto> lecturerGrid, TextField searchField) {
        GridListDataView<LecturerDto> dataView = lecturerGrid.getListDataView();
        if (dataView == null) {
            dataView = lecturerGrid.setItems(lecturerService.listLecturers());
        }
        dataView.addFilter(lecturer -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesFullName = matchesTerm(lecturer.getFullName(), searchTerm);
            boolean matchesStatus = matchesTerm(lecturer.isExtern() ? "Extern" : "Intern", searchTerm);
            boolean matchesEmail = matchesTerm(lecturer.getEmail(), searchTerm);
            boolean matchesPhone = matchesTerm(lecturer.getPhone(), searchTerm);

            return matchesFullName || matchesStatus || matchesEmail || matchesPhone;
        });
        return dataView;
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value != null && value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
