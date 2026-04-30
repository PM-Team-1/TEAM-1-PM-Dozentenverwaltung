package teameins.lecturerassignmentsystem.model.enums;

import java.util.Arrays;

public enum Qualification {
    IMMEDIATELY("S", "Kann sofort gehalten werden."),
    FOUR_WEEKS("4", "Kann nach 4 Wochen Vorbereitung gehalten werden."),
    OVER_FOUR_WEEKS("M", "Kann nach mehr als 4 Wochen Vorbereitung gehalten werden."),;

    private final String value;

    private final String description;

    Qualification(String value, String description) {
        this.value = value;
        this.description = description;
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

    public static String mapQualification(Qualification code) {
        if (code == null) return "-";
        return switch (code) {
            case Qualification.FOUR_WEEKS -> "Über vier Wochen";
            case Qualification.IMMEDIATELY -> "Keine";
            case Qualification.OVER_FOUR_WEEKS -> "vier Wochen";
            default -> code.getValue();
        };
    }

	public String getValue() {
		return value;
	}

    public String getDescription() {
        return description;
    }
}
