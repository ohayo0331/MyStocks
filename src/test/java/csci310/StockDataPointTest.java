package csci310;

import static org.junit.Assert.*;

import org.junit.Test;

public class StockDataPointTest {

	@Test
	public void testStockDataPoint() {
		long date = 100000;
		double close = 200.0;
		StockDataPoint sdp = new StockDataPoint(date, close);
		assertEquals(date, sdp.date);
		assertTrue("expected close price (" + sdp.close + ") to match provided close price (" + close + ")", 
				sdp.close > close - 0.001 && sdp.close < close + 0.001);
	}

}
