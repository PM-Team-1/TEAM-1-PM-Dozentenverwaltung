package teameins.lecturerassignmentsystem.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataView;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import teameins.lecturerassignmentsystem.model.dto.CourseDto;
import teameins.lecturerassignmentsystem.service.CourseService;
import teameins.lecturerassignmentsystem.views.components.CreateCourseDialog;

@Route("vorlesungen")
@PageTitle("Vorlesungen")
public class AllCoursesView extends VerticalLayout {

    private final transient CourseService courseService;

    @Autowired
    public AllCoursesView(CourseService courseService) {
        this.courseService = courseService;

        H2 heading = new H2("Vorlesungen");
        heading.addClassName("h2-custom");

        Grid<CourseDto> courseGrid = getCoursesGrid();

        Div toolbar = getToolbar(courseGrid);

        add(heading);
        add(toolbar);
        add(courseGrid);
    }

    private Div getToolbar(Grid<CourseDto> courseGrid) {
        Div toolbar = new Div();
        toolbar.setWidthFull();
        toolbar.addClassName("toolbar");

        Button addLecturerButton = new Button("Vorlesung hinzufügen");
        addLecturerButton.addClickListener(e -> new CreateCourseDialog(courseService));

        TextField searchField = new TextField();
        searchField.setPlaceholder("Suche");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        DataView<CourseDto> dataView = addSearchFunctionality(courseGrid, searchField);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        toolbar.add(addLecturerButton);
        toolbar.add(searchField);
        return toolbar;
    }

    private Grid<CourseDto> getCoursesGrid() {
        Grid<CourseDto> grid = new Grid<>(CourseDto.class, false);
        grid.addClassName("grid-custom");
        grid.setAllRowsVisible(true);

        grid.addColumn(CourseDto::getName).setHeader("Name")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(course -> course.isMaster() ? "Master" : "Bachelor").setHeader("Grad")
                .setSortable(true)
                .setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(CourseDto::getSemester).setHeader("Semester")
                .setSortable(true).setComparator(CourseDto::getSemesterSortable)
                .setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(course -> course.isClosed() ? "Geschlossen" : "Offen").setHeader("Zugänglichkeit")
                .setAutoWidth(true).setFlexGrow(1);
        grid.addComponentColumn(course -> {
            Button courseButton = new Button("Details");
            courseButton.addClickListener(e -> {
                int id = course.getId();
                UI.getCurrent().navigate("vorlesungen/" + id);
            });
            return courseButton;
        }).setWidth("150px").setFlexGrow(0);

        grid.addItemDoubleClickListener(event -> {
            CourseDto item = event.getItem();
            if (item != null && item.getId() != 0) {
                UI.getCurrent().navigate("vorlesungen/" + item.getId());
            }
        });

        grid.setItems(courseService.listCourses());
        return grid;
    }

    private DataView<CourseDto> addSearchFunctionality(Grid<CourseDto> courseGrid, TextField searchField) {
        GridListDataView<CourseDto> dataView = courseGrid.getListDataView();
        if (dataView == null) {
            dataView = courseGrid.setItems(courseService.listCourses());
        }
        dataView.addFilter(course -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesName = matchesTerm(course.getName(), searchTerm);
            boolean matchesDegree = matchesTerm(course.isMaster() ? "Master" : "Bachelor", searchTerm);
            boolean matchesSemester = matchesTerm(course.getSemester(), searchTerm);
            boolean matchesAccessibility = matchesTerm(course.isClosed() ? "Geschlossen" : "Offen", searchTerm);

            return matchesName || matchesDegree || matchesSemester || matchesAccessibility;
        });
        return dataView;
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value != null && value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
