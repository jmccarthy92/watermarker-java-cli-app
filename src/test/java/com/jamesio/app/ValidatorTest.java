package com.jamesio.app;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Unit test for Command Dialog
 */
public class ValidatorTest {

    @Test
    public void validateImageFiles() {
        String[] paths = new String[] {
                "image.tiff",
                "image.bmp",
                "image.png",
                "image.gif",
                "image.jpg",
                "image.jpeg"
        };
        for (String path : paths) {
            try {
                assertTrue(new Validator(path).validate());
            } catch (IOException e) {
                assertTrue(false);
            }
        }
    }

    @Test
    public void validateVideoFiles() {
        String[] paths = new String[] {
                "video.mp4",
                "video.mov",
                "video.avi",
                "video.webm",
        };
        for (String path : paths) {
            try {
                assertTrue(new Validator(path).validate());
            } catch (IOException e) {
                System.out.println(e);
                assertTrue(false);
            }
        }
    }
}
