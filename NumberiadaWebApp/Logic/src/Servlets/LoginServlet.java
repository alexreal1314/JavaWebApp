package Servlets;

import Logic.GamesManager;
import Logic.UserManager;
import Utils.ServletUtils;
import Utils.SessionUtils;

import javax.servlet.RequestDispatcher;
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

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            String userTypeFromParameter = request.getParameter(USER_TYPE);
            if (usernameFromParameter == null || usernameFromParameter.trim().equals("")) {
                //no username in session and no username in parameter -
                //redirect back to the index page
                //this return an HTTP code back to the browser telling it to load
                //the given URL (in this case: "index.html")
                response.sendRedirect("index.html");
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();
                if (userManager.isUserExists(usernameFromParameter)) {
                    String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username";
                    //username already exists, forward the request back to index.jsp
                    //with a parameter that indicates that an error should be displayed
                    request.setAttribute(USER_NAME_ERROR, errorMessage);
                    getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
                }
                else {
                    //add the new user to the users list
                    userManager.addUser(usernameFromParameter,userTypeFromParameter);
                    //set the username in a session so it will be available on each request
                    //the true parameter means that is a session object does not exists yet
                    //create a new one
                    request.getSession(true).setAttribute(USERNAME, usernameFromParameter);

                    //redirect the request to the chat room - in order to actually change the URL
                    response.sendRedirect("gamesRoom.html");
                    System.out.println("new userName = " + SessionUtils.getUsername(request));
                    System.out.println("new userName = " + request.getSession(true).getAttribute(USERNAME));

                }
            }

        } else {

            String usernameFromParameter = request.getParameter(USERNAME);
            String userTypeFromParameter = request.getParameter(USER_TYPE);

            //user is already logged in
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            // String gameTitle;
            if(gamesManager.userExistInGame(usernameFromSession) != null){
                RequestDispatcher RD = request.getRequestDispatcher("movetogame?param=doPost");
                RD.forward(request,response);
            }
            else {
                response.sendRedirect("gamesRoom.html");
            }
            request.setAttribute(USERNAME, usernameFromSession);
            System.out.println("already :" + request.getParameter(USERNAME));

            /*if (userManager.isUserExists(usernameFromParameter)) {
                String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username";
                //username already exists, forward the request back to index.jsp
                //with a parameter that indicates that an error should be displayed
                request.setAttribute(USER_NAME_ERROR, errorMessage);
                getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
            }*/
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>




}
