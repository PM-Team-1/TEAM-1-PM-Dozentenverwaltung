package teameins.lecturerassignmentsystem.model.enums;

import java.util.Arrays;

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

    public static boolean validate(String name) {
        return Arrays.stream(AlreadyHeld.values())
                .map(AlreadyHeld::getValue)
                .anyMatch(value -> value.equals(name));
    }

    public static String[] getValidValues() {
        return Arrays.stream(AlreadyHeld.values())
                .map(AlreadyHeld::getValue)
                .toArray(String[]::new);
    }

    public static String mapAlreadyHeld(String code) {
        if (code == null || code.isEmpty()) return "-";
        return switch (code.trim().toUpperCase()) {
            case "P" -> "Provadis";
            case "A" -> "Andere Hochschule";
            case "N" -> "Noch nicht gehalten";
            default -> code;
        };
    }
}
