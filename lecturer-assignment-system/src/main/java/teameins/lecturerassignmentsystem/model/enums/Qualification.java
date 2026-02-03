package teameins.lecturerassignmentsystem.model.enums;

public enum Qualification {
    IMMEDIATELY("S"),
    FOUR_WEEKS("4"),
    OVER_FOUR_WEEKS("M");

    private final String value;

    Qualification(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
