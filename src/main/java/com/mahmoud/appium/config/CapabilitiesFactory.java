package com.mahmoud.appium.config;

import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.remote.CapabilityType;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Central place that builds the Appium {@link UiAutomator2Options} (Desired
 * Capabilities) for every test scenario. Keeping this logic in one class avoids
 * duplicated capability setup across the test classes.
 */
public final class CapabilitiesFactory {

    private CapabilitiesFactory() {
    }

    /**
     * Capabilities for a pre-installed built-in app (Android Calculator).
     * We only tell Appium which package/Activity to open; the app is already on
     * the device, so no APK is installed.
     */
    public static UiAutomator2Options builtInApp() {
        return common()
                // appPackage + appActivity launch an already-installed app.
                .setAppPackage(ConfigReader.get("calculator.appPackage"))
                .setAppActivity(ConfigReader.get("calculator.appActivity"))
                // noReset keeps app data between runs (faster, no re-install).
                .setNoReset(true);
    }

    /**
     * Capabilities for installing and launching a real APK file.
     * The {@code app} capability points to the local APK; Appium installs it
     * before the session starts and removes it afterwards.
     */
    public static UiAutomator2Options apk() {
        Path apkPath = Paths.get(ConfigReader.get("apidemos.apkPath")).toAbsolutePath();
        return common()
                // app = absolute path to the APK to install and launch.
                .setApp(apkPath.toString())
                .setAppPackage(ConfigReader.get("apidemos.appPackage"))
                .setAppActivity(ConfigReader.get("apidemos.appActivity"))
                // Full reset so the APK is re-installed on every run.
                .setNoReset(false)
                .setFullReset(false);
    }

    /**
     * Capabilities that target a physical device instead of the emulator.
     * It reuses the built-in app config but pins the device via {@code udid}.
     */
    public static UiAutomator2Options realDevice() {
        UiAutomator2Options options = builtInApp();
        String udid = ConfigReader.get("udid");
        if (!udid.isEmpty()) {
            options.setUdid(udid);
        }
        return options;
    }

    /** Capabilities for mobile web testing in the Chrome browser. */
    public static UiAutomator2Options chrome() {
        UiAutomator2Options options = common();
        // browserName=Chrome tells UiAutomator2 to drive the Chrome browser,
        // not an app. UiAutomator2Options exposes it via the capability map.
        options.setCapability(CapabilityType.BROWSER_NAME, "Chrome");
        // Chromedriver arguments that make web sessions more stable.
        options.setChromedriverArgs(java.util.List.of("--disable-popup-blocking"));
        String udid = ConfigReader.get("udid");
        if (!udid.isEmpty()) {
            options.setUdid(udid);
        }
        return options;
    }

    /**
     * Capabilities shared by every scenario: platform, device name and
     * automation engine.
     */
    private static UiAutomator2Options common() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName(ConfigReader.getOrDefault("platformName", "Android"));
        options.setDeviceName(ConfigReader.get("deviceName"));
        options.setAutomationName(ConfigReader.getOrDefault("automationName", "UiAutomator2"));

        String platformVersion = ConfigReader.get("platformVersion");
        if (!platformVersion.isEmpty()) {
            options.setPlatformVersion(platformVersion);
        }

        String udid = ConfigReader.get("udid");
        if (!udid.isEmpty()) {
            options.setUdid(udid);
        }
        return options;
    }
}
