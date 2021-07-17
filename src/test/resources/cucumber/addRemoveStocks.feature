Feature: Ability to add or remove stocks from the portfolio
	
Scenario: Open add popup
	Given The user "user100" "pass" is logged in
	When I click the "addStockButton"
	Then I should see the "addStockButtonModal" popup

Scenario: User enters an invalid ticker symbol
	Given The user "user100" "pass" is logged in
	When I click the "addStockButton"
	And I enter "01-31-2020" to the "addStockSellDate" box
	And I enter "01-01-2020" to the "addStockBuyDate" box
	And I enter "Nonexistent" to the "addStockTickerSymbol" box
	And I enter "10" to the "addStockNumberOfShares" box
	And I click the "submitAddStock"
	And I wait 5 seconds
	Then I should see a "addStockError" element with "innerHTML" "No stock data found for Nonexistent!"
	
Scenario: User enters sell date before buy date
	Given The user "user100" "pass" is logged in
	When I click the "addStockButton"
	And I enter "10/08/002020" to the "addStockSellDate" box
	And I enter "10/08/002020" to the "addStockBuyDate" box
	And I enter "AAPL" to the "addStockTickerSymbol" box
	And I enter "10" to the "addStockNumberOfShares" box
	And I click the "submitAddStock"
	And I wait 5 seconds
	Then I should see a "addStockError" element with "innerHTML" "There was an error adding stocks!"

Scenario: User presses the cancel button and returns to the home page
	Given The user "user100" "pass" is logged in
	When I click the "addStockButton"
	And I click the "cancelAddStock"
	Then I should be on the "home" page

Scenario: User leaves stock buy date field empty.
	Given The user "user100" "pass" is logged in
	When I click the "addStockButton"
	And I enter "01-31-2020" to the "addStockSellDate" box
	And I enter "" to the "addStockBuyDate" box
	And I enter "AAPL" to the "addStockTickerSymbol" box
	And I enter "10" to the "addStockNumberOfShares" box
	And I click the "submitAddStock"
	And I wait 5 seconds
	Then I should see a "addStockBuyDateError" element with "innerHTML" "This is a required field!"

Scenario: User leaves stock sell date field empty.
	Given The user "user100" "pass" is logged in
	When I click the "addStockButton"
	And I enter "01-31-2020" to the "addStockBuyDate" box
	And I enter "AAPL" to the "addStockTickerSymbol" box
	And I enter "10" to the "addStockNumberOfShares" box
	And I click the "submitAddStock"
	And I wait 5 seconds
	Then The trend line for the stock "AAPL" should be "added"
	
Scenario: User enters correct inputs and sees the stock in their portfolio
	Given The user "user100" "pass" is logged in
	When I click the "addStockButton"
	And I enter "01-31-2021" to the "addStockSellDate" box
	And I enter "01-31-2020" to the "addStockBuyDate" box
	And I enter "AAPL" to the "addStockTickerSymbol" box
	And I enter "10" to the "addStockNumberOfShares" box
	And I click the "submitAddStock"
	And I wait 5 seconds
	Then The trend line for the stock "AAPL" should be "added"
	
Scenario: User enters correct inputs and deletes a stock from their portfolio
	Given The user "user100" "pass" is logged in
	And The user "user100" has "3" stocks of "MSFT" purchased on "01-01-2020" and sold on "01-31-2021"
	When I click the "deleteStockButton"
	And I select stock "MSFT"
	And I click the "submitDeleteStock"
	And I confirm
	And I wait 5 seconds
	And The trend line for the stock "MSFT" should be "removed"

Scenario: User is back at the home page when pressing the cancel button for delete stock
	Given The user "user100" "pass" is logged in
	When I click the "deleteStockButton"
	And I click the "cancelDeleteStock"
	Then I should be on the "home" page
	
Scenario: User does not specify the ticker to remove stock
	Given The user "user100" "pass" is logged in
   	And The user "user100" has "3" stocks of "MSFT" purchased on "01-01-2020" and sold on "01-31-2020"
   	When I click the "deleteStockButton"
   	And I click the "submitDeleteStock"
   	And I wait 5 seconds
   	Then I see the error message "This is a required field"
	
Scenario: Open bulk add popup
	Given The user "user100" "pass" is logged in
	When I click the "bulkAddStockButton"
	Then I should see the "bulkAddStockForm" popup
	
Scenario: Close bulk add popup
	Given The user "user100" "pass" is logged in
	When I click the "bulkAddStockButton"
	And I click the "cancelBulkAddStock"
	Then I should be on the "home" page
	