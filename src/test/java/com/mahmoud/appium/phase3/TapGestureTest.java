package com.mahmoud.appium.phase3;

import com.mahmoud.appium.base.BaseTest;
import com.mahmoud.appium.config.CapabilitiesFactory;
import com.mahmoud.appium.pages.ApiDemosPage;
import com.mahmoud.appium.utils.Gestures;
import com.mahmoud.appium.utils.ScreenshotUtil;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Phase 3 - Gesture 1: Tap.
 *
 * <p>Taps the center of a visible element ("Views" row in ApiDemos) using a
 * raw W3C pointer tap and verifies navigation happened.</p>
 */
public class TapGestureTest extends BaseTest {

    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.apk();
    }

    @Test(description = "Tap gesture: taps the Views row and verifies navigation")
    public void tapOnViewsMenuItem() {
        ApiDemosPage apiDemos = new ApiDemosPage(driver);

        WebElement views = driver.findElement(
                io.appium.java_client.AppiumBy.androidUIAutomator("new UiSelector().text(\"Views\")"));

        // Perform the tap gesture on the element's center.
        Gestures.tap(driver, views);

        Assert.assertTrue(apiDemos.isMenuItemDisplayed("Controls"),
                "Tap did not open the Views screen");
        ScreenshotUtil.capture(driver, "phase3-gesture-tap");
    }
}
