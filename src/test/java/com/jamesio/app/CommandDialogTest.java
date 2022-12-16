package com.jamesio.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import com.jamesio.app.CommandDialog.CommandDialogField;

/**
 * Unit test for Command Dialog
 */
public class CommandDialogTest {
    private final CommandDialog dialog = new CommandDialog();

    @Test
    public void testHelpDialog() {
        String[] args = new String[] { "--help" };
        try {
            dialog.parseOptions(args);
            assertTrue(true);
        } catch (ParseException pe) {
            assertTrue(false);
        }
    }

    @Test
    public void testPrintOptions() {
        String[] args = new String[] { "--sources", "file1.mp4", "--watermark", "watermark.mp4" };
        try {
            dialog.parseOptions(args);
            dialog.printOptions();
        } catch (ParseException pe) {
            assertTrue(false);
        }
    }

    @Test
    public void testDefaultDestination() {
        String[] args = new String[] { "--sources", "file1.mp4", "--watermark", "watermark.mp4" };
        try {
            dialog.parseOptions(args);
            assertEquals(dialog.getDestination(), ".");
        } catch (ParseException pe) {
            assertTrue(false);
        }
    }

    @Test
    public void testWatermark() {
        String[] args = new String[] { "--sources", "file1.mp4", "--watermark", "watermark.mp4" };
        try {
            dialog.parseOptions(args);
            assertEquals(dialog.getWatermark(), "watermark.mp4");
        } catch (ParseException pe) {
            assertTrue(false);
        }
    }

    @Test
    public void testSources() {
        String[] args = new String[] { "--sources", "file1.mp4", "file2.mp4", "--watermark", "watermark.mp4" };
        try {
            dialog.parseOptions(args);
            String[] expected = new String[] { "file1.mp4", "file2.mp4" };
            assertArrayEquals(dialog.getSources(), expected);
        } catch (ParseException pe) {
            assertTrue(false);
        }
    }

    @Test
    public void testHasHttpUri() {
        String[] args = new String[] { "--sources", "https://site.com/file2.mp4", "--watermark",
                "https://site.com/watermark.mp4", "--dest", "https://site.com/storage/" };
        try {
            dialog.parseOptions(args);
            assertTrue(dialog.hasHttpUri(CommandDialogField.SOURCES));
            assertTrue(dialog.hasHttpUri(CommandDialogField.WATERMARK));
            assertTrue(dialog.hasHttpUri(CommandDialogField.DESTINATION));
        } catch (ParseException pe) {
            assertTrue(false);
        }
    }
}
