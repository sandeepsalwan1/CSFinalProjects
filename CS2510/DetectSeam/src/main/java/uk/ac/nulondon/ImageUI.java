package uk.ac.nulondon;
import java.awt.*;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

/**
 * UI class
 */
public class ImageUI {
    private static final Scanner scanner = new Scanner(System.in);
    private ImageEditor imageEditor; // A reference to the image editor for operations

    /**
     * Constructor initializing UI with an instance
     * @param editor
     */
    public ImageUI(ImageEditor editor) {
        this.imageEditor = editor;
    }

    /**
     *     Main interaction Loop
     */
    public void start() throws IOException {
        boolean exit = false;
        while (!exit) {
            switch (promptUserChoice()) {
                case 'b':
                    if(imageEditor.image.getWidth() >= 2) {
                        imageEditor.highlight(Operation.BLUE_HIGHLIGHT);
                        confirmAndDeleteSeam();
                    } else {
                        System.out.println("Cannot highlight any more seams!");
                    }
                    break;
                case 'e':
                    if(imageEditor.image.getWidth() >= 2) {
                        imageEditor.highlight(Operation.ENERGY_HIGHLIGHT);
                        confirmAndDeleteSeam();
                    } else {
                        System.out.println("Cannot highlight any more seams!");
                    }
                    break;
                case 'd':
                    if(imageEditor.image.getWidth() < 2 ) {
                        System.out.println("Cannot delete any more seams!");
                    } else if(imageEditor.editHistory.isEmpty() || imageEditor.editHistory.size() % 2 != 0) {
                        System.out.println("Need to highlight a column first!");
                    } else {
                        confirmAndDeleteSeam();
                    }
                    break;
                case 'u':
                    // if edit history length is even, that means that a highlight has happened
                    // and a removal has happened
                    // which means we can undo a deletion
                    if(imageEditor.editHistory.isEmpty())
                        System.out.println("Cannot undo, no edit history!");
                    else
                        undoLastAction(Operation.UNDO_DELETION);
                    break;
                case 'q':
                    imageEditor.exportImage("newImg.png");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
                    break;
            }
        }
        System.exit(0);
    }

    /**
     * Confirms with user before deleting a seam
     * @throws IOException
     */
    private void confirmAndDeleteSeam() throws IOException {
        System.out.println("Do you want to delete the highlighted seam? Press d to continue, press any other key to cancel");
        char response = scanner.next().charAt(0);
        if (response == 'd' || response == 'D') {
            imageEditor.delete(Operation.DELETION);
        } else {
            undoLastAction(Operation.UNDO_HIGHLIGHT);
        }
    }

    /**
     * undos user input
     */
    private void undoLastAction(Operation op) throws IOException {
        if(op == Operation.UNDO_DELETION) {
            imageEditor.undo(Operation.UNDO_DELETION);
            System.out.println("Undo successful.");
        } else if(op == Operation.UNDO_HIGHLIGHT) {
            imageEditor.undo(Operation.UNDO_HIGHLIGHT);
            System.out.println("Deletion canceled.");
        }
    }

    /**
     * Prompts image path from user
     * @return
     */
    public static String promptImagePath() {
        while (true) {
            System.out.println("Enter the file path of the image (ex: beach.png):");
            String imagePath = scanner.nextLine();
            if (new File("src/main/resources/" + imagePath).exists()) {
                return imagePath;
            } else {
                System.out.println("Invalid file path. Please try again.");
            }
        }
    }

    /**
     *     Displays command menu
     */
    public static char promptUserChoice() {
        System.out.println("\nPlease enter a command:");
        System.out.println("b - Highlight the bluest seam");
        System.out.println("e - Highlight the seam with the lowest energy");
        System.out.println("d - Remove the highlighted seam");
        System.out.println("u - Undo previous edit");
        System.out.println("q - Quit");
        return scanner.next().charAt(0);
    }

    /**
     * main method
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ImageEditor editor = new ImageEditor();
        try {
            String imagePath = promptImagePath(); // Called inside the main
            editor.importImage(imagePath);
        } catch (IOException e) {
            System.out.println("Failed to load image: " + e.getMessage());
            return;
        }

        ImageUI ui = new ImageUI(editor);
        ui.start(); // start the UI interaction loop
    }
}
