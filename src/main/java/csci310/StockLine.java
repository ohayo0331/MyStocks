package csci310;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class StockLine {
	private String name;
	private ArrayList<StockDataPoint> data;
	
	public StockLine(String name, ArrayList<StockDataPoint> data) {
		this.name = name;
		this.data = data;
	}
	
	public String getName() {
		return name;
	}
	
	public String getData() {
		String dataString = "";
		try {
			if(data.isEmpty()) {
				throw new Exception("No stock data found for stock " + name);
			}
			JSONObject stockDataJSONObject = new JSONObject();
			JSONArray stockDataJSONArray = new JSONArray();
			for(int i = 0; i < data.size(); i++) {
				JSONArray stockDataJSON = new JSONArray();
				stockDataJSON.put(data.get(i).date);
				stockDataJSON.put(data.get(i).close);
				stockDataJSONArray.put(stockDataJSON);
			}
			stockDataJSONObject.put("data", stockDataJSONArray);
			stockDataJSONObject.put("name", name);
			dataString = stockDataJSONObject.toString();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return dataString;
	}
}
