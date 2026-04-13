package teameins.lecturerassignmentsystem.model.enums;

import java.util.Arrays;

public enum Affinity {
    HIGH("hoch"),
    MEDIUM("mittel"),
    LOW("niedrig");

    private final String value;

    Affinity(String value) {
        this.value = value;
    }

    public static boolean validate(String name) {
        return Arrays.stream(Affinity.values())
                .map(Affinity::getValue)
                .anyMatch(value -> value.equals(name));
    }

    public static String[] getValidValues() {
        return Arrays.stream(Affinity.values())
                .map(Affinity::getValue)
                .toArray(String[]::new);
    }

	public String getValue() {
		return value;
	}
}
