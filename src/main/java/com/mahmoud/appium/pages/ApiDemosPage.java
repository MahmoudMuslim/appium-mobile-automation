package com.mahmoud.appium.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

/**
 * Page object for the ApiDemos sample APK (package {@code io.appium.android.apis}).
 *
 * <p>Locator strategy: ApiDemos menu rows have no stable resource-id, so we use
 * the UiAutomator {@code text} selector. {@code UiScrollable} is used to reach
 * rows that are below the fold.</p>
 */
public class ApiDemosPage extends BasePage {

    private static final String PKG = "io.appium.android.apis";

    public ApiDemosPage(AndroidDriver driver) {
        super(driver);
    }

    /** Taps a visible menu row by its exact label. */
    public ApiDemosPage openMenu(String label) {
        click(menuItem(label));
        return this;
    }

    /** Scrolls the current list until the labelled row is visible, then taps it. */
    public ApiDemosPage scrollToAndOpenMenu(String label) {
        scrollToMenuItem(label);
        click(menuItem(label));
        return this;
    }

    /** Scrolls a scrollable list until the labelled row is on screen. */
    public void scrollToMenuItem(String label) {
        String uiAutomator = "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                + ".scrollIntoView(new UiSelector().text(\"" + label + "\").instance(0))";
        driver.findElement(AppiumBy.androidUIAutomator(uiAutomator));
    }

    private static By menuItem(String label) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + label + "\")");
    }

    public By seekBar() {
        return By.id(PKG + ":id/seek");
    }

    public By seekBarProgress() {
        return By.id(PKG + ":id/progress");
    }

    public By checkbox1() {
        return By.id(PKG + ":id/check1");
    }

    public By dragSource() {
        return By.id(PKG + ":id/drag_dot_1");
    }

    public By dragTarget() {
        return By.id(PKG + ":id/drag_dot_2");
    }

    public By dragStatusText() {
        return By.id(PKG + ":id/drag_result_text");
    }

    public boolean isMenuItemDisplayed(String label) {
        return isDisplayed(menuItem(label));
    }

    /**
     * XPath locator for a widget label on the "Controls" theme screen, e.g.
     * {@code //*[@text="Checkbox 1"]}. Used to confirm the real widget screen
     * finished loading before interacting with an element.
     */
    public By widgetLabel(String text) {
        return By.xpath("//*[@text=\"" + text + "\"]");
    }

    /** True when the given widget label is present on screen (XPath-based). */
    public boolean isWidgetLabelDisplayed(String text) {
        return isDisplayed(widgetLabel(text));
    }
}
