package com.mahmoud.appium.phase2;

import com.mahmoud.appium.base.BaseTest;
import com.mahmoud.appium.config.CapabilitiesFactory;
import com.mahmoud.appium.pages.CalculatorPage;
import com.mahmoud.appium.utils.ScreenshotUtil;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Phase 2 - Test 1: automated test against a built-in Android app.
 *
 * <p>Scenario: launch the pre-installed Calculator app, add two numbers and
 * verify the result is shown on screen.</p>
 */
public class BuiltInAppTest extends BaseTest {

    /**
     * Desired Capabilities for the built-in Calculator app:
     * platformName=Android, deviceName (emulator), appPackage + appActivity.
     */
    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.builtInApp();
    }

    @Test(description = "Adds 7 + 3 in the built-in Calculator and verifies the result")
    public void addTwoNumbersInBuiltInCalculator() {
        CalculatorPage calculator = new CalculatorPage(driver);

        Assert.assertTrue(calculator.isLoaded(), "The Calculator app did not open");

        String result = calculator.add(7, 3);

        ScreenshotUtil.capture(driver, "phase2-builtin-calculator");
        Assert.assertEquals(result, "10", "Unexpected calculation result");
    }
}
