package teameins.lecturerassignmentsystem.model.enums;

import java.util.Arrays;

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

    public static boolean validate(String name) {
        return Arrays.stream(Preference.values())
                .map(Preference::getValue)
                .anyMatch(value -> value.equals(name));
    }
}
