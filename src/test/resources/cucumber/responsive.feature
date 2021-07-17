Feature: Works on the Chrome web browser and mobile devices

Scenario: Login page fits well on chrome web browser
	Given I am on the "login" page
	And The user "test" "pass" is logged in	
	Then I should be on the "home" page
	
@mobile	
Scenario: Login page fits well on mobile devices
	Given I am on the "login" page for mobile
	And The user "test" "pass" is logged in on mobile
	Then I should be on the "home" page for mobile
	
Scenario: Create Account page fits well chrome web browser
	Given I am on the "createAccount" page
	When I enter a unique username to the "username" box 
	And I enter "password" to the "password" box 
	And I enter "password" to the "confirmPassword" box 
	And I click the "createAccountButton"
	And I wait 3 seconds
	Then I should be on the "login" page

@mobile
Scenario: Create Account page fits well on mobile devices
	Given I am on the "createAccount" page for mobile
	When I enter a unique username to the "username" box on mobile
	And I enter "password" to the "password" box on mobile
	And I enter "password" to the "confirmPassword" box on mobile
	And I click the "createAccountButton" on mobile
	And I wait 3 seconds
	Then I should be on the "login" page for mobile
	
@mobile
Scenario: User can create account on mobile devices
	Given I am on the "createAccount" page for mobile
	When I enter a unique username to the "username" box on mobile
	And I enter "password" to the "password" box on mobile
	And I enter "password" to the "confirmPassword" box on mobile
	And I click the "createAccountButton" on mobile
	And I wait 3 seconds
	Then I should be on the "login" page for mobile

@mobile
Scenario: User can login on mobile devices
	Given I am on the "login" page 
	Then I should see a "username" element with "placeholder" "Username" 
	And I should see a "password" element with "placeholder" "Password" 
	And I should see a "signInButton" element with "value" "Log In" 
	And I should see a "createAccountButton" element with "innerHTML" "Create Account"

@mobile
Scenario: User can add stock on mobile devices
	Given The user "user100" "pass" is logged in
	When I click the "addStockButton"
	And I enter "01-31-2021" to the "addStockSellDate" box
	And I enter "01-31-2020" to the "addStockBuyDate" box
	And I enter "AAPL" to the "addStockTickerSymbol" box
	And I enter "10" to the "addStockNumberOfShares" box
	And I click the "submitAddStock"
	And I wait 5 seconds
	Then The trend line for the stock "AAPL" should be "added"

@mobile
Scenario: User can delete stock on mobile devices
	Given The user "user100" "pass" is logged in
	And The user "user100" has "3" stocks of "MSFT" purchased on "01-01-2020" and sold on "01-31-2021"
	When I click the "deleteStockButton"
	And I select stock "MSFT"
	And I click the "submitDeleteStock"
	And I confirm
	And I wait 5 seconds
	And The trend line for the stock "AAPL" should be "removed"

@mobile 
Scenario: User can add view stock on mobile devices
	Given The user "user100" "pass" is logged in
	When I click the "viewStockButton"
	And I enter "01-31-2021" to the "viewStockStartDate" box
	And I enter "AAPL" to the "viewStockTickerSymbol" box
	And I click the "submitViewStock"
	And I wait 5 seconds
	Then The trend line for the stock "AAPL" should be "added" in view stock

@mobile
Scenario: User can delete view stock on mobile devices
	Given The user "user100" "pass" is logged in
	The user "user100" has "1" stocks of "MSFT" in view stock lists with start date "01-01-2020"
	When I click the "deleteViewStockButton"
	And I select stock "MSFT"
	And I click the "submitDeleteViewStock"
	And I confirm
	And I wait 5 seconds
	And The trend line for the stock "AAPL" should be "removed" in view stock
