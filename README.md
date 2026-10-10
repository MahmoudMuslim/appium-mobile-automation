# Appium Mobile Automation — Mini Task

Automated mobile tests written in **Java** with **Appium 2**, the **Appium Java Client 9**,
**TestNG** and **Maven**. The project covers the full mini task:

| Phase | What it contains | Test class(es) |
|-------|------------------|----------------|
| 1 | Windows/Android environment setup | `README.md` (this file) |
| 2 | Built-in app test + real APK test | `BuiltInAppTest`, `ApkInstallTest` |
| 3 | Real device run + 4 gestures | `RealDeviceTest`, `TapGestureTest`, `ScrollGestureTest`, `SlideGestureTest`, `DragAndDropGestureTest` |
| 4 | Mobile web test in Chrome | `ChromeWebTest` |
| 5 | Appium Java cheat sheet | [`docs/appium-java-cheatsheet.md`](docs/appium-java-cheatsheet.md) |

---

## 1. Project structure

```
.
├── apps/                                  # put ApiDemos-debug.apk here
├── docs/
│   └── appium-java-cheatsheet.md          # Phase 5 deliverable
├── src
│   ├── main/java/com/mahmoud/appium
│   │   ├── config
│   │   │   ├── ConfigReader.java          # reads config.properties (+ -D overrides)
│   │   │   └── CapabilitiesFactory.java   # builds every Desired Capabilities set
│   │   ├── pages                          # Page Objects
│   │   │   ├── BasePage.java
│   │   │   ├── CalculatorPage.java
│   │   │   ├── ApiDemosPage.java
│   │   │   └── ChromeLoginPage.java
│   │   └── utils
│   │       ├── Gestures.java              # W3C tap / scroll / slide / drag & drop
│   │       └── ScreenshotUtil.java
│   └── test
│       ├── java/com/mahmoud/appium
│       │   ├── base/BaseTest.java         # shared driver setup/teardown
│       │   ├── phase2/...
│       │   ├── phase3/...
│       │   └── phase4/ChromeWebTest.java
│       └── resources/config.properties    # device + app configuration
├── pom.xml
└── testng.xml
```

---

## 2. Environment setup (Phase 1 — Windows + Android)

Install the following and add them to `PATH`.

1. **Java JDK 11+**
   - Install a JDK (e.g. Temurin 17).
   - Set `JAVA_HOME` to the JDK folder, e.g. `C:\Program Files\Eclipse Adoptium\jdk-17`.
   - Verify: `java -version`.

2. **Node.js (LTS)** — https://nodejs.org
   - Verify: `node -v` and `npm -v`.

3. **Appium 2 server**
   ```powershell
   npm install -g appium
   appium driver install uiautomator2
   appium -v
   ```

4. **Appium Inspector** (element locators) — https://github.com/appium/appium-inspector/releases
   - Download the Windows `.exe` and install.

5. **Android Studio + SDK**
   - Install Android Studio, then in **SDK Manager** install `Android SDK Platform`, `Android SDK Platform-Tools`, `Android SDK Build-Tools` and `Android SDK Command-line Tools`.
   - Create an **AVD** in **Device Manager** (e.g. Pixel 6, API 33).
   - Set the environment variables:
     ```
     ANDROID_HOME = %LOCALAPPDATA%\Android\Sdk
     ANDROID_SDK_ROOT = %LOCALAPPDATA%\Android\Sdk
     JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-17
     PATH = ...;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator;%JAVA_HOME%\bin
     ```
   - Verify:
     ```powershell
     adb devices
     emulator -list-avds
     ```

6. **appium-doctor** — confirm the setup is clean
   ```powershell
   npm install -g appium-doctor
   appium-doctor --android
   ```
   All Android checks should be green. **Take a screenshot of this output** (deliverable).

7. **Maven** — https://maven.apache.org/download.cgi (or use the one bundled with your IDE).
   - Verify: `mvn -v`.

### Getting the APK under test
Download the ApiDemos sample APK and place it at `apps/ApiDemos-debug.apk`:
https://github.com/appium/android-apidemos/releases

### Getting an Appium Java Client starter
Not needed — the dependency is declared in `pom.xml`
(`io.appium:java-client:9.3.0`).

---

## 3. Start the Appium server

```powershell
appium
```
It listens on `http://127.0.0.1:4723` (the URL in `config.properties`).

For the **Chrome web test**, Appium must download a chromedriver that matches the
Chrome version on the device. Start the server with the (insecure) auto-download
feature enabled — Appium 3 requires the driver-qualified name:

```powershell
appium --allow-insecure "*:chromedriver_autodownload"
```

Start the emulator (or connect a phone):
```powershell
emulator -avd <AVD_NAME>
adb devices
```

---

## 4. Configuration

Everything lives in `src/test/resources/config.properties`. Any value can be
overridden from the command line with `-Dkey=value`, for example:

```powershell
mvn test -DdeviceName=emulator-5554
mvn test -DplatformVersion=14.0
mvn test -Dudid=RF8M123ABCD        # target a specific real device
```

---

## 5. Running the tests

Run everything listed in `testng.xml`:
```powershell
mvn test
```

Run a single phase/scenario with TestNG + Surefire:
```powershell
# Phase 2 - built-in app
mvn test -Dtest=BuiltInAppTest
# Phase 2 - APK
mvn test -Dtest=ApkInstallTest
# Phase 3 - gestures
mvn test -Dtest=TapGestureTest
mvn test -Dtest=ScrollGestureTest
mvn test -Dtest=SlideGestureTest
mvn test -Dtest=DragAndDropGestureTest
# Phase 3 - real device (also set -Dudid=...)
mvn test -Dtest=RealDeviceTest -Dudid=YOUR_DEVICE_ID
# Phase 4 - Chrome web test
mvn test -Dtest=ChromeWebTest
```

> When running a single `-Dtest=...` class, Surefire ignores `testng.xml`, so the
> other classes are not executed.

### Running on a real device (example: Xiaomi / MIUI)

The default capabilities target the Google Calculator on an emulator. On a
physical Xiaomi device the built-in calculator is MIUI, so override the package,
activity and the "equals" button id on the command line:

```powershell
mvn test "-Dtest=BuiltInAppTest,RealDeviceTest" `
  -Dudid=YOUR_DEVICE_ID -DdeviceName=YOUR_DEVICE_ID `
  -Dcalculator.appPackage=com.miui.calculator `
  -Dcalculator.appActivity=com.miui.calculator.cal.CalculatorActivity `
  -Dcalculator.equalsId=btn_equal_s
```

The APK, gesture and Chrome tests need no app-specific overrides — just pass the
`udid`:

```powershell
mvn test "-Dtest=ApkInstallTest,TapGestureTest,ScrollGestureTest,SlideGestureTest,DragAndDropGestureTest,ChromeWebTest" `
  -Dudid=YOUR_DEVICE_ID -DdeviceName=YOUR_DEVICE_ID
```

### Where results go
- TestNG reports: `target/surefire-reports/`
- Screenshots taken by the tests: `target/screenshots/`

---

## 6. Test scenarios

### Phase 2
- **`BuiltInAppTest#addTwoNumbersInBuiltInCalculator`** — launches the built-in
  Android Calculator, computes `7 + 3` and asserts the result is `10`.
- **`ApkInstallTest#installApkNavigateAndToggleCheckbox`** — installs
  `ApiDemos-debug.apk`, navigates `Views → Controls` and toggles `Checkbox 1`
  (three element interactions).

### Phase 3
- **`RealDeviceTest`** — the Calculator scenario on a physical device.
- **`TapGestureTest`** — W3C tap on the `Views` row.
- **`ScrollGestureTest`** — repeated W3C swipes until `WebView` is visible.
- **`SlideGestureTest`** — slides the Seek Bar thumb and checks the progress label changed.
- **`DragAndDropGestureTest`** — long-press + drag the dot onto the target dot.

### Phase 4
- **`ChromeWebTest`** — opens SauceDemo in Chrome on Android, logs in with the
  demo account and verifies the products page.

---

## 7. Locator strategy

Locators were identified with **Appium Inspector**:

| App | Locator type | Example |
|-----|--------------|---------|
| Calculator | resource-id | `com.google.android.calculator:id/eq` |
| ApiDemos menus | UiAutomator text | `new UiSelector().text("Views")` |
| ApiDemos widgets | resource-id | `io.appium.android.apis:id/seekbar` |
| Chrome web | id / CSS | `#login-button`, `.title` |

If a device image ships different resource-ids, update the constants in the
relevant `*Page` class after inspecting the screen.

---

## 8. Deliverables checklist

- [ ] Public GitHub repository with all test scripts + this README.
- [ ] Screenshot of `appium-doctor --android` output.
- [ ] Screenshot of `phase2-builtin-calculator` (built-in app on emulator).
- [ ] Screenshot of `phase2-apk-checkbox` (APK on emulator).
- [ ] Screenshot of `phase3-real-device-calculator` (real device).
- [ ] Screenshots `phase3-gesture-tap`, `phase3-gesture-scroll`, `phase3-gesture-slide`, `phase3-gesture-drag-drop`.
- [ ] Screenshot `phase4-chrome-web`.
- [ ] `docs/appium-java-cheatsheet.md` (Phase 5).

Screenshots are written automatically to `target/screenshots/` while the tests run.
