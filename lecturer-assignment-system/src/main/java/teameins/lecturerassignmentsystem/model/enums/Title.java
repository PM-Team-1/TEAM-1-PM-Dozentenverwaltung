package teameins.lecturerassignmentsystem.model.enums;

public enum Title {
    DOCTOR("Dr."),
    PROFESSOR("Prof."),
    NO_TITLE("");

    private final String value;

    Title(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
