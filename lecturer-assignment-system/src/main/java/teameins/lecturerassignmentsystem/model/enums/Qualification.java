package teameins.lecturerassignmentsystem.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Qualification {
    IMMEDIATELY("S"),
    FOUR_WEEKS("4"),
    OVER_FOUR_WEEKS("M");

    private final String value;

    Qualification(String value) {
        this.value = value;
    }

    public static boolean validate(String name) {
        return Arrays.stream(Qualification.values())
                .map(Qualification::getValue)
                .anyMatch(value -> value.equals(name));
    }

    public static String[] getValidValues() {
        return Arrays.stream(Qualification.values())
                .map(Qualification::getValue)
                .toArray(String[]::new);
    }

    public static String mapQualification(String code) {
        if (code == null || code.isEmpty()) return "-";
        return switch (code.trim().toUpperCase()) {
            case "M" -> "Über vier Wochen";
            case "S" -> "Keine";
            case "4" -> "vier Wochen";
            default -> code;
        };
    }
}
