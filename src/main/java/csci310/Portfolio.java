package csci310;


import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class Portfolio {
	private String username;
	private ArrayList<Stock> stocks;
	private Map<Long, Double> portfolio;
	private ArrayList<Stock> viewStocks;
	private Map<Long, Double> viewP;
	
	public Portfolio(String username) {
		this.username = username;
		stocks = DatabaseJDBC.getStocks(username);
		// get view only stocks also
		viewStocks = DatabaseJDBC.getViewStocks(username);
		portfolio = new TreeMap<Long, Double>();
		viewP = new TreeMap<Long, Double>();
				
		Map<Long, Double> sellOff = new TreeMap<Long, Double>();
		for(Stock s : stocks) {
			ArrayList<StockDataPoint> stockData = s.getData();
			LocalDate sellDate = s.getSell();

			for(StockDataPoint sdp : stockData) {
				Double currentValue = portfolio.get(sdp.date);
				if (sellDate != null) {					
					if (sdp.equals(stockData.get(stockData.size()-1))) {
						if (sellOff.containsKey(sdp.date)) {
							Double newPrice = sellOff.get(sdp.date) + sdp.close * s.getQty();
							sellOff.replace(sdp.date, newPrice);
						}
						else {
							sellOff.put(sdp.date, sdp.close * s.getQty());
						}
					}
				}
				if(currentValue == null) {
					portfolio.put(sdp.date, sdp.close * s.getQty());
				}
				else {
					portfolio.put(sdp.date, sdp.close * s.getQty() + currentValue);
				}
			}
			
			for (Map.Entry<Long, Double> p : portfolio.entrySet()) {
				for (Map.Entry<Long, Double> currSellOff : sellOff.entrySet()) {
					if (p.getKey() > currSellOff.getKey()) {
						p.setValue(p.getValue() + currSellOff.getValue());
					}
				}
			}
		}
		
		// view stock
		for(Stock vs : viewStocks) {
			ArrayList<StockDataPoint> stockData = vs.getData();
		}
		
	}
	
	public ArrayList<StockLine> getData() {
		ArrayList<StockLine> lines = new ArrayList<StockLine>();
		
		ArrayList<StockDataPoint> portfolioPoints = new ArrayList<>();
		for(Map.Entry<Long, Double> dataPoint : portfolio.entrySet()) {
			portfolioPoints.add(new StockDataPoint(dataPoint.getKey(), dataPoint.getValue()));
		}
		lines.add(new StockLine("Portfolio", portfolioPoints));
		
		for(Stock s : stocks) {
			lines.add(new StockLine(s.getName(), s.getData()));
		}
		
		return lines;
	}
	
	public ArrayList<StockLine> getViewOnlyData() {
		ArrayList<StockLine> lines = new ArrayList<StockLine>();
		// insert
		ArrayList<StockDataPoint> ViewPoints = new ArrayList<>();

		lines.add(new StockLine("viewStocks", ViewPoints));
		
		for(Stock vs : viewStocks) {
			lines.add(new StockLine(vs.getName(), vs.getData()));
		}
		
		return lines;
	}
	
	public int addViewStock(String stockName, LocalDate startDate, int qty) {
		int success = DatabaseJDBC.addViewStock(username, stockName, startDate, qty);
		if(success < 1) {
			System.out.println("error in adding view stock");
		}
		else {
			Stock vStock = new Stock(stockName, qty, startDate, null);
			ArrayList<StockDataPoint> stockData = vStock.getData();
			System.out.println("portfolio view stock: " + stockData);
			viewStocks.add(vStock);
		}
		return success;
	}
	
	public int addStock(String stockName, LocalDate buyDate, LocalDate sellDate, int qty) {
		int success = DatabaseJDBC.addStock(username, stockName, buyDate, sellDate, qty);
		if(success > 0) {
			
			// keeps track of selling to factor it into portfolio after sell date
			Map<Long, Double> sellOff = new TreeMap<Long, Double>();
			
			Stock stock = new Stock(stockName, qty, buyDate, sellDate);
			ArrayList<StockDataPoint> stockData = stock.getData();
			for(StockDataPoint sdp : stockData) {
				Double currentValue = portfolio.get(sdp.date);
				
				if (sellDate != null) {
					if (sdp.equals(stockData.get(stockData.size()-1))) {
						sellOff.put(sdp.date, sdp.close * qty);
					}
				}
				
				if(currentValue == null) {
					portfolio.put(sdp.date, sdp.close * qty);
				}
				else {
					portfolio.put(sdp.date, sdp.close * qty + currentValue);
				}
			}
			stocks.add(stock);
			
			for (Map.Entry<Long, Double> p : portfolio.entrySet()) {
				for (Map.Entry<Long, Double> currSellOff : sellOff.entrySet()) {
					if (p.getKey() > currSellOff.getKey()) {
						p.setValue(p.getValue() + currSellOff.getValue());
					}
				}
			}
		}
		return success;
	}
	
	public int removeViewStock(String stockName) {
		int success = DatabaseJDBC.removeViewStock(username, stockName);
		if(success > 0) {
			for(int i = 0; i < viewStocks.size(); i++) {
				if(viewStocks.get(i).getName().contentEquals(stockName)){
					viewStocks.remove(i);
				}
			}
		}
		
		return success;
	}
	
	public int removeStock(String stockName) {
		int success = DatabaseJDBC.removeStock(username, stockName);
		if(success > 0) {
			Map<Long, Double> sellOff = new TreeMap<Long, Double>();
			
			for(int i = 0; i < stocks.size(); i++) {
				if(stocks.get(i).getName().contentEquals(stockName)) {
					ArrayList<StockDataPoint> stockData = stocks.get(i).getData();
					for(StockDataPoint sdp : stockData) {
						double currentValue = portfolio.get(sdp.date);
						
						LocalDate sellDate = stocks.get(i).getSell();
						if (sellDate != null) {
							if (sdp.equals(stockData.get(stockData.size()-1))) {
								sellOff.put(sdp.date, sdp.close * stocks.get(i).getQty());
							}
						}
						
						double newValue = currentValue - (sdp.close * stocks.get(i).getQty());
						if(newValue < 0.001) {
							portfolio.remove(sdp.date);
						}
						else {
							portfolio.put(sdp.date, newValue);
						}
					}
					stocks.remove(i);
					
					for (Map.Entry<Long, Double> p : portfolio.entrySet()) {
						for (Map.Entry<Long, Double> currSellOff : sellOff.entrySet()) {
							if (p.getKey() > currSellOff.getKey()) {
								p.setValue(p.getValue() - currSellOff.getValue());
							}
						}
					}
				}
			}
		}
		return success;
	}
	
	public int getPercentChange() {
		long latestDay = 0, secondLatestDay = 0;
		for(Map.Entry<Long, Double> dataPoint : portfolio.entrySet()) {
			secondLatestDay = latestDay;
			latestDay = dataPoint.getKey();
		}
		if(secondLatestDay == 0) {
			return 0;
		}
		Double latestPortfolioAmount = portfolio.get(latestDay);
		Double secondLatestPortfolioAmount = portfolio.get(secondLatestDay);
		int percentChange = (int) Math.round((latestPortfolioAmount - secondLatestPortfolioAmount) / secondLatestPortfolioAmount * 100);
		return percentChange;
	}
	
	public Double getPortfolioPrediction(LocalDate predictionDate) {
		stocks = DatabaseJDBC.getStocks(username);
		TreeMap<Long, Double> p = (TreeMap<Long, Double>) portfolio;
		long lastDate = p.lastKey();
		Double lastClose = p.lastEntry().getValue();
		ArrayList<Entry<Double, Double>> avgReturns = new ArrayList<Entry<Double, Double>>();
		
		Double avgReturn = 1.0;
		int count = 0;
		for (Stock stock: stocks) {
			Double closeT0 = 0.0, closeT1 = 0.0;
			int numDays = 0;
			if (stock.getSell() == null) {
				ArrayList<StockDataPoint> sdps = stock.getData();
				Double stockLastClose = sdps.get(sdps.size()-1).close;
				for (StockDataPoint sdp: sdps) {
					numDays += 1;
					closeT1 = sdp.close;
					if (closeT0 != 0.0) {
						Double percentDifference = (closeT1 - closeT0) / closeT0;
						avgReturn *= (1+percentDifference);
					}
					closeT0 = closeT1;
				}
				avgReturn = Math.pow(avgReturn, 1.0/numDays) - 1;
				Map.Entry<Double,Double> s = new AbstractMap.SimpleEntry(avgReturn,(stockLastClose/lastClose));
				avgReturns.add(s);
				
			}
		}
		
		Double predictedReturn = 0.00;
		
		if (avgReturns.size() > 0) {
			System.out.println("Prediction being made");
			for (Entry<Double, Double> stocks: avgReturns) {
				predictedReturn += stocks.getKey()*stocks.getValue();
			}
			LocalDate today = LocalDate.now();
			long period = ChronoUnit.DAYS.between(today, predictionDate);
			Double prediction = ((Math.pow(1.0+predictedReturn, period))-1.0)*100.0;
			DecimalFormat df = new DecimalFormat("#.#####");      
			predictedReturn = Double.valueOf(df.format(prediction));
			
		}
		
		return predictedReturn;
	}
	
	public double getTotalValue() {
        long latestDay = 0;
        for(Map.Entry<Long, Double> dataPoint : portfolio.entrySet()) {
            latestDay = dataPoint.getKey();
        }
        if (portfolio.get(latestDay) == null) {
            return 0.0;
        }
        return portfolio.get(latestDay);
	}
	
	public ArrayList<Integer> getStockNumbers(){
		ArrayList<Stock> stock = DatabaseJDBC.getStocks(username);
		ArrayList<Integer> numShares = new ArrayList<Integer>();
		for(int i = 0; i < stock.size(); i++) {
			numShares.add(stock.get(i).getQty());
		}
		return numShares;
	}
	
	public ArrayList<Integer> getViewStockNumbers(){
		ArrayList<Stock> stock = DatabaseJDBC.getViewStocks(username);
		ArrayList<Integer> numShares = new ArrayList<Integer>();
		for(int i = 0; i < stock.size(); i++) {
			numShares.add(stock.get(i).getQty());
		}
		return numShares;
	}
}
