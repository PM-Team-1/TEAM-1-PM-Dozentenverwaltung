package teameins.lecturerassignmentsystem.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Title {
    DOCTOR("Dr."),
    PROFESSOR("Prof."),
    NO_TITLE("");

    private final String value;

    Title(String value) {
        this.value = value;
    }

    public static boolean validate(String name) {
        return Arrays.stream(Title.values())
                .map(Title::getValue)
                .anyMatch(value -> value.equals(name));
    }

    public static String[] getValidValues() {
        return Arrays.stream(Title.values())
                .map(Title::getValue)
                .toArray(String[]::new);
    }
}
