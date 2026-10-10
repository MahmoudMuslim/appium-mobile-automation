package com.mahmoud.appium.utils;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

/**
 * Low-level mobile gesture helpers implemented with the W3C Actions API
 * (Selenium 4 / Appium 9). The legacy {@code TouchAction} class was removed in
 * Appium 8, so every gesture here is expressed as a pointer sequence.
 */
public final class Gestures {

    private static final PointerInput.Kind TOUCH = PointerInput.Kind.TOUCH;
    /** Pointer button id expected by createPointerDown/createPointerUp (0 = left). */
    private static final int BUTTON_LEFT = PointerInput.MouseButton.LEFT.asArg();

    private Gestures() {
    }

    /** Taps a specific point on the screen. */
    public static void tap(AndroidDriver driver, int x, int y) {
        PointerInput finger = new PointerInput(TOUCH, "finger");
        Sequence tap = new Sequence(finger, 0);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y));
        tap.addAction(finger.createPointerDown(BUTTON_LEFT));
        tap.addAction(finger.createPointerUp(BUTTON_LEFT));
        driver.perform(List.of(tap));
    }

    /** Taps the center of the given element. */
    public static void tap(AndroidDriver driver, WebElement element) {
        Point center = centerOf(element);
        tap(driver, center.x, center.y);
    }

    /** Free-form swipe between two absolute coordinates. */
    public static void swipe(AndroidDriver driver, int startX, int startY,
                             int endX, int endY, int durationMillis) {
        PointerInput finger = new PointerInput(TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(BUTTON_LEFT));
        swipe.addAction(new Pause(finger, Duration.ofMillis(150)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(durationMillis),
                PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(BUTTON_LEFT));
        driver.perform(List.of(swipe));
    }

    /** Scrolls the current screen down (content moves up) by a fraction of the height. */
    public static void scrollDown(AndroidDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int centerX = size.width / 2;
        swipe(driver, centerX, (int) (size.height * 0.75), centerX, (int) (size.height * 0.25), 600);
    }

    /** Scrolls the current screen up (content moves down) by a fraction of the height. */
    public static void scrollUp(AndroidDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int centerX = size.width / 2;
        swipe(driver, centerX, (int) (size.height * 0.25), centerX, (int) (size.height * 0.75), 600);
    }

    /**
     * Slides horizontally inside an element (e.g. a SeekBar) from one relative
     * position to another.
     *
     * @param startRatio 0.0 = left edge, 1.0 = right edge
     * @param endRatio   0.0 = left edge, 1.0 = right edge
     */
    public static void slide(AndroidDriver driver, WebElement element, double startRatio, double endRatio) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        int y = location.y + size.height / 2;
        int startX = location.x + (int) (size.width * startRatio);
        int endX = location.x + (int) (size.width * endRatio);
        swipe(driver, startX, y, endX, y, 800);
    }

    /**
     * Long-presses the source element, drags it onto the target and releases
     * (drag &amp; drop). The initial hold is required so the UI recognises the
     * long-press drag start.
     */
    public static void dragAndDrop(AndroidDriver driver, WebElement source, WebElement target, int holdMillis) {
        Point start = centerOf(source);
        Point end = centerOf(target);
        PointerInput finger = new PointerInput(TOUCH, "finger");
        Sequence drag = new Sequence(finger, 0);
        drag.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y));
        drag.addAction(finger.createPointerDown(BUTTON_LEFT));
        drag.addAction(new Pause(finger, Duration.ofMillis(holdMillis)));
        drag.addAction(finger.createPointerMove(Duration.ofMillis(900),
                PointerInput.Origin.viewport(), end.x, end.y));
        drag.addAction(new Pause(finger, Duration.ofMillis(200)));
        drag.addAction(finger.createPointerUp(BUTTON_LEFT));
        driver.perform(List.of(drag));
    }

    /** Center point of an element, computed from its bounding rectangle. */
    private static Point centerOf(WebElement element) {
        Rectangle rect = element.getRect();
        return new Point(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
    }
}
