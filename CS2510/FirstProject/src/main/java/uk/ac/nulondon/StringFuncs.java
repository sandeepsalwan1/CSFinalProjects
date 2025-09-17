package uk.ac.nulondon;

public final class StringFuncs {

    public static boolean startsWithY(String test) {
        char firstCharacter = test.charAt(0);
        return firstCharacter == 'y' || firstCharacter == 'Y';
    }

    // Beware!! An empty string will break this.
    public static boolean startsWithY2(String test) {
        return test.charAt(0) == 'y' || test.charAt(0) == 'Y';
    }

    // Returns the first character concatenated with the string length
    // Ex: "ben" would become "B3"
    public static String bingoWord1(String toConvert) {
        char firstCharacter = toConvert.toUpperCase().charAt(0);
        int len = toConvert.length();
        return "%s%d".formatted(firstCharacter, len);
    }

    public static String bingoWord2(String toConvert) {
        return Character.toUpperCase(toConvert.charAt(0))
            + Integer.toString(toConvert.length());
    }
}
