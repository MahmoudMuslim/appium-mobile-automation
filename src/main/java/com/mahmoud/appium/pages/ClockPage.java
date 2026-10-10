package com.mahmoud.appium.pages;

import com.mahmoud.appium.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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

    /**
     * True once the app has finished launching.
     * <p>Uses {@code desk_clock_pager} rather than a tab-specific id: DeskClock
     * restores whichever tab was open last, so the ids differ per tab
     * ({@code digital_clock} on Clock, {@code timer_setup} on Timer). The pager
     * container is present on every tab.</p>
     */
    public boolean isLoaded() {
        try {
            waitVisible(tab("desk_clock_pager"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
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

    /**
     * XPath variant of {@link #isTabSelected(String)}. Uses XPath (rather than the
     * accessibility-id locator) to assert the tab node carries {@code selected="true"}.
     */
    public boolean isTabSelectedByXpath(String label) {
        return isDisplayed(By.xpath("//*[@content-desc=\"" + label + "\" and @selected=\"true\"]"));
    }

    /** Reads the text of the toolbar title, e.g. "Timer", via XPath. */
    public String toolbarTitle() {
        return textOf(By.xpath("//*[@resource-id=\"" + pkg() + ":id/action_bar_title\"]"));
    }
}