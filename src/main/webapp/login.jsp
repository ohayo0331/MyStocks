<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- // three.js - https://github.com/mrdoob/three.js
var THREE=THREE||{REVISION:"56"} -->
<!DOCTYPE html>
<html>
	<head>
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js">
		 </script>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta charset= "utf-8">
		
		<!-- Bootstrap CSS -->
    	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    	
    	<!-- Google Fonts -->
    	<link href="https://fonts.googleapis.com/css2?family=Lato:wght@100;300;400&display=swap" rel="stylesheet">
		
		<!-- Our Stylesheets -->
		<link rel = "stylesheet" href = "css/style.css"> <!-- shared across pages -->
		<link rel = "stylesheet" href = "css/login.css"> <!-- specific to login.jsp -->
		
		<title>MyStocks | Login </title>
	</head>
	
	<body>
		<!-- reference to three.js file -->
		<script  src="./three.js"></script>
		<!-- banner on the top of the page -->	
		<div class="header jumbotron jumbotron-fluid">
		  	<h1 class="display-4" style="text-align: left; padding-left: 50px;">MyStocks</h1>
		</div>
		
		
		<div class="container">
			<div class="row justify-content-center">
				<div class="col-md-6 align-self-center">
					<!-- user log in text -->	
					<h1 class="form-heading text-center"> User Login </h1>
				</div>
			</div>
			
			<div class="row justify-content-center">
        		<div class="col-md-6 align-self-center">
					 <form id="loginForm" autocomplete="off" method="POST">
					 	<div class="form-group">
			              <small id="invalidCredentials" class="error">
			              </small>
			            </div>
			            <!-- username text box -->
			            <div class="form-group">
			              <i class = "icon fa fa-user" aria-hidden="true" style="color: white;"></i>		             
			              <label for="username" style="color: white">Username</label>
			              <input id="username" name="username" type="text" placeholder="Username" class="form-control"/>
			              <small id="usernameError" class="error invalid-feedback">Please enter a username!</small>
			            </div>
			            
			            <!-- password text box-->  
			            <div class="form-group"> 
			              <i class = "icon fa fa-lock" aria-hidden="true" style="color: white;"></i>			             
			              <label for="password" style="color: white">Password</label>
			              <input id="password" name="password" type="password" placeholder="Password" class="form-control"/> 
			              <small id="passwordError" class="error invalid-feedback">Please enter a password!</small>
			            </div>	
			            
			            <!-- login button -->  	
						<div class="row">
							<div class="col text-center">
							    <!-- sign in button --> 
								<button class="submitButton btn btn-primary yellow-button loginButton" id="signInButton" type="submit" value="Log In">
									Log In
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>

			<div class="row justify-content-center top-buffer">
				<div class="col-md-6 align-self-center">
				    <!-- create account button --> 
					<button class="switchPageButton btn btn-primary cancelButton grey-button" id="createAccountButton" type="button" onclick="window.location='createAccount.jsp'">
						Create Account
					</button>
				</div>
			</div>
		</div>
		
		<script>
			document.querySelector('form').onsubmit = function() {
				document.querySelector("#invalidCredentials").innerHTML = "";
				event.preventDefault();
				
				// Check if username or password are empty
				if (document.querySelector('#username').value.trim().length == 0) {
					document.querySelector('#username').classList.add('is-invalid');
				}
				else {
		        	document.querySelector('#username').classList.remove('is-invalid');
		        }
				
				if(document.querySelector('#password').value.trim().length == 0) {
					document.querySelector('#password').classList.add('is-invalid');
				}
				else {
		        	document.querySelector('#password').classList.remove('is-invalid');
		        }
				
				if(document.querySelectorAll('.is-invalid').length > 0) {
					return;
				}
				
				var xhttp = new XMLHttpRequest();
				xhttp.open("POST", "LoginServlet", true);
				xhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				var params = "username=" + document.querySelector('#username').value + "&password=" + document.querySelector('#password').value;
				xhttp.send(params);
				
				xhttp.onreadystatechange = function() {
					if(xhttp.readyState == XMLHttpRequest.DONE) {
						if(xhttp.responseText.trim().length > 0) {
					  		document.getElementById("invalidCredentials").innerHTML = xhttp.responseText;
					  		document.querySelector('#username').value = "";
							document.querySelector('#password').value = "";
					  	}
						else {
							window.location.href = "home.jsp";
						}
					}
				};
			}
	    </script>
	</body>
</html>