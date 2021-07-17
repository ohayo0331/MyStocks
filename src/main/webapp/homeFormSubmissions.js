var lines;
var viewLines;
var portfolioPercentChange;
var numShares;

function onLoad() {
	sendRequest(document.querySelector('#username').getAttribute('username'), "getData", "", "", "");
}

function sendRequest(username, action, tickerSymbol, buyDate, sellDate, numShares) {
	var xhttp = new XMLHttpRequest();
	//var startDate = buyDate;
	xhttp.open("POST", "HomeServlet", true);
	xhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	var params = "username=" + username + "&action=" + action + "&tickerSymbol=" + tickerSymbol
		+ "&buyDate=" + buyDate + "&sellDate=" + sellDate + "&numberOfShares=" + numShares;
	xhttp.send(params);
	
	xhttp.onreadystatechange = function() {
		if(xhttp.readyState == XMLHttpRequest.DONE) {
			try {
				parsedJSON = JSON.parse(xhttp.responseText);
				console.log(parsedJSON)
				if (parsedJSON.error != undefined) {
					if (action == "addStock") {
						document.querySelector("#addStockForm").reset();
						if (parsedJSON.error.includes("No stock data found")) {
							document.querySelector("#addStockError").innerHTML = "No stock data found for " + tickerSymbol + "!";
						} else {
							document.querySelector("#addStockError").innerHTML = "There was an error adding stocks!";
						}
					} else {
						document.querySelector("#deleteStockForm").reset();
						document.querySelector("#deleteStockError").innerHTML = "There was an error deleting stocks!";
					}
					return;
				} else {
					console.log("Hello error");
				}
				if (action == "addStock") {
					document.querySelector("#addStockForm").reset();
					$("#addStockButtonModal").modal('hide');
				}
				else if (action == "futureValue") {
					$("#futureValueForm").hide();
					var percentChange = parsedJSON.futureValuePercentChange;
					var htmlString = "Predicted Value: ";
					htmlString += parsedJSON.futureTotalValue;
					
					if (percentChange >= 0) {
						htmlString += " <img src=\"css/arrow-up.png\" />";
					}
					else {
						htmlString += " <img src=\"css/arrow-down.png\" />";
					}
					htmlString += percentChange + "%";
					console.log(htmlString);
					document.querySelector("#predictionResults").innerHTML = htmlString;
					document.querySelector("#predictionResults").style.fontSize = "20px";
					document.querySelector("#predictionResults").style.paddingBottom = "20px";
					$("#predictionResults").show();
					
					var feasibilityString = "Feasibility Report: Since past stock performance is not a " +
							"reliable indicator of future preformance this metric is unlikely to be within " +
							"5% of the actual price. This is due to upredictable risks that can influence stock price. " +
							" Even the best investors can not create sophisticated models to accurately predict the future performance of stocks" +
							", especially as the prediction date increases. This prediction is calculated using " +
							"avg daily returns to give an estimate into the general portfolio trajectory. But as the prediction date increases, " +
							"the reliability of this metric decreases.";
					
					document.querySelector("#feasibilityReport").innerHTML = feasibilityString;
					document.querySelector("#feasibilityReport").style.fontSize = "16px";
					document.querySelector("#feasibilityReport").style.paddingBottom = "20px";
					$("#feasibilityReport").show();
					
					var percentChange = parsedJSON.certainty;
					var certaintyString = "Confidence Metric: " + percentChange + "%"; 
					var certaintyExplanation = " Our confidence metric is to represent our certainty in being within a 5% window of" +
							" the actual future portfolio price. It is determined by factoring in the risk due to the length of time" +
							" between now " +
							"and the future date.";
					
					document.querySelector("#certainty").innerHTML = certaintyString;
					document.querySelector("#certainty").style.fontSize = "16px";
					document.querySelector("#certainty").style.paddingBottom = "16px";
					$("#certainty").show();
					
					document.querySelector("#certaintyExplanation").innerHTML = certaintyExplanation;
					document.querySelector("#certaintyExplanation").style.fontSize = "16px";
					document.querySelector("#certaintyExplanation").style.paddingBottom = "10px";
					$("#certaintyExplanation").show();
					
					
				}
				else if (action == "viewStock") {
					//viewStock action
					document.querySelector("#viewStockForm").reset();
					$("#viewStockButtonModal").modal('hide');
				}
				else if (action == "removeViewStock"){
					document.querySelector("#deleteViewStockForm").reset();
					$("#deleteViewStockButtonModal").modal('hide');
				}
				else {
					document.querySelector("#deleteStockForm").reset();
					$("#deleteStockButtonModal").modal('hide');
				}
				numShares = parsedJSON.numShares;
				lines = parsedJSON.chartData;
				viewLines = parsedJSON.chartViewData;
				if(viewLines == null){
					viewLines = [];
				}
				//lines.add("data=" + null, "name=" + null);
				viewLines.push({"data":null, "name":"Toggle All Off"});
				updateTickerToggleButtons(lines, viewLines);
				viewLines.pop();
		  		updateRemoveDropdown(lines);
		  		updateRemoveViewDropdown(viewLines);
		  		viewLines.push({"data":null, "name":"Toggle All Off"});
		  		updateChart(lines, numShares, viewLines);
				  		  		
		  		portfolioPercentChange = parsedJSON.portfolioPercentChange;
				portfolioTotalValue = parsedJSON.portfolioTotalValue;
		  		updatePortfolioValue(portfolioPercentChange, portfolioTotalValue);
		  		
			}
			catch (e) {
				console.log(xhttp.responseText);
			}
		}
	};
}

// Adding Stocks
document.querySelector('#addStockForm').onsubmit = function() {
	event.preventDefault();
	document.querySelector("#addStockError").innerHTML = "";
	if (document.querySelector('#addStockBuyDate').value.trim().length == 0) {
		document.querySelector('#addStockBuyDate').classList.add('is-invalid');
    }
	else {
    	document.querySelector('#addStockBuyDate').classList.remove('is-invalid');
    }
    if (document.querySelector('#addStockTickerSymbol').value.trim().length == 0) {
		document.querySelector('#addStockTickerSymbol').classList.add('is-invalid');
    }
	else {
    	document.querySelector('#addStockTickerSymbol').classList.remove('is-invalid');
    }
    if (document.querySelector('#addStockNumberOfShares').value.trim().length == 0) {
		document.querySelector('#addStockNumberOfShares').classList.add('is-invalid');
    }
	else {
    	document.querySelector('#addStockNumberOfShares').classList.remove('is-invalid');
    }
	if(!(document.querySelectorAll('.is-invalid').length > 0)) {
		
		var today = new Date();
		var dd = String(today.getDate()).padStart(2, '0');
		var mm = String(today.getMonth() + 1).padStart(2, '0');
		var yyyy = today.getFullYear();
		today = yyyy + '-' + mm + '-' + dd;
		
		if(document.querySelector('#addStockBuyDate').value > today) {
			document.querySelector('#addStockBuyDate').classList.add('is-invalid');
			document.querySelector('#addStockBuyDateError').innerHTML = "Buy date needs to be in the past!";
		}
		
		else if (document.querySelector('#addStockBuyDate').value > document.querySelector('#addStockSellDate').value
					&& document.querySelector('#addStockSellDate').value.trim().length > 0) {
				document.querySelector('#addStockBuyDate').classList.add('is-invalid');
				document.querySelector('#addStockBuyDateError').innerHTML = "Buy date needs to be before sell date!";
		}
		
		else {
			sendRequest(document.querySelector('#username').getAttribute('username'),
					"addStock", document.querySelector('#addStockTickerSymbol').value,
					document.querySelector('#addStockBuyDate').value, 
					document.querySelector('#addStockSellDate').value, 
					document.querySelector('#addStockNumberOfShares').value);
		}
	}
}

// View stock
document.querySelector('#viewStockForm').onsubmit = function() {
	event.preventDefault();
	document.querySelector("#viewStockError").innerHTML = "";
	if (document.querySelector('#viewStockStartDate').value.trim().length == 0) {
		document.querySelector('#viewStockStartDate').classList.add('is-invalid');
    }
	else {
    	document.querySelector('#viewStockStartDate').classList.remove('is-invalid');
    }
    if (document.querySelector('#viewStockTickerSymbol').value.trim().length == 0) {
		document.querySelector('#viewStockTickerSymbol').classList.add('is-invalid');
	}
	else {
    	document.querySelector('#viewStockTickerSymbol').classList.remove('is-invalid');
	}
	if (document.querySelector('#viewStockNumberOfShares').value.trim().length == 0) {
		document.querySelector('#viewStockNumberOfShares').classList.add('is-invalid');
    }
	else {
    	document.querySelector('#viewStockNumberOfShares').classList.remove('is-invalid');
    }
	if(!(document.querySelectorAll('.is-invalid').length > 0)) {
		
		sendRequest(document.querySelector('#username').getAttribute('username'),
				"viewStock", document.querySelector('#viewStockTickerSymbol').value,
				document.querySelector('#viewStockStartDate').value, null,
				document.querySelector('#viewStockNumberOfShares').value);
	}
}

document.querySelector('#deleteStockForm').onsubmit = function() {
	event.preventDefault();
	document.querySelector("#deleteStockError").innerHTML = "";
    if (document.querySelector('#deleteStockTickerSymbol').value.trim().length == 0) {
		document.querySelector('#deleteStockTickerSymbol').classList.add('is-invalid');
    }
	else {
    	document.querySelector('#deleteStockTickerSymbol').classList.remove('is-invalid');
    }
	if(!(document.querySelectorAll('.is-invalid').length > 0)) {
		if(confirm('Are you sure you want to delete this stock?')) {
			let result = sendRequest(document.querySelector('#username').getAttribute('username'),
					"removeStock", document.querySelector('#deleteStockTickerSymbol').value,
					"", "", "");
		}
	}
}

document.querySelector('#deleteViewStockForm').onsubmit = function() {
	event.preventDefault();
	document.querySelector("#deleteViewStockError").innerHTML = "";
    if (document.querySelector('#deleteViewStockTickerSymbol').value.trim().length == 0) {
		document.querySelector('#deleteViewStockTickerSymbol').classList.add('is-invalid');
    }
	else {
    	document.querySelector('#deleteViewStockTickerSymbol').classList.remove('is-invalid');
    }
	if(!(document.querySelectorAll('.is-invalid').length > 0)) {
		if(confirm('Are you sure you want to delete this stock from view list?')) {
			let result = sendRequest(document.querySelector('#username').getAttribute('username'),
					"removeViewStock", document.querySelector('#deleteViewStockTickerSymbol').value,
					"", "", "");
		}
	}
}

document.querySelector('#futureValueForm').onsubmit = function() {
	event.preventDefault();
    if (document.querySelector('#valueFutureDate').value.trim().length == 0) {
  
		document.querySelector('#valueFutureDate').classList.add('is-invalid');
    }
	else {
    	document.querySelector('#valueFutureDate').classList.remove('is-invalid');
    }
	if(!(document.querySelectorAll('.is-invalid').length > 0)) {
		var today = new Date();
		var dd = String(today.getDate()).padStart(2, '0');
		var mm = String(today.getMonth() + 1).padStart(2, '0');
		var yyyy = today.getFullYear();
		var nextYear = today.getFullYear() + 1;
		today = yyyy + '-' + mm + '-' + dd;
		yearFromToday = nextYear + '-' + mm + '-' + dd;
		
		if(document.querySelector('#valueFutureDate').value < today) {
			document.querySelector('#valueFutureDate').classList.add('is-invalid');
			document.querySelector('#valueFutureDateError').innerHTML = "Date needs to be in the future!";
		}
		
		else if (document.querySelector('#valueFutureDate').value > yearFromToday) {
			document.querySelector('#valueFutureDate').classList.add('is-invalid');
			document.querySelector('#valueFutureDateError').innerHTML = "Date can't exceed one year into the future!";
		}
		
		else {
			sendRequest(document.querySelector('#username').getAttribute('username'),
					"futureValue", document.querySelector('#valueFutureDate').value,
					"", "", "");
		}
	}
}

document.querySelector('#cancelAddStock').onclick = function() {
	document.querySelector("#addStockForm").reset();
}
document.querySelector('#cancelViewStock').onclick = function() {
	document.querySelector("#viewStockForm").reset();
}
document.querySelector('#cancelDeleteStock').onclick = function() {
	document.querySelector("#deleteStockForm").reset();
}
document.querySelector('#cancelDeleteViewStock').onclick = function() {
	document.querySelector("#deleteViewStockForm").reset();
}

document.querySelector('#cancelFutureValue').onclick = function() {
	document.querySelector("#futureValueForm").reset();
	$("#predictionResults").hide();
	$("#feasibilityReport").hide();
	$("#certainty").hide();
	$("#certaintyExplanation").hide();
	$("#futureValueForm").show();
}