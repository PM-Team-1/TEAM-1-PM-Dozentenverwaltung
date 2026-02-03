package teameins.lecturerassignmentsystem.model.db;

public enum Preference {
    ALLES("A"),
    ONLY_MASTER("XM"),
    ONLY_BACHELOR("XB"),
    PREFER_MASTER("M"),
    PREFER_BACHELOR("B");

    private final String value;

    Preference(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
