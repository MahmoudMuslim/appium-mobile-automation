package com.mahmoud.appium.phase2;

import com.mahmoud.appium.base.BaseTest;
import com.mahmoud.appium.config.CapabilitiesFactory;
import com.mahmoud.appium.pages.ApiDemosPage;
import com.mahmoud.appium.utils.ScreenshotUtil;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Phase 2 - Test 2: automated test against a real APK file.
 *
 * <p>Scenario: install and launch the ApiDemos APK, navigate two menu levels
 * and interact with a checkbox element (more than two element interactions).</p>
 */
public class ApkInstallTest extends BaseTest {

    /**
     * Desired Capabilities for the APK: the {@code app} capability points to the
     * local APK so Appium installs it, plus appPackage and appActivity.
     */
    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.apk();
    }

    @Test(description = "Installs ApiDemos, navigates Views > Controls and toggles a checkbox")
    public void installApkNavigateAndToggleCheckbox() {
        ApiDemosPage apiDemos = new ApiDemosPage(driver);

        // Interaction 1: open the "Views" menu.
        apiDemos.openMenu("Views");
        Assert.assertTrue(apiDemos.isMenuItemDisplayed("Controls"),
                "The Views screen did not open");

        // Interaction 2: open the "Controls" theme list (scrolling if required).
        apiDemos.scrollToAndOpenMenu("Controls");

        // Interaction 3: open a concrete theme; this screen holds the real widgets.
        apiDemos.openMenu("1. Light Theme");
        Assert.assertTrue(apiDemos.isWidgetLabelDisplayed("Checkbox 1"),
                "The Controls widget screen did not load");

        // Interaction 4: toggle a checkbox and verify it became checked.
        WebElement checkbox = driver.findElement(apiDemos.checkbox1());
        checkbox.click();
        Assert.assertEquals(checkbox.getAttribute("checked"), "true",
                "Checkbox 1 was not checked");

        ScreenshotUtil.capture(driver, "phase2-apk-checkbox");
    }
}
