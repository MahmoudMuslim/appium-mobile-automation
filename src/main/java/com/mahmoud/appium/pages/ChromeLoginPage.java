package com.mahmoud.appium.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

/**
 * Page object for the SauceDemo login page opened in Chrome on Android.
 *
 * <p>Once Appium switches a session to a browser, the page is a normal DOM, so
 * we use standard web locators: element ids and CSS selectors.</p>
 */
public class ChromeLoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By PRODUCTS_TITLE = By.cssSelector(".title");

    public ChromeLoginPage(AndroidDriver driver) {
        super(driver);
    }

    /** Navigates Chrome to the given URL. */
    public ChromeLoginPage open(String url) {
        driver.get(url);
        return this;
    }

    public ChromeLoginPage enterUsername(String username) {
        type(USERNAME, username);
        return this;
    }

    public ChromeLoginPage enterPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public ChromeLoginPage submit() {
        click(LOGIN_BUTTON);
        return this;
    }

    /** Full login flow used by the test. */
    public ChromeLoginPage login(String username, String password) {
        return enterUsername(username).enterPassword(password).submit();
    }

    public String title() {
        return textOf(PRODUCTS_TITLE);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(LOGIN_BUTTON);
    }
}
