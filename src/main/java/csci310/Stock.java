package csci310;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

public class Stock {
	
	public static Boolean isValidTicker(String ticker) {
		String urlString = "https://api.tiingo.com/tiingo/daily/" + ticker
				+ "?token=128f812e2432640977225e5057358e3826354a03";
		Boolean found = false;
		
		try {
			HttpURLConnection connection = (HttpURLConnection)(new URL(urlString)).openConnection();
			connection.getInputStream(); // throws exception if error
			found = true;
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return found;
	}

	private String name;
	private int qty;
	private LocalDate buyDate;
	private LocalDate sellDate;
	private ArrayList<StockDataPoint> data;
	
	public Stock(String name_, int qty_, LocalDate buyDate_, LocalDate sellDate_) {
		name = name_;
		qty = qty_;
		buyDate = buyDate_;
		sellDate = sellDate_;
		
		data = new ArrayList<>();
		String str = getHistory();
		try {
			JSONArray arr = new JSONArray(str);
			for(int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				
				String dateString = obj.getString("date");
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				long date = sdf.parse(dateString).getTime();
				double close = obj.getDouble("close");
				
				data.add(new StockDataPoint(date, close));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String getName() {
		return name;
	}

	public int getQty() {
		return qty;
	}

	public LocalDate getBuy() {
		return buyDate;
	}

	public LocalDate getSell() {
		return sellDate;
	}
	
	public ArrayList<StockDataPoint> getData() {
		return data;
	}
	
	public String getHistory() {
		LocalDate lastDate = sellDate;
		if (lastDate == null) {
			lastDate = LocalDate.now();
		}
		String urlString = "https://api.tiingo.com/tiingo/daily/" 
				+ name
				+ "/prices?" 
				+ "startDate=" +  buyDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
				+ "&endDate=" + lastDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
				+ "&columns=close"
				+ "&token=128f812e2432640977225e5057358e3826354a03";
		
		URLConnection connection;
		String history = "";
		try {
			connection = new URL(urlString).openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
			StringBuilder responseStrBuilder = new StringBuilder();

			String str;
			while ((str = streamReader.readLine()) != null)
			    responseStrBuilder.append(str);
			history = responseStrBuilder.toString();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return history;
	}

}
