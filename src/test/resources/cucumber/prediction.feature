Feature: Prediction of future values of the portfolio
	
Scenario: Open prediction popup
	Given The user "user100" "pass" is logged in
	When I click the predict button
	Then I should see the "futureValueButtonModal" popup
	
Scenario: Get future prediction error
	Given The user "userTest" "password" is logged in 
	When I click the "addStockButton"
	And I enter "11-05-2020" to the "addStockSellDate" box
	And I enter "03-01-2020" to the "addStockBuyDate" box
	And I enter "MSFT" to the "addStockTickerSymbol" box
	And I enter "10" to the "addStockNumberOfShares" box
	And I click the "submitAddStock"
	And I wait 5 seconds
	And I click the predict button
	And I enter "01-01-2020" to the "valueFutureDate" box
	And I click the "submitFutureValue"
	And I wait 5 seconds
	Then I should see a "valueFutureDateError" element with "innerHTML" "Date needs to be in the future!"
	
	
Scenario: Get future prediction amount from predict future portfolio value popup
	Given The user "userTest" "password" is logged in 
	When I click the "addStockButton"
	And I enter "11-05-2020" to the "addStockSellDate" box
	And I enter "03-01-2020" to the "addStockBuyDate" box
	And I enter "NVDA" to the "addStockTickerSymbol" box
	And I enter "10" to the "addStockNumberOfShares" box
	And I click the "submitAddStock"
	And I wait 5 seconds
	And I click the predict button
	And I enter "04-01-2021" to the "valueFutureDate" box
	And I click the "submitFutureValue"
	And I wait 5 seconds
	Then I should see a portfolio value within 5% of current