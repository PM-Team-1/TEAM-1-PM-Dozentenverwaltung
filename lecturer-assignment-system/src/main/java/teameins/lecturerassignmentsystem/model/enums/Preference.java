package teameins.lecturerassignmentsystem.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
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
}
