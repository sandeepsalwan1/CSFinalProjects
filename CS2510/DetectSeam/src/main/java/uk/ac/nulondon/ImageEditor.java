package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Uses operations from the Image class to store edithistory, and to perform more complex operations
 * than the Image class. This includes deletion, highlighting, and undoes, all interleaved with exporting
 * images and pushing/popping to/from editHistory
 * also has the number of versions
 */
public class ImageEditor {

    Image image;

    public Stack < List < Pixel >> editHistory;
    private static int NUM_OF_VERSIONS = 0;

    public ImageEditor() {
        editHistory = new Stack < > ();
    }

    /**
     * converts a file path to a buffered image
     * this bufferedImage is converted to the internal representation
     * in the Image constructor
     * assumes that this is a valid file path
     * the validity is checked in the call in the ImageUI class
     * @param filePath
     */
    public void importImage(String filePath) throws IOException {
        // convert the String to a file path
        File file = new File("src/main/resources/" + filePath);
        // convert file to an image
        BufferedImage rawImage = ImageIO.read(file);
        // convert image to internal representation
        image = new Image(rawImage);
    }

    /**
     * converts a BufferedImage to an actual .png
     * assumes that the fileName is already determined
     */
    public void exportImage(String fileName) throws IOException {
        BufferedImage rawImage = image.toBI();
        File newFile = new File(fileName);
        ImageIO.write(rawImage, "png", newFile);
        System.out.println(newFile.getName() + " saved successfully");
    }

    /**
     * @return file name for temp file
     */
    public static String fileName() {
        // first temp is "00"
        String name = "tempIMG_0" + (NUM_OF_VERSIONS) + ".png";
        NUM_OF_VERSIONS++;
        return name;
    }

    // maximizing the value (blue), is same as maximizing the negative value of the energy (energy)

    /**
     * helper function for highlightSeam, if want the blue
     * @param pixel
     * @return
     */
    public static double getBlue(Pixel pixel) {
        return pixel.getBlue();
    }

    /**
     * helper function for highlight seam, if want lowest energy
     * @param pixel
     * @return negative so we can still maximize
     */
    public static double getNegativeEnergy(Pixel pixel) {
        return -pixel.energy;
    }

    /**
     * finds a seam given a function, from top to bottom
     * @param criteria
     * @return
     */
    public List < Pixel > findSeam(Function < Pixel, Double > criteria) {

        int width = image.getWidth();
        int height = image.getHeight();
        // stores potential sums for each column
        double[] prevSum = new double[width];
        double[] currentSum = new double[width];

        // stores potential seams for each column
        List < List < Pixel >> prevSeams = new ArrayList < > (width);
        List < List < Pixel >> currentSeams = new ArrayList < > (width);

        // first row of seams/sums initialized
        Pixel currentPixel = image.pixels.getFirst();
        for (int x = 0; x < image.getWidth(); x++) {
            // adding values
            prevSum[x] = criteria.apply(currentPixel);
            prevSeams.add(new ArrayList < > (List.of(currentPixel)));
            // don't add anything to current seams b/c will fill later
            currentSeams.add(new ArrayList < > ());
            // iterate right
            currentPixel = currentPixel.right;
        }

        // going down each row, y=1 b/c already did y=0
        for (int y = 1; y < height; y++) {
            currentPixel = image.pixels.get(y);
            for (int x = 0; x < width; x++) {
                // Determine the maximum cumulative sum and the corresponding seam path
                double maxRunningTotal = prevSum[x];
                int bestPreviousIndex = x;
                // check upper left
                if (x > 0 && prevSum[x - 1] > maxRunningTotal) {
                    maxRunningTotal = prevSum[x - 1];
                    bestPreviousIndex = x - 1;
                }
                // check upper right
                if (x < width - 1 && prevSum[x + 1] > maxRunningTotal) {
                    maxRunningTotal = prevSum[x + 1];
                    bestPreviousIndex = x + 1;
                }
                // get the culmulative sum right now
                currentSum[x] = maxRunningTotal + criteria.apply(currentPixel);
                // get the best index right now and add it
                currentSeams.get(x).addAll(prevSeams.get(bestPreviousIndex));
                currentSeams.get(x).add(currentPixel);
                // iterate
                currentPixel = currentPixel.right;
            }

            // update prev sums
            prevSum = currentSum;
            prevSeams = new ArrayList < > (currentSeams);
            // reset sums for next iteration
            currentSum = new double[width];
            currentSeams.clear();
            // but also have to fill up inner oarts
            for (int i = 0; i < width; i++) {
                currentSeams.add(new ArrayList < > ());
            }
        }

        // get max index
        int maxIndex = 0;
        double maxSum = prevSum[0];
        for (int i = 1; i < width; i++) {
            if (prevSum[i] > maxSum) {
                maxSum = prevSum[i];
                maxIndex = i;
            }
        }
        return prevSeams.get(maxIndex);
    }
    /**
     * Highlights Seam
     * also pushes to edit history
     */
    public void highlightSeam(Function < Pixel, Double > seamCriteria, Color color) {
        // gets the actual pixels before the highlight
        List < Pixel > seamToSave = findSeam(seamCriteria);
        // will store the highlighted pixels
        List < Pixel > highlightedPixels = new ArrayList < > ();
        for (int i = 0; i < seamToSave.size(); i++) {
            Pixel highlightedPixel = new Pixel(color, seamToSave.get(i).left, seamToSave.get(i).right);
            highlightedPixels.add(highlightedPixel);
        }
        // now have the seamToSave
        editHistory.push(seamToSave);

        // and will remove the original seam
        image.removeSeam(seamToSave);
        // replace it with the highlighted one
        image.addSeam(highlightedPixels);
    }

    /**
     * deletes a seam and pushes it to the edithistory. This is not used in ImageUi, delete() is instead
     * does not
     * @param toBeDeleted
     */
    public void deleteSeam(List < Pixel > toBeDeleted) {
        editHistory.push(toBeDeleted);
        image.removeSeam(toBeDeleted);
    }

    /**
     * iterates through the entire image to update all the energies, takes O(n^2) time
     */
    public void updateImageEnergy() {
        // get three iterators, start calc on top-left corner
        Pixel current;
        Pixel above;
        Pixel below;

        for (int y = 0; y < image.getHeight(); y++) {
            current = image.pixels.get(y);
            // if above and below are at valid indicies, then assign them
            // else, they will be null
            above = y - 1 >= 0 ? image.pixels.get(y - 1) : null;
            below = y + 1 < image.getHeight() ? image.pixels.get(y + 1) : null;
            for (int x = 0; x < image.getWidth(); x++) {
                // first, calculate the actual energy
                current.calculateEnergy(above, below);
                // then move the iterators accordingly rightwards, if null, do nothing
                current = current.right;
                // if currently equals null, then do nothing
                above = above == null ? null : above.right;
                below = below == null ? null : below.right;
            }
        }
    }

    /**
     * Undos the operations, either the deletion (pop twice) or the highlights
     * (pop once). Used in ImageUI
     * @param op
     */
    public void undo(Operation op) throws IOException {
        if (!editHistory.isEmpty()) {
            List < Pixel > lastEdit = null;
            if (op == Operation.UNDO_DELETION) {
                editHistory.pop();
                lastEdit = editHistory.pop();
                image.addSeam(lastEdit);
                exportImage(fileName());

            } else if (op == Operation.UNDO_HIGHLIGHT) {
                lastEdit = editHistory.pop();
                image.addSeam(lastEdit);
            }

        }
    }

    /**
     * highlights a column, either blue for bluest column
     * or red for lowest energy column
     * @param op
     * @throws IOException
     */
    public void highlight(Operation op) throws IOException {
        if (op == Operation.BLUE_HIGHLIGHT) {
            highlightSeam(ImageEditor::getBlue, new Color(0, 0, 255));
        } else if (op == Operation.ENERGY_HIGHLIGHT) {
            // update energy b/c need to check
            updateImageEnergy();
            highlightSeam(ImageEditor::getNegativeEnergy, new Color(255, 0, 0));
        }
        exportImage(fileName());
    }

    /**
     * deletes a column. Pushes
     * @param op
     * @throws IOException
     */
    public void delete(Operation op) throws IOException {
        if (op == Operation.DELETION) {
            // .peek() b/c assuming highlight is at top of stack
            // this function should not be called if highlight has not been
            deleteSeam(editHistory.peek());
        }
        exportImage(fileName());
    }

    // runs test
    public static void main(String[] args) throws Exception {
        String fileName = "beach.png";

        ImageEditor ie = new ImageEditor();
        ie.importImage(fileName);
        // check the toString function
        // referencing, test works
        ie.updateImageEnergy();
        System.out.println(ie.image.toString());
        ie.highlight(Operation.ENERGY_HIGHLIGHT);
        System.out.println(ie.image.toString());
        ie.delete(Operation.DELETION);
        ie.highlight(Operation.BLUE_HIGHLIGHT);
        ie.delete(Operation.DELETION);
        ie.highlight(Operation.BLUE_HIGHLIGHT);
        ie.delete(Operation.DELETION);
    }
}