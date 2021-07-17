<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="csci310.DatabaseJDBC"%>
<%@ page import="csci310.Stock"%>
<%@ page import="csci310.StockLine"%>
<%@ page import="csci310.Portfolio"%>
<%@ page import="java.time.LocalDate"%>
<!DOCTYPE html>
<html>
  <head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta charset= "utf-8">
	<!-- Highcharts -->
	<link rel="stylesheet" type="text/css" href="https://code.highcharts.com/css/stocktools/gui.css">
	<link rel="stylesheet" type="text/css" href="https://code.highcharts.com/css/annotations/popup.css">
	
	<!-- Bootstrap CSS -->
   	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
   	
   	<!-- Google Fonts -->
   	<link href="https://fonts.googleapis.com/css2?family=Lato:wght@100;300;400&display=swap" rel="stylesheet">
	
	<!-- Font Awesome Kit -->
	<script src="https://kit.fontawesome.com/90477c1bbb.js" crossorigin="anonymous"></script>
		
	<!-- Our Stylesheets -->
	<link rel = "stylesheet" href = "css/style.css"> <!-- shared across pages -->
	<link rel="stylesheet" type="text/css" href="css/home.css"> <!-- specific to home.jsp -->
    
    <!-- Load jQuery -->
	<script
		  src="http://code.jquery.com/jquery-3.4.1.min.js"
		  integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
		  crossorigin="anonymous">
	</script>
	
    <title>MyStocks | Home</title>
  </head>
  
  <body onload="onLoad()">
  	<!-- reference to three.js file -->
	<script  src="./three.js"></script>
  	<%
		// Check if user is logged in, and if so, get user's stocks.
		String username = (String) session.getAttribute("User");
  		if (username == null) { %>
  			<script>window.location='login.jsp';</script>
  	<% } %>
	<!-- hidden div to access username from JS -->
	<div id="username" username="<%= username %>"></div>
	<!-- banner on the top of the page -->	
	<div class="header jumbotron jumbotron-fluid">
		<!-- <div class="header"> -->
		<div class="row">
			<div class="col-8">
				<h1 class="display-4" style="text-align: left; padding-left: 50px;">MyStocks</h1>
			</div>
			<div class="col-4">
				<h1 class="display-4" style="text-align:right; padding-right: 120px;">Hi, <%= username %></h1>
			</div>
		</div>
		<button class="btn btn-primary logoutButton" id="logoutButton" type = "button" onclick="logout()">Logout</button>
	</div>
	
	<!-- Portfolio Value -->
	<div class="container">
		<div class="row justify-content-center">
	      	<div class="col-12 align-self-left">
	      		<div id="portfolioValueLabel" style="color: white;">Portfolio Value: <span id="porfolioValue"></span></div>
	      	</div>
	    </div>
	</div>
	
	<!-- add/remove stock buttons -->
	<div class="top-buffer container">
		<div class="row justify-content-center">
			<div class="col-12 text-right">
				<!-- view stock button --> 
			 	<button id="viewStockButton" class="btn btn-primary blue-button viewStockButton" type = "button" data-toggle="modal" data-target="#viewStockButtonModal">Add View Stock</button>
				<!-- remove stock button -->
				<button id="deleteViewStockButton" class="btn btn-primary blue-button deleteViewStockButton" type = "button" data-toggle="modal" data-target="#deleteViewStockButtonModal">Delete View Stock</button>
				 <!-- add stock button --> 
			 	<button id="addStockButton" class="btn btn-primary blue-button addStockButton" type = "button" data-toggle="modal" data-target="#addStockButtonModal">Add Stock</button>
				<!-- remove stock button -->
				<button id="deleteStockButton" class="btn btn-primary blue-button deleteStockButton" type = "button" data-toggle="modal" data-target="#deleteStockButtonModal">Delete Stock</button>
				<!-- FV button -->  
 				<button id="futureValueButton" class="btn btn-primary blue-button futureValueButton" type = "button" data-toggle="modal" data-target="#futureValueButtonModal">Future Value</button>
			 	
			 	<!-- add stocks in bulk button --> 
			 	<button id="bulkAddStockButton" class="btn btn-primary blue-button bulkAddStockButton" type = "button" data-toggle="modal" data-target="#bulkAddStockButtonModal">Bulk Add Stocks</button>
			
			</div>
		</div>
	</div> 

<!-- pop up dialog which asks user to bulk add stocks  -->  
  <div class = "modal fade1" id="bulkAddStockButtonModal">
      <div class = "modal-dialog">
          <div class = "modal-content">
              <div class = "modal-body text-center">
                      <form id="bulkAddStockForm">
                            <div class="form-group" style="text-align: left">
                                  <label for="cvsFile">Upload a File (only .csv files are accepted)</label>
    							  <input type="file" accept=".csv" class="form-control-file" id="addBulkStockFile" required>
                            </div>                         
                            <button id="submitBulkAddStock" type="submit" class="btn btn-primary yellow-button mb-2">Upload File</button>
                      </form>
                     <!-- if cancel, close pop up and go to home page -->  
                    <button id="cancelBulkAddStock" type="button" class="btn btn-secondary grey-button" data-dismiss="modal">Cancel</button>
               </div>   
          </div>
      </div>
  </div>

  <!-- pop up dialog which asks user to input future value -->  
  <div class = "modal fade1" id="futureValueButtonModal">
      <div class = "modal-dialog">
          <div class = "modal-content">
              <div class = "modal-body text-center">
                      <form id="futureValueForm">
                            <div class="form-group" style="text-align: left">
                                  <label for="valueFutureDate" class="col-form-label">Future Value Date:</label>
								  <input type="date" class="form-control" id="valueFutureDate"/>
								  <small id="valueFutureDateError" class="error invalid-feedback">This is a required field!</small>
                            </div>                         
                            <button id="submitFutureValue" type="submit" class="btn btn-primary yellow-button mb-2">Compute FV</button>
                      </form>
                     <!-- if cancel, close pop up and go to home page -->  
                     <div id="predictionResults"></div>
                     <p id="feasibilityReport"></p>
                     <p id="certainty"></p>
                     <p id="certaintyExplanation"></p>
                    <button id="cancelFutureValue" type="button" class="btn btn-secondary grey-button" data-dismiss="modal">Cancel</button>
               </div>   
          </div>
      </div>
  </div>

  <!-- pop up dialog which asks user to view stock -->  
  <div class = "modal fade1" id="viewStockButtonModal">
      <div class = "modal-dialog">
          <div class = "modal-content">
              <div class = "modal-body text-center">
                     <!-- if add stock, show user form info on pop up -->  
                      <form id="viewStockForm">
                      		<div class="form-group">
				              <small id="viewStockError" class="error"></small>
				            </div>
                            <div class="form-group" style="text-align: left">
                                  <label for="viewStockTickerSymbol" class="col-form-label">Ticker Symbol:</label>
                                  <input type="text" class="form-control" placeholder="ex: AAPL" id="viewStockTickerSymbol">
                                  <small id="viewStockTickerError" class="error invalid-feedback">Ticker is a required field!</small>
                            </div>
                            <div class="form-group" style="text-align: left">
                                  <label for="viewStockNumberOfShares" class="col-form-label">Number of Shares:</label>
                                  <input type="number" min="0" class="form-control" placeholder = "1" id="viewStockNumberOfShares">
                                  <small id="viewStockNumberOfSharesError" class="error invalid-feedback">Number is a required field!</small>
                            </div>
                            <div class="form-group" style="text-align: left">
                                  <label for="viewStockStartDate" class="col-form-label">View Stock Start Date:</label>
                                  <input type="date" class="form-control" id="viewStockStartDate"/> 
                            </div>
                            <button id="submitViewStock" type="submit" class="btn btn-primary yellow-button mb-2">View Stock</button>
                      </form>
                     <!-- if cancel, close pop up and go to home page -->  
                    <button id="cancelViewStock" type="button" class="btn btn-secondary grey-button" data-dismiss="modal">Cancel</button>
               </div>   
          </div>
      </div>
  </div>
  <!-- pop up dialog which asks user to delete stock or cancel -->  
  <div class = "modal fade2" id="deleteViewStockButtonModal">
      <div class = "modal-dialog">
          <div class = "modal-content">
              <div class = "modal-body text-center">
                     <!-- if delete stock, show user form info on pop up -->  
                      <form id="deleteViewStockForm">
                      		<div class="form-group">
				              <small id="deleteViewStockError" class="error"></small>
				            </div>
                            <div class="form-group" style="text-align: left">
                                  <label for="deleteViewStockTickerSymbol" class="col-form-label">Ticker Symbol:</label>
                                  <select id="deleteViewStockTickerSymbol" class="form-control">
										<option value="" selected>-- Select One --</option>
									</select>
                                  <small id="deleteViewStockTickerError" class="error invalid-feedback">This is a required field!</small>
                            </div>
                            <button id="submitDeleteViewStock" type="submit" class="btn btn-primary yellow-button mb-2">Delete Stock</button>
                      </form>
                     <!-- if cancel, close pop up and go to home page -->  
                    <button id="cancelDeleteViewStock" type="button" class="btn btn-secondary grey-button" data-dismiss="modal">Cancel</button>
               </div>
          </div>
      </div>
  </div>
  <!-- pop up dialog which asks user to add stock or cancel -->  
  <div class = "modal fade1" id="addStockButtonModal">
      <div class = "modal-dialog">
          <div class = "modal-content">
              <div class = "modal-body text-center">
                     <!-- if add stock, show user form info on pop up -->  
                      <form id="addStockForm">
                      		<div class="form-group">
				              <small id="addStockError" class="error"></small>
				            </div>
				            <div class="form-group" style="text-align: left">
                                  <label for="addStockTickerSymbol" class="col-form-label">Ticker Symbol:</label>
                                  <input type="text" class="form-control" placeholder="ex: AAPL" id="addStockTickerSymbol">
                                  <small id="addStockTickerError" class="error invalid-feedback">This is a required field!</small>
                            </div>
                            <div class="form-group" style="text-align: left">
                                  <label for="addStockNumberOfShares" class="col-form-label">Number of Shares:</label>
                                  <input type="number" min="0" class="form-control" placeholder = "5" id="addStockNumberOfShares">
                                  <small id="addStockNumberOfSharesError" class="error invalid-feedback">This is a required field!</small>
                            </div>
                            <div class="form-group" style="text-align: left">
                                  <label for="addStockBuyDate" class="col-form-label">Stock Buy Date:</label>
                                  <input type="date" class="form-control" id="addStockBuyDate"/>
                                  <small id="addStockBuyDateError" class="error invalid-feedback">This is a required field!</small>
                            </div>
                            <div class="form-group" style="text-align: left">
                                  <label for="addStockSellDate" class="col-form-label">Stock Sell Date:</label>
                                  <input type="date" class="form-control" id="addStockSellDate"/> 
                            </div>
                            <button id="submitAddStock" type="submit" class="btn btn-primary yellow-button mb-2">Add Stock</button>
                      </form>
                     <!-- if cancel, close pop up and go to home page -->  
                    <button id="cancelAddStock" type="button" class="btn btn-secondary grey-button" data-dismiss="modal">Cancel</button>
               </div>   
          </div>
      </div>
  </div>

 <!-- pop up dialog which asks user to delete stock or cancel -->  
  <div class = "modal fade2" id="deleteStockButtonModal">
      <div class = "modal-dialog">
          <div class = "modal-content">
              <div class = "modal-body text-center">
                     <!-- if delete stock, show user form info on pop up -->  
                      <form id="deleteStockForm">
                      		<div class="form-group">
				              <small id="deleteStockError" class="error"></small>
				            </div>
                            <div class="form-group" style="text-align: left">
                                  <label for="deleteStockTickerSymbol" class="col-form-label">Ticker Symbol:</label>
                                  <select id="deleteStockTickerSymbol" class="form-control">
										<option value="" selected>-- Select One --</option>
									</select>
                                  <small id="deleteStockTickerError" class="error invalid-feedback">This is a required field!</small>
                            </div>
                            <button id="submitDeleteStock" type="submit" class="btn btn-primary yellow-button mb-2">Delete Stock</button>
                      </form>
                     <!-- if cancel, close pop up and go to home page -->  
                    <button id="cancelDeleteStock" type="button" class="btn btn-secondary grey-button" data-dismiss="modal">Cancel</button>
               </div>
          </div>
      </div>
  </div>
	
	
	<!-- Stock Graph -->
	<div class="top-buffer container">
		<div class="row justify-content-center">
				<div class="col-md-2 align-self-center">
					<div id="toggleContainer" class="container">
						<div class="row justify-content-center">
							
						</div>
					</div>
				</div>
	      		<div class="col-md-10 col-12 align-self-center">
	      			<div id="chartContainer"></div>  			
	      		</div>
	      	</div>
	</div>

	<!-- zoom in/out buttons -->
	<div class="top-buffer container">
		<div class="row justify-content-center">
			<div class="col-12 text-right">
				<!-- zoom in button --> 
			 	<button id="zoomInButton" class="btn btn-primary blue-button" type="button"><i class="fas fa-search-plus"></i></button>
				<!-- zoom out button -->
				<button id="zoomOutButton" class="btn btn-primary blue-button" type="button"><i class="fas fa-search-minus"></i></button>
			</div>
		</div>
	</div>
	
	<!-- JAVASCRIPT CODE -->
	<script src="homeFormSubmissions.js"></script>
	<script src="homeUpdates.js"></script>
		
		<script type="text/javascript">
	       var timeIdle = 0; //idle time set to 0
	       $(document).ready(function () { //executed as soon as the page is loaded
	           var intervalIdleTime = setInterval(incrementTime, 1000); //set the interval to 1 sec
	           $(this).mousemove(function (e) { //when the mouse moves
	               timeIdle = 0;                //set idle time to 0
	           });
	           $(this).keypress(function (e) { //e is for event and this is when you press your keyboard keys 
	               timeIdle = 0;               //set idle time to 0
	           });
	       });
	
	       function incrementTime() { 
	           timeIdle = timeIdle + 1; //every one second we call this function to check and increment it by 1
	           if (timeIdle > 120) { //if idle time if more than 120 seconds
	        	   logout(); //then auto log out the user
	           }
	       }
	    </script> 
     
		<!-- Highcharts -->
		<script src="https://code.highcharts.com/stock/highstock.js"></script>
		<script src="https://code.highcharts.com/stock/modules/data.js"></script>
		
		<script src="https://code.highcharts.com/stock/indicators/indicators-all.js"></script>
		<script src="https://code.highcharts.com/stock/modules/drag-panes.js"></script>
		
		<script src="https://code.highcharts.com/modules/price-indicator.js"></script>
		<script src="https://code.highcharts.com/modules/full-screen.js"></script>
		
		<!-- Logout functionality -->
		<script>
			function logout() {
				<% 
					// Make sure user and portfolio are cleaned when logged out
					session.setAttribute("User", null); 
					session.removeAttribute("User");
					session.setAttribute("MyPortfolio", null);
					session.removeAttribute("MyPortfolio");
				%>
				window.location='login.jsp'
			}
		</script>

  <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
  </body>
</html>