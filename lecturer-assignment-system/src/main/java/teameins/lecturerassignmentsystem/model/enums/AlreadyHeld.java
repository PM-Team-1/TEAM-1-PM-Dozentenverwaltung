package teameins.lecturerassignmentsystem.model.enums;

import java.util.Arrays;

public enum AlreadyHeld {
    PROVADIS("P", "Bereits an der Provadis gehalten."),
    OTHER_SCHOOL("A", "Bereits an einer anderen Schule als der Provadis gehalten."),
    NOT_YET_HELD("N", "Noch nicht gehalten.");

    private final String value;

    private final String description;

    AlreadyHeld(String value, String description) {
        this.value = value;
        this.description = description;
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

    public static String mapAlreadyHeld(AlreadyHeld code) {
        if (code == null) return "-";
        return switch (code) {
            case AlreadyHeld.PROVADIS -> "Provadis";
            case AlreadyHeld.OTHER_SCHOOL -> "Andere Hochschule";
            case AlreadyHeld.NOT_YET_HELD -> "Noch nicht gehalten";
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
