package teameins.lecturerassignmentsystem.model.enums;

import java.util.Arrays;

public enum Preference {
    ALLES("A", "Alle Vorlesungen."),
    ONLY_MASTER("XM", "Ausschließlich Master Vorlesungen."),
    ONLY_BACHELOR("XB", "Ausschließlich Bachelor Vorlesungen."),
    PREFER_MASTER("M", "Alle Vorlesungen mit Master Vorlesungen als Präferenz."),
    PREFER_BACHELOR("B", "Alle Vorlesungen mit Bachelor Vorlesungen als Präferenz.");

    private final String value;

    private final String description;

    Preference(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static boolean validate(String name) {
        return Arrays.stream(Preference.values())
                .map(Preference::getValue)
                .anyMatch(value -> value.equals(name));
    }

    public static String[] getValidValues() {
        return Arrays.stream(Preference.values())
                .map(Preference::getValue)
                .toArray(String[]::new);
    }

	public String getValue() {
		return value;
	}

    public String getDescription() {
        return description;
    }
}
