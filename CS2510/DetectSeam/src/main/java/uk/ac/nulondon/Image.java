package uk.ac.nulondon;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.image.BufferedImage;

/**
 * List of pixels is the leftmost column of the image. I can access any of the leftmost pixels in O(1) time
 * Accessing everuthing else to the right requires iteration, similar to a linked list
 * Has basic operations such as adding a column, deleting one, importing and exporting images,
 */
public class Image {

    // each pixel is of type color
    // each node in the linked list has access to its left/right neighbor
    // Each linked list represents a row in the image
    // pixels is the list of rows, which make an image

    List < Pixel > pixels;

    /**
     * constructor converts BufferedImage to type of pixels
     * converts it row by row, from left to right
     * @param bi
     */
    public Image(BufferedImage bi) {
        // instantiate the list
        this.pixels = new ArrayList < > ();
        // now loop through the buffered image, left to right, top to bottom
        // looping through the bi, left to right, top to bottom, row by row

        for (int y = 0; y < bi.getHeight(); y++) {
            // starts with the leftmost pixel
            Pixel previousPixel = new Pixel(bi.getRGB(0, y));
            pixels.add(previousPixel);
            for (int x = 1; x < bi.getWidth(); x++) {
                // now need to make the row, need to have 2 iterators
                // first create the new pixel
                Pixel currentPixel = new Pixel(bi.getRGB(x, y));
                // then link it up with its previous one both ways
                // (there will always exist a previous one due to "leftmostPixel")
                currentPixel.left = previousPixel;
                previousPixel.right = currentPixel;
                // reassign previous pixel for next cycle
                previousPixel = currentPixel;
            }
        }
    }

    /**
     * used for testing
     * @param pixels
     */
    public Image(List<Pixel> pixels) {
        this.pixels = pixels;
    }

    /**
     * converts image rep. to BufferredImage for controller
     * @return BufferedImage
     */
    public BufferedImage toBI() {
        // new buffer for alteredImage
        BufferedImage newImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        // iterate through the image and add rgb values to the newImg
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                // get the color at this coordinate
                Color pixelColor = getPixel(x, y).color;
                // flipped because j=x, i=y
                newImg.setRGB(x, y, pixelColor.getRGB());
            }
        }
        return newImg;
    }

    /**
     * gets a pixel at a specific coordinate
     * (0,0) at top left corner
     * @param x coord
     * @param y coord
     * @return color
     */
    Pixel getPixel(int x, int y) {
        Pixel pixel = pixels.get(y);
        for (int i = 0; i < x; i++)
            pixel = pixel.right;
        return pixel;
    }
    /**
     * gets the height of the image
     * @return the height
     */
    public int getHeight() {
        return pixels.size();
    }

    /**
     * gets the width of the image
     * @return the width
     */
    public int getWidth() {
        return getWidth(pixels.getFirst());
    }

    /**
     * helper function for getWidth
     * @param pixel
     * @return
     */
    private int getWidth(Pixel pixel) {
        if (pixel.right == null)
            return 1;
        return 1 + getWidth(pixel.right);
    }

    // Calculate energy

    /**
     * converts the list representation to a String for testing
     * @return the String
     */
    public String toString() {
        StringBuilder rv = new StringBuilder();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                rv.append(getPixel(x, y).energy);
                // if it is the last pixel in the row, no space
                if (x + 1 != getWidth())
                    rv.append(" ");
            }
            rv.append(System.lineSeparator());
        }
        return rv.toString();
    }

    // need to highlight a seam, to take O(n)
    // assuming that I already have searched for it

    /**
     * removes a seam
     * @param removed has the data on what to remove
     */
    public void removeSeam(List < Pixel > removed) {
        for (int i = 0; i < removed.size(); i++) {
            // if not on left edge
            if (removed.get(i).left != null) {
                // get left pixel to point to you
                removed.get(i).left.right = removed.get(i).right;
            } else {
                // update the list element
                pixels.set(i, removed.get(i).right);
            }
            if (removed.get(i).right != null)
                removed.get(i).right.left = removed.get(i).left;
        }
    }
    /**
     * adds seam
     /* @param List<Pixel> data
     */
    public void addSeam(List < Pixel > data) {
        for (int i = 0; i < data.size(); i++) {
            // if the pixel we are inserting is not on left edge
            if (data.get(i).left != null) {
                data.get(i).left.right = data.get(i);
            } else {
                pixels.set(i, data.get(i));
            }
            if (data.get(i).right != null) {
                data.get(i).right.left = data.get(i);
            }
        }
    }
}