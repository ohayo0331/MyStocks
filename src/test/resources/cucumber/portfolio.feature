Feature: Track and visualize changes in value over time of user’s portfolio

Scenario: Visualize user portfolio
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	And The user "test" has "10" stocks of "NVDA" purchased on "09-02-2020" and sold on "09-03-2020"
	And The toggle for stock "NVDA" is "on"
	And The toggle for stock "NVDA" is "off"
	When I toggle a stock "NVDA"
	Then The trend line on the graph for the stock "NVDA" should be "visible"
	
Scenario: Hide user portfolio
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	And The user "test" has "10" stocks of "NVDA" purchased on "09-02-2020" and sold on "09-03-2020"
	And The toggle for stock "NVDA" is "on"
	When I toggle a stock "NVDA"
	Then The trend line on the graph for the stock "NVDA" should be "hidden"
	
Scenario: Update Portfolio by toggling off a stock
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	And The user "test" has "10" stocks of "NVDA" purchased on "09-02-2020" and sold on "09-03-2020"
	Then The trend line on the graph for the stock "Portfolio" should be changed as well when "NVDA" is toggled
	
Scenario: Update Portfolio by toggling on a stock
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	And The user "test" has "10" stocks of "NVDA" purchased on "09-02-2020" and sold on "09-03-2020"
	When I toggle a stock "NVDA"
	And I toggle a stock "NVDA"
	Then The trend line on the graph for the stock "Portfolio" should be changed as well when "NVDA" is toggled

Scenario: Update Portfolio by toggling all off
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	And The user "test" has "10" stocks of "NVDA" purchased on "09-02-2020" and sold on "09-03-2020"
	When I toggle a stock "Toggle All Off"
	Then "Portfolio" and "NVDA" should be "hidden"

Scenario: Update Portfolio by Adding Stock
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	And The user "test" has "10" stocks of "NVDA" purchased on "09-02-2020" and sold on "09-03-2020"
	Then Adding a stock to "test" with "10" stocks of "AAPL" purchased on "09-02-2020" and sold on "09-03-2020" changes "Portfolio" line
	
Scenario: Removing a stock updates the Portfolio line
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	And The user "test" has "10" stocks of "NVDA" purchased on "09-02-2020" and sold on "09-03-2020"
	And The user "test" has "10" stocks of "AAPL" purchased on "09-02-2020" and sold on "09-03-2020"
	Then Removing the stock "AAPL" should update my "Portfolio" trendline
	