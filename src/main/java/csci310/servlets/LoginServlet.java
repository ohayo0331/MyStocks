package csci310.servlets;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import csci310.DatabaseJDBC;
import csci310.Portfolio;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
   public LoginServlet() {
       super();
   }

   /**
    * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	*/
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		Integer result = DatabaseJDBC.login(username, password);
		HttpSession session = request.getSession();
		if (result == 1) {
			session.setAttribute("User", username);
			Portfolio portfolio= new Portfolio(username);
			session.setAttribute("MyPortfolio", portfolio);
		}
		else {
			session.removeAttribute("User");
		}
		
		// If user is locked out
		if(result == -1) {
			response.getWriter().println("User " + username + " is locked out due to too many invalid attempts.");
		}
		// If login is unsuccessful
		else if(result == 0) {
			response.getWriter().println("The username and password entered was not correct.");
		}
		else if (result == -2) {
			response.getWriter().println("The password you've entered is incorrect.");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}
}