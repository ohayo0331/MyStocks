Feature: The application must be secure and protect confidentiality of users’ data

Scenario: User tries to sign in without a username 
	Given I am on the "login" page 
	When I enter "password" to the "password" box 
	And I click the "signInButton" 
	Then I should see a "usernameError" element with "innerHTML" "Please enter a username!"
	And I should be on the "login" page 
	
Scenario: User tries to sign in without a password 
	Given I am on the "login" page 
	When I enter "username" to the "username" box 
	And I click the "signInButton" 
	Then I should see a "passwordError" element with "innerHTML" "Please enter a password!"
	And I should be on the "login" page 
	
Scenario: User enters incorrect login information 
	Given I am on the "login" page 
	When I enter "username" to the "username" box 
	And I enter "wrongPassword" to the "password" box 
	And I click the "signInButton" 
	And I wait 3 seconds
	Then I should see a "invalidCredentials" element with "innerHTML" "The username and password entered was not correct."
	And I should be on the "login" page 
	

Scenario: User gets locked out after three invalid inputs
	Given The user "testUser" "password123" exists 
	And I am on the "login" page
	When I have had three invalid logins with "testUser"
	And I enter "testUser" to the "username" box
	And I enter "wrongpassword" to the "password" box
	And I click the "signInButton"
	And I wait 3 seconds
	Then I should see a "invalidCredentials" element with "innerHTML" "User testUser is locked out due to too many invalid attempts."
	
Scenario: Creating an account with missing username field 
	Given I am on the "createAccount" page 
	When I enter "password" to the "password" box 
	And I enter "password" to the "confirmPassword" box 
	And I click the "createAccountButton" 
	Then I should see a "usernameError" element with "innerHTML" "Please enter a username!"
	And I should be on the "createAccount" page 
	
Scenario: Creating an account with missing password field 
	Given I am on the "createAccount" page 
	When I enter "username" to the "username" box 
	And I click the "createAccountButton" 
	Then I should see a "passwordError" element with "innerHTML" "Please enter a password!"
	And I should be on the "createAccount" page 
	
Scenario: Creating an account with missing confirm password field 
	Given I am on the "createAccount" page 
	When I enter "username" to the "username" box 
	And I enter "password" to the "password" box
	And I click the "createAccountButton" 
	Then I should see a "confirmPasswordError" element with "innerHTML" "Please confirm password!"
	And I should be on the "createAccount" page 
	
Scenario: Creating an account with mismatched passwords 
	Given I am on the "createAccount" page 
	When I enter "username" to the "username" box 
	And I enter "password" to the "password" box 
	And I enter "differentPassword" to the "confirmPassword" box 
	And I click the "createAccountButton"
	Then I should see a "confirmPasswordError" element with "innerHTML" "Please make sure passwords match!"
	And I should be on the "createAccount" page 
	
Scenario: Creating an already existing account fails
	Given The user "testUser" "password123" exists 
	And I am on the "createAccount" page 
	When I enter "testUser" to the "username" box 
	And I enter "wrongPassword" to the "password" box 
	And I enter "wrongPassword" to the "confirmPassword" box 
	And I click the "createAccountButton" 
	And I wait 3 seconds
	Then I should see a "invalidUsername" element with "innerHTML" "Username has already been taken!" 
	And I should be on the "createAccount" page 
	
Scenario: Logout by pressing the logout button 
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	When I click the "logoutButton"
	Then I should be on the "login" page
	
Scenario: Site uses HTTPS on login
	Given I am on the "login" page
	Then I should see https in the url

Scenario: Site uses HTTPS on register
	Given I am on the "createAccount" page
	Then I should see https in the url
	
Scenario: Site uses HTTPS on home
	Given The user "test" "pass" exists
	And The user "test" "pass" is logged in
	Then I should see https in the url

Scenario: Site redirects from home to login if user isn't logged in
	Given I am on the "home" page
	Then I should be on the "login" page
	
Scenario: Site isn't accessible on port 8080 without SSL certificate
	Given I do not have an SSL certificate
	Then I cannot access the application on port "8080"