package Servlets;

import Logic.GamesManager;
import Logic.UserManager;
import Utils.ServletUtils;
import Utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static Constants.Constants.*;

/**
 * Created by alex on 09/02/2017.
 */
@WebServlet(name = "BackToLoginPage", urlPatterns = {"/backtologin"})
public class BackToLoginPageServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameter("Logout") != null){
            LogoutUser(request,response);
            System.out.println("we get to LOGOUT");

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.html");
    }

    private void LogoutUser(HttpServletRequest request, HttpServletResponse response) {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        response.setContentType("text/html;charset=UTF-8");

        String usernameFromParameter = request.getParameter(USERNAME);
        gamesManager.DeleteUserFromGames(usernameFromParameter);
        userManager.deleteUser(usernameFromParameter);
        SessionUtils.clearSession(request);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
