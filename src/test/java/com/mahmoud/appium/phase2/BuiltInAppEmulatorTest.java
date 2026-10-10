package com.mahmoud.appium.phase2;

import com.mahmoud.appium.base.BaseTest;
import com.mahmoud.appium.config.CapabilitiesFactory;
import com.mahmoud.appium.pages.ClockPage;
import com.mahmoud.appium.utils.ScreenshotUtil;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Phase 2 - built-in app scenario for the AVD emulator.
 *
 * <p>Scenario: launch the pre-installed Google Clock app, open the Timer tab and
 * verify the app reacted to the interaction. This is the emulator counterpart of
 * {@link BuiltInAppTest} (Calculator), which needs Google Calculator - not present
 * on Google Play AVD images.</p>
 */
public class BuiltInAppEmulatorTest extends BaseTest {

    /**
     * Desired Capabilities for the built-in Clock app:
     * platformName=Android, deviceName (emulator), appPackage + appActivity.
     */
    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.builtInClockApp();
    }

    @Test(description = "Opens the built-in Google Clock app and switches to the Timer tab")
    public void openTimerTabInBuiltInClock() {
        ClockPage clock = new ClockPage(driver);

        Assert.assertTrue(clock.isLoaded(), "The Google Clock app did not open");

        // Interact with an element: open the Timer tab (located by accessibility id).
        clock.openTab("Timer");

        ScreenshotUtil.capture(driver, "phase2-builtin-clock");

        // Verify the interaction worked - accessibility-id attribute check.
        Assert.assertTrue(clock.isTabSelected("Timer"), "The Timer tab was not selected");
        // Same check expressed as an XPath locator.
        Assert.assertTrue(clock.isTabSelectedByXpath("Timer"),
                "XPath did not report the Timer tab as selected");
        Assert.assertEquals(clock.toolbarTitle(), "Timer", "The Timer screen did not open");
    }
}