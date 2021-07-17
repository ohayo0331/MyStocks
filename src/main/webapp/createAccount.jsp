<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta charset= "utf-8">
    
    <!-- Bootstrap CSS -->
   	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
   	
   	<!-- Google Fonts -->
   	<link href="https://fonts.googleapis.com/css2?family=Lato:wght@100;300;400&display=swap" rel="stylesheet">
	
	<!-- Our Stylesheets -->
	<link rel = "stylesheet" href = "css/style.css"> <!-- shared across pages -->		
    <link rel = "stylesheet" href = "css/createAccount.css"> <!-- specific to create account -->	
    
    <title>MyStocks | Create Account</title>
  </head>
  <body>
  	<!-- reference to three.js file -->
	<script  src="./three.js"></script>
  	<script
		src="http://code.jquery.com/jquery-3.4.1.min.js"
		integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
		crossorigin="anonymous">
	</script>

	<!-- banner on the top of the page -->	
	<div class="header jumbotron jumbotron-fluid">
	  	<h1 class="display-4" style="text-align: left; padding-left: 50px;">MyStocks</h1>
	</div>
	
	
	<div class="container">
		<div class="row justify-content-center">
			<div class="col-md-6 align-self-center">
				<!-- user log in text -->	
				<h1 class="form-heading text-center"> Create Account </h1>
			</div>
		</div>
		
		<div class="row justify-content-center">
       		<div class="col-md-6 align-self-center">
				 <form id="createAccountForm" autocomplete="off" method="POST">
				 	<div class="form-group">
		              <small id="invalidUsername" class="error">
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
		            
		            <!-- confirm password text box--> 
		            <div class="form-group">
		              <i class = "icon fa fa-lock" aria-hidden="true" style="color: white;"></i>		             
		              <label for="confirmPassword" style="color: white">Confirm Password</label>
		              <input id="confirmPassword" name="confirmPassword" type="password" placeholder="Password" class="form-control"/> 
		              <small id="confirmPasswordError" class="error invalid-feedback"></small>
		            </div>	  
		            
		            <!-- login button -->  	
					<div class="row">
						<div class="col text-center">
						    <!-- sign in button --> 
							<button class="submitButton btn btn-primary yellow-button createUserButton" id="createAccountButton" type="submit" value="Create Account">
								Create User
							</button>
						</div>
					</div>
				</form>
			</div>
		</div>

		<div class="row justify-content-center top-buffer">
			<div class="col-md-6 align-self-center">
			    <!-- create account button --> 
				<button class="switchPageButton btn btn-primary grey-button cancelButton" id="cancelButton" type="button" onclick="window.location='login.jsp'">
					Cancel
				</button>
			</div>
		</div>
	</div>
	
	<script>
		document.querySelector('form').onsubmit = function() {
			document.querySelector("#invalidUsername").innerHTML = "";
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
			
			if(document.querySelector('#confirmPassword').value.trim().length == 0 ) {
				document.querySelector('#confirmPasswordError').innerHTML = "Please confirm password!";
				document.querySelector('#confirmPassword').classList.add('is-invalid');
			}
			else {
				if(document.querySelector('#password').value.trim() !== document.querySelector('#confirmPassword').value.trim()) {
					document.querySelector('#confirmPasswordError').innerHTML = "Please make sure passwords match!";
					document.querySelector('#confirmPassword').classList.add('is-invalid');
				}
				else {
					document.querySelector('#confirmPassword').classList.remove('is-invalid');
				}
			}
			
			console.log(document.querySelectorAll('.is-invalid').length);
			if(document.querySelectorAll('.is-invalid').length > 0) {
				return;
			}
			
			var xhttp = new XMLHttpRequest();
			xhttp.open("POST", "CreateAccountServlet", true);
			xhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			var params = "username=" + document.querySelector('#username').value + "&password=" + document.querySelector('#password').value;
			xhttp.send(params);
			
			xhttp.onreadystatechange = function() {
				if(xhttp.readyState == XMLHttpRequest.DONE) {
					if(xhttp.responseText.trim().length > 0) {
				  		document.getElementById("invalidUsername").innerHTML = xhttp.responseText;
				  		document.querySelector('#username').value = "";
						document.querySelector('#password').value = "";
						document.querySelector('#confirmPassword').value = "";
				  	}
					else {
						window.location.href = "login.jsp";
						document.getElementById("invalidUsername").style.color = "green";
						document.getElementById("invalidUsername").innerHTML = "Successfully registered";
					}
				}
			};
		}
    </script>
  </body>
</html>