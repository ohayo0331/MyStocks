package csci310;

import static org.junit.Assert.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import io.cucumber.java.After;

public class PortfolioTest {
	private String appleJsonString = "\"name\":\"AAPL\"";
	private String netflixJsonString = "\"name\":\"NFLX\"";
	private String teslaJsonString = "\"name\":\"TSLA\"";
	private final String appleName = "AAPL";
	private final String netflixName = "NFLX";
	private final String teslaName = "TSLA";
	private final String zoomName = "ZM";
	private LocalDate buyDate = LocalDate.of(2020, 9, 10);
	private LocalDate sellDate = LocalDate.of(2020, 9, 20);
	// adding mock start date for view
	private LocalDate startDate = LocalDate.of(2020, 9, 1);
	private LocalDate buyDate2 = LocalDate.of(2020, 9, 05);
	private LocalDate sellDate2 = LocalDate.of(2020, 9, 30);
	private int quantity = 1;
	private Portfolio p;

	@Before
	public void setUp() throws Exception {
		// Register user named user7A
		DatabaseJDBC.removeUser("user7A");
		DatabaseJDBC.register("user7A", "password");
		p = new Portfolio("user7A");
	}

	 @After
	    public final void setDown() { 
		 DatabaseJDBC.removeUser("user7A");
	 }
	
	@Test
	public void testPortfolio() {
		p.addStock(appleName, buyDate, sellDate, quantity);
		p.addStock(teslaName, buyDate, sellDate, quantity);
		p.addStock(netflixName, buyDate2, sellDate2, quantity);
		p.addViewStock(zoomName, buyDate, quantity);
		Portfolio portfolio = new Portfolio("user7A");
		ArrayList<StockLine> data = portfolio.getData();
		System.out.println(data.size());
		assertEquals(4, data.size());
		p.removeStock(appleName);
		p.removeStock(netflixName);
		p.removeStock(teslaName);
		p.removeViewStock(zoomName);
		
//		p.addViewStock(appleName, startDate, quantity);
//		p.addViewStock(netflixName, startDate, quantity);

		
	}
	
	@Test
	public void testGetViewOnlyData() {
		assertEquals(1, p.getViewOnlyData().size());
		p.addViewStock(appleName, startDate, 1);
		ArrayList<StockLine> data = p.getViewOnlyData();
		assertEquals(2, data.size());
	}

	@Test
	public void testGetData() {
		assertEquals(1, p.getData().size());
		
		p.addStock(appleName, buyDate, sellDate, quantity);
		ArrayList<StockLine> data = p.getData();
		assertEquals(2, data.size());
		p.removeStock("AAPL");
		
		Portfolio errorPortfolio = new Portfolio("");
		assertEquals(1, errorPortfolio.getData().size());
	}
	
	@Test
	public void testViewAddStock() {
		int success = p.addViewStock(zoomName, startDate, quantity);
		assertEquals(1, success);
		
		success = p.addViewStock(zoomName, null, quantity);
		assertEquals(0, success);
		
		p.removeViewStock("ZM");
	}
	
	@Test
	public void testAddStock() {
		p.removeStock(appleName);
		int success = p.addStock(appleName, buyDate, sellDate2, quantity);
		assertEquals(1, success);
		
		success = p.addStock(appleName, buyDate, buyDate, quantity);
		assertEquals(0, success);
		
		success = p.addStock(netflixName, buyDate2, sellDate, quantity);
		assertEquals(1, success);
		
		p.removeStock("AAPL");
		p.removeStock("NFLX");
	}
	
	@Test
	public void testRemoveViewStock() {
		p.addViewStock(appleName, startDate, quantity);
		p.addViewStock(zoomName, startDate, quantity);
		
		int success = p.removeViewStock(appleName);
		assertEquals(1, success);
		success = p.removeViewStock(zoomName);
		assertEquals(1, success);
		success = p.removeViewStock(zoomName);
		assertEquals(0, success);
		
		p.addViewStock(teslaName, startDate, 1);
		p.addViewStock(appleName, startDate, 1);
		success = p.removeViewStock(appleName);
	}
	
	@Test
	public void testRemoveStock() {
		p.addStock(netflixName, buyDate, sellDate, quantity);
		p.addStock(appleName, buyDate, sellDate, quantity);
		
		int success = p.removeStock(appleName);
		assertEquals(1, success);
		
		success = p.removeStock(netflixName);
		assertEquals(1, success);
		
		success = p.removeStock("APPL");
		assertEquals(0, success);
	}
	
	@Test
	public void testGetPercentChange() {
		assertEquals(0, p.getPercentChange());
		
		p.addStock(netflixName, buyDate, null, quantity);
		Stock netflixStock = new Stock(netflixName, quantity, buyDate, null);
		ArrayList<StockDataPoint> netflixData = netflixStock.getData();
		double latestDay = netflixData.get(netflixData.size()-1).close;
		double secondLatestDay = netflixData.get(netflixData.size()-2).close;
		int percentChange = (int) Math.round((latestDay - secondLatestDay) / secondLatestDay * 100);
		assertEquals(percentChange, p.getPercentChange());
		
		p.addStock(appleName, buyDate, null, quantity);
		Stock appleStock = new Stock(appleName, quantity, buyDate, null);
		ArrayList<StockDataPoint> appleData = appleStock.getData();
		latestDay = appleData.get(appleData.size()-1).close + latestDay;
		secondLatestDay = appleData.get(appleData.size()-2).close + secondLatestDay;
		percentChange = (int) Math.round((latestDay - secondLatestDay) / secondLatestDay * 100);
		assertEquals(percentChange, p.getPercentChange());
		
		p.removeStock(appleName);
		p.removeStock(netflixName);
	}
	
	@Test
	public void testGetPortfolioPrediction() {
		LocalDate predictionDate = LocalDate.of(2020, 11, 19);
		p.addStock(netflixName, buyDate, null, quantity);
		Stock netflixStock = new Stock(netflixName, quantity, buyDate, null);
		ArrayList<StockDataPoint> netflixData = netflixStock.getData();
		Double closeT0 = 0.0, closeT1 = 0.0;
		Double avgReturn = 1.0;
		int numDays = 0;
		Double stockLastClose = netflixData.get(netflixData.size()-1).close;
		for (StockDataPoint currStock : netflixData) {
			numDays += 1;
			closeT1 = currStock.close;
			if (closeT0 != 0.0) {
				Double percentDifference = (closeT1 - closeT0) / closeT0;
				avgReturn *= (1+percentDifference);
			}
			
			closeT0 = closeT1;
		}
		avgReturn = Math.pow(avgReturn, 1.0/numDays) - 1;
		
		
		LocalDate today = LocalDate.now();
		Period period = Period.between(today, predictionDate);
		
		Double prediction = ((Math.pow(1+avgReturn, period.getDays()))-1)*100;
		DecimalFormat df = new DecimalFormat("#.#####");      
		avgReturn = Double.valueOf(df.format(prediction));
		
		System.out.println("Predicton: " + avgReturn);
		assertEquals(avgReturn, p.getPortfolioPrediction(predictionDate));
		
		p.removeStock(netflixName);
		
		p.addStock(appleName, buyDate, sellDate, quantity);
		Double test = 0.000;
		assertEquals(test, p.getPortfolioPrediction(predictionDate));
		
		p.removeStock(appleName);
	}

	public void testGetTotalValue() {
		assertTrue(0.0 == p.getTotalValue());
        p.addStock(netflixName, buyDate, null, quantity);
        Stock netflixStock = new Stock(netflixName, quantity, buyDate, null);
        ArrayList<StockDataPoint> netflixData = netflixStock.getData();
        double latestDay = netflixData.get(netflixData.size()-1).close;
        assertTrue(latestDay == p.getTotalValue());
        
        p.addStock(appleName, buyDate, null, quantity);
        Stock appleStock = new Stock(appleName, quantity, buyDate, null);
        ArrayList<StockDataPoint> appleData = appleStock.getData();
        latestDay = appleData.get(appleData.size()-1).close + latestDay;
        assertTrue(latestDay == p.getTotalValue());
        
        p.removeStock(appleName);
        p.removeStock(netflixName);
	}
	@Test
	public void testGetStockNumbers() {
		assertTrue(p.getStockNumbers().isEmpty());
		p.addStock(netflixName, buyDate, null, quantity);
		p.addStock(appleName, buyDate, null, quantity);
		assertTrue(!p.getStockNumbers().isEmpty());
		assertTrue(p.getStockNumbers().get(0) == 1);
		assertTrue(p.getStockNumbers().get(1)== 1);
		
		p.removeStock(netflixName);
		p.removeStock(appleName);
	}
	@Test
	public void testGetViewStockNumbers() {
		assertTrue(p.getViewStockNumbers().isEmpty());
		p.addViewStock(netflixName, buyDate, quantity);
		p.addViewStock(appleName, buyDate, quantity);
		assertTrue(!p.getViewStockNumbers().isEmpty());
		assertTrue(p.getViewStockNumbers().get(0) == 1);
		assertTrue(p.getViewStockNumbers().get(1)== 1);
		
		p.removeViewStock(netflixName);
		p.removeViewStock(appleName);
	}
}
