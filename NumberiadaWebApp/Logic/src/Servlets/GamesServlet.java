package Servlets;

import Logic.*;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

import static Constants.Constants.*;

/**
 * Created by alex on 09/02/2017.
 */
@WebServlet(name = "GamesServlet", urlPatterns = {"/gameslist"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class GamesServlet extends HttpServlet {
    private String m_lastOrganizer;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            gamesManager.updateGamesProperties();
            String json = gson.toJson(gamesManager.getGamesProperties());
            out.println(json);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("addUserToTheGame") != null){
            addUserToAGame(request,response);
            System.out.println("we get to add user to the game");

        }
        else if (request.getParameter("removeGame") != null){

            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String gameTitle = request.getParameter("gameTitle");
                GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
                Boolean couldRemove = gamesManager.removeGame(gameTitle);
                if (couldRemove == false){
                    MoveError errorDeleteGame = new MoveError();
                    errorDeleteGame.setParams("notRemove", "Can't remove the game, game already started or there are players waiting");
                    String json = gson.toJson(errorDeleteGame);
                    out.println(json);
                    out.flush();
                }

            }
            catch (Exception e) {
                String errorMessage = e.getMessage();
                System.out.println("Error undo: " + e.getMessage());
            }
        }
        else if (request.getParameter("resetGame") != null){
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            String gameTitle = request.getParameter("gameTitle");
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            Game gameManager = gamesManager.getGameByTitle(gameTitle);
            Boolean couldReset = gameManager.gameIsFinished();
            if (couldReset){
                gameManager.ClearPlayersTech();
                gameManager.ResetPlayersCount();
                gameManager.resetGameStatus();
                gameManager.resetGameFinish();
                gameManager.ResetMoves();
                gameManager.initAvailableColors();
                gameManager.initAvailableBasicPlayers();
                gameManager.GetBoard().resetBoard();
                gameManager.initChatManager();
                gamesManager.getGamePropertyByName(gameTitle).setPlayersCount(gamesManager.setPlayerCount(gameManager));
                gamesManager.getGamePropertyByName(gameTitle).setGameStatus("Ready");
                MoveError errorDeleteGame = new MoveError();
                errorDeleteGame.setParams("Reset", "Game properties have been reset");
                String json = gson.toJson(errorDeleteGame);
                out.println(json);
                out.flush();
            }
            else {
                MoveError errorDeleteGame = new MoveError();
                errorDeleteGame.setParams("notReset", "Can't reset the game, game has not finished yet");
                String json = gson.toJson(errorDeleteGame);
                out.println(json);
                out.flush();
            }


        }
        else{
            processRequest(request, response);
        }
    }

    private void addUserToAGame(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String gameTitleFromParameter = request.getParameter("gameTitle");
        String playerNameFromParameter = request.getParameter("userName");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        boolean isHuman = userManager.isUserHuman(playerNameFromParameter);
        System.out.println("before add user in servlet");
        System.out.println("title " + gameTitleFromParameter);
        System.out.println("name " + playerNameFromParameter);
        System.out.println("is Human: " + isHuman);
        String errorMessage = null;
        try {
            boolean isNotOkToJoin = (gamesManager.getGameByTitle(gameTitleFromParameter).isFullOfPlayers() || gamesManager.getGameByTitle(gameTitleFromParameter).isGameStarted());
            if (isNotOkToJoin){
                throw new Exception("Game already started, you can reset the game when it is finished");
            }
            else {

                gamesManager.addUserToAGame(gameTitleFromParameter, playerNameFromParameter, isHuman);
                gamesManager.getGamePropertyByName(gameTitleFromParameter).setPlayersCount(gamesManager.setPlayerCount(gamesManager.getGameByTitle(gameTitleFromParameter)));
            }
        }
        catch (Exception e){
            errorMessage = "Error in adding player to the game: " + e.getMessage();
        }

        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(errorMessage);
        out.println(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        if (request.getParameter("organizer") != null) {
            addGameOrganizer(usernameFromSession);
            //processRequestForPost(request, response);
        }
        else if(request.getParameter("showBoard") != null) {
            String gameTitle = request.getParameter("gameTitle");
            GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
            gamesManager.addUserToShowBoard(usernameFromSession, gameTitle);
        }
        else {
            processRequestForPost(request, response);
        }
    }


    private void addGameOrganizer(String organizer) {
        m_lastOrganizer = organizer;
    }


    private void processRequestForPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        Collection<Part> parts = request.getParts();
        StringBuilder fileContent = new StringBuilder();
        String errorMessage;

        for (Part part : parts) {
            //part.write("samplefile");
            fileContent.append(readFromInputStream(part.getInputStream()));
        }
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        try {
            gamesManager.addGame(m_lastOrganizer, fileContent); //change the parameter
            response.sendRedirect("gamesRoom.html");
        }
        catch (JAXBException exc1) {
            errorMessage = "Error in loading game file: " + exc1.getMessage();
            request.setAttribute(GAME_ERROR,errorMessage);
            getServletContext().getRequestDispatcher("/gamesRoom.jsp").forward(request, response);
        }
        catch (SAXException exc2) {
            errorMessage = "Error in loading game file: " + exc2.getMessage();
            request.setAttribute(GAME_ERROR,errorMessage);
            getServletContext().getRequestDispatcher("/gamesRoom.jsp").forward(request, response);
        }
        catch (InvalidXMLException exc3) {
            errorMessage = "Error in loading game file: " + exc3.getMessage();
            request.setAttribute(GAME_ERROR,errorMessage);
            getServletContext().getRequestDispatcher("/gamesRoom.jsp").forward(request, response);

        }
        catch (InvalidExtension exc4) {
            errorMessage = "Error in loading game file: " + exc4.getMessage();
            request.setAttribute(GAME_ERROR,errorMessage);
            getServletContext().getRequestDispatcher("/gamesRoom.jsp").forward(request, response);
        }
        catch (XmlValueException exc5) {
            errorMessage = "Error in loading game file: " + exc5.getMessage();
            request.setAttribute(GAME_ERROR,errorMessage);
            getServletContext().getRequestDispatcher("/gamesRoom.jsp").forward(request, response);
        }
        catch (Exception e){
            errorMessage = "Error in loading game file: " + e.getMessage();
            request.setAttribute(GAME_ERROR,errorMessage);
            getServletContext().getRequestDispatcher("/gamesRoom.jsp").forward(request, response);
        }

    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>





}
