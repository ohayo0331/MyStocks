/**
 * 
 */
package csci310.servlets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;

import csci310.DatabaseJDBC;

public class CreateAccountServletTest extends Mockito {
	// Private Helper Functions
	private void verifyCreateAccount(HttpServletRequest request, HttpServletResponse response, PrintWriter writer, 
			StringWriter stringWriter, String username, boolean success) throws ServletException, IOException {
		
		verify(request, atLeast(1)).getParameter("username");
		verify(request, atLeast(1)).getParameter("password");
		writer.flush();
		
		// CreateAccountServlet receives "1" from DatabaseJDBC if account creation was successful
		// and writes no error message.
		if(success) {
			assertTrue(stringWriter.toString().length() == 0);
		}
		// CreateAccountServlet receives "0" from DatabaseJDBC if account creation was unsuccessful
		// and writes the failure message "Username has already been taken!".
		else {
			assertTrue(stringWriter.toString().contains("Username has already been taken!"));
		}
		
		// Clear writer for next test
		stringWriter.getBuffer().setLength(0);
	}
	
	@Test
	public final void testDoGet() throws ServletException, IOException {
		// Setup variables
		CreateAccountServlet c = new CreateAccountServlet();
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn("user100");
		when(request.getParameter("password")).thenReturn("pass");
		when(response.getWriter()).thenReturn(writer);
		
		// Remove user100 in database before testing.
		DatabaseJDBC.removeUser("user100");
		
		// Scenario 1: test create account with valid user name and passwords
		c.doGet(request, response);
		verifyCreateAccount(request, response, writer, stringWriter, "user100", true);
		
		// Scenario 2: test create account with taken user name
		c.doGet(request, response);
		verifyCreateAccount(request, response, writer, stringWriter, "user100", false);
	}

	@Test
	public final void testDoPost() throws ServletException, IOException {
		// Setup variables
		CreateAccountServlet c = new CreateAccountServlet();
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		
		when(request.getParameter("username")).thenReturn("user100");
		when(request.getParameter("password")).thenReturn("pass");
		when(response.getWriter()).thenReturn(writer);
		
		// Remove user100 in database before testing.
		DatabaseJDBC.removeUser("user100");
		
		// Scenario 1: test create account with valid user name and passwords
		c.doPost(request, response);
		verifyCreateAccount(request, response, writer, stringWriter, "user100", true);
		
		// Scenario 2: test create account with taken user name
		c.doPost(request, response);
		verifyCreateAccount(request, response, writer, stringWriter, "user100", false);
		
	}
}
