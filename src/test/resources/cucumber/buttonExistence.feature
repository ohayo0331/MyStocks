Feature: Existence of buttons and their functions

Scenario: Existence of Login button
	Given I am on the "login" page
	Then I should see a "signInButton" element with text "Log In"
	
Scenario: Existence of CreateAccount button on Login page
	Given I am on the "login" page
	Then I should see a "createAccountButton" element with text "Create Account"
	
Scenario: Existence of CreateAccount button on CreateAccount Page
	Given I am on the "createAccount" page
	Then I should see a "createAccountButton" element with text "Create User"
	
Scenario: Existence of Cancel button on CreateAccount Page
	Given I am on the "createAccount" page
	Then I should see a "cancelButton" element with text "Cancel"
	
Scenario: Existence of Logout button on Home Page
	Given The user "test" "pass" is logged in
	Then I should see a "logoutButton" element with text "Logout"
	
Scenario: Existence of Add Stock button on Home Page
	Given The user "test" "pass" is logged in
	Then I should see a "addStockButton" element with text "Add Stock"
	
Scenario: Existence of Delete Stock button on Home Page
	Given The user "test" "pass" is logged in
	Then I should see a "deleteStockButton" element with text "Delete Stock"
	
Scenario: Existence of Future Value button on Home Page
	Given The user "test" "pass" is logged in
	Then I should see a "futureValueButton" element with text "Future Value"

Scenario: Existence of Bulk Add Stock button on Home Page
	Given The user "test" "pass" is logged in
	Then I should see a "bulkAddStockButton" element with text "Bulk Add Stocks"

Scenario: Existence of Add Stock button when adding a stock
	Given The user "test" "pass" is logged in
	When I click the "addStockButton"
	Then I should see a "submitAddStock" element with text "Add Stock"	
	
Scenario: Existence of Cancel button when adding a stock
	Given The user "test" "pass" is logged in
	When I click the "addStockButton"
	Then I should see a "cancelAddStock" element with text "Cancel"

Scenario: Existence of Delete Stock button when deleting a stock
	Given The user "test" "pass" is logged in
	When I click the "deleteStockButton"
	Then I should see a "submitDeleteStock" element with text "Delete Stock"
	
Scenario: Existence of Cancel button when deleting a stock
	Given The user "test" "pass" is logged in
	When I click the "deleteStockButton"
	Then I should see a "cancelDeleteStock" element with text "Cancel"

Scenario: Existence of Compute Future Value button when getting prediction
	Given The user "test" "pass" is logged in
	When I click the "futureValueButton"
	Then I should see a "submitFutureValue" element with text "Compute FV"

Scenario: Existence of Cancel button when getting prediction
	Given The user "test" "pass" is logged in
	When I click the "futureValueButton"
	Then I should see a "cancelFutureValue" element with text "Cancel"
	
Scenario: Existence of Upload File button when bulk adding stock
	Given The user "test" "pass" is logged in
	When I click the "bulkAddStockButton"
	Then I should see a "submitBulkAddStock" element with text "Upload File"
	
Scenario: Existence of Cancel button when bulk adding stock
	Given The user "test" "pass" is logged in
	When I click the "bulkAddStockButton"
	Then I should see a "cancelBulkAddStock" element with text "Cancel"
	