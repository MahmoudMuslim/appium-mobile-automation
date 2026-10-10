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
 * Phase 3 - Gesture 4: Drag &amp; Drop.
 *
 * <p>Opens the ApiDemos "Drag and Drop" screen, long-presses the source dot and
 * drags it onto the target dot, verifying the on-screen status text changed.</p>
 */
public class DragAndDropGestureTest extends BaseTest {

    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.apk();
    }

    @Test(description = "Drag & Drop gesture: moves the dot onto the target and verifies status text")
    public void dragDotOntoTarget() {
        ApiDemosPage apiDemos = new ApiDemosPage(driver);
        apiDemos.openMenu("Views");
        apiDemos.scrollToAndOpenMenu("Drag and Drop");

        WebElement source = driver.findElement(apiDemos.dragSource());
        WebElement target = driver.findElement(apiDemos.dragTarget());
        String before = driver.findElement(apiDemos.dragStatusText()).getText();

        // Long-press (1000 ms) then drag the dot onto the target.
        Gestures.dragAndDrop(driver, source, target, 1000);

        String after = driver.findElement(apiDemos.dragStatusText()).getText();
        ScreenshotUtil.capture(driver, "phase3-gesture-drag-drop");
        Assert.assertNotEquals(after, before, "Drag & Drop did not update the status text");
    }
}
