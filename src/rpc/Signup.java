package rpc;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class Signup
 */
@WebServlet("/signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQLConnection conn = new MySQLConnection ();
		try {
			JSONObject obj = new JSONObject();
			HttpSession session = request.getSession(false);

			if (session != null) {
				obj.put("status", "Already loged in");
			} else {
				JSONObject input = RpcHelper.readJSONObject(request);
				String userId = input.getString("user_id");
				String password = input.getString("password");
				String firstName = input.getString("first_name");
				String lastName = input.getString("last_name");
				obj.put("user_id", userId);
				obj.put("first_name", firstName);
				obj.put("last_name", lastName);
				
				
				if (userId == null || password == null || firstName == null || lastName == null) {
					obj.put("status", "Invalid format.");
				} else if (conn.register(userId, password, firstName, lastName)) {
					obj.put("status", "OK");
				} else {
					obj.put("status", "Try another username");
				}
			}
			
			RpcHelper.writeJsonObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
