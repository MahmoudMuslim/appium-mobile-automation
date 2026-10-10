package com.mahmoud.appium.phase4;

import com.mahmoud.appium.base.BaseTest;
import com.mahmoud.appium.config.CapabilitiesFactory;
import com.mahmoud.appium.config.ConfigReader;
import com.mahmoud.appium.pages.ChromeLoginPage;
import com.mahmoud.appium.utils.ScreenshotUtil;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Phase 4 - Mobile web test: drives the Chrome browser on Android with Appium.
 *
 * <p>Scenario: open SauceDemo, log in with the demo account and verify the
 * products page loaded.</p>
 */
public class ChromeWebTest extends BaseTest {

    /** Desired Capabilities for mobile web: browserName=Chrome (no appPackage). */
    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.chrome();
    }

    @Test(description = "Opens SauceDemo in Chrome on Android, logs in and verifies the products page")
    public void loginToSauceDemoInChrome() {
        ChromeLoginPage page = new ChromeLoginPage(driver);

        page.open(ConfigReader.get("chrome.url"));
        Assert.assertTrue(page.isLoginButtonDisplayed(), "SauceDemo login page did not load");

        page.login(ConfigReader.get("chrome.username"), ConfigReader.get("chrome.password"));

        ScreenshotUtil.capture(driver, "phase4-chrome-web");
        Assert.assertEquals(page.title(), "Products", "Login did not reach the products page");
    }
}
