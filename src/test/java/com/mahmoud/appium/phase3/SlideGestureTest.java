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
 * Phase 3 - Gesture 3: Slide.
 *
 * <p>Opens the ApiDemos "Seek Bar" screen and slides the thumb from the left
 * toward the right, verifying the progress label changes.</p>
 */
public class SlideGestureTest extends BaseTest {

    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.apk();
    }

    @Test(description = "Slide gesture: drags the Seek Bar thumb and verifies progress changed")
    public void slideSeekBar() {
        ApiDemosPage apiDemos = new ApiDemosPage(driver);
        apiDemos.openMenu("Views");
        apiDemos.scrollToAndOpenMenu("Seek Bar");

        WebElement seekBar = driver.findElement(apiDemos.seekBar());
        String before = driver.findElement(apiDemos.seekBarProgress()).getText();

        // Slide from near the left edge to near the right edge.
        Gestures.slide(driver, seekBar, 0.1, 0.9);

        String after = driver.findElement(apiDemos.seekBarProgress()).getText();
        ScreenshotUtil.capture(driver, "phase3-gesture-slide");
        Assert.assertNotEquals(after, before, "Seek Bar progress did not change after sliding");
    }
}
