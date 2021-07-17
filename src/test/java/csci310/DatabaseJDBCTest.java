package csci310;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class DatabaseJDBCTest extends Mockito {
	
	private static LocalDate buy;
	private static LocalDate sell;
	private static LocalDate start;
	private static LocalDate invalidStart;
	
	@BeforeClass
	public static void databaseSetup() {
	    buy = LocalDate.parse("2019-04-15");
	    sell = LocalDate.parse("2019-08-10");
	    start = LocalDate.parse("2020-10-10");
	    invalidStart = LocalDate.parse("2021-01-01");
	    DatabaseJDBC.register("user", "pass");
	}
	
	@Test
	public void testConstructor() {
		// To test the class declaration
		DatabaseJDBC db = new DatabaseJDBC();
	}

	@Test
	public void testRegister() {
		// Setup
		DatabaseJDBC.removeUser("user");
		
		// Testing a valid register
		assertTrue("Register - valid register - failing", DatabaseJDBC.register("user", "pass") == 1);
		
		// Tests an invalid register with a username already taken
		DatabaseJDBC.register("user", "pass");
		assertTrue("Register - username already taken - failing", DatabaseJDBC.register("user", "pass") == 0);
		
		// Tests an invalid register with an empty username and password
		assertTrue("Register - empty username and password - failing", DatabaseJDBC.register("", "") == 0);
		
		// Tests an invalid register with an empty password
		assertTrue("Register - empty password - failing", DatabaseJDBC.register("user", "") == 0);
		
		// Tests an invalid register with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("Register - SQL exception - failing", DatabaseJDBC.register("user", "pass") == 0);
	}
	
	@Test
	public void testLogin() {
		// Setup
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
		
		// Tests a valid login
		assertTrue("Login - valid login - failing", DatabaseJDBC.login("user", "pass") == 1);
		
		// Tests first invalid login when the password is wrong and no first login
		assertTrue("Login - wrong password 1 - failing", DatabaseJDBC.login("user", "random") == -2);
		
		// Tests first invalid login when the password is wrong and first login was not today and awhile ago
		DatabaseJDBC.setFirstLogin("user", 1, 1);
		assertTrue("Login - wrong password 1 -day -hour - failing", DatabaseJDBC.login("user", "random") == -2);
		
		// Tests first invalid login when password is wrong and first login was today and awhile ago
		DatabaseJDBC.setFirstLogin("user", 0, 1);
		//System.out.println(DatabaseJDBC.login("user", "random"));
		assertTrue("Login - wrong password 1 -hour - failing", DatabaseJDBC.login("user", "random") == -2);
				
		// Tests second invalid login when the password is wrong
		assertTrue("Login - wrong password 2 - failing", DatabaseJDBC.login("user", "random") == -2);
		
		// Tests third invalid login when the password is wrong and third login was not today and awhile ago
		DatabaseJDBC.setThirdLogin("user", 1, 1);
		assertTrue("Login - wrong password 3 -day -hour - failing", DatabaseJDBC.login("user", "random") == -1);
		
		// Tests third invalid login when password is wrong and third login was today and awhile ago
		DatabaseJDBC.setThirdLogin("user", 0, 1);
		assertTrue("Login - wrong password 3 -hour - failing", DatabaseJDBC.login("user", "random") == -1);
		
		// Tests lockout after three invalid logins
		assertTrue("Login - lockout - failing", DatabaseJDBC.login("user", "random") == -1);
		
		// Tests an invalid login when the user does not exist
		DatabaseJDBC.removeUser("user");
		assertTrue("Login - user doesn't exist - failing", DatabaseJDBC.login("user", "pass") == 0);
		
		// Tests an invalid login with an empty username and password
		assertTrue("Login - empty usernamd and password - failing", DatabaseJDBC.login("", "") == 0);
		
		// Tests an invalid login with an empty password
		assertTrue("Login - empty password - failing", DatabaseJDBC.login("user", "") == 0);
		
		// Tests an invalid login with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("Login - SQL exception - failing", DatabaseJDBC.login("user", "pass") == 0);
	}
	
	@Test 
	public void testAddViewStock() {
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
		assertTrue("AddViewStock - valid", DatabaseJDBC.addViewStock("user", "AAPL", start, 1) == 1);
		
		assertTrue("AddViewStock - invalid start date", DatabaseJDBC.addViewStock("user", "AAPL", invalidStart, 1)==0);
		
		assertTrue("AddViewStock - already in the viewStock", DatabaseJDBC.addViewStock("user", "AAPL", start, 1)==0);
		
		assertTrue("AddViewStock - stock doesn't exist - failing", DatabaseJDBC.addViewStock("user", "INVALID", start, 1)==0);
		
		assertTrue("AddViewStock - empty start", DatabaseJDBC.addViewStock("user", "AAPL", null, 1) == 0);
		
		DatabaseJDBC.removeUser("user");
		
		DatabaseJDBC.removeUser("user");
		assertTrue("AddViewStock - user doesn't exist - failing", DatabaseJDBC.addViewStock("user", "TSLA", start, 3) == 0);
		
		// Tests an invalid add stock with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("AddViewStock - SQL exception - failing", DatabaseJDBC.addViewStock("user", "TSLA", start, 5) == -1);
	}

	@Test
	public void testAddStock() {
		// Setup
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
		
		// Tests a valid add stock
		assertTrue("AddStock - valid - failing", DatabaseJDBC.addStock("user", "AAPL", buy, sell, 3) == 1);
		
		// Tests a valid add stock when there is no sell date
		assertTrue("AddStock - valid and no sell date - failing", DatabaseJDBC.addStock("user", "AMZN", buy, null, 5) == 1);
		
		// Tests an invalid add stock when the user already owns the stock
		assertTrue("AddStock - user already owns the stock - failing", DatabaseJDBC.addStock("user", "AMZN", buy, null, 5) == 0);
		
		// Tests an invalid add stock when the sell date is before the buy date
		assertTrue("AddStock - sell date before buy date - failing", DatabaseJDBC.addStock("user", "TSLA", sell, buy, 3) == 0);
		
		// Tests an invalid add stock when the quantity is zero or negative
		assertTrue("AddStock - zero quantity - failing", DatabaseJDBC.addStock("user", "TSLA", buy, sell, 0) == 0);
		assertTrue("AddStock - negative quantity - failing", DatabaseJDBC.addStock("user", "TSLA", sell, buy, -5) == 0);
		
		// Tests an invalid add stock when the stock doesn't exist
		assertTrue("AddStock - stock doesn't exist - failing", DatabaseJDBC.addStock("user", "randomStock", buy, sell, 3) == -1);
		
		// Tests an invalid add stock when the user does not exist
		DatabaseJDBC.removeUser("user");
		assertTrue("AddStock - user doesn't exist - failing", DatabaseJDBC.addStock("user", "TSLA", buy, sell, 3) == 0);
		
		// Tests an invalid add stock with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("AddStock - SQL exception - failing", DatabaseJDBC.addStock("user", "TSLA", buy, sell, 5) == 0);
	}
	@Test
	public void testRemoveViewStock() {
		// Setup
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
		
		// Tests a valid remove stock
		DatabaseJDBC.addViewStock("user", "TSLA", start, 3);
		assertTrue("RemoveViewStock - valid - failing", DatabaseJDBC.removeViewStock("user", "TSLA") == 1);
		// Tests an invalid remove view only stock when the user does not own the stock
		
		assertTrue("RemoveViewStock - user doesn't own stock - failing", DatabaseJDBC.removeViewStock("user", "TSLA") == 0);
		
		// Tests an invalid remove view only stock when the user does not exist
		DatabaseJDBC.removeUser("user");
		assertTrue("RemoveViewStock - user doesn't exist - failing", DatabaseJDBC.removeViewStock("user", "MSFT") == 0);
		
		// Tests an invalid remove view only stock with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("RemoveViewStock - SQL exception - failing", DatabaseJDBC.removeViewStock("user", "AAPl") == 0);
	}
	
	
	@Test
	public void testRemoveStock() {
		// Setup
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
		
		// Tests a valid remove stock
		DatabaseJDBC.addStock("user", "TSLA", buy, sell, 3);
		assertTrue("RemoveStock - valid - failing", DatabaseJDBC.removeStock("user", "TSLA") == 1);
		
		// Tests an invalid remove stock when the user does not own the stock
		assertTrue("RemoveStock - user doesn't own stock - failing", DatabaseJDBC.removeStock("user", "TSLA") == 0);
		
		// Tests an invalid remove stock when the user does not exist
		DatabaseJDBC.removeUser("user");
		assertTrue("RemoveStock - user doesn't exist - failing", DatabaseJDBC.removeStock("user", "MSFT") == 0);
		
		// Tests an invalid remove stock with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("RemoveStock - SQL exception - failing", DatabaseJDBC.removeStock("user", "AAPl") == 0);
	}
	@Test 
	public void testGetViewStocks() {
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
		
		DatabaseJDBC.addViewStock("user", "MSFT", start, 1);
		ArrayList<Stock> viewStocks = DatabaseJDBC.getViewStocks("user");
		
		boolean flag = false;
		
		for(int i = 0; i < viewStocks.size(); i++) {
			Stock vs = viewStocks.get(i);
			if(vs.getName().contentEquals("MSFT") && vs.getQty() == 1) {
				flag = true;
			}
		}
		
		assertTrue("GetViewStock - valid - failing", flag == true);
		
		// Tests an invalid get view stock, where user does not exist
		DatabaseJDBC.removeUser("user");
		viewStocks = DatabaseJDBC.getViewStocks("user");
		assertTrue("GetStocks - user doesn't exist - failing", viewStocks.size() == 0);
		
		// Tests an invalid get stocks with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("GetStocks - SQL exception - failing", DatabaseJDBC.getViewStocks("user").size() == 0);
	}
	
	@Test
	public void testGetStocks() {
		// Setup
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
		
		// Tests a valid get stocks
		DatabaseJDBC.addStock("user", "MSFT", buy, sell, 10);
		DatabaseJDBC.addStock("user", "TSLA", buy, null, 5);
		ArrayList<Stock> stocks = DatabaseJDBC.getStocks("user");
		boolean flagTSLA = false;
		boolean flagMSFT = false;
		for(int i = 0; i < stocks.size(); i++) {
			Stock s = stocks.get(i);
			if(s.getName().contentEquals("MSFT") && s.getQty() == 10 && s.getBuy().equals(buy) && s.getSell().equals(sell)) {
				flagMSFT = true;
			}
			if(s.getName().contentEquals("TSLA") && s.getQty() == 5 && s.getBuy().equals(buy)) {
				flagTSLA = true;
			}
		}
		assertTrue("GetStocks - valid - failing", (flagTSLA && flagMSFT) == true);
		
		// Tests an invalid get stocks when the user does not exist
		DatabaseJDBC.removeUser("user");
		stocks = DatabaseJDBC.getStocks("user");
		assertTrue("GetStocks - user doesn't exist - failing", stocks.size() == 0);
		
		// Tests an invalid get stocks with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("GetStocks - SQL exception - failing", DatabaseJDBC.getStocks("user").size() == 0);
	}
	
	@Test
	public void testRemoveUser() {
		// Setup
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
				
		// Tests a valid remove user
		assertTrue("RemoveUser - valid - failing", DatabaseJDBC.removeUser("user") == 1);
		
		// Tests an invalid remove user when the user does not exist
		DatabaseJDBC.removeUser("user");
		assertTrue("RemoveUser - no user - failing", DatabaseJDBC.removeUser("user") == 0);
		
		// Tests an invalid remove user with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("RemoveUser - SQL exception - failing", DatabaseJDBC.removeUser("user") == 0);
	}
	
	@Test
	public void testSetFirstLogin() {
		// Setup
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
				
		// Tests a valid set first login
		assertTrue("SetFirstLogin - valid - failing", DatabaseJDBC.setFirstLogin("user", 1, 1) == 1);
		
		// Tests an invalid set first login when the user does not exist
		DatabaseJDBC.removeUser("user");
		assertTrue("SetFirstLogin - no user - failing", DatabaseJDBC.setFirstLogin("user", 1, 1) == 0);
		
		// Tests an invalid remove user with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("SetFirstLogin - SQL exception - failing", DatabaseJDBC.setFirstLogin("user", 1, 1) == 0);
	}
	
	@Test
	public void testSetThirdLogin() {
		// Setup
		DatabaseJDBC.removeUser("user");
		DatabaseJDBC.register("user", "pass");
		
		// Tests a valid set first login
		assertTrue("SetThirdLogin - valid - failing", DatabaseJDBC.setThirdLogin("user", 1, 1) == 1);
		
		// Tests an invalid set first login when the user does not exist
		DatabaseJDBC.removeUser("user");
		assertTrue("SetThirdLogin - no user - failing", DatabaseJDBC.setThirdLogin("user", 1, 1) == 0);
		
		// Tests an invalid remove user with a SQLException when the connection url is invalid
		DatabaseJDBC.setUrl("garbage");
		assertTrue("SetThirdLogin - SQL exception - failing", DatabaseJDBC.setThirdLogin("user", 1, 1) == 0);
	}
	
	@After
	public void setUrl() {
		DatabaseJDBC.setUrl("jdbc:mysql://cs310-groupag.cmudegxvolac.us-east-2.rds.amazonaws.com:3306/TestDB?user=admin&password=cs310groupag&useSSL=false");
	}

}
