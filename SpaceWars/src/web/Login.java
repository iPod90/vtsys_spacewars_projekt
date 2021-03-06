package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;

import clientServer.Client;
import clientServer.GameServer;
import clientServer.Server;
import helper.Highscore;
import helper.NameHelper;
import helper.UserOnline;
import logic.Game;
import logic.Human;
import logic.PlayerVsPC;
import logic.PlayerVsPlayer;

/**
 * Servlet implementation class hello
 */
@WebServlet(asyncSupported = true)
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	/**
	 * 
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
		HttpSession session = request.getSession();
		String uID = session.getId();
		Client user = null;
		try {
			user = UserOnline.getUserById(uID);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("user is not logged in!");
			String site = "http://www.google.com";
			response.setStatus(response.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", site);
			response.sendError(1001);
		}
		if (user == null) {

		} else {
			String uname = user.getUsername();
			PrintWriter out = response.getWriter();
			if (request.getParameter("createGame").equals("true")) {
				createGame(request, user, out);
			} else if (request.getParameter("logout").equals("true")) {
				logout(session, uname);
			} else if (request.getParameter("joinGame").equals("true")) {
				Server gameServer = null;
				try {
					gameServer = (Server) Naming.lookup(NameHelper.getServeraddress());
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String gameName = request.getParameter("gameName");
				Game tmp = gameServer.getGameByName(gameName);
				if (tmp.getUniverse().getSize() == 1) {
					out.write("gameThree.html?gameName=" + gameName + "&universeSize=" + tmp.getUniverse().getSize()
							+ "&");

				} else if (tmp.getUniverse().getSize() == 2) {
					out.write("gameFive.html?gameName=" + gameName + "&universeSize=" + tmp.getUniverse().getSize()
							+ "&");
				} else {
					out.write("gameSeven.html?gameName=" + gameName + "&universeSize=" + tmp.getUniverse().getSize()
							+ "&");
				}

				user.joinGame(request.getParameter("gameName"));
			} else if (request.getParameter("getGames").equals("true")) {
				getGamesFromLobby(response, out);
			} else if (request.getParameter("highscore").equals("true")) {
				response.setContentType("application/json");
				out.write(Highscore.getScores().toString());
			} else if (request.getParameter("getUsername").equals("true")) {
				System.out.println(uname + " username request");
				out.write(uname);
			} else {
				out.write("username=" + uname);

			}
			System.out.println(session.getId());
			out.flush();
		}

		// out.close();
	}

	/**
	 * @param response
	 * @param out
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws JSONException
	 */

	/**
	 * @param response
	 * @param out
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws JSONException
	 */
	private void getGamesFromLobby(HttpServletResponse response, PrintWriter out)
			throws MalformedURLException, RemoteException, JSONException {
		System.out.println("Get Games");
		JSONObject gamesList = null;
		try {
			Server gameServer = (Server) Naming.lookup(NameHelper.getServeraddress());
			JSONObject val;
			gamesList = new JSONObject();
			int i = 0;
			Map<String, Game> games = gameServer.gamesInLobby();
			for (String s : games.keySet()) {
				val = new JSONObject();
				if (games.get(s).getVariation() == 0) {
					val.put("gameMode", "PvP");
				} else if (games.get(s).getVariation() == 1) {
					val.put("gameMode", "PvPC");
				} else if (games.get(s).getVariation() == 2) {
					val.put("gameMode", "PPvC");
				}
				val.put("host", games.get(s).getHostName());
				val.put("universeSize", games.get(s).getUniverse().getSize());
				val.put("gameName", s);
				gamesList.put("" + (i++), val);

			}
			response.setContentType("application/json");
			out.write(gamesList.toString());
			System.out.println(gamesList);

		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param session
	 * @param uname
	 */
	/**
	 * @param session
	 * @param uname
	 */
	private void logout(HttpSession session, String uname) {
		UserOnline.logout(session.getId());
		System.out.println(uname + " successfully logged out");
	}

	/**
	 * @param request
	 * @param user
	 * @param out
	 * @throws NumberFormatException
	 * @throws RemoteException
	 */
	/**
	 * @param request
	 * @param user
	 * @param out
	 * @throws NumberFormatException
	 * @throws RemoteException
	 */
	private void createGame(HttpServletRequest request, Client user, PrintWriter out)
			throws NumberFormatException, RemoteException {
		String gameName = request.getParameter("gameName");

		int variation = Integer.parseInt(request.getParameter("gameMode").trim()); // 0=pvp
																					// 1=pvpc
																					// 2=ppvpc
		int universeSize = Integer.parseInt(request.getParameter("universeSize").trim()); // 0=3planets
		// 1=5planets
		// 2=7planets
		if (variation == 0) {

			if (universeSize == 1) {
				out.write("gameThree.html?gameName=" + gameName + "&universeSize=" + universeSize + "&");

			} else if (universeSize == 2) {
				out.write("gameFive.html?gameName=" + gameName + "&universeSize=" + universeSize + "&");
			} else {
				out.write("gameSeven.html?gameName=" + gameName + "&universeSize=" + universeSize + "&");
			}
			user.openGame(gameName, variation, universeSize);
			System.out.println("createGame: " + gameName + " mode: " + variation + " universeSize: " + universeSize);
		} else if (variation == 1) {
			out.write("gameSeven.html?gameName=" + gameName + "&universeSize=" + universeSize + "&");
			user.openGame(gameName, variation, universeSize);
			System.out.println("createGame: " + gameName + " mode: " + variation + " universeSize: " + universeSize);
		} else if (variation == 2) {
			if (universeSize == 1) {
				out.write("gameThree.html?gameName=" + gameName + "&universeSize=" + universeSize + "&");

			} else if (universeSize == 2) {
				out.write("gameFive.html?gameName=" + gameName + "&universeSize=" + universeSize + "&");
			} else {
				out.write("gameSeven.html?gameName=" + gameName + "&universeSize=" + universeSize + "&");
			}
			user.openGame(gameName, variation, universeSize);
			System.out.println("createGame: " + gameName + " mode: " + variation + " universeSize: " + universeSize);
		} else {
			out.write("?gameName=" + gameName + "&universeSize=" + universeSize + "&");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		PrintWriter out = response.getWriter();
		String uname = request.getParameter("username");
		try {
			if (!UserOnline.isUserExisting(sessionId)) {
				UserOnline.addUser(sessionId, new Human(uname));
			} else {
				response.sendError(1000);
				System.out.println("username is in use");
			}
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.sendError(2000);
			System.out.println("Server not bound");
		}
		System.out.println(uname + " with iD: " + sessionId + " has logged in!");
		// TODO log in user here!
		out.write("?username=" + uname);
		response.getWriter().close();

	}
}
