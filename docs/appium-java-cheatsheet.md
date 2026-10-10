# Appium Java Cheat Sheet

Quick reference for the Appium **Java Client 9.x** (Selenium 4 / W3C) — the API
used across the course.

> Everything below is valid for `io.appium:java-client:9.3.0` on Android
> (`UiAutomator2`). Selenium 4 uses the **W3C Actions API**; the old `TouchAction`
> class no longer exists.

---

## 1. Maven dependency

```xml
<dependency>
    <groupId>io.appium</groupId>
    <artifactId>java-client</artifactId>
    <version>9.3.0</version>
</dependency>
```

---

## 2. Desired Capabilities (Android)

Appium 9 replaces the old `DesiredCapabilities` map with typed option classes.

```java
import io.appium.java_client.android.options.UiAutomator2Options;

UiAutomator2Options options = new UiAutomator2Options()
        .setPlatformName("Android")                     // OS under test
        .setDeviceName("emulator-5554")                 // from `adb devices`
        .setAutomationName("UiAutomator2")              // Android automation engine
        .setAppPackage("com.example.app")               // launch an installed app...
        .setAppActivity("com.example.app.MainActivity") // ...by package + activity
        .setNoReset(true);                              // keep app data

// Install + launch a local APK instead of an installed app:
options.setApp("C:/apks/my-app.apk")
       .setNoReset(false)
       .setFullReset(false);

// Mobile web (Chrome):
options.setCapability("browserName", "Chrome");
```

Common capabilities:

| Capability | Java setter | Purpose |
|------------|-------------|---------|
| `platformName` | `setPlatformName` | `Android` / `iOS` |
| `deviceName` | `setDeviceName` | device label from `adb devices` |
| `udid` | `setUdid` | pin an exact device |
| `automationName` | `setAutomationName` | `UiAutomator2` |
| `appPackage` | `setAppPackage` | installed app to open |
| `appActivity` | `setAppActivity` | start activity |
| `app` | `setApp` | local APK/IPA to install |
| `noReset` | `setNoReset` | do not clear app data |
| `fullReset` | `setFullReset` | uninstall app after session |
| `browserName` | `setCapability` | mobile web (`Chrome`) |
| `autoGrantPermissions` | `setAutoGrantPermissions` | auto-accept runtime permissions |

---

## 3. Driver setup & teardown

```java
import io.appium.java_client.android.AndroidDriver;
import java.net.URL;
import java.time.Duration;

AndroidDriver driver = new AndroidDriver(
        new URL("http://127.0.0.1:4723"), options);
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

// ... run test ...

driver.quit();          // close session
driver.close();         // close current window
```

---

## 4. Element locators

```java
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

driver.findElement(By.id("com.example.app:id/button"));           // resource-id
driver.findElement(AppiumBy.accessibilityId("Login"));            // content-desc
driver.findElement(By.xpath("//android.widget.TextView[@text='OK']"));
driver.findElement(By.className("android.widget.Button"));
driver.findElement(AppiumBy.androidUIAutomator(
        "new UiSelector().text(\"Views\")"));                     // UiAutomator
driver.findElement(AppiumBy.androidViewMatcher(...));             // Espresso matcher
driver.findElement(By.cssSelector("#login-button"));              // web context only
```

Scrolling to an element with UiAutomator:

```java
driver.findElement(AppiumBy.androidUIAutomator(
    "new UiScrollable(new UiSelector().scrollable(true))"
  + ".scrollIntoView(new UiSelector().text(\"WebView\"))"));
```

| Strategy | When to use |
|----------|-------------|
| `id` | native apps with stable resource-ids (preferred) |
| `accessibilityId` | content-description / label |
| `xpath` | no id available (slow, use last) |
| `-android uiautomator` | complex/scrollable searches |
| `css` | web/Hybrid/Chrome contexts |

---

## 5. Common driver commands

```java
driver.getPageSource();                                   // XML dump / HTML
driver.getCurrentPackage();                               // Android app package
driver.getCurrentActivity();                              // Android activity
driver.activateApp("com.example.app");                    // foreground an app
driver.terminateApp("com.example.app");                   // kill an app
driver.installApp("C:/apks/my-app.apk");
driver.removeApp("com.example.app");
driver.isAppInstalled("com.example.app");
driver.lockDevice(); driver.unlockDevice();
driver.getOrientation();                                  // PORTRAIT / LANDSCAPE
driver.rotate(ScreenOrientation.LANDSCAPE);
driver.getClipboardText();
driver.runAppInBackground(Duration.ofSeconds(3));
driver.getContextHandles();                               // NATIVE_APP, WEBVIEW_*
driver.context("WEBVIEW_com.example.app");                // switch to webview
driver.context("NATIVE_APP");
driver.getCurrentUrl();                                   // web context
driver.get("https://example.com");                        // open URL in browser
```

---

## 6. Element interactions

```java
WebElement el = driver.findElement(By.id("..."));
el.click();
el.sendKeys("text");
el.clear();
el.getText();
el.getAttribute("checked");       // "true" / "false"
el.isDisplayed(); el.isEnabled(); el.isSelected();
el.getLocation(); el.getSize(); el.getRect();
el.getDomAttribute("content-desc");               // Selenium 4
```

---

## 7. Waits

```java
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("...")));
wait.until(ExpectedConditions.elementToBeClickable(By.id("...")));
wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("...")));
```

---

## 8. Mobile gestures (W3C Actions API)

The legacy `TouchAction`/`MultiTouchAction` were removed. Build a
`PointerInput` + `Sequence` and call `driver.perform(...)`.

```java
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.*;
import java.time.Duration;
import java.util.List;

private static final int BUTTON_LEFT = PointerInput.MouseButton.LEFT.asArg();

// --- TAP ---
public static void tap(AndroidDriver driver, WebElement el) {
    Rectangle r = el.getRect();
    int x = r.getX() + r.getWidth() / 2;
    int y = r.getY() + r.getHeight() / 2;
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence tap = new Sequence(finger, 0)
        .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
        .addAction(finger.createPointerDown(BUTTON_LEFT))
        .addAction(finger.createPointerUp(BUTTON_LEFT));
    driver.perform(List.of(tap));
}

// --- SWIPE / SCROLL / SLIDE (drag between two points) ---
public static void swipe(AndroidDriver driver, int x1, int y1, int x2, int y2, int ms) {
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence swipe = new Sequence(finger, 0)
        .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x1, y1))
        .addAction(finger.createPointerDown(BUTTON_LEFT))
        .addAction(new Pause(finger, Duration.ofMillis(150)))
        .addAction(finger.createPointerMove(Duration.ofMillis(ms), PointerInput.Origin.viewport(), x2, y2))
        .addAction(finger.createPointerUp(BUTTON_LEFT));
    driver.perform(List.of(swipe));
}

// Scroll down = swipe from ~75% to ~25% of the screen height.
Dimension size = driver.manage().window().getSize();
swipe(driver, size.width / 2, (int)(size.height * 0.75),
              size.width / 2, (int)(size.height * 0.25), 600);

// --- DRAG & DROP (long-press then move then release) ---
public static void dragAndDrop(AndroidDriver driver, WebElement src, WebElement dst, int holdMs) {
    Point s = src.getRect().getPoint(), d = dst.getRect().getPoint();
    PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    Sequence drag = new Sequence(finger, 0)
        .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), s.x, s.y))
        .addAction(finger.createPointerDown(BUTTON_LEFT))
        .addAction(new Pause(finger, Duration.ofMillis(holdMs)))            // long press
        .addAction(finger.createPointerMove(Duration.ofMillis(900), PointerInput.Origin.viewport(), d.x, d.y))
        .addAction(finger.createPointerUp(BUTTON_LEFT));
    driver.perform(List.of(drag));
}
```

Gesture test pattern (see `src/main/java/.../utils/Gestures.java` for the full class).

---

## 9. Mobile "mobile:" script commands

```java
driver.executeScript("mobile: shell", Map.of(
        "command", "input", "args", List.of("keyevent", "KEYCODE_BACK")));

// UiAutomator2 shortcuts
driver.executeScript("mobile: clickGesture", Map.of("x", 100, "y", 200));
driver.executeScript("mobile: swipeGesture", Map.of(
        "left", 100, "top", 200, "width", 400, "height", 100,
        "direction", "left", "percent", 0.75));
driver.executeScript("mobile: dragGesture", Map.of(
        "startX", 100, "startY", 200, "endX", 400, "endY", 500));
driver.executeScript("mobile: scrollGesture", Map.of(
        "left", 0, "top", 0, "width", 500, "height", 800, "direction", "down"));
driver.executeScript("mobile: pressKey", Map.of("keycode", 4));      // BACK
```

---

## 10. Web testing configuration (Chrome on Android)

```java
UiAutomator2Options options = new UiAutomator2Options()
        .setPlatformName("Android")
        .setDeviceName("emulator-5554")
        .setAutomationName("UiAutomator2");
options.setCapability("browserName", "Chrome");
options.setChromedriverArgs(List.of("--disable-popup-blocking"));

AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
driver.get("https://www.saucedemo.com/");

driver.findElement(By.id("user-name")).sendKeys("standard_user");
driver.findElement(By.id("password")).sendKeys("secret_sauce");
driver.findElement(By.id("login-button")).click();
driver.findElement(By.cssSelector(".title")).getText();   // "Products"
```

Useful Chrome capabilities:

```java
options.setCapability("chromedriverExecutable", "C:/chromedriver/chromedriver.exe");
options.setCapability("chromedriverAutodownload", true);   // let Appium fetch it
options.setCapability("autoWebviewTimeout", 10000);
options.setCapability("goog:chromeOptions", Map.of(
        "args", List.of("--incognito", "--disable-notifications")));
```

---

## 11. Hybrid apps (native ⇄ webview)

```java
driver.getContextHandles().forEach(System.out::println);  // NATIVE_APP, WEBVIEW_...
driver.context("WEBVIEW_com.example.app");                // switch to web view
driver.findElement(By.cssSelector("#submit")).click();
driver.context("NATIVE_APP");                             // switch back
```

---

## 12. Screenshots & evidence

```java
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

File shot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
// copy `shot` to a report folder
```

---

## 13. Typed option imports (cheat list)

```java
import io.appium.java_client.android.options.UiAutomator2Options;   // Android
import io.appium.java_client.ios.options.XCUITestOptions;           // iOS
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.AppiumBy;
```
