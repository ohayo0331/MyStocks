/**
 * 
 */
package csci310.servlets;

import static org.junit.Assert.*;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.junit.Test;
import org.mockito.Mockito;

import csci310.DatabaseJDBC;

public class LoginServletTest extends Mockito{
	
	// Private helper functions
	private void verifyLoginUser(HttpServletRequest request, HttpServletResponse response, PrintWriter writer, 
			StringWriter stringWriter, String username, int success) throws ServletException, IOException {
		
		verify(request, atLeast(1)).getParameter("username");
		verify(request, atLeast(1)).getParameter("password");
		writer.flush();
		
		// LoginServlet receives "1" from DatabaseJDBC if account creation was successful
		// and writes no error message.
		if(success == 1) {
			assertTrue(stringWriter.toString().length() == 0);
		}
		// LoginServlet receives "0" from DatabaseJDBC if account creation was unsuccessful
		// and writes the failure message "The username and password entered was not correct.".
		else if(success == 0) {
			assertTrue(stringWriter.toString().contains("The username and password entered was not correct."));
		}
		// LoginServlet receives "-1" from DatabaseJDBC if user is locked out
		// and writes the failure message "User {username} is locked out due to too many invalid attempts.".
		else if(success == -1) {
			assertTrue(stringWriter.toString().contains("User " + username + " is locked out due to too many invalid attempts."));
		}
		// LoginServlet receives "-1" from DatabaseJDBC if user is found but the password was not correct.
		// and writes the failure message "The password you've entered is incorrect.".
		else if(success == -2) {
			assertTrue(stringWriter.toString().contains("The password you've entered is incorrect."));
		}
		
		// Clear writer for next test
		stringWriter.getBuffer().setLength(0);
	}

	@Test
	public void testDoGet() throws Exception {
		LoginServlet ls = new LoginServlet();
		
		HttpServletRequest request = mock(HttpServletRequest.class);       
	    HttpServletResponse response = mock(HttpServletResponse.class);
	    StringWriter stringWriter = new StringWriter();
	    PrintWriter writer = new PrintWriter(stringWriter);
	    HttpSession session = mock(HttpSession.class);

	    when(request.getParameter("username")).thenReturn("user100");
	    when(request.getParameter("password")).thenReturn("pass");
	    when(response.getWriter()).thenReturn(writer);
	    when(request.getSession()).thenReturn(session);
	    doNothing().when(session).setAttribute(anyString(), anyString());

	    // Register user100
	    DatabaseJDBC.removeUser("user100");
	    
	    // Scenario 1: test account login with nonexistent user
	    ls.doGet(request, response);
	    verifyLoginUser(request, response, writer, stringWriter, "user100", 0);

	    // Scenario 2: test account login with valid username and password
	    DatabaseJDBC.register("user100", "pass");
	 	ls.doGet(request, response);
	 	verifyLoginUser(request, response, writer, stringWriter, "user100", 1);
	 	
	 	// Scenario 3: test account login with wrong password
	 	request = mock(HttpServletRequest.class); 
	 	when(request.getParameter("username")).thenReturn("user100");
	    when(request.getParameter("password")).thenReturn("wrongpassword");
	    when(request.getSession()).thenReturn(session);
	    doNothing().when(session).removeAttribute(anyString());
	 	ls.doGet(request, response);
	 	verifyLoginUser(request, response, writer, stringWriter, "user100", -2);
	 	
	 	// Scenario 4: test account login when three invalid logins
	 	DatabaseJDBC.login("user100", "wrongpassword");
 	 	ls.doGet(request, response);
 	 	verifyLoginUser(request, response, writer, stringWriter, "user100", -1);
	}
	
	// doPost
	@Test
	public void testDoPost() throws Exception {
		LoginServlet ls = new LoginServlet();
		
		HttpServletRequest request = mock(HttpServletRequest.class);       
	    HttpServletResponse response = mock(HttpServletResponse.class);
	    StringWriter stringWriter = new StringWriter();
	    PrintWriter writer = new PrintWriter(stringWriter);
	    HttpSession session = mock(HttpSession.class);

	    when(request.getParameter("username")).thenReturn("user100");
	    when(request.getParameter("password")).thenReturn("pass");
	    when(response.getWriter()).thenReturn(writer);
	    when(request.getSession()).thenReturn(session);
	    doNothing().when(session).setAttribute(anyString(), anyString());
	    
	    // Register user100
	    DatabaseJDBC.removeUser("user100");
	    
	    // Scenario 1: test account login with nonexistent user
	    ls.doPost(request, response);
	    verifyLoginUser(request, response, writer, stringWriter, "user100", 0);

	    // Scenario 2: test account login with valid username and password
	    DatabaseJDBC.register("user100", "pass");
	 	ls.doPost(request, response);
	 	verifyLoginUser(request, response, writer, stringWriter, "user100", 1);
	 	
	 	// Scenario 3: test account login with wrong password
	 	request = mock(HttpServletRequest.class); 
	 	when(request.getParameter("username")).thenReturn("user100");
	    when(request.getParameter("password")).thenReturn("wrongpassword");
	    when(request.getSession()).thenReturn(session);
	    doNothing().when(session).removeAttribute(anyString());
	 	ls.doPost(request, response);
	 	verifyLoginUser(request, response, writer, stringWriter, "user100", -2);
	 	
	 	// Scenario 4: test account login when three invalid logins
	 	DatabaseJDBC.login("user100", "wrongpassword");
 	 	ls.doPost(request, response);
 	 	verifyLoginUser(request, response, writer, stringWriter, "user100", -1);
	}
}

