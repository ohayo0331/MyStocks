package cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import csci310.DatabaseJDBC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

/**
 * Step definitions for Cucumber tests.
 */
public class StepDefinitions {
	private static final String ROOT_URL = "https://localhost:8443/";
	
	private WebDriver mobileDriver;
	
	private WebDriver driver = null;
	static ChromeOptions options = new ChromeOptions();
	 
	@Before
	public void setup() {
		options.addArguments("--allow-insecure-localhost");
		driver = new ChromeDriver(options);
	}

	
	@Before("@mobile")
	public final void mobileSetup() {
		Map<String, String> mobileEmulation = new HashMap<>();
		mobileEmulation.put("deviceName", "iPhone 6");
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
		chromeOptions.addArguments("--allow-insecure-localhost");
		mobileDriver = new ChromeDriver(chromeOptions);
	}
	
	@Given("I am on the {string} page")
	public void i_am_on_the_page(String page) {
		driver.get(ROOT_URL + page + ".jsp"); 
	}
	
	@Given("The user {string} {string} exists")
	public void the_user_exists(String username, String password) {
		DatabaseJDBC.removeUser(username);
		DatabaseJDBC.register(username, password);
	}
	
	@Given("The user {string} has {string} stocks of {string} in view stock lists with start date {string}")
	public void the_user_has_stocks_of_in_view_stock(String username, String qty, String ticker, String startDate) {
	    // view stock setup
		driver.findElement(By.id("viewStockButton")).click();
		driver.findElement(By.id("viewStockStartDate")).sendKeys(startDate);
		driver.findElement(By.id("viewStockTickerSymbol")).sendKeys(ticker);
		driver.findElement(By.id("viewStockNumberOfShares")).sendKeys(qty);
		driver.findElement(By.id("submitViewStock")).click();
		try {
			Thread.sleep(1000 * 20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	@When("I click the {string}")
	public void i_click_the(String id) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated((By.id(id))));
		driver.findElement(By.id(id)).click();
	}
	
	@When("I enter {string} to the {string} box")
	public void i_enter_to_the_box(String keys, String id) {
		driver.findElement(By.id(id)).sendKeys(keys);
	}
	
	@When("I enter a unique username to the {string} box")
	public void i_enter_a_unique_username_to_the_box(String id) {
		DatabaseJDBC.removeUser("user");
		String uniqueUsername = "user";
		driver.findElement(By.id(id)).sendKeys(uniqueUsername);
	}
	
	@When("I wait {int} seconds")
	public void i_wait_seconds(int seconds) {
		try {
			Thread.sleep(1000 * seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@When("I have had three invalid logins with {string}")
	public void i_have_had_three_invalid_logins_with(String username) {
		DatabaseJDBC.setThirdLogin(username, 0, 0);
	}
	
	@Then("I should be on the {string} page")
	public void i_should_be_on_the_page(String page) {
		String url = ROOT_URL + page + ".jsp";
		assertEquals(url, driver.getCurrentUrl());
	}
	
	@Then("I should see a {string} element with {string} {string}")
	public void i_should_see_a_element_with(String id, String attribute, String placeholder) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
		String att = we.getAttribute(attribute);
		att = att.replaceAll("\\s", "");
		placeholder = placeholder.replaceAll("\\s", "");
		assertEquals(att, placeholder);
		
	}
	
	@Given("The user {string} {string} is logged in")
	public void the_user_is_logged_in(String username, String password) {
		driver.get(ROOT_URL + "login.jsp"); 
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.id("signInButton")).click();
		try {
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Given("The user {string} has {string} stocks of {string} purchased on {string} and sold on {string}") 
	public void the_user_has_stocks_of_purchased_on_and_sold_on(String username, String qty, String stockName, String buyDate, String sellDate) {
		driver.findElement(By.id("addStockButton")).click();
		driver.findElement(By.id("addStockBuyDate")).sendKeys(buyDate);
		driver.findElement(By.id("addStockSellDate")).sendKeys(sellDate);
		driver.findElement(By.id("addStockTickerSymbol")).sendKeys(stockName);
		driver.findElement(By.id("addStockNumberOfShares")).sendKeys(qty);
		driver.findElement(By.id("submitAddStock")).click();
		try {
			Thread.sleep(1000 * 20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	@When("The user {string} adds {string} stocks of {string} purchased on {string} and sold on {string}") 
	public void the_user_adds_stocks_of_purchased_on_and_sold_on(String username, String qty, String stockName, String buyDate, String sellDate) {
		driver.findElement(By.id("addStockButton")).click();
		driver.findElement(By.id("addStockBuyDate")).sendKeys(buyDate);
		driver.findElement(By.id("addStockSellDate")).sendKeys(sellDate);
		driver.findElement(By.id("addStockTickerSymbol")).sendKeys(stockName);
		driver.findElement(By.id("addStockNumberOfShares")).sendKeys(qty);
		driver.findElement(By.id("submitAddStock")).click();
		try {
			Thread.sleep(1000 * 20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Then("I should see a toggle for {string}")
	public void i_should_see_a_toggle_for(String name) {
		WebElement toggleContainer = driver.findElement(By.id("toggleContainer"));
		Boolean toggleExists = true;
		try {
			toggleContainer.findElement(By.name(name));
		}
		catch (NoSuchElementException e)
		{
			toggleExists = false;
		}
		assertTrue(toggleExists);
	}
	
	@Then("The trend line for the stock {string} should be {string} in view stocks list")
	public void the_trend_line_for_the_stock_should_be_in_view_stocks_list(String name, String addedOrRemoved) {
	    // view stock list trend line check 
		Boolean lineExists = true;
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + name + "']")));
			driver.findElement(By.cssSelector("g[seriesName='" + name + "']"));
		}
		catch (NoSuchElementException e)
		{
			lineExists = false;
		}
		if (addedOrRemoved.equals("added"))
			assertEquals(true, lineExists);
		else
			assertEquals(false, lineExists);
	}
	
	@Given("The toggle for stock {string} is {string}")
	public void the_toggle_for_stock_is(String name, String onOrOff) {
		WebElement toggleContainer = driver.findElement(By.id("toggleContainer"));
		WebElement toggle = toggleContainer.findElement(By.name(name));
		if ((toggle.isSelected() && onOrOff == "off") || (!toggle.isSelected() && onOrOff == "on")) {
			// Need to use JS Executor because of permanent label overlay on toggle.
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", toggle);
		}
	}
	
	@When("I toggle a stock {string}")
	public void i_toggle_a_stock_on(String name) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("toggleContainer")));
		WebElement toggleContainer = driver.findElement(By.id("toggleContainer"));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		WebElement toggle = toggleContainer.findElement(By.name(name));
		// Need to use JS Executor because of permanent label overlay on toggle.
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", toggle);
	}
	
	@Then("The trend line on the graph for the stock {string} should be {string}")
	public void the_trend_line_on_the_graph_for_the_stock_should_be(String name, String visibility) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + name + "']")));
		WebElement trendLine = driver.findElement(By.cssSelector("g[seriesName='" + name + "']"));
		trendLine.getAttribute("visibility").equals(visibility);
	}
	
	@Then("I should see the {string} popup")
	public void see_pop_up(String id) {
		assertTrue(!driver.findElement(By.id(id)).getAttribute("style").contains("display: none;"));
	}
	
	@Then("The trend line for the stock {string} should be {string}")
	public void the_trend_line_for_the_stock_should_be(String name, String addedOrRemoved) {
		Boolean lineExists = true;
		try {
			try {
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			driver.findElement(By.cssSelector("g[seriesName='" + name + "']"));
		}
		catch (NoSuchElementException e)
		{
			lineExists = false;
		}
		if (addedOrRemoved.equals("added"))
			assertEquals(true, lineExists);
		else
			assertEquals(false, lineExists);
	}
	
	@When("I select stock {string}")
	public void i_select_stock(String name) {
		Select dropdown = new Select(driver.findElement(By.id("deleteStockTickerSymbol")));
	    dropdown.selectByValue(name);
	}
	
	@Given("I confirm")
	public void i_confirm() {
		 driver.switchTo().alert().accept();
	}
	
	@Then("I should see the {string} error")
	public void i_should_see_the_error(String string) {
		WebElement errMessage;
	    if (string.equals("invalid credential")) {
	    	errMessage = driver.findElement(By.id("invalidCredentials"));
	    	assertTrue(errMessage.getText().equalsIgnoreCase("The username and password entered was not correct.") || errMessage.getText().equalsIgnoreCase("The password you've entered is incorrect."));
	    }
	    else if (string.equals("confirm register")) {
	    	errMessage = driver.findElement(By.id("confirmPasswordError"));
	    	assertTrue(errMessage.getText().equalsIgnoreCase("Please make sure passwords match!"));
	    }   
	}
	
	@Then("I see the error message {string}")
	public void i_see_the_error_message(String string) {
	   WebElement eMessage = driver.findElement(By.id("deleteStockTickerError"));
	   assertTrue(eMessage.getText().contains("This is a required field"));
	}
	
	@Given("I am on the {string} page for mobile")
	public void i_am_on_the_page_for_mobile(String string) {
		mobileDriver.get(ROOT_URL + string + ".jsp"); 
	}
	
	@Then("I should be on the {string} page for mobile")
	public void i_should_be_on_the_page_for_mobile(String string) {
		String url = ROOT_URL + string + ".jsp";
		assertEquals(url, mobileDriver.getCurrentUrl());
	}
	
	@When("I enter a unique username to the {string} box on mobile")
	public void i_enter_a_unique_username_to_the_box_on_mobile(String string) {
		DatabaseJDBC.removeUser("user");
		String uniqueUsername = "user";
		mobileDriver.findElement(By.id(string)).sendKeys(uniqueUsername);
	}
	
	@When("I enter {string} to the {string} box on mobile")
	public void i_enter_to_the_box_on_mobile(String string, String string2) {
		mobileDriver.findElement(By.id(string2)).sendKeys(string);
	}
	
	@When("I click the {string} on mobile")
	public void i_click_the_on_mobile(String string) {
		WebDriverWait wait = new WebDriverWait(mobileDriver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(string)));
		mobileDriver.findElement(By.id(string)).click();
	}
	
	@Given("The user {string} {string} is logged in on mobile")
	public void the_user_is_logged_in_on_mobile(String string, String string2) {
		mobileDriver.get(ROOT_URL + "login.jsp"); 
		mobileDriver.findElement(By.id("username")).sendKeys(string);
		mobileDriver.findElement(By.id("password")).sendKeys(string2);
		mobileDriver.findElement(By.id("signInButton")).click();
		try {
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@When("I click the predict button")
	public void i_click_the_predict_button() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement we = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[5]/div/div/button[3]")));
		we.click();
	}
	
	@Then("I should see a portfolio value within {int}% of current")
	public void i_should_see_a_portfolio_value_within_of_current(Integer int1) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement we = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("predictionResults")));
		String[] elements = we.getText().split(" ");
		System.out.println(elements[3]);
		String percent = elements[3].replaceAll("%", "");
		int numericPercent = Integer.parseInt(percent);
		assertTrue(numericPercent <= 5 && numericPercent >= -5);
	}
	
	@Then("I should see https in the url")
	public void i_should_see_https_in_the_url() {
		String url = driver.getCurrentUrl();
		assertTrue(url.contains("https"));
	}
	
	@Then("I should see a {string} element with text {string}")
	public void i_should_see_a_element_with_text(String id, String text) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement we = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
		String att = we.getAttribute("innerHTML");
		att = att.replaceAll("\\s", "");
		text = text.replaceAll("\\s", "");
		assertEquals(att, text);
	}
	
	@Then("The trend line on the graph for the stock {string} should be changed as well when {string} is toggled")
	public void the_trend_line_on_the_graph_for_the_stock_should_be_changed_as_well_when_is_toggled(String portfolio, String stock) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("toggleContainer")));
		WebElement toggleContainer = driver.findElement(By.id("toggleContainer"));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(stock)));
		WebElement toggle = toggleContainer.findElement(By.name(stock));
		// Need to use JS Executor because of permanent label overlay on toggle.
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		WebElement trendLine = driver.findElement(By.cssSelector("g[seriesName='" + portfolio + "']"));
		executor.executeScript("arguments[0].click();", toggle);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + portfolio + "']")));
		WebElement trendLine2 = driver.findElement(By.cssSelector("g[seriesName='" + portfolio + "']"));
		assertTrue(!trendLine.equals(trendLine2));
	}
	
	@Then("{string} and {string} should be {string}")
	public void and_should_be(String stock1, String stock2, String hidden) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + stock1 + "']")));
		WebElement trendLine = driver.findElement(By.cssSelector("g[seriesName='" + stock1 + "']"));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + stock2 + "']")));
		WebElement trendLine2 = driver.findElement(By.cssSelector("g[seriesName='" + stock2 + "']"));
		assertTrue(trendLine.getAttribute("visibility").equals(hidden) && trendLine2.getAttribute("visibility").equals(hidden));
	}
	
	@Then("Adding a stock to {string} with {string} stocks of {string} purchased on {string} and sold on {string} changes {string} line")
	public void adding_a_stock_to_with_stocks_of_purchased_on_and_sold_on_changes_line(String username, String numStocks, String stockName, String buyDate, String sellDate, String portfolio) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + portfolio + "']")));
		WebElement trendLine = driver.findElement(By.cssSelector("g[seriesName='" + portfolio + "']"));
		driver.findElement(By.id("addStockButton")).click();
		driver.findElement(By.id("addStockBuyDate")).sendKeys(buyDate);
		driver.findElement(By.id("addStockSellDate")).sendKeys(sellDate);
		driver.findElement(By.id("addStockTickerSymbol")).sendKeys(stockName);
		driver.findElement(By.id("addStockNumberOfShares")).sendKeys(numStocks);
		driver.findElement(By.id("submitAddStock")).click();
		try {
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + portfolio + "']")));
		WebElement trendLine2 = driver.findElement(By.cssSelector("g[seriesName='" + portfolio + "']"));
		assertTrue(!(trendLine.equals(trendLine2)));
	}
	
	@Then("Removing the stock {string} should update my {string} trendline")
	public void removing_the_stock_should_update_my_trendline(String stock, String portfolio) {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + portfolio + "']")));
		WebElement trendLine = driver.findElement(By.cssSelector("g[seriesName='" + portfolio + "']"));
		wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("deleteStockButton"))));
		driver.findElement(By.id("deleteStockButton")).click();
		Select dropdown = new Select(driver.findElement(By.id("deleteStockTickerSymbol")));
	    dropdown.selectByValue(stock);
		wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("deleteStockButton"))));
		driver.findElement(By.id("submitDeleteStock")).click();
		driver.switchTo().alert().accept();
		try {
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("g[seriesName='" + portfolio + "']")));
		WebElement trendLine2 = driver.findElement(By.cssSelector("g[seriesName='" + portfolio + "']"));
		assertTrue(!(trendLine.equals(trendLine2)));
	}
	
	@Given("I do not have an SSL certificate")
	public void i_do_not_have_an_SSL_certificate() {
		try {
			driver.get("https://localhost:8080/" + "home" + ".jsp");
		} catch (Exception e) {
			assertTrue(e.toString().contains("ERR_CONNECTION_REFUSED"));
		}
	}

	@Then("I cannot access the application on port {string}")
	public void i_cannot_access_the_application_on_port(String string) {
		assertTrue(driver.getCurrentUrl().contains(string));
	}
	
	@Given("The user is registered with id {string} and password {string}")
	public void the_user_is_registered_with_id_and_password_(String id, String pw) {
		DatabaseJDBC.register(id, pw);
	}
	
	@When("the user logs in with {string} {string}")
	public void the_user_logs_in_with_(String username, String password) {
		driver.get(ROOT_URL + "login.jsp"); 
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.id("signInButton")).click();
		try {
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@After()
	public void after() {
		DatabaseJDBC.removeUser("user100");
		DatabaseJDBC.removeUser("userTest");
		DatabaseJDBC.register("user100", "pass");
		DatabaseJDBC.register("userTest", "password");
		driver.quit();
	}
}
