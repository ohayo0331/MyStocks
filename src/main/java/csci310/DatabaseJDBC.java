package csci310;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class DatabaseJDBC {
	
	private static String url = "jdbc:mysql://aws-mystocks.cdrxcxtcizmo.us-east-2.rds.amazonaws.com:3306/TestDB?user=admin&password=19990331&useSSL=false";
	
	// Registers a new user. Returns 1 if valid and 0 if failed
	public static int register(String username, String password) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int success = 0;
		
		try {
			conn = DriverManager.getConnection(url);
			// Check if user already exists
			if(!username.isEmpty() && !password.isEmpty()) {
				ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
				ps.setString(1, username);
				rs = ps.executeQuery();
				if(!rs.next()) {
					// Hash the password
					password = Hasher.hash(password, "SHA-256");
					// Insert the user
					ps = conn.prepareStatement("INSERT INTO Users (username, password) VALUES (?,?)");
					ps.setString(1, username);
					ps.setString(2, password);
					ps.executeUpdate();
					success = 1;
				}
			}
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} 
		
		try {
			rs.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return success;
	}
	
	// Logs in a new user. Returns 1 if valid, 0 if regular failure, -1 if the user is locked out, -2 if the password is incorrect
	public static int login(String username, String password) {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		LocalTime now = LocalTime.now();
		int success = 0;
		
		try {
			conn = DriverManager.getConnection(url);
			// Check if user exists
			if(!username.isEmpty() && !password.isEmpty()) {
				ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
				ps.setString(1, username);
				rs = ps.executeQuery();
				if(rs.next()) {
					// Check if there have been three invalid logins within the last minute
					boolean thirdLogin = false;
					if(rs.getDate("thirdLoginDate") != null) {
						LocalDate thirdLoginDate = rs.getDate("thirdLoginDate").toLocalDate();
						LocalTime thirdLoginTime = rs.getTime("thirdLoginTime").toLocalTime();
						// If third invalid login is today and you have tried to login within one minute
						if(thirdLoginDate.isEqual(LocalDate.now()) && thirdLoginTime.plusMinutes(1).isAfter(now)) {
							thirdLogin = true;
							success = -1;
						}
					}
					if(!thirdLogin) {
						// Hash the password
						password = Hasher.hash(password, "SHA-256");
						// Check if password matches username
						ps = conn.prepareStatement("SELECT * FROM Users WHERE username=? AND password=?");
						ps.setString(1, username);
						ps.setString(2, password);
						rs2 = ps.executeQuery();
						if(rs2.next()) {
							// Set first, second, and third login variables to null
							ps = conn.prepareStatement("UPDATE Users SET firstLoginDate=?, firstLoginTime=?, secondLogin=?, "
									+ "thirdLoginDate=?, thirdLoginTime=? WHERE username=?");
							ps.setDate(1, null);
							ps.setDate(2, null);
							ps.setBoolean(1, false);
							ps.setDate(4, null);
							ps.setDate(5, null);
							success = 1;
						} else {
							boolean firstLogin = false;
							// Update database with an invalid login
							if(rs.getDate("firstLoginDate") != null) {
								LocalDate firstLoginDate = rs.getDate("firstLoginDate").toLocalDate();
								LocalTime firstLoginTime = rs.getTime("firstLoginTime").toLocalTime();
								// Check if first invalid login is today and within the past minute
								if(firstLoginDate.isEqual(LocalDate.now()) && firstLoginTime.plusMinutes(1).isAfter(now)) {
									firstLogin = true;
									// If a second invalid login has occurred, update third login
									if(rs.getBoolean("secondLogin") == true) {
										ps = conn.prepareStatement("UPDATE Users SET thirdLoginDate=?, thirdLoginTime=? WHERE username=?");
										ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
										ps.setTime(2, java.sql.Time.valueOf(now));
										ps.setString(3, username);
										ps.executeUpdate();
										success = -1;
									}
									// Otherwise update second login
									else {
										ps = conn.prepareStatement("UPDATE Users SET secondLogin=? WHERE username=?");
										ps.setBoolean(1, true);
										ps.setString(2, username);
										ps.executeUpdate();
										success = -2;
									}
								}
							}
							// Update first login and set second login to false
							if(!firstLogin) {
								ps = conn.prepareStatement("UPDATE Users SET firstLoginDate=?, firstLoginTime=?, secondLogin=? WHERE username=?");
								ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
								ps.setTime(2, java.sql.Time.valueOf(now));
								ps.setBoolean(3, false);
								ps.setString(4, username);
								ps.executeUpdate();
								success = -2;
							}
						}
					}
				}
			}
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} 
		
		try {
			rs.close();
			rs2.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return success;
	}
	
	public static int addViewStock(String username, String stockName, LocalDate start, int qty) {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		LocalDate currDate = LocalDate.now();
		int success = 0;
		try {
			conn = DriverManager.getConnection(url);
			if(Stock.isValidTicker(stockName) == true) {
				if((start != null) && (start.isBefore(currDate))) {
					ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
					ps.setString(1, username);
					rs = ps.executeQuery();
					if(rs.next()) {
						int userId = rs.getInt("userId");
						// check if stock is already in view stock
						ps =conn.prepareStatement("SELECT * FROM viewOnlyStocks WHERE userId=? AND stockName=?");
						ps.setInt(1, userId);
						ps.setString(2, stockName);
						rs2 = ps.executeQuery();
						//if stock is not in user's viewOnlyStocks
						if(!rs2.next()) {
							// Insert the stock
							ps = conn.prepareStatement("INSERT INTO viewOnlyStocks (userId, stockName, quantity, startDate) VALUES (?, ?, ?, ?)");
							ps.setInt(1, userId);
							ps.setString(2, stockName);
							ps.setInt(3, qty);
							ps.setDate(4, java.sql.Date.valueOf(start));
							ps.executeUpdate();
							success = 1;
						}
					}
				}
			}
		}catch(SQLException sqle) {
			System.out.println("sql Exception: " + sqle);
			return -1;
		}
		try {
			rs.close();
			rs2.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return success;
	}
	
	// Adds stock for a user with sell-date. Returns 1 if valid and 0 if failed
	public static int addStock(String username, String stockName, LocalDate buyDate, LocalDate sellDate, int qty) {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		int success = 0;
		
		try {
			conn = DriverManager.getConnection(url);
			// Check if stock name is a valid ticker
			if(Stock.isValidTicker(stockName) == true) {
				// Check if buyDate before sellDate and quantity is greater than 0
				if((sellDate == null || (buyDate.isBefore(sellDate))) && (qty > 0)) {
					// Check if user exists
					ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
					ps.setString(1, username);
					rs = ps.executeQuery();
					if(rs.next()) {
						int userId = rs.getInt("userId");
						// Check if user already owns stock
						ps = conn.prepareStatement("SELECT * FROM Stocks WHERE userId=? AND stockName=?");
						ps.setInt(1, userId);
						ps.setString(2, stockName);
						rs2 = ps.executeQuery();
						if(!rs2.next()) {
							// Insert the stock
							ps = conn.prepareStatement("INSERT INTO Stocks (userId, stockName, quantity, buyDate, sellDate) VALUES (?, ?, ?, ?, ?)");
							ps.setInt(1, userId);
							ps.setString(2, stockName);
							ps.setInt(3, qty);
							ps.setDate(4, java.sql.Date.valueOf(buyDate));
							if(sellDate != null) {
								ps.setDate(5, java.sql.Date.valueOf(sellDate));
							}
							else {
								ps.setDate(5, null);
							}
							ps.executeUpdate();
							success = 1;
						}
					}
				}
			} else {
				success = -1;
			}
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} 
		
		try {
			rs.close();
			rs2.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return success;
	}
	
	// Removes stock for a user. Returns 1 if valid and 0 if failed.
	public static int removeViewStock(String username, String stockName) {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		int success = 0;
		
		try {
			conn = DriverManager.getConnection(url);
			// Get userId of user
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()) {
				int userId = rs.getInt("userId");
				ps = conn.prepareStatement("SELECT * FROM viewOnlyStocks WHERE userId=? AND stockName=?");
				ps.setInt(1, userId);
				ps.setString(2, stockName);
				rs2 = ps.executeQuery();
				if(rs2.next()) {
					// Delete stocks for the user
					ps = conn.prepareStatement("DELETE FROM viewOnlyStocks WHERE userId=? AND stockName=?");
					ps.setInt(1, userId);
					ps.setString(2, stockName);
					ps.executeUpdate();
					success = 1;
				}
			}
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		}
		
		try {
			rs.close();
			rs2.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
			
		return success;
	}
	
	// Removes stock for a user. Returns 1 if valid and 0 if failed.
	public static int removeStock(String username, String stockName) {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		int success = 0;
		
		try {
			conn = DriverManager.getConnection(url);
			// Get userId of user
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()) {
				int userId = rs.getInt("userId");
				ps = conn.prepareStatement("SELECT * FROM Stocks WHERE userId=? AND stockName=?");
				ps.setInt(1, userId);
				ps.setString(2, stockName);
				rs2 = ps.executeQuery();
				if(rs2.next()) {
					// Delete stocks for the user
					ps = conn.prepareStatement("DELETE FROM Stocks WHERE userId=? AND stockName=?");
					ps.setInt(1, userId);
					ps.setString(2, stockName);
					ps.executeUpdate();
					success = 1;
				}
			}
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		}
		
		try {
			rs.close();
			rs2.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return success;
	}
	
	public static ArrayList<Stock> getViewStocks(String username) {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		ArrayList<Stock> viewStocks = new ArrayList<Stock>();
		
		try {
			conn = DriverManager.getConnection(url);
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()) {
				int userId = rs.getInt("userId");
				ps = conn.prepareStatement("SELECT * FROM viewOnlyStocks WHERE userId=?");
				ps.setInt(1, userId);
				rs2 = ps.executeQuery();
				
				while(rs2.next()) {
					String stockName = rs2.getString("stockName");
					int qty = rs2.getInt("quantity");
					LocalDate startDate = rs2.getDate("startDate").toLocalDate();
					Stock vs = new Stock(stockName, qty, startDate, null);
					viewStocks.add(vs);
				}
			}
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		}
		
		try {
			rs.close();
			rs2.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return viewStocks;
	}
	
	
	// Returns all stocks for a user in the form of an ArrayList
	public static ArrayList<Stock> getStocks(String username) {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		PreparedStatement ps = null;
		ArrayList<Stock> ret = new ArrayList<Stock>();
		
		try {
			conn = DriverManager.getConnection(url);
			// Check if user exists
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()) {
				int userId = rs.getInt("userId");
				// Get all stocks that the user has bought
				ps = conn.prepareStatement("SELECT * FROM Stocks WHERE userId=?");
				ps.setInt(1, userId);
				rs2 = ps.executeQuery();
				// For each row, create a new Stock variable and add to ArrayList
				while(rs2.next()) {
					String stockName = rs2.getString("stockName");
					int qty = rs2.getInt("quantity");
					LocalDate buyDate = rs2.getDate("buyDate").toLocalDate();
					LocalDate sellDate = null;
					if(rs2.getDate("sellDate") != null) {
						sellDate = rs2.getDate("sellDate").toLocalDate();
					}
					Stock s = new Stock(stockName, qty, buyDate, sellDate);
					ret.add(s);
				}
			}
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		}
		
		try {
			rs.close();
			rs2.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return ret;
	}
	
	// Removes a user and all their stocks. Only for testing
	public static int removeUser(String username) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int success = 0;
		
		try {
			conn = DriverManager.getConnection(url);
			// Find user's userId
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()) {
				int userId = rs.getInt("userId");
				// Delete viewOnlyStocks for the user
				ps = conn.prepareStatement("DELETE FROM viewOnlyStocks WHERE userId=?");
				ps.setInt(1, userId);
				ps.executeUpdate();
				// Delete stocks for the user
				ps = conn.prepareStatement("DELETE FROM Stocks WHERE userId=?");
				ps.setInt(1, userId);
				ps.executeUpdate();
				// Delete the user
				ps = conn.prepareStatement("DELETE FROM Users WHERE username=?");
				ps.setString(1, username);
				ps.executeUpdate();
				success = 1;
			}
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} 

		try {
			rs.close();
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return success;
	}
	
	// Setter function for firstLogin. Only for testing
	public static int setFirstLogin(String username, int day, int hours) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int success = 0;
		
		try {
			conn = DriverManager.getConnection(url);
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()) {
				ps = conn.prepareStatement("UPDATE Users SET firstLoginDate=?, firstLoginTime=?, secondLogin=? WHERE username=?");
				ps.setDate(1, java.sql.Date.valueOf(LocalDate.now().minusDays(day)));
				ps.setTime(2, java.sql.Time.valueOf(LocalTime.now().minusHours(hours)));
				ps.setBoolean(3, false);
				ps.setString(4, username);
				ps.executeUpdate();
				success = 1;
			}
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} 

		try {
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return success;
	}
	
	// Setter function for thirdLogin. Only for testing
	public static int setThirdLogin(String username, int day, int hours) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int success = 0;
		
		try {
			conn = DriverManager.getConnection(url);
			ps = conn.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			if(rs.next()) {
				ps = conn.prepareStatement("UPDATE Users SET thirdLoginDate=?, thirdLoginTime=? WHERE username=?");
				ps.setDate(1, java.sql.Date.valueOf(LocalDate.now().minusDays(day)));
				ps.setTime(2, java.sql.Time.valueOf(LocalTime.now().minusHours(hours)));
				ps.setString(3, username);
				ps.executeUpdate();
				success = 1;
			}
			
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} 

		try {
			conn.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return success;
	}
	
	// Setter function for url. Only used for testing
	public static void setUrl(String url_) {
		url = url_;
	}
	
	

}
