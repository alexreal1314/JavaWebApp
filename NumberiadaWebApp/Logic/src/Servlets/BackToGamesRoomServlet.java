package Servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by alex on 15/02/2017.
 */
@WebServlet(name = "BackToGamesRoom", urlPatterns = {"/backtogamesroom"})
public class BackToGamesRoomServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("gamesRoom.html");
    }


    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
