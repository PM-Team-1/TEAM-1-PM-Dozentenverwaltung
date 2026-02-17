package teameins.lecturerassignmentsystem.model.enums;

import java.util.Arrays;

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

    public static boolean validate(String name) {
        return Arrays.stream(Preference.values())
                .map(Preference::getValue)
                .anyMatch(value -> value.equals(name));
    }

    public static String[] getValidValues() {
        return Arrays.stream(AlreadyHeld.values())
                .map(AlreadyHeld::getValue)
                .toArray(String[]::new);
    }
}
