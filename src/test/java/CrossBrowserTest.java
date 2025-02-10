import org.omg.CORBA.TIMEOUT;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.logging.Level;

public class CrossBrowserTest {

    private WebDriver driver;
    private String browserType;

    @BeforeClass
    @Parameters({"browser"})
    public void setUp(String browser) throws Exception {
        System.out.println("Initializing tests for browser: " + browser);

        this.browserType = browser.toLowerCase();
        String hubURL = "http://localhost:4444/wd/hub";

        if (browser.equalsIgnoreCase("chrome")) {
            System.out.println("Chrome is starting...");
            ChromeOptions chromeOptions = new ChromeOptions();
            enableLogging(chromeOptions);
            driver = new RemoteWebDriver(new URL(hubURL), chromeOptions);
        } else if (browser.equalsIgnoreCase("firefox")) {
            System.out.println("Launching Firefox browser...");
            FirefoxOptions firefoxOptions = new FirefoxOptions();

            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            logPrefs.enable(LogType.DRIVER, Level.ALL);

            firefoxOptions.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
            firefoxOptions.setCapability("moz:firefoxOptions", new HashMap<>()); // Required for Grid
            enableLogging(firefoxOptions);
            driver = new RemoteWebDriver(new URL(hubURL), firefoxOptions);
        }
    }


    @AfterMethod
    public void captureBrowserLogs() {
        try {
            String logFileName = "logs/" + browserType + ".log";
            File logFile = new File(logFileName);
            logFile.getParentFile().mkdirs();  // Create directory if not exists

            LogEntries logs = driver.manage().logs().get("browser");
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                for (LogEntry entry : logs) {
                    writer.println(entry.getTimestamp() + " " + entry.getLevel() + " " + entry.getMessage());
                }
                System.out.println("Logs saved at: " + logFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error saving logs: " + e.getMessage());
        }
    }


    private void enableLogging(MutableCapabilities options) {
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable("browser", Level.ALL);
        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
    }


    public void captureDockerLogs(String containerName) {
        try {
            Process process = Runtime.getRuntime().exec("docker logs " + containerName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[DOCKER LOG] " + line);
            }
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error retrieving Docker logs: " + e.getMessage());
        }
    }


    @Test
    public void testGoogleSearch() throws InterruptedException {
        try {

            driver.get("https://www.google.com");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Wait for max 10 seconds
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button/div[contains(text(),'Zaakceptuj wszystko')]")));
            acceptButton.click();
            takeScreenshot("google_search1");

            WaitUtils.waitForElementToBeVisible(driver, By.name("q"), 10);
            driver.findElement(By.name("q")).sendKeys("Apple");

            WaitUtils.waitForElementToBeClickable(driver, By.name("btnK"), 10);
            driver.findElement(By.name("btnK")).click();

            takeScreenshot("google_search_22");
            String title = driver.getTitle();
            Assert.assertTrue(title.contains("Apple"), "Title does not contain 'Apple'");
            takeScreenshot("google_search_23");

        } catch (Exception e) {

            takeScreenshot("google_search_failure");
            throw e;
        }
    }


    private void takeScreenshot(String fileName) {
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String browserName = ((RemoteWebDriver) driver).getCapabilities().getBrowserName();

            File directory = new File("screenshots");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destinationFile = new File(directory,browserName +"_" +fileName + ".png");


            Files.copy(srcFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Screenshot saved at: " + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to take screenshot: " + fileName, e);
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }

        captureDockerLogs("selenium-chrome");  // Replace with correct container name
        captureDockerLogs("selenium-firefox");
    }
}
