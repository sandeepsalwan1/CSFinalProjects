package uk.ac.nulondon;

import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;


/*Summary:
 * Program includes functionality to interactively modify an image
 * The end result will be a new image file
 * Tip: type a file path such as src/main/resources/beach.png to get started
 * */

// main method
public class Operation {
    //initializing variables and data
    private final Image image;
    private final Stack < Modification > alterations;
    private int tempImgIndex;
    private final Scanner scanner;

    // class for performing operations
    public Operation(Image image) {
        this.image = image;
        this.alterations = new Stack < > (); // initialize alterations stack
        this.tempImgIndex = 0;
        scanner = new Scanner(System.in);
    }
    // deletes a random column from the image
    public void deleteRandomColumn() {
        int randomColumnIndex = getRandomColumnIndex(image.getPixels()); // get random column index
        Color[][] editedImage = highlightColumn(image.getPixels(), randomColumnIndex, Color.RED);
        createTempImage(editedImage);
        System.out.println("Do you want to continue(y/n)?");
        String choice = scanner.next();
        if (choice.equals("y")) {
            Color[] removedColumn = image.deleteColumn(randomColumnIndex);
            createTempImage(image.getPixels()); // create temporary image
            String changeType = "RANDOM_HIGHLIGHT";
            alterations.push(new Modification(randomColumnIndex, removedColumn, changeType));
        } else {
            System.out.println("No changes made");
            createTempImage(image.getPixels());
        }
    }
    // creates a temporary image with given pixels
    public void createTempImage(Color[][] pixels) {
        String fileName = "tempIMG_0" + tempImgIndex + ".png"; // generate temporary file
        tempImgIndex++;
        ImageHandler.saveImage(new Image(pixels), fileName);
    }
    // deletes column with the highest sum
    private void deleteBluestColumnIndex() {
        int bluestSum = Integer.MIN_VALUE;
        int bluestColumnIndex = -1;
        Color[][] pixels = image.getPixels();
        for (int i = 0; i < pixels.length; i++) {
            int sum = 0;
            for (Color color: pixels[i]) {
                sum += color.getBlue();
            }
            if (sum > bluestSum) {
                bluestSum = sum;
                bluestColumnIndex = i;
            }
        }
        Color[][] editedImage = highlightColumn(pixels, bluestColumnIndex, Color.BLUE);
        createTempImage(editedImage);
        System.out.println("Do you want to continue(y/n)?");
        String choice = scanner.next(); // get user choice
        if (choice.equals("y")) {
            Color[] removedColumn = image.deleteColumn(bluestColumnIndex);
            createTempImage(image.getPixels());
            String changeType = "BLUEST_HIGHLIGHT";
            alterations.push(new Modification(bluestColumnIndex, removedColumn, changeType));
        } else {
            System.out.println("No changes made");
            createTempImage(image.getPixels());
        }

    }
    // returns a random column index
    private int getRandomColumnIndex(Color[][] pixels) {
        return new Random().nextInt(pixels.length);
    }
    // highlights a column in the image
    private Color[][] highlightColumn(Color[][] pixels, int columnIndex, Color highlightColor) {
        Color[][] temp = new Color[pixels.length][pixels[0].length]; // temporary array for edited pixels
        for (int x = 0; x < pixels.length; x++) {
            System.arraycopy(pixels[x], 0, temp[x], 0, pixels[0].length);
        }
        for (int y = 0; y < temp[0].length; y++) {
            temp[columnIndex][y] = highlightColor;
        }
        return temp;
    }
    // undoes the previous edit
    public void undoPreviousEdit() {
        if (alterations.isEmpty()) {
            System.out.println("No edits to undo");
            return;
        }
        System.out.println("Do you want to undo the previous edit(y/n)?");
        String choice = scanner.next();
        if (!choice.equals("y")) {
            return;
        }
        Modification previousEdit = alterations.pop();
        Color[] removedColumn = previousEdit.data; // get removed column from previous edit
        int columnIndex = previousEdit.index;
        String changeType = previousEdit.changeType;
        image.addColumn(columnIndex, removedColumn);
        System.out.println("UNDO: DELETE");
        System.out.println("UNDO: " + changeType);
        createTempImage(image.getPixels());
    }
    // main method for executing all operations
    public static void main(String[] args) {
        String imagePath = ImageUI.promptImagePath();
        Image image = ImageHandler.loadImage(imagePath); // load image
        Operation operation = new Operation(image);
        String userChoice = String.valueOf(ImageUI.promptUserChoice());
        while (!userChoice.equals("q")) {
            switch (userChoice) {
                case "b":
                case "B":
                    operation.deleteBluestColumnIndex();
                    break;
                case "r":
                case "R":
                    operation.deleteRandomColumn();
                    break;
                case "u":
                case "U":
                    operation.undoPreviousEdit();
                    break;
                default:
                    System.out.println("Invalid Command");
            }
            userChoice = String.valueOf(ImageUI.promptUserChoice());
        }
        ImageHandler.saveImage(image, "newImg.png"); // saves final image
    }
    // encapsulates a modification made to image
    private static class Modification {
        private final int index;
        private final Color[] data;
        private final String changeType;

        public Modification(int index, Color[] data, String changeType) {
            this.index = index;
            this.data = data;
            this.changeType = changeType;
        }
    }
}