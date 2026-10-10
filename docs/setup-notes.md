# Setup notes & troubleshooting (Windows + real device)

Real-world issues encountered while building and running this project on Windows,
and how they were resolved. Useful for the write-up.

---

## 1. Maven is not in winget

`winget install Apache.Maven` has no package. Installed manually instead:

```powershell
# 1. Download the binary zip from https://maven.apache.org/download.cgi
#    (apache-maven-3.9.9-bin.zip)
# 2. Extract to C:\Tools\apache-maven-3.9.9
# 3. Set the environment variable (User scope) and add to PATH:
[Environment]::SetEnvironmentVariable('MAVEN_HOME','C:\Tools\apache-maven-3.9.9','User')
#    PATH += ;%MAVEN_HOME%\bin
mvn -v
```

---

## 2. `ANDROID_HOME` not exported → session creation fails

Symptom:

```
A new session could not be created. Details: session not created
... Neither ANDROID_HOME nor ANDROID_SDK_ROOT environment variable was exported.
```

Fix (User scope, then restart the shell / Appium):

```powershell
[Environment]::SetEnvironmentVariable('ANDROID_HOME',"$env:LOCALAPPDATA\Android\Sdk",'User')
[Environment]::SetEnvironmentVariable('ANDROID_SDK_ROOT',"$env:LOCALAPPDATA\Android\Sdk",'User')
```

The Appium **server process** must be started from a shell that has these set.

---

## 3. Chrome test: "No Chromedriver found that can automate Chrome '154...'"

The device ships Chrome 154, and no matching chromedriver was installed.
Appium 3 can download one, but the feature must be **allowed** at server start,
and the feature name must be **driver-qualified**:

```powershell
# WRONG on Appium 3 -> validator error
appium --allow-insecure chromedriver_autodownload

# CORRECT
appium --allow-insecure "*:chromedriver_autodownload"
```

Appium then downloads chromedriver `154.0.8037.92` into `%USERPROFILE%\.appium`.

Follow-up symptom — **`chrome not reachable`**: the chromedriver started, but
Chrome on the device was not exposed (screen locked / Chrome already running).
Fix by waking the device and force-stopping Chrome before the run:

```powershell
adb shell input keyevent KEYCODE_WAKEUP
adb shell wm dismiss-keyguard
adb shell am force-stop com.android.chrome
```

---

## 4. `appium-doctor --android`: "`android` could NOT be found"

Modern Android SDKs removed the legacy `android` command, but
`appium-doctor` 1.16 still checks for it. Harmless stub to get a fully green
necessary-dependencies report:

`%ANDROID_HOME%\android.bat`:
```bat
@echo off
echo The 'android' command is deprecated. Use 'sdkmanager' / Android Studio instead.
```

After that: `necessary dependencies completed, no fix needed.` The optional
warnings (opencv4nodejs, mjpeg-consumer, bundletool, gstreamer) do not affect
Android UI tests.

> `appium-doctor` reads the **process** environment. If you set the variables with
> `[Environment]::SetEnvironmentVariable(...,'User')` in the same shell, also set
> `$env:ANDROID_HOME` / `$env:JAVA_HOME` in-process, or run doctor from a new shell.

---

## 5. Vendor-specific calculator locators (Xiaomi / MIUI)

The device has MIUI's calculator (`com.miui.calculator`), not Google Calculator
(`com.google.android.calculator`). Rather than hard-coding, the ids are
config-driven in `config.properties` and overridable per run:

```powershell
mvn test "-Dtest=BuiltInAppTest,RealDeviceTest" `
  -Dudid=<UDID> -DdeviceName=<UDID> `
  -Dcalculator.appPackage=com.miui.calculator `
  -Dcalculator.appActivity=com.miui.calculator.cal.CalculatorActivity `
  -Dcalculator.equalsId=btn_equal_s
```

MIUI also renders the result as `"= 10"`; `CalculatorPage.result()` strips the
leading `=` and whitespace before comparing.

---

## 6. ApiDemos v6.0.18 navigation details

`Views → Controls` is a **theme submenu**, so the widgets only appear after
picking a theme (`1. Light Theme`). Verified resource-ids:

| Widget | id |
|--------|----|
| Checkbox 1 | `io.appium.android.apis:id/check1` |
| Seek bar | `io.appium.android.apis:id/seek` |
| Drag dots | `io.appium.android.apis:id/drag_dot_1` (and `_2`, `_3`) |
| Drag result | `io.appium.android.apis:id/drag_result_text` |

---

## 7. Selenium version pin

`io.appium:java-client:9.3.0` declares Selenium `[4.19.0, 5.0)`. Selenium 4.26+
removed `ContextAware#getCenter()` and changed `createPointerDown` to take an
`int`, which breaks `Gestures.java`. Pin via BOM:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.selenium</groupId>
      <artifactId>selenium-bom</artifactId>
      <version>4.25.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

Also note: `UiAutomator2Options` has no `setBrowserName`; set it via
`options.setCapability(CapabilityType.BROWSER_NAME, "Chrome")`.

---

## 8. Surefire ignores `testng.xml` for a single `-Dtest=...`

Running `mvn test -Dtest=SomeTest` bypasses `testng.xml`, so only that class
runs. To run one class **but still use the suite definition**, run the full
suite: `mvn test` (all 8 classes).