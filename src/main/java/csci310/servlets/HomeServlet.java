package csci310.servlets;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import csci310.Portfolio;
import csci310.StockLine;


public class HomeServlet extends HttpServlet {
	

	private static final long serialVersionUID = 1L;

	public HomeServlet() {
		super();
    }

	/**
	 * @throws IOException 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String action = request.getParameter("action");
		String name = request.getParameter("tickerSymbol");
		Portfolio p = new Portfolio(username);
		Integer result = 1;
//		Integer viewResult = 1;
		ArrayList<String> responseList = new ArrayList<>();
		
		if (action.equals("addStock")) {
			String buyDateString = request.getParameter("buyDate");
		    LocalDate buyDate = LocalDate.parse(buyDateString);
		    
		    String sellDateString = request.getParameter("sellDate");
		    LocalDate sellDate = null;
		    if(sellDateString.length() > 0) {
		    	sellDate = LocalDate.parse(sellDateString);
		    }
		    
			int numShares = Integer.parseInt(request.getParameter("numberOfShares"));
	
			result = p.addStock(name, buyDate, sellDate, numShares);
			
			
			if (result == -1) {
				responseList.add("\"error\":\"No stock data found for" + name + "\"");
			} else if (result < 1) {
				responseList.add("\"error\":\"Error adding stock!\"");
			}
		}
		// Otherwise, we are removing stock(s)
		else if (action.equals("removeStock")) {
			result = p.removeStock(name);
			if(result < 1) {
				responseList.add("\"error\":\"Error removing stock!\"");
			}
		}
		
		// Get predictions
		else if (action.equals("futureValue")) {
			String valueFutureDateString = name;
			LocalDate valueFutureDate = LocalDate.parse(valueFutureDateString);
			System.out.println("future Date" + valueFutureDate);
			Double prediction  = p.getPortfolioPrediction(valueFutureDate);
			System.out.println("prediction: " + prediction);
			responseList.add("\"futureValuePercentChange\":" + prediction);
			
			Double predictionValue = p.getTotalValue();
			LocalDate today = LocalDate.now();
			long period = ChronoUnit.DAYS.between(today, valueFutureDate);

			double certainty = 0.0;
			if (prediction != 0.0) {
				predictionValue = p.getTotalValue()* (1+(prediction/100));
				DecimalFormat df = new DecimalFormat("#.##");      
				predictionValue = Double.valueOf(df.format(predictionValue));
				
				// feasibility certainty
				double test = (1.0/365.0 * Double.valueOf(period));
				System.out.println("test: " + test);
				
				certainty = (.85 - test);
				if (certainty < .01) {
					certainty = .01;
				}
				certainty *= 100;
			}
			DecimalFormat df = new DecimalFormat("#.##");      
			certainty = Double.valueOf(df.format(certainty));
			System.out.println("certainty: " + certainty);
			
			responseList.add("\"certainty\":" + certainty);
			
			responseList.add("\"futureTotalValue\":" + predictionValue);
		}
		else if (action.equals("viewStock")) {
			String startDateString = request.getParameter("buyDate");

			LocalDate startDate = LocalDate.parse(startDateString);
		    
			int numShares = Integer.parseInt(request.getParameter("numberOfShares"));
	
		    if(startDate.isAfter(LocalDate.now())) {
		    	responseList.add("\"error\":\"Invalid Start Date!\"");
		    }else{
		    	result = p.addViewStock(name, startDate, numShares);
		    	
		    	if(result < 1) {
		    		responseList.add("\"error\":\"Error adding view stock!\"");
		    	}
		    }
		}
		else if (action.equals("removeViewStock")){
			result = p.removeViewStock(name);
			if(result < 1) {
				responseList.add("\"error\":\"Error removing view stock!\"");
			}
		}
		
		// On success, return json of values to update
		if (result == 1) {
			
			// Chart data
			String linesJSON = "\"chartData\":[";
			ArrayList<StockLine> lines = p.getData();
			for(StockLine line : lines) {
				linesJSON += line.getData() + ",";
			}

			if(lines.isEmpty()) {
				linesJSON = linesJSON.substring(0, linesJSON.length()) + "]";
			}
			else {
				linesJSON = linesJSON.substring(0, linesJSON.length()-1) + "]";
			}
			responseList.add(linesJSON);
			
			String linesJSON2 = "\"chartViewData\":[";
			ArrayList<StockLine> lines2 = p.getViewOnlyData();
			//lines2.removeIf(null);

			if(lines2.size() > 1) {
//				for(StockLine line : lines2) {
//					linesJSON2 += line.getData() + ",";
//				}
				for(int i = 1; i < lines2.size(); i++) {
					if(!lines2.get(i).getData().contains("No stock")) {
						linesJSON2 += lines2.get(i).getData() + ",";
					}
				}
			}
			if(lines2.size() <= 1) {
				linesJSON2 = linesJSON2.substring(0, linesJSON2.length()) + "]";
			}
			else {
				linesJSON2 = linesJSON2.substring(0, linesJSON2.length()-1) + "]";
			}
			responseList.add(linesJSON2);
			
			String numSharesJson = "\"numShares\":[";
			ArrayList<Integer> numShares = p.getStockNumbers();

			if(!numShares.isEmpty()) {
				for(Integer nums : numShares) {
					numSharesJson += nums + ",";
				}
			}

			ArrayList<Integer> numViewShares = p.getViewStockNumbers();
			if(!numViewShares.isEmpty()) {
				for(Integer nums : numViewShares) {
					numSharesJson += (-nums) + ",";
				}
			}

			if(numShares.isEmpty() && numViewShares.isEmpty()) {
				numSharesJson = numSharesJson.substring(0, numSharesJson.length()) + "]";
			}
			else {
				numSharesJson = numSharesJson.substring(0, numSharesJson.length()-1) + "]";
			}
			
			responseList.add(numSharesJson);
			
			// Portfolio percentage
			responseList.add("\"portfolioPercentChange\":" + p.getPercentChange());
			
			// Portfolio total value
			responseList.add("\"portfolioTotalValue\":" + p.getTotalValue());
			
		}
//		if(viewResult == 1) {
//			String linesJSON2 = "\"chartViewData\":[";
//			ArrayList<StockLine> lines2 = p.getViewOnlyData();
//			for(StockLine line : lines2) {
//				linesJSON2 += line.getData() + ",";
//			}
//			linesJSON2 = linesJSON2.substring(0, linesJSON2.length()-1) + "]";
//			responseList.add(linesJSON2);
//		}
		

		// view stock add and remove
//		if (viewResult == 1) {
//			// Chart data
//			String linesJSON = "\"viewChartData\":[";
//			ArrayList<StockLine> lines = p.getViewOnlyData();
//			System.out.println("lines: " + lines);
//			for(StockLine line : lines) {
//				System.out.println("HomeServlet: view do get: line name= " + line.getName());
//				System.out.println("HomeServlet: view do get: line data= " + line.getData());
//				linesJSON += line.getData() + ",";
//			}
//			linesJSON = linesJSON.substring(0, linesJSON.length()-1) + "]";
//			responseList.add(linesJSON);
//		}
		
		String responseJSON = "{";
		for(String responseItem : responseList) {
			responseJSON += responseItem + ",";
		}
		responseJSON = responseJSON.substring(0, responseJSON.length()-1) + "}";
		
		response.getWriter().println(responseJSON);		
	}

	/**
	 * @throws IOException 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}
	
}
