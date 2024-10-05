package uk.ac.nulondon;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// class for handling image loading and operations
public class ImageHandler {
    // loads image from specfic file path
    public static Image loadImage(String imagePath) {
        try {
            File file = new File(imagePath);
            BufferedImage bufferedImage = ImageIO.read(file);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            Color[][] pixels = new Color[width][height]; // initialize a 2D array

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    pixels[x][y] = new Color(bufferedImage.getRGB(x, y)); // create a BufferedImage
                }
            }
            return new Image(pixels);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // saves the given image to output path
    public static void saveImage(Image image, String outputPath) {
        int width = image.getPixels().length;
        int height = image.getPixels()[0].length; // creates image with specific dimensions
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, image.getPixels()[x][y].getRGB());
            }
        }

        try {
            ImageIO.write(bufferedImage, "png", new File(outputPath));
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace(); // print the stack trace
        }
    }
}