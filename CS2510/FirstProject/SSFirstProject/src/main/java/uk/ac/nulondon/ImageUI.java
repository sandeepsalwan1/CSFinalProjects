package uk.ac.nulondon;

import java.util.Scanner;
import java.io.File;

// user interface
public class ImageUI {
    private static final Scanner scanner = new Scanner(System.in);
    // asks user for image path
    public static String promptImagePath() {
        while (true) {
            System.out.println("Enter the file path of the image:");
            String imagePath = scanner.nextLine();
            if (new File(imagePath).exists()) {
                return imagePath;
            } else {
                System.out.println("Invalid file path. Please try again.");
            }
        }
    }
// asks user for command choice
    public static char promptUserChoice() {
        System.out.println("\nPlease enter a command");
        System.out.println("b - Remove the bluest column");
        System.out.println("r - Remove a random column");
        System.out.println("u - Undo previous edit");
        System.out.println("q - Quit");
        return scanner.next().charAt(0); // reads string from user
    }
    //closes scanner
    public static void closeScanner() {
        scanner.close();
    }
}