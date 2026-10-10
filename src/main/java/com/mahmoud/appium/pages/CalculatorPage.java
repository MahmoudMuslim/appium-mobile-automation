package com.mahmoud.appium.pages;

import com.mahmoud.appium.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

/**
 * Page object for a built-in Android Calculator app.
 *
 * <p>Locator strategy: every control lives in the calculator package, so we use
 * resource-id locators. The exact package and ids differ between vendors, so they
 * are read from config.properties (and can be overridden per device with -D):
 * <ul>
 *   <li>Google Calculator (emulator): {@code com.google.android.calculator}, id {@code eq}, result {@code result}</li>
 *   <li>MIUI Calculator (Xiaomi): {@code com.miui.calculator}, id {@code btn_equal_s}, result {@code expression}</li>
 * </ul></p>
 */
public class CalculatorPage extends BasePage {

    public CalculatorPage(AndroidDriver driver) {
        super(driver);
    }

    private static String pkg() {
        return ConfigReader.get("calculator.appPackage");
    }

    /** Locates a digit button, e.g. {@code pressDigit(7)} -> digit_7. */
    private static By digit(int value) {
        String pattern = ConfigReader.getOrDefault("calculator.digitIdFormat", "digit_%d");
        return By.id(pkg() + ":id/" + String.format(pattern, value));
    }

    private static By byId(String idKey, String defaultId) {
        return By.id(pkg() + ":id/" + ConfigReader.getOrDefault(idKey, defaultId));
    }

    public CalculatorPage pressDigit(int value) {
        click(digit(value));
        return this;
    }

    public CalculatorPage pressAdd() {
        click(byId("calculator.addId", "op_add"));
        return this;
    }

    public CalculatorPage pressEquals() {
        click(byId("calculator.equalsId", "eq"));
        return this;
    }

    /** Reads the calculation result from the display field. */
    public String result() {
        String raw;
        try {
            raw = waitVisible(byId("calculator.resultId", "result")).getText();
        } catch (NoSuchElementException e) {
            raw = waitVisible(byId("calculator.resultId", "result_preview")).getText();
        }
        // Some vendor skins prefix the result with '=' (e.g. MIUI shows "= 10");
        // normalise so the assertion stays the same across devices.
        return raw.replaceFirst("^\\s*=?\\s*", "").trim();
    }

    /** Adds two single-digit numbers and returns the displayed result. */
    public String add(int first, int second) {
        pressDigit(first);
        pressAdd();
        pressDigit(second);
        pressEquals();
        return result();
    }

    public boolean isLoaded() {
        return isDisplayed(digit(0));
    }
}
