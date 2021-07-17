Feature: Users must be able to create an account in the system

Scenario: Create Accounts’ requirements 
	Given I am on the "createAccount" page 
	Then I should see a "username" element with "placeholder" "Username" 
	And I should see a "password" element with "placeholder" "Password" 
	And I should see a "confirmPassword" element with "placeholder" "Password" 
	And I should see a "createAccountButton" element with "value" "Create Account" 
	
Scenario: Login page’s requirements 
	Given I am on the "login" page 
	Then I should see a "username" element with "placeholder" "Username" 
	And I should see a "password" element with "placeholder" "Password" 
	And I should see a "signInButton" element with "value" "Log In" 
	And I should see a "createAccountButton" element with "innerHTML" "Create Account" 

Scenario: Pressing the create account button from the login page
	Given I am on the "login" page 
	When I click the "createAccountButton" 
	Then I should be on the "createAccount" page 
	
Scenario: Pressing Cancel button
	Given I am on the "createAccount" page 
	When I click the "cancelButton" 
	Then I should be on the "login" page
	
Scenario: Creating account with valid entries to all required fields 
	Given I am on the "createAccount" page 
	When I enter a unique username to the "username" box 
	And I enter "password" to the "password" box 
	And I enter "password" to the "confirmPassword" box 
	And I click the "createAccountButton"
	And I wait 3 seconds
	Then I should be on the "login" page 
	
Scenario: User enters the correct login inputs 
	Given The user "testUser" "password123" exists 
	And I am on the "login" page 
	When I enter "testUser" to the "username" box 
	And I enter "password123" to the "password" box 
	And I click the "signInButton"
	And I wait 3 seconds
	Then I should be on the "home" page
	
Scenario: User enters unregistered username
	Given I am on the "login" page
	When I enter "unregisteredUser123" to the "username" box
	And I enter "unregistered" to the "password" box
	And I click the "signInButton"
	And I wait 3 seconds
	Then I should see the "invalid credential" error 

Scenario: User enters wrong password
	Given The user "testUser" "password123" exists
	And I am on the "login" page
	When I enter "testUser" to the "username" box
	And I enter "wrongPW" to the "password" box
	And I click the "signInButton"
	And I wait 3 seconds
	Then I should see the "invalid credential" error

Scenario: User enters password not matching during the registration
	Given I am on the "createAccount" page
	When I enter a unique username to the "username" box 
	And I enter "password" to the "password" box 
	And I enter "different" to the "confirmPassword" box 
	And I click the "createAccountButton"
	And I wait 3 seconds
	Then I should see the "confirm register" error
	 