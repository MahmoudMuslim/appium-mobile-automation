package com.mahmoud.appium.utils;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Saves device screenshots to {@code target/screenshots}. Used by the tests to
 * capture the evidence required by the task deliverables.
 */
public final class ScreenshotUtil {

    private static final Path SCREENSHOT_DIR = Paths.get("target", "screenshots");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private ScreenshotUtil() {
    }

    /**
     * Takes a screenshot and stores it as {@code <name>-<timestamp>.png}.
     *
     * @return the saved file path, or {@code null} if capturing failed.
     */
    public static Path capture(AndroidDriver driver, String name) {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = SCREENSHOT_DIR.resolve(name + "-" + LocalDateTime.now().format(STAMP) + ".png");
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not capture screenshot '" + name + "': " + e.getMessage());
            return null;
        }
    }
}
