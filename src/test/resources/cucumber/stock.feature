Feature: View and compare the historical performance of a stock
	
Scenario: Toggle menu displays on home page
	Given The user "user100" "pass" is logged in
	And The user "user100" has "3" stocks of "MSFT" purchased on "01-01-2020" and sold on "12-31-2020"
	And The user "user100" has "3" stocks of "AAPL" purchased on "01-01-2020" and sold on "12-31-2020"
	Then I should see a toggle for "MSFT"
	
Scenario: Toggle stock to not show on graph
	Given The user "user100" "pass" is logged in
	And The user "user100" has "3" stocks of "MSFT" purchased on "01-01-2020" and sold on "12-31-2020"
	And The user "user100" has "3" stocks of "AAPL" purchased on "01-01-2020" and sold on "12-31-2020"
	And The toggle for stock "MSFT" is "on"
	When I toggle a stock "MSFT"
	Then The trend line on the graph for the stock "MSFT" should be "hidden"

Scenario: Toggle stock to show on graph
	Given The user "user100" "pass" is logged in
	And The user "user100" has "3" stocks of "MSFT" purchased on "01-01-2020" and sold on "12-31-2020"
	And The user "user100" has "3" stocks of "AAPL" purchased on "01-01-2020" and sold on "12-31-2020"
	And The toggle for stock "MSFT" is "off"
	When I toggle a stock "MSFT"
	Then The trend line on the graph for the stock "MSFT" should be "visible"


Scenario: Able to open view stock popup
	Given The user "user100" "pass" is logged in
	When I click the "viewStockButton"
	Then I should see the "viewStockButtonModal" popup

Scenario: User enters an invalid ticker symbol in view stock
	Given The user "user100" "pass" is logged in
	When I click the "viewStockButton"
	And I enter "01-31-2020" to the "viewStockStartDate" box
	And I enter "Nonexistent" to the "viewStockTickerSymbol" box
	And I enter "1" to the "viewStockNumberOfShares" box
	And I click the "submitViewStock"
	And I wait 5 seconds
	Then I should see a "viewStockError" element with "innerHTML" "Invalid Ticker"

Scenario: User enters start date after Today in view stock
	Given The user "user100" "pass" is logged in
	When I click the "viewStockButton"
	And I enter "10-08-002021" to the "viewStockStartDate" box
	And I enter "AAPL" to the "viewStockTickerSymbol" box
	And I enter "1" to the "viewStockNumberOfShares" box
	And I click the "submitViewStock"
	And I wait 5 seconds
	Then I should see a "viewStockError" element with "innerHTML" "Invalid start date error"
	
Scenario: User leaves start date empty in view stock
	Given The user "user100" "pass" is logged in
	When I click the "viewStockButton"
	And I enter "AAPL" to the "viewStockTickerSymbol" box
	And I enter "1" to the "viewStockNumberOfShares" box
	And I click the "submitViewStock"
	And I wait 5 seconds
	Then I should see a "viewStockError" element with "innerHTML" "Start date is a required field"

Scenario: User leaves ticker empty in view stock
	Given The user "user100" "pass" is logged in
	When I click the "viewStockButton"
	And I enter "10/08/002021" to the "viewStockStartDate" box
	And I enter "1" to the "viewStockNumberOfShares" box
	And I click the "submitViewStock"
	And I wait 5 seconds
	Then I should see a "addStockError" element with "innerHTML" "Ticker is a required field"

Scenario: User presses the cancel button and returns to the home page from view stock pop up
	Given The user "user100" "pass" is logged in
	When I click the "viewStockButton"
	And I click the "cancelViewStock"
	Then I should be on the "home" page

Scenario: User presses the cancel button and returns to the home page from delete view stock pop up
	Given The user "user100" "pass" is logged in
	When I click the "deleteViewStockButton"
	And I click the "cancelDeleteViewStock"
	Then I should be on the "home" page

Scenario: User does not specify the stock ticker to remove from view stock
	Given The user "user100" "pass" is logged in
   	And The user "user100" has "1" stocks of "MSFT" in view stock lists with start date "01-01-2020"
   	When I click the "deleteViewStockButton"
   	And I click the "submitDeleteViewStock"
   	And I wait 5 seconds
   	Then I see the error message "Ticker is a required field"

Scenario: User enters corrects inputs in view stock and sees the stock in their portfolio
	Given The user "user100" "pass" is logged in
	When I click the "viewStockButton"
	And I enter "01-31-2021" to the "viewStockStartDate" box
	And I enter "AAPL" to the "viewStockTickerSymbol" box
	And I enter "1" to the "viewStockNumberOfShares" box
	And I click the "submitViewStock"
	And I wait 5 seconds
	Then The trend line for the stock "AAPL" should be "added" in view stocks list


Scenario: User enters corrects inputs and deletes a stock from view stock chart
	Given The user "user100" "pass" is logged in
	And The user "user100" has "1" stocks of "MSFT" in view stock lists with start date "01-01-2020"
	When I click the "deleteViewStockButton"
	And I select stock "MSFT"
	And I click the "submitDeleteViewStock"
	And I confirm
	And I wait 5 seconds
	Then The trend line for the stock "MSFT" should be "removed" in view stocks list

	
Scenario: User should S&P 500 line after register and login as a default
	Given The user is registered with id "user100" and password "pass"
	When the user logs in with "user100" "pass"
	Then The trend line for the stock "ES" should be "added" in view stocks list
	