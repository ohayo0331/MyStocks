// Globals:
var stocksChart;
var viewStocksChart;
var seriesOptions = [];
var names = [];

function updatePortfolioValue(percentChange, totalValue) {
	
	var htmlString = "";
	htmlString += totalValue;
	console.log(htmlString);
	if (percentChange >= 0) {
		htmlString += " <img src=\"css/arrow-up.png\" />";
	}
	else {
		htmlString += " <img src=\"css/arrow-down.png\" />";
	}
	htmlString += percentChange + "%";
	console.log(htmlString);
	document.querySelector("#porfolioValue").innerHTML = htmlString;
}

function updateTickerToggleButtons(lines, viewLines) {
	// Remove previously existing toggle buttons
	document.querySelectorAll('.toggle').forEach(t => t.remove());
	// Recreate new ones
	var htmlString = "<div class=\"row justify-content-center\">"
	for (var i = 0; i < lines.length; ++i) {
		var id = "switch" + i;
		htmlString += "<div class=\"col-md-12 col-sm-3 col-4 custom-control custom-switch\">";
		htmlString +=     "<input type=\"checkbox\" class=\"toggle custom-control-input\" id=\"" + id + "\"name=\"" + lines[i].name + "\" checked>";
		htmlString +=     "<label class=\"toggle-label custom-control-label\" for=\"" + id + "\">" + lines[i].name + "</label>";
		htmlString += "</div>";
	}
	for (var i = 0; i < viewLines.length; ++i) {
		var id = "switch" + (i+lines.length);
		htmlString += "<div class=\"col-md-12 col-sm-3 col-4 custom-control custom-switch\">";
		htmlString +=     "<input type=\"checkbox\" class=\"toggle custom-control-input\" id=\"" + id + "\"name=\"" + viewLines[i].name + "\" checked>";
		htmlString +=     "<label class=\"toggle-label custom-control-label\" for=\"" + id + "\">" + viewLines[i].name + "</label>";
		htmlString += "</div>";
	}
	htmlString += "</div>";
	document.getElementById("toggleContainer").innerHTML = htmlString;
}

function updateRemoveDropdown(lines) {
	var htmlString = "<option value=\"\" selected>-- Select One --</option>"
	for (var i = 0; i < lines.length; ++i) {
		if (lines[i].name == "Portfolio") {
			continue;
		}
		htmlString += "<option value=\"" + lines[i].name + "\">";
		htmlString += 	 lines[i].name;
		htmlString += "</option>";
	}	
	htmlString += "</div>";
	document.getElementById("deleteStockTickerSymbol").innerHTML = htmlString;
}


function updateRemoveViewDropdown(lines){
	var htmlString = "<option value=\"\" selected>-- Select One --</option>"
	for (var i = 0; i < lines.length; ++i) {
		if (lines[i].name == "Portfolio") {
			continue;
		}
		htmlString += "<option value=\"" + lines[i].name + "\">";
		htmlString += 	 lines[i].name;
		htmlString += "</option>";
	}	
	htmlString += "</div>";
	document.getElementById("deleteViewStockTickerSymbol").innerHTML = htmlString;
}


function updateChart(lines, numShares, viewLines) {

	seriesOptions = [];
	names = [];
	
	// clear names and series
	names = [];
	seriesOptions = [];
	
	// repopulate
	var populationName;
	var populationData;
	for (var i = 0; i < lines.length; ++i) {
		names.push(lines[i].name);
		seriesOptions.push(lines[i]);
	}
	if(viewLines.lines != 0){
		for(var i = 0; i < viewLines.length; ++i){
			names.push(viewLines[i].name);
			seriesOptions.push(viewLines[i]);
		}
	}

	// redisplay chart
	stocksChart = Highcharts.stockChart('chartContainer', {
	    xAxis: {
	        range: 3 * 30 * 24 * 3600 * 1000 // 3 months
	    },
		
	    yAxis: {
	      labels: {
	        formatter: function () {
	        	return this.value;
	        }
	      },
	      
	      plotLines: [{
	        value: 0,
	        width: 2,
	        color: 'silver'
	      }]
	    },
	    
	    navigator: {
	    	enabled: false
	    },
	    
	    legend: {
            enabled: true
        },
	
	    series: seriesOptions,

		chart: {
			backgroundColor: "#F1F3F4"
		}
	});
	
	// Label chart series elements with identifiable name
	// accessible by Cucumber.
	let seriesElements = document.querySelectorAll(".highcharts-series");
	for (let i = 0; i < seriesElements.length; i++) {
			seriesElements[i].setAttribute('seriesName', names[i]);
	}
		
	// update toggles
	let toggles = document.querySelectorAll(".toggle");
	for (let i = 0; i < toggles.length; i++) {
		// give toggles functionality to hide/show trendlines
		toggles[i].onchange = function() {
			if (this.checked && this.name != "Portfolio" && this.name != "Toggle All Off"){
				for(let j = 0; j < seriesOptions[i].data.length; j++){
					if(numShares[i-1] > 0){
						seriesOptions[0].data[j][1] += seriesOptions[i].data[j][1] * numShares[i-1];
					}
				}

				if(numShares[i-1] > 0){
					updateChart(lines, numShares, viewLines);
				}
				stocksChart.series[i].setVisible(true, false);
				if(numShares[i-1] > 0){
					for(let k = 0; k < stocksChart.series.length - 1; k++){
						if(toggles[k].checked != true){
							stocksChart.series[k].setVisible(false, false);
						}
						if(toggles[k].checked == true){
							stocksChart.series[k].setVisible(true, false);
						}
					}
				}
			}
			else if(!this.checked && this.name != "Portfolio" && this.name != "Toggle All Off"){
				for(let j = 0; j < seriesOptions[i].data.length; j++){
					if(numShares[i-1] > 0){
						seriesOptions[0].data[j][1] -= seriesOptions[i].data[j][1] * numShares[i-1];
					}

				}
				if(numShares[i-1] > 0){
					updateChart(lines, numShares, viewLines);
				}

				stocksChart.series[i].setVisible(false, false);
				if(numShares[i-1] > 0){
					for(let k = 0; k < stocksChart.series.length - 1; k++){
						if(toggles[k].checked != true){
							stocksChart.series[k].setVisible(false, false);
						}
					}
				}
			}
			else if(this.checked && this.name == "Portfolio"){
				stocksChart.series[i].setVisible(true, false);
			}
			else if(!this.checked && this.name == "Portfolio"){
				stocksChart.series[i].setVisible(false, false);
			}
			else if(this.name == "Toggle All Off" && this.checked){
				for(let j = 0; j < toggles.length; j++){
					stocksChart.series[j].setVisible(true, false);

				}
			}
			else if(this.name == "Toggle All Off" && !this.checked){
				for(let j = 0; j < toggles.length; j++){
					stocksChart.series[j].setVisible(false, false);
				}
			}
		}		
	}
	
	// give toggle labels the correct colors
	let toggleLabels = document.querySelectorAll(".toggle-label");
	for (let i = 0; i < toggleLabels.length; i++) {
		// give toggle labels colors that match trendlines
		toggleLabels[i].style.color = stocksChart.series[i].color;
	}

	// zooming in
	$("#zoomInButton").on("click", function(event) {
		if (stocksChart) {
			let day = 24 * 3600 * 1000; // ms in a day
			let currAxis = stocksChart.xAxis[0];
			let currMin = currAxis.getExtremes().min; // min displayed
			let absMin = currAxis.getExtremes().dataMin;
			let currMax = currAxis.getExtremes().max; // max displayed
			let absMax = currAxis.getExtremes().dataMax;
			let newMin = currMin + day;
			if (newMin < absMin)
				newMin = absMin;
			let newMax = currMax - day;
			if (newMax > absMax)
				newMax = absMax;
			currAxis.setExtremes(newMin, newMax);
		}
	})
	
	// zooming out
	$("#zoomOutButton").on("click", function(event) {
		if (stocksChart) {
			let day = 24 * 3600 * 1000; // ms in a day
			let currAxis = stocksChart.xAxis[0];
			let currMin = currAxis.getExtremes().min; // min displayed
			let absMin = currAxis.getExtremes().dataMin;
			let currMax = currAxis.getExtremes().max; // max displayed
			let absMax = currAxis.getExtremes().dataMax;
			let newMin = currMin - day;
			if (newMin < absMin)
				newMin = absMin;
			let newMax = currMax + day;
			if (newMax > absMax)
				newMax = absMax;
			currAxis.setExtremes(newMin, newMax);
		}
	})
}
