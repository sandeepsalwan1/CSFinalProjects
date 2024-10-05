package uk.ac.nulondon;

import java.awt.*;

/**
 * represents a Pixel similar to a linkedlist. Has a color, and energy (data) and the left and right neigbhors.
 * Can also calculate its energy given two pixels
 */
public class Pixel {

    double energy;

    Pixel left;
    Pixel right;

    Color color;


    /**
     * used for initialization
     * @param color
     * @param left
     * @param right
     */
    public Pixel(Color color, Pixel left, Pixel right) {
        // default is -1 since we didn't do the energy calc yet
        this.energy = -1;
        this.left = left;
        this.right = right;
        this.color = color;
    }

    /**
     * helper constructor
     * @param RGB
     */
    public Pixel(int RGB) {
        this.energy = -1;
        this.left = null;
        this.right = null;
        this.color = new Color(RGB);
    }

    /**
     * helper constructor
     * @param color
     */
    public Pixel(Color color) {
        this.energy = -1;
        this.left = null;
        this.right = null;
        this.color = color;
    }

    /**
     * gets the blue RGB value
     * @return
     */
    public int getBlue() {
        return color.getBlue();
    }

    /**
     * gets the brightness of a pixel
     * @return
     */
    public double getBrightness() {
        return (color.getRed() + color.getBlue() + color.getGreen()) / 3.0;
    }

    /**
     * calculates the energy of a pixel given its top and bottom neighbors
     * doesn't return anything, instead reassign the energy value of the current pixel
     * This runs in O(1) time
     * @param above the top
     * @param below the bottom
     */
    public void calculateEnergy(Pixel above, Pixel below) {
        // need to check edges to make sure they exist, otherwise their values default to current pixel brightness
        double defaultBright = getBrightness();

        // from "above"
        double topMid = above != null ? above.getBrightness() : defaultBright;
        double topLeft = above != null && above.left != null ? above.left.getBrightness() : defaultBright;
        double topRight = above != null && above.right != null ? above.right.getBrightness() : defaultBright;

        // from current pixel
        double midLeft = this.left != null ? left.getBrightness() : defaultBright;
        double midRight = this.right != null ? right.getBrightness() : defaultBright;

        // from "below"
        double botMid = below != null ? below.getBrightness() : defaultBright;
        double botLeft = below != null && below.left != null ? below.left.getBrightness() : defaultBright;
        double botRight = below != null && below.right != null ? below.right.getBrightness() : defaultBright;

        // now putting it altogether
        double leftHorizontal = topLeft + 2*midLeft + botLeft;
        double rightHorizontal = topRight + 2*midRight + botRight;
        double horizontalEnergy = leftHorizontal - rightHorizontal;

        double topVertical = topLeft + 2*topMid + topRight;
        double botVertical = botRight + 2*botMid + botLeft;
        double verticalEnergy = topVertical - botVertical;

        this.energy = Math.sqrt((horizontalEnergy * horizontalEnergy) + (verticalEnergy * verticalEnergy));

    }
}