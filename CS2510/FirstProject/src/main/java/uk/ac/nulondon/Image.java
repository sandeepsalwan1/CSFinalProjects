package uk.ac.nulondon;
import java.awt.Color;
// class representing an image with pixel data
public class Image {
    // 2D array storing colors
    private Color[][] pixels;

    // constructor
    public Image(Color[][] pixels) {
        this.pixels = pixels;
    }

    // Getting pixel data
    public Color[][] getPixels() {
        return pixels;
    }

    // setting pixel data
    public void setPixels(Color[][] pixels) {
        this.pixels = pixels;
    }

    // Deletes column + returns colors of deleted part
    public Color[] deleteColumn(int columnIndex) {
        Color[] removedColumn = pixels[columnIndex];
        Color[][] temp = new Color[pixels.length - 1][pixels[0].length];
        for (int x = 0; x < pixels.length; x++) {
            if (x < columnIndex) {
                temp[x] = pixels[x];
            } else if (x > columnIndex) {
                temp[x - 1] = pixels[x];
            }
        }
        pixels = temp;
        return removedColumn; // return the colors
    }

    // Adds column + update pixel array with temp array
    public void addColumn(int columnIndex, Color[] column) {
        Color[][] temp = new Color[pixels.length + 1][pixels[0].length];
        for (int x = 0; x < pixels.length; x++) {
            if (x < columnIndex) {
                temp[x] = pixels[x];
            } else {
                temp[x + 1] = pixels[x];
            }
        }
        temp[columnIndex] = column;
        pixels = temp;
    }
}
}