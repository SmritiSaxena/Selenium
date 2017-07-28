package Datalicious;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.csvreader.CsvWriter;

public class Datalicious {
	public static WebDriver driver;
	public static CsvWriter csvOutput;

	public static void main(String[] args) throws IOException {

		// Task1: Login into Google >> Search Datalicious >> Click first URL
		// from organic search

		System.setProperty("webdriver.chrome.driver",
				System.getProperty("user.dir") + "\\src\\test\\resources\\executables\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.navigate().to("https://www.google.com");
		driver.findElement(By.xpath(".//*[@id='lst-ib']")).sendKeys("Datalicious");
		Actions action = new Actions(driver);
		action.sendKeys(Keys.ENTER).perform();
		driver.findElement(By.xpath(".//*[@id='rso']/div/div/div[1]/div/div/h3/a")).click();
		String url = driver.getCurrentUrl();

		// Task 2: Checking request made to host and google analytics:

		DesiredCapabilities dc = DesiredCapabilities.chrome();
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
		dc.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		WebDriver driver = new ChromeDriver(dc);
		driver.get(url);
		LogEntries les = driver.manage().logs().get(LogType.PERFORMANCE);

		// Task 3: Capturing google analytics endpoints in csv file

		boolean alreadyExists = new File("Output.csv").exists();
		csvOutput = new CsvWriter(new FileWriter("Output.csv", true), ',');
		for (LogEntry le : les) {
			if (le.getMessage().contains("dc.optimahub.com")) {
				System.out.println(
						"A request was made to host: dc.optimahub.com having data in json format:" + le.getMessage());
			} else if (le.getMessage().contains("google-analytics")) {
				try {
					if (!alreadyExists) {
						csvOutput.write("S_No");
						csvOutput.write("GoogleAnalyticsEndPoints");
						csvOutput.endRecord();
					}
					for (int i = 1; i < 10; i++) {
						csvOutput.write(i + le.getMessage());
						csvOutput.endRecord();
					}
					csvOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("A request was made to google analytics:" + le.getMessage());
			}
		}
		driver.close();
		driver.quit();
	}

}
