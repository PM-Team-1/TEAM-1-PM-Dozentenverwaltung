package teameins.lecturerassignmentsystem.model.enums;

import java.util.Arrays;

public enum TeachingPreference {
    ALLES("A"),
    ONLY_MASTER("XM"),
    ONLY_BACHELOR("XB"),
    PREFER_MASTER("M"),
    PREFER_BACHELOR("B");

    private final String value;

    TeachingPreference(String value) {
        this.value = value;
    }

    public static boolean validate(String name) {
        return Arrays.stream(TeachingPreference.values())
                .map(TeachingPreference::getValue)
                .anyMatch(value -> value.equals(name));
    }

    public static String[] getValidValues() {
        return Arrays.stream(TeachingPreference.values())
                .map(TeachingPreference::getValue)
                .toArray(String[]::new);
    }

	public String getValue() {
		return value;
	}
}
