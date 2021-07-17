package cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import csci310.DatabaseJDBC;

/**
 * Run all the cucumber tests in the current package.
 */
@RunWith(Cucumber.class)
@CucumberOptions(strict = true)
// For running specific tests:
//@CucumberOptions(strict=true, features = {"src/test/resources/cucumber/addRemoveStocks.feature"})
public class RunCucumberTests {

	@BeforeClass
	public static void setup() {
		WebDriverManager.chromedriver().setup();
		DatabaseJDBC.removeUser("test");
		DatabaseJDBC.removeUser("user100");
		DatabaseJDBC.removeUser("userTest");
		DatabaseJDBC.register("test", "pass");
		DatabaseJDBC.register("user100", "pass");
		DatabaseJDBC.register("userTest", "password");
	}
}
