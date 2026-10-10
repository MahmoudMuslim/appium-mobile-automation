package com.mahmoud.appium.base;

import com.mahmoud.appium.config.ConfigReader;
import com.mahmoud.appium.utils.ScreenshotUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.URL;
import java.time.Duration;

/**
 * Base class for every test. It owns the driver lifecycle so that individual
 * tests only declare their Desired Capabilities and assertions.
 */
public abstract class BaseTest {

    protected AndroidDriver driver;

    /** Each test class supplies the Appium options for its scenario. */
    protected abstract UiAutomator2Options buildOptions();

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        String serverUrl = ConfigReader.getOrDefault("appium.server.url", "http://127.0.0.1:4723");
        driver = new AndroidDriver(new URL(serverUrl), buildOptions());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver == null) {
            return;
        }
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtil.capture(driver, "FAILED-" + result.getName());
        }
        driver.quit();
    }
}
