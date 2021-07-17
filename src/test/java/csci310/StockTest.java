package csci310;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class StockTest {
	private final String appleName = "AAPL";
	private final String netflixName = "NFLX";
	private final int quantity = 1;
	private final LocalDate buyDate = LocalDate.of(2020, 9, 10);
	private final LocalDate sellDate = LocalDate.of(2020, 9, 20);
	private Stock appleStock, netflixStock, errorStock;

	@Before
	public void setUp() throws Exception {
		netflixStock = new Stock(netflixName, quantity, buyDate, null);
		appleStock = new Stock(appleName, quantity, buyDate, sellDate);
		errorStock = new Stock("", quantity, buyDate, sellDate);
	}

	@Test
	public void testStock() {
		Stock stock = new Stock(appleName, quantity, buyDate, sellDate);
		assertTrue(stock.getName().contains(appleName));
		assertEquals(quantity, stock.getQty());
		assertEquals(buyDate, stock.getBuy());
		assertEquals(sellDate, stock.getSell());
	}

	@Test
	public void testGetName() {
		assertTrue(appleStock.getName().contains(appleName));
	}

	@Test
	public void testGetQty() {
		assertEquals(quantity, appleStock.getQty());
	}

	@Test
	public void testGetBuy() {
		assertEquals(buyDate, appleStock.getBuy());
	}

	@Test
	public void testGetSell() {
		assertEquals(sellDate, appleStock.getSell());
	}
	
	@Test
	public void testGetHistory() {
		String appleString = "[{\"date\":\"2020-09-10T00:00:00.000Z\",\"close\":113.49},{\"date\":\"2020-09-11T00:00:00.000Z\",\"close\":112.0},{\"date\":\"2020-09-14T00:00:00.000Z\",\"close\":115.355},{\"date\":\"2020-09-15T00:00:00.000Z\",\"close\":115.54},{\"date\":\"2020-09-16T00:00:00.000Z\",\"close\":112.13},{\"date\":\"2020-09-17T00:00:00.000Z\",\"close\":110.34},{\"date\":\"2020-09-18T00:00:00.000Z\",\"close\":106.84}]";
		String netflixString = "[{\"date\":\"2020-09-10T00:00:00.000Z\",\"close\":480.67},{\"date\":\"2020-09-11T00:00:00.000Z\",\"close\":482.03},{\"date\":\"2020-09-14T00:00:00.000Z\",\"close\":476.26},";
				
		assertEquals(appleString, appleStock.getHistory());
		
		assertTrue(netflixStock.getHistory().contains(netflixString));
		
		assertEquals("", errorStock.getHistory());
	}
	
	@Test
	public void testIsValidTicker() {
		assertFalse(Stock.isValidTicker("/"));
		assertFalse(Stock.isValidTicker(""));
		assertFalse(Stock.isValidTicker("APPLE123"));
		assertTrue(Stock.isValidTicker("AAPL"));
	}
	
	@Test
	public void testGetData() {
		assertTrue(errorStock.getData().isEmpty());
		assertEquals(7, appleStock.getData().size());
	}

}
