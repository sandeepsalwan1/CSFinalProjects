package uk.ac.nulondon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TestImage {

    Image original;
    ImageEditor editor;

    Image tiny;

    ImageEditor tinyEditor;

    Pixel p1 = new Pixel(new Color(0,0,1)), p5 = new Pixel(new Color(0,0,5)), p9 = new Pixel(new Color(0,0,9));
    Pixel p2 = new Pixel(new Color(0,0,2)), p4 = new Pixel(new Color(0,0,4)), p8 = new Pixel(new Color(0,0,8));
    Pixel p3 = new Pixel(new Color(0,0,3)), p6 = new Pixel(new Color(0,0,6)), p7 = new Pixel(new Color(0,0,7));


//    Finding the lowest energy seam
//    Highlighting a seam
    @BeforeEach
    void setUp() throws IOException {
        editor = new ImageEditor();
        editor.importImage("beach.png");
        original = editor.image; //'image' is accessible

        p1.right = p2;
        p2.left = p1; p2.right = p3;
        p3.left = p2;

        p4.right = p5;
        p5.left = p4; p5.right = p6;
        p6.left = p5;

        p7.right = p8;
        p8.left = p7; p8.right = p9;
        p9.left = p8;

        List<Pixel> pixels = new ArrayList<>();
        pixels.add(p1);
        pixels.add(p4);
        pixels.add(p7);
        tiny = new Image(pixels);

        tinyEditor = new ImageEditor();
        tinyEditor.image = tiny;
    }


    // Calculating the energy of a pixel
    @Test
    void testCalculateEnergy() {
        editor.updateImageEnergy();
        double try1 = original.getPixel(1, 1).energy;
        double check1;
        // need to check edges to make sure they exist, otherwise their values default to current pixel brightness
        double defaultBright = original.getPixel(1,1).getBrightness();

        // from "original.getPixel(1,0)"
        double topMid = original.getPixel(1,0) != null ? original.getPixel(1,0).getBrightness() : defaultBright;
        double topLeft = original.getPixel(1,0) != null && original.getPixel(1,0).left != null ? original.getPixel(1,0).left.getBrightness() : defaultBright;
        double topRight = original.getPixel(1,0) != null && original.getPixel(1,0).right != null ? original.getPixel(1,0).right.getBrightness() : defaultBright;

        // from current pixel
        double midLeft = original.getPixel(1,1).left != null ? original.getPixel(1,1).left.getBrightness() : defaultBright;
        double midRight = original.getPixel(1,1).right != null ? original.getPixel(1,1).right.getBrightness() : defaultBright;

        // from "original.getPixel(1,2)"
        double botMid = original.getPixel(1,2) != null ? original.getPixel(1,2).getBrightness() : defaultBright;
        double botLeft = original.getPixel(1,2) != null && original.getPixel(1,2).left != null ? original.getPixel(1,2).left.getBrightness() : defaultBright;
        double botRight = original.getPixel(1,2) != null && original.getPixel(1,2).right != null ? original.getPixel(1,2).right.getBrightness() : defaultBright;

        // now putting it altogether
        double leftHorizontal = topLeft + 2*midLeft + botLeft;
        double rightHorizontal = topRight + 2*midRight + botRight;
        double horizontalEnergy = leftHorizontal - rightHorizontal;

        double topVertical = topLeft + 2*topMid + topRight;
        double botVertical = botRight + 2*botMid + botLeft;
        double verticalEnergy = topVertical - botVertical;

        check1 = Math.sqrt((horizontalEnergy * horizontalEnergy) + (verticalEnergy * verticalEnergy));
        assertThat(try1).isEqualTo(check1);
        double try2 = original.getPixel(0,7).energy;
        double check2 = 0;
        assertThat(try2).isEqualTo(check2);
    }

    @Test
    void testRemoveSeamThenAddSeam() {
        List<Pixel> removed1 = List.of(p1,p5,p9);
        tiny.removeSeam(removed1);
        assertThat(p2.left == null && p4.right == p6 && p8.right == null).isEqualTo(true);
        List<Pixel> removed2 = List.of(p2,p6,p7);
        tiny.removeSeam(removed2);
        assertThat(p3.left == null && p3.right == null && p4.left == null && p4.right == null && p8.left == null && p8.right == null).isEqualTo(true);

        // now we are adding the seams back
        // is in same test b/c I would have to remove the seams again if I made a new one to add it back
        tiny.addSeam(removed2);
        assertThat(p3.left == p2 && p4.right == p6 && p8.left == p7).isEqualTo(true);
        tiny.addSeam(removed1);
        assertThat(p2.left == p1 && p4.right == p5 && p6.left == p5 && p8.right == p9).isEqualTo(true);
    }

    @Test
    void testSeamFindingBlue() {
        // will test finding a seam by finding it, removing it, and checking if the image
        // resulting is correct
        List<Pixel> blue1 = tinyEditor.findSeam(ImageEditor::getBlue);
        tiny.removeSeam(blue1);
        assertThat(p2.right == null && p5.right == null && p8.right == null).isEqualTo(true);

        List<Pixel> blue2  = tinyEditor.findSeam(ImageEditor::getBlue);
        tiny.removeSeam(blue2);
        assertThat(p1.right == null && p4.right == null && p7.right == null).isEqualTo(true);
    }

    @Test
    void testSeamFindingEnergy() {
        // will test finding a seam by finding it, removing it, and checking if the image
        // resulting is correct
        tinyEditor.updateImageEnergy();
        List<Pixel> energy1 = tinyEditor.findSeam(ImageEditor::getNegativeEnergy);
        tiny.removeSeam(energy1);
        assertThat(p2.right == null && p5.right == null && p8.right == null).isEqualTo(true);

        tinyEditor.updateImageEnergy();
        System.out.println(tiny);
        List<Pixel> energy2 = tinyEditor.findSeam(ImageEditor::getNegativeEnergy);
        tiny.removeSeam(energy2);
        System.out.println(tiny);

        assertThat(p1.right == null && p4.right == null && p8.left == null).isEqualTo(true);
    }

    @Test
    void testHighlightSeam() throws IOException {
        tinyEditor.highlight(Operation.BLUE_HIGHLIGHT);
        assertThat(tiny.getPixel(2,0).getBlue() == 255 && tiny.getPixel(2,1).getBlue() == 255 && tiny.getPixel(2,2).getBlue() == 255).isEqualTo(true);
        tinyEditor.highlight(Operation.ENERGY_HIGHLIGHT);
    }

    @Test
    void testImageDimensions() {
        // Test to ensure the image is loaded correctly by checking its dimensions
        assertThat(original).isNotNull();
        assertThat(original.getHeight()).isGreaterThan(0);
        assertThat(original.getWidth()).isGreaterThan(0);
    }

    @Test
    void testPixelColor() {
        // Test to check if we can retrieve a pixel's color correctly
        Pixel pixel = original.getPixel(0, 0); // Assuming the image has at least one pixel
        assertThat(pixel).isNotNull();
        assertThat(pixel.color).isInstanceOf(Color.class);
    }

    @Test
    void testToBufferedImageConversion() {
        // Test the conversion to BufferedImage
        BufferedImage bi = original.toBI();
        assertThat(bi).isNotNull();
        assertThat(bi.getWidth()).isEqualTo(original.getWidth());
        assertThat(bi.getHeight()).isEqualTo(original.getHeight());
    }

    @Test
    void testGetPixel() {
        Pixel origin = original.getPixel(0,0);
        Pixel botLeft = original.getPixel(7,7);
        assertThat(origin.color.getRGB()).isEqualTo(-3584);
        assertThat(botLeft.color.getRGB()).isEqualTo(-662372);
    }

    @Test
    void testImportExport() {
        Image importExport = new Image(original.toBI());
        assertThat(importExport.toString()).isEqualTo(original.toString());
    }
}
