package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import utilities.AccountDAO;
import utilities.BookingDAO;
import utilities.JsonHelper;
import model.Account;
import model.Book;
import model.Flight;
import model.User;

/**
 * Servlet implementation class ReviewAndBook
 */
public class ReviewAndBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpSession session;
	private final double TX_ECONOMY_SEAT = 123.43;
	private final double TX_FIRST_CLASS_SEAT = 550;
	private final double TX_BUSINESS_CLASS_SEAT = 325.56;
	private ArrayList <Book> shoppingCart = null;
	private Flight flight;
	private int economyClass = 0;
	private int firstClass = 0;
	private int businessClass = 0;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReviewAndBook() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet (HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException{
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JsonHelper js = new JsonHelper();
		session = request.getSession();
		Account account = new Account();
		Book book = new Book ();
		User user = (User) session.getAttribute("user");
		double totalCost = 0;
		
		String action = request.getParameter("action");
		String json = request.getParameter("json");
		JSONObject jObj;
		try {
			jObj = new JSONObject(json);

		session.setAttribute("classe", jObj.get("classe").toString());
		session.setAttribute("numberOfSeats",jObj.get("numberOfSeats").toString());

		if (shoppingCart == null && session.getAttribute("shoppingCart") == null) {
	    	shoppingCart = new ArrayList <Book> ();
	    }
	    else {
	    	shoppingCart = (ArrayList<Book>) session.getAttribute ("shoppingCart");
	    }

		flight = (Flight) session.getAttribute("flightBean");
		book.setUserId(user.getId());
		book.setFlightIds(flight.getId());
			//book.setTotalCost(totalCost);
		Iterator it = shoppingCart.iterator();
		while (it.hasNext()) {
			totalCost += ((Book) it.next()).getTotalCost();
			System.out.println (totalCost);
		}
			session.setAttribute("totalCost", totalCost);
			shoppingCart.add(book);
		    session.setAttribute("shoppingCart", shoppingCart);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		session = request.getSession();

		flight = (Flight) session.getAttribute("flightBean");
		economyClass = (int) session.getAttribute("economyClass");
		businessClass = (int) session.getAttribute("businessClass");
		firstClass = (int) session.getAttribute("firstClass");

		double totalCost = 0;
		if ((flight.getBusinessClassReserved() - businessClass >= 0)
				&& (flight.getEconomyClassReserved() - economyClass >= 0)
				&& (flight.getFirstClassReserved() - firstClass >= 0)) {

			totalCost = economyClass * TX_ECONOMY_SEAT + businessClass
					* TX_BUSINESS_CLASS_SEAT + firstClass * TX_FIRST_CLASS_SEAT;
			NumberFormat formatter = NumberFormat.getCurrencyInstance();

			session.setAttribute("totalCostFormatted",
					formatter.format(totalCost));
			session.setAttribute("totalCost", totalCost);
			session.setAttribute("totalSeats", firstClass + businessClass
					+ economyClass);

			RequestDispatcher rd = request
					.getRequestDispatcher("WEB-INF/transaction.jsp");
			rd.forward(request, response);
		} else {
			RequestDispatcher rd = request
					.getRequestDispatcher("WEB-INF/reviewandbook.jsp");
			rd.forward(request, response);
		}

	}
}
