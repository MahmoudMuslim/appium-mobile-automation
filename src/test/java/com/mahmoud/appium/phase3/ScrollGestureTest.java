package com.mahmoud.appium.phase3;

import com.mahmoud.appium.base.BaseTest;
import com.mahmoud.appium.config.CapabilitiesFactory;
import com.mahmoud.appium.pages.ApiDemosPage;
import com.mahmoud.appium.utils.Gestures;
import com.mahmoud.appium.utils.ScreenshotUtil;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Phase 3 - Gesture 2: Scroll.
 *
 * <p>Opens the long "Views" list and scrolls down with repeated swipe gestures
 * until the "WebView" row (below the fold) becomes visible.</p>
 */
public class ScrollGestureTest extends BaseTest {

    private static final int MAX_SCROLLS = 6;

    @Override
    protected UiAutomator2Options buildOptions() {
        return CapabilitiesFactory.apk();
    }

    @Test(description = "Scroll gesture: scrolls the Views list until WebView is visible")
    public void scrollViewsListUntilWebView() {
        ApiDemosPage apiDemos = new ApiDemosPage(driver);
        apiDemos.openMenu("Views");

        int scrolls = 0;
        while (!apiDemos.isMenuItemDisplayed("WebView") && scrolls < MAX_SCROLLS) {
            Gestures.scrollDown(driver);
            scrolls++;
        }

        ScreenshotUtil.capture(driver, "phase3-gesture-scroll");
        Assert.assertTrue(apiDemos.isMenuItemDisplayed("WebView"),
                "WebView was not reached after " + scrolls + " scrolls");
    }
}
