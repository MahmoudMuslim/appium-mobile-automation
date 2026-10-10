package com.mahmoud.appium.pages;

import com.mahmoud.appium.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object for the pre-installed Google Clock app (Android DeskClock).
 *
 * <p>This app is present on every AVD image, including Google Play images that
 * ship without Google Calculator, so it is used as the built-in-app scenario on
 * the emulator. Locator strategy: resource-id first (stable across versions) and
 * accessibility-id ({@code content-desc}) for the tab buttons.</p>
 */
public class ClockPage extends BasePage {

    public ClockPage(AndroidDriver driver) {
        super(driver);
    }

    private static String pkg() {
        return ConfigReader.get("clock.appPackage");
    }

    private static By tab(String id) {
        return By.id(pkg() + ":id/" + id);
    }

    /** The tab buttons are labelled via accessibility-id (content-desc). */
    private static By tabByLabel(String label) {
        return AppiumBy.accessibilityId(label);
    }

    public boolean isLoaded() {
        return isDisplayed(tab("digital_clock"));
    }

    /** Taps a bottom-navigation tab by its accessibility label. */
    public ClockPage openTab(String label) {
        click(tabByLabel(label));
        return this;
    }

    /** Verifies the given tab is the active/selected one. */
    public boolean isTabSelected(String label) {
        WebElement tab = waitVisible(tabByLabel(label));
        return tab.getAttribute("selected").equals("true");
    }

    public String currentTime() {
        return textOf(tab("digital_clock"));
    }
}