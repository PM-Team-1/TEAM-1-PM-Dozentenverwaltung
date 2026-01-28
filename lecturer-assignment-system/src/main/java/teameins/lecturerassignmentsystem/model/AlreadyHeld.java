package teameins.lecturerassignmentsystem.model;

public enum AlreadyHeld {
    PROVADIS("P"),
    OTHER_SCHOOL("A"),
    NOT_YET_HELD("N");

    private final String value;

    AlreadyHeld(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
