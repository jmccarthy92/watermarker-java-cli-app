package com.jamesio.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Validator {
    private static final String[] IMAGE_TYPES = new String[] { "image/png", "image/gif", "image/jpeg", "image/jpg",
            "image/tiff", "image/bmp" };
    private static final String[] VIDEO_TYPES = new String[] { "video/mp4", "video/quicktime", "video/x-ms-asf",
            "video/avi",
            "video/webm" };
    private String path;

    public Validator(String path) {
        this.path = path;
    }

    public boolean validate() throws IOException {
        String contentType = this.getContentType();
        if (contentType.contains("video") && !validateVideo(contentType))
            throw new IOException("Incorrect Video Type only types " + Arrays.toString(VIDEO_TYPES) + "accepted");
        else if (contentType.contains("image") && !validateImage(contentType))
            throw new IOException("Incorrect Image Type only types " + Arrays.toString(IMAGE_TYPES) + "accepted");
        return true;

    }

    private boolean validateImage(String contentType) {
        return Arrays.stream(IMAGE_TYPES).anyMatch(contentType::equals);

    }

    private boolean validateVideo(String contentType) {
        return Arrays.stream(VIDEO_TYPES).anyMatch(contentType::equals);
    }

    private String getContentType() throws IOException {
        return Files.probeContentType(Paths.get(path));
    }

}
