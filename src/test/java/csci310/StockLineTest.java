package csci310;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class StockLineTest {

	@Test
	public void testStockLine() {
		StockLine sl = new StockLine("", new ArrayList<StockDataPoint>());
		assertEquals("", sl.getName());
	}

	@Test
	public void testGetName() {
		StockLine sl = new StockLine("AAPL", new ArrayList<StockDataPoint>());
		assertEquals("AAPL", sl.getName());
	}

	@Test
	public void testGetData() {
		StockLine sl = new StockLine("AAPL", new ArrayList<StockDataPoint>());
		assertEquals("", sl.getData());
		
		ArrayList<StockDataPoint> arr = new ArrayList<StockDataPoint>();
		arr.add(new StockDataPoint(0,0.0));
		arr.add(new StockDataPoint(1,1.0));
		StockLine sl2 = new StockLine("AAPL", arr);
		assertEquals("{\"data\":[[0,0],[1,1]],\"name\":\"AAPL\"}", sl2.getData());
	}

}
