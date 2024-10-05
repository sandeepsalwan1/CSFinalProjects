package uk.ac.nulondon;

import java.util.function.Predicate;

public final class App {
    private App() {

    }
    static boolean startsWithY(String input1) {
        return input1.charAt(0) == 'y';
    }
    static String bingoWord(String input2) {
        return input2.toUpperCase().charAt(0) + " " + input2.length();
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        String s = "hello";
        System.out.println();
        //        App app = new App();
        System.out.println(startsWithY("test"));
        bingoWord("Yosh");
        bingoWord("sda");

    }
}