package com.mahmoud.appium.phase3;

import com.mahmoud.appium.base.BaseTest;
import com.mahmoud.appium.config.CapabilitiesFactory;
import com.mahmoud.appium.pages.CalculatorPage;
import com.mahmoud.appium.utils.ScreenshotUtil;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Phase 3 - Real device run: the same Calculator scenario executed on a
 * physical Android device connected over USB (USB debugging enabled).
 *
 * <p>Point Appium at the real device by setting {@code udid} in
 * config.properties or by running: {@code mvn test -Dudid=YOUR_DEVICE_ID}.</p>
 */
public class RealDeviceTest extends BaseTest {

    /** Reuses the built-in app capabilities but targets the physical device. */
    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.realDevice();
    }

    @Test(description = "Runs the Calculator test on a real Android device")
    public void addTwoNumbersOnRealDevice() {
        CalculatorPage calculator = new CalculatorPage(driver);

        Assert.assertTrue(calculator.isLoaded(), "The Calculator app did not open on the device");

        String result = calculator.add(6, 6);

        ScreenshotUtil.capture(driver, "phase3-real-device-calculator");
        Assert.assertEquals(result, "12", "Unexpected calculation result on the real device");
    }
}
