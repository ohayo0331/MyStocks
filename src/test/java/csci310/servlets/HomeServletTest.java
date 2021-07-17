package csci310.servlets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import csci310.DatabaseJDBC;

public class HomeServletTest extends Mockito {
	private String username = "user100";
	private String addedStockErrorMessage = "Error adding stock!";
	private String removedStockErrorMessage = "Error removing stock!";
	
	@Before
	public void setUp() {
		// Remove username from database before testing.
		DatabaseJDBC.removeUser(username);
		// Create temporary account to test with.
		DatabaseJDBC.register(username, "pass");
	}

	@Test
	public final void testHomeServlet() {
		HomeServlet hs = new HomeServlet();
		assertTrue(hs != null);
	}

	@Test
	public final void testDoGet() throws IOException {	
		HomeServlet hs = new HomeServlet();
		
		// Scenario 1: adding stock successfully
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("addStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("sellDate")).thenReturn("2020-10-31");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(request.getParameter("numberOfShares")).thenReturn("3");
		when(response.getWriter()).thenReturn(writer);

		hs.doGet(request, response);
		writer.flush();	
		
		assertTrue("Add stock - Expected to contain chartData json, but was: " + stringWriter.toString(), 
				stringWriter.toString().contains("chartData"));
		
		// Scenario 2: Get stock prediction for stock that is sold
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("futureValue");
		when(request.getParameter("tickerSymbol")).thenReturn("2021-11-03");
		when(response.getWriter()).thenReturn(writer);

		hs.doGet(request, response);
		writer.flush();	
		
		assertTrue("Prediction made", 
				stringWriter.toString().contains("futureValuePercentChange"));
		
		// Scenario 3: adding stock with no sell date
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("addStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("sellDate")).thenReturn("");
		when(request.getParameter("tickerSymbol")).thenReturn("AMZN");
		when(request.getParameter("numberOfShares")).thenReturn("3");
		when(response.getWriter()).thenReturn(writer);

		hs.doGet(request, response);
		writer.flush();
		
		assertTrue("Add stock no sell date - Expected to contain chartData json, but was: " + stringWriter.toString(), 
				stringWriter.toString().contains("chartData"));
		
		// Scenario 4: adding stock where sell Date < buy Date
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("addStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("sellDate")).thenReturn("2020-08-31");
		when(request.getParameter("tickerSymbol")).thenReturn("TSLA");
		when(request.getParameter("numberOfShares")).thenReturn("3");
		when(response.getWriter()).thenReturn(writer);

		hs.doGet(request, response);
		writer.flush();	
		
		assertTrue("Add stock sell < buy date - Expected to contain: " + addedStockErrorMessage + ", but was: " + stringWriter.toString(), 
				stringWriter.toString().contains(addedStockErrorMessage));
		
		// Scenario 5: removing stock successfully	
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("removeStock");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(response.getWriter()).thenReturn(writer);

		hs.doGet(request, response);
		writer.flush();	
		
		assertTrue("Remove stock - Expected to contain chartData json, but was: " + stringWriter.toString(), 
			stringWriter.toString().contains("chartData"));
		
		// Scenario 6: removing stock that user doesn't own	
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("removeStock");
		when(request.getParameter("tickerSymbol")).thenReturn("NFLX");
		when(response.getWriter()).thenReturn(writer);

		hs.doGet(request, response);
		writer.flush();	
		
		assertTrue("Removing stock not owned - Expected to contain: " + removedStockErrorMessage + ", but was: " + stringWriter.toString(), 
				stringWriter.toString().contains(removedStockErrorMessage));
		
		// Scenario 7: making call that is neither add nor remove stock
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("getData");
		when(response.getWriter()).thenReturn(writer);

		hs.doGet(request, response);
		writer.flush();	
		
		assertTrue("Neither add nor remove stock - Expected to contain chartData json, but was: " + stringWriter.toString(), 
				stringWriter.toString().contains("chartData"));
		
		// Scenario 8: Get stock prediction
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("futureValue");
		when(request.getParameter("tickerSymbol")).thenReturn("2021-11-03");
		when(response.getWriter()).thenReturn(writer);

		hs.doGet(request, response);
		writer.flush();	
		
		assertTrue("Prediction made", 
				stringWriter.toString().contains("futureValuePercentChange"));
		
		// scenario 9: Get num shares
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("addStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("sellDate")).thenReturn("2020-10-31");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(request.getParameter("numberOfShares")).thenReturn("3");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doGet(request, response);
		writer.flush();
		assertTrue("Error - no Shares", stringWriter.toString().contains("numShares"));
		
		// Scenario 10: viewStock
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("viewStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(request.getParameter("numberOfShares")).thenReturn("1");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doGet(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("chartViewData"));
		
		// Scenario 11: viewStock invalid ticker
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("viewStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("tickerSymbol")).thenReturn("InvalidTicker");
		when(request.getParameter("numberOfShares")).thenReturn("1");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doGet(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("error"));
		
		// Scenario 12: viewStock start date in future
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("viewStock");
		when(request.getParameter("buyDate")).thenReturn("2022-09-30");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(request.getParameter("numberOfShares")).thenReturn("1");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doGet(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("error"));
		
		// Scenario 13: remove viewStock
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("removeViewStock");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doGet(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("chartViewData"));
		
		// Scenario 14: remove viewStock invalid ticker
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("removeViewStock");
		when(request.getParameter("tickerSymbol")).thenReturn("InvalidTicker");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doGet(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("error"));
		
	}

	@Test
	public final void testDoPost() throws IOException {
		HomeServlet hs = new HomeServlet();
		
		// Scenario 1: adding stock successfully
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("addStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("sellDate")).thenReturn("2020-10-31");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(request.getParameter("numberOfShares")).thenReturn("3");
		when(response.getWriter()).thenReturn(writer);

		hs.doPost(request, response);
		writer.flush();	
		
		assertTrue("Add stock - Expected to contain chartData json, but was: " + stringWriter.toString(), 
				stringWriter.toString().contains("chartData"));
		
		// Scenario 2: Get stock prediction for stock that is sold
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("futureValue");
		when(request.getParameter("tickerSymbol")).thenReturn("2021-11-03");
		when(response.getWriter()).thenReturn(writer);

		hs.doPost(request, response);
		writer.flush();	
		
		assertTrue("Prediction made", 
				stringWriter.toString().contains("futureValuePercentChange"));
		
		// Scenario 3: adding stock with no sell date
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("addStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("sellDate")).thenReturn("");
		when(request.getParameter("tickerSymbol")).thenReturn("AMZN");
		when(request.getParameter("numberOfShares")).thenReturn("3");
		when(response.getWriter()).thenReturn(writer);

		hs.doPost(request, response);
		writer.flush();
		
		assertTrue("Add stock no sell date - Expected to contain chartData json, but was: " + stringWriter.toString(), 
				stringWriter.toString().contains("chartData"));
		
		// Scenario 4: adding stock where sell Date < buy Date
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("addStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("sellDate")).thenReturn("2020-08-31");
		when(request.getParameter("tickerSymbol")).thenReturn("TSLA");
		when(request.getParameter("numberOfShares")).thenReturn("3");
		when(response.getWriter()).thenReturn(writer);

		hs.doPost(request, response);
		writer.flush();	
		
		assertTrue("Add stock sell < buy date - Expected to contain: " + addedStockErrorMessage + ", but was: " + stringWriter.toString(), 
				stringWriter.toString().contains(addedStockErrorMessage));
		
		// Scenario 5: removing stock successfully	
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("removeStock");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(response.getWriter()).thenReturn(writer);

		hs.doPost(request, response);
		writer.flush();	
		
		assertTrue("Remove stock - Expected to contain chartData json, but was: " + stringWriter.toString(), 
				stringWriter.toString().contains("chartData"));
		
		// Scenario 6: removing stock that user doesn't own	
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("removeStock");
		when(request.getParameter("tickerSymbol")).thenReturn("NFLX");
		when(response.getWriter()).thenReturn(writer);

		hs.doPost(request, response);
		writer.flush();	
		
		assertTrue("Removing stock not owned - Expected to contain: " + removedStockErrorMessage + ", but was: " + stringWriter.toString(), 
				stringWriter.toString().contains(removedStockErrorMessage));
		
		// Scenario 7: making call that is neither add nor remove stock
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("getData");
		when(response.getWriter()).thenReturn(writer);

		hs.doPost(request, response);
		writer.flush();	
		
		assertTrue("Neither add nor remove stock - Expected to contain chartData json, but was: " + stringWriter.toString(), 
				stringWriter.toString().contains("chartData"));
		
		// Scenario 8: Get stock prediction
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("futureValue");
		when(request.getParameter("tickerSymbol")).thenReturn("2021-11-03");
		when(response.getWriter()).thenReturn(writer);

		hs.doPost(request, response);
		writer.flush();	
		
		assertTrue("Prediction made", 
				stringWriter.toString().contains("futureValuePercentChange"));
		
		// Scenario 9: Get num shares
		
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("addStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("sellDate")).thenReturn("2020-10-31");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(request.getParameter("numberOfShares")).thenReturn("3");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doPost(request, response);
		writer.flush();
		assertTrue("Error - no Shares", stringWriter.toString().contains("numShares"));
		
		// Scenario 10: viewStock
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("viewStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(request.getParameter("numberOfShares")).thenReturn("1");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doPost(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("chartViewData"));
		
		// Scenario 11: viewStock invalid ticker
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("viewStock");
		when(request.getParameter("buyDate")).thenReturn("2020-09-30");
		when(request.getParameter("tickerSymbol")).thenReturn("InvalidTicker");
		when(request.getParameter("numberOfShares")).thenReturn("1");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doPost(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("error"));
		
		
		// Scenario 12: viewStock start date in future
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("viewStock");
		when(request.getParameter("buyDate")).thenReturn("2022-09-30");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(request.getParameter("numberOfShares")).thenReturn("1");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doPost(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("error"));
		
		// Scenario 13: remove viewStock
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("removeViewStock");
		when(request.getParameter("tickerSymbol")).thenReturn("AAPL");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doPost(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("chartViewData"));
		
		// Scenario 14: remove viewStock invalid ticker
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("action")).thenReturn("removeViewStock");
		when(request.getParameter("tickerSymbol")).thenReturn("InvalidTicker");
		when(response.getWriter()).thenReturn(writer);
		
		hs.doPost(request, response);
		writer.flush();
		assertTrue("Error - view Stock", stringWriter.toString().contains("error"));
	}
}
