package uk.ac.nulondon;

public class StringFuncs {

    public boolean startsWithY(String test) {
        char firstCharacter = test.charAt(0);
        if (firstCharacter == 'y' || firstCharacter == 'Y') {
            return true;
        } else {
            return false;
        }
    }

    // A cleaner implementation of startsWithY
    // Beware!! An empty string will break this.
    public boolean startsWithY2(String test) {
        return (test.charAt(0) == 'y' || test.charAt(0) == 'Y');
    }

    // Returns the first character concatenated with the string length
    // Ex: "ben" would become "B3"
    public String bingoWord1(String toConvert) {
        // Capitalize the word and then grab the first character
        char firstCharacter = toConvert.toUpperCase().charAt(0);

        // Get the word length
        int len = toConvert.length();

        // Returns the initial and concatenates it with the string length
        return "%s%d".formatted(firstCharacter, len);
    }

    // A faster version of bingoWord
    public String bingoWord2(String toConvert) {
        return (Character.toUpperCase(toConvert.charAt(0)) +
                Integer.toString(toConvert.length()));
    }

    public static void main(String[] args) {}

}