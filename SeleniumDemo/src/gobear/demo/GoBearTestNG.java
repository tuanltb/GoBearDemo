package gobear.demo;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class GoBearTestNG {

	WebDriver driver;
	String baseURL = "https://www.gobear.com/ph?x_session_type=UAT";

	@Test
	public void testTravelInsurance() throws InterruptedException {

		/** Step1: Go to website https://www.gobear.com/ph?x_session_type=UAT */
		driver.get(baseURL);
		Thread.sleep(3000); // Wait for page load

		/** Step2: Go to Travel section */
		// Go to Insurance tab first
		WebElement element = driver.findElement(By.xpath("//li[@data-gb-name='Insurance']"));
		element.click();
		Thread.sleep(1000);

		// Then go to Travel section
		element = driver.findElement(By.xpath("//li[@data-gb-name='Travel']"));
		element.click();
		Thread.sleep(1000);

		/** Step3: Go to the Travel results page */
		WebElement button = driver.findElement(By.name("product-form-submit"));
		button.click();
		Thread.sleep(5000);
		// Verify result bar is displayed
		element = driver.findElement(By.xpath("//div[@data-gb-name='travel-nav-data']"));
		Assert.assertTrue(element.isDisplayed());

		/** Step4: Make sure at least 3 cards are being displayed */
		List<WebElement> cardsList = driver.findElements(By.xpath("//div[contains(@class,'card-full')]"));
		int sizeNoFilter = cardsList.size();
		Assert.assertTrue(sizeNoFilter > 2);

		/**
		 * Step5: Make sure the left side menu categories are functional. FYI, there are
		 * 3 categories: Filter, Sort and Details. Basic goal: test at least 1 option
		 * per option (Filter, Sort, Details) changing at least 1 radio button, 1 range
		 * selector, 1 check box, 1 dropdown, 1 calendar picker. Stretch goal*: write a
		 * test to ensure the left side menu is functional.
		 */
		// Change 1 option in Filter by selecting "FPG Insurance" check box
		WebElement insurerFPG = driver.findElement(By.xpath("//div[@data-filter-name='FPG Insurance']"));
		insurerFPG.click();
		Thread.sleep(1000);

		// Verify cards result after selecting filter FPG Insurance
		List<WebElement> cardsListFiltered = driver.findElements(By.xpath("//div[contains(@class,'card-full')]"));
		Assert.assertFalse(cardsListFiltered.size() > sizeNoFilter);

		// Roll back data by de-selecing filter FPG Insurance
		insurerFPG.click();
		Thread.sleep(1000);

		// Change 1 option in Sort by selecting option "Price: Low to High"
		WebElement optPriceLowToHigh = driver.findElement(By.id("gb_radio_3"));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", optPriceLowToHigh);
		Thread.sleep(3000);

		// Verify cards result after selecting option Price Low to High - Ascending order
		boolean bolVal = true;
		long previousElementPrice = 0L;
		List<WebElement> cardsListSorted = driver
				.findElements(By.xpath("//div[@class='policy-price']//span[@class='value']"));
		for (WebElement elePrice : cardsListSorted) {
			// Remove "," in number format and convert it to number, e.g: 1,295 -> 1295
			long currentElementPrice = Long.parseLong(elePrice.getText().replace(",", ""));
			if (currentElementPrice < previousElementPrice) {
				bolVal = false;
				break;
			} else {
				previousElementPrice = currentElementPrice;
			}
		}
		Assert.assertTrue(bolVal, "Cards are not in Price: Low to High.");

		// Change Sort option "Price: High to Low"
		WebElement optPriceHighToLow = driver.findElement(By.id("gb_radio_4"));
		executor.executeScript("arguments[0].click();", optPriceHighToLow);
		Thread.sleep(3000);

		// Verify cards result after selecting option Price Low to High - Descending order
		bolVal = true;
		cardsListSorted = driver.findElements(By.xpath("//div[@class='policy-price']//span[@class='value']"));
		previousElementPrice = Long.parseLong(cardsListSorted.get(0).getText().replace(",", ""));
		for (WebElement elePrice : cardsListSorted) {
			long currentElementPrice = Long.parseLong(elePrice.getText().replace(",", ""));
			if (currentElementPrice > previousElementPrice) {
				bolVal = false;
				break;
			} else {
				previousElementPrice = currentElementPrice;
			}
		}
		Assert.assertTrue(bolVal, "Cards are not in Price: High to Low.");
	}

	@BeforeTest
	public void beforeTest() {
		// Initiate Chrome driver
//		System.setProperty("webdriver.chrome.driver", "D:\\SeleniumDemo\\WebDriver\\chromedriver.exe");
		String path = System.getProperty("user.dir");
		System.out.println(path);
		System.setProperty("webdriver.chrome.driver", path + "\\resources\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars"); // disable message "Chrome is being controlled by automated test software"
		options.addArguments("--start-maximized"); // maximize browser window
		driver = new ChromeDriver(options);
	}

	@AfterTest
	public void afterTest() {
		driver.close();
	}

}
