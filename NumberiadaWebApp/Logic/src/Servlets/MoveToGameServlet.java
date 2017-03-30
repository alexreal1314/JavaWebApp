package Servlets;

import Logic.*;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import com.sun.deploy.net.HttpRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alex on 14/02/2017.
 */

@WebServlet(name = "MoveToGameServlet", urlPatterns = {"/movetogame"})
public class MoveToGameServlet extends HttpServlet {
    private static final String ROW_PARAMETER = "row";
    private static final String COL_PARAMETER = "col";
    ExecutorService m_threadExecutor = Executors.newFixedThreadPool(3);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String userName = SessionUtils.getUsername(request);
        if (request.getParameter("getGameBoard") != null){
            String gameTitle = gamesManager.getGameTitleForShowBoard(userName);
            Game gameManager = gamesManager.getGameByTitle(gameTitle);
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            String json = null;
            try {
                json = gson.toJson(getNewBoardHTML(gameManager, userName, true, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
            out.println(json);
            out.flush();
        }
        else if (request.getParameter("updateUserName") != null){
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            out.println(gson.toJson(userName));
            out.flush();
        }
        else {
            String gameTitle = gamesManager.getGameTitleByUser(userName);
            Game gameManager = gamesManager.getGameByTitle(gameTitle);

            if (request.getParameter("updatePlayresList") != null) {
                sendParametersForPlayersTable(request, response);
            } else if (request.getParameter("updateMoves") != null) {
                sendMovesParameter(request, response);
            } else if (request.getParameter("updateCurrentPlayer") != null) {
                sendCurrentPlayerParameter(request, response);
            } else if (request.getParameter("updateTurn") != null) {
                //sendTurnParameter(request, response);
            } else if (request.getParameter("refreshBoard") != null) {
                try {
                    if (gameManager != null){

                        PrintWriter out = response.getWriter();
                        refreshBoard(request, response, gameManager, userName);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (request.getParameter("updateReplayBoard") != null){
                try {
                    updateReplayBoard(request,response, gameManager, userName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (request.getParameter("winnerExist") != null){

                PrintWriter out = response.getWriter();
            }
            else {
                String param;
                param = request.getParameter("param");
                if (param.compareTo("doPost") == 0) {
                    doPost(request, response);
                }
            }
        }

    }


    private void updateReplayBoard(HttpServletRequest request, HttpServletResponse response, Game gameManager, String userName) throws Exception {
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(getNewBoardHTML(gameManager, userName, false, true));
        out.println(json);
        out.flush();
    }

    private void sendUserNameToScript(HttpServletRequest request, HttpServletResponse response, String userName) throws IOException {
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(userName);
        out.println(json);
        out.flush();
    }

    private void refreshBoard(HttpServletRequest request, HttpServletResponse response, Game gameManager, String userName) throws Exception {
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String jsonObject = getNewBoardHTML(gameManager, userName, false, false);
        String json = gson.toJson(jsonObject);
        out.println(json);
        out.flush();
    }

    private String getNewBoardHTML(Game gameManager, String userName, boolean isForShowBoard, boolean isForReplay) throws Exception {
        String newBoard = "";
        Integer numOfRows = gameManager.GetBoard().NumOfRows();
        Integer numOfCols = gameManager.GetBoard().NumOfCols();
        Cell checkedcell = gameManager.getCheckedCell();
        int checkedRow = -1;
        int checkedCol = -1;


        if(checkedcell != null){
            checkedRow = checkedcell.GetNumOfRow();
            checkedCol = checkedcell.GetNumOfCol();
        }


        Cell[][] arrayOfCells;
        Board playerBoard;
        if (isForShowBoard) {
            Player dummyPlayer = new Player(gameManager.getCurrentPlayerID(),userName,"Human", gameManager.getCurrentPlayerID());

        }

        playerBoard = gameManager.GetBoard();
        arrayOfCells = playerBoard.getCellBoard();

        newBoard = newBoard + "<tr>";
        newBoard = newBoard + "<td class='boardCol'>" + "</td>";
        for (int i = 0; i < numOfCols; i++) {
            newBoard = newBoard + "<td class='boardCol'>" + (i + 1 ) + " </td>";
        }
        newBoard = newBoard + "</tr>";


        for (int i = 0; i < numOfRows; i++) {
            newBoard = newBoard + "<tr>";
            newBoard = newBoard + "<td class='boardRow'>" + (i + 1 ) + " </td>";
            for (int j = 0; j < numOfCols; j++) {
                if(i == checkedRow && j == checkedCol){
                    newBoard = newBoard + "<td class='boardCell' data-selected='true' row='" + i + "' col='" + j + "'" + "cellValue='" + arrayOfCells[i][j].GetNumInCell() + "' + color='" + arrayOfCells[i][j].getCellStringColor() +"'>" + arrayOfCells[i][j].GetNumInCell() + "</td>";
                }
                else {
                    newBoard = newBoard + "<td class='boardCell' data-selected='false' row='" + i + "' col='" + j + "'" + "cellValue='" + arrayOfCells[i][j].GetNumInCell() + "' + color='" + arrayOfCells[i][j].getCellStringColor() +"'>" + arrayOfCells[i][j].GetNumInCell() + "</td>";
                }
            }
            newBoard = newBoard + "</tr>";
        }

        newBoard = newBoard + ("<form id='clickBoardCell' method='post' action='singlegame'>");
        newBoard = newBoard + ("<input id='form_col' type='hidden' name='" + COL_PARAMETER + "'/>");
        newBoard = newBoard + ("<input id='form_row' type='hidden' name='" + ROW_PARAMETER + "'/>");
        newBoard = newBoard + ("<input id='form_rowcol_value' type='hidden' name='" + "cellValue" + "'/>");
        newBoard = newBoard + ("<input id='button' type='hidden' name='button'/>");
        newBoard = newBoard + ("</form>");

        return newBoard;
    }

    private void sendCurrentPlayerParameter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String userName = SessionUtils.getUsername(request);
        String gameTitle = gamesManager.getGameTitleByUser(userName);
        Game gameManager = gamesManager.getGameByTitle(gameTitle);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String jsonObject;
        if (gameManager.isGameStarted()) {
            jsonObject = "<h5 class='currPlayer'> Current player turn: " + gameManager.getCurrentPlayerManager().GetPlayerName() + "</h5>";
        } else {
            jsonObject = "<h5 class='currPlayer'> Note: Game didn't started yet, waiting for more players</h5>";
        }
        out.println(gson.toJson(jsonObject));
        out.flush();
    }

    private void sendMovesParameter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String userName = SessionUtils.getUsername(request);
        String gameTitle = gamesManager.getGameTitleByUser(userName);
        Game gameManager = gamesManager.getGameByTitle(gameTitle);
        PrintWriter out = response.getWriter();

        Integer totalMoves;
        if (gameManager.isGameStarted()) {
            totalMoves = gameManager.getNumOfMoves();
        } else {
            totalMoves = 0;
        }
        Gson gson = new Gson();
        String jsonObject = "<h3>Moves: " + totalMoves + "</h3>";
        String json = gson.toJson(jsonObject);

        out.println(json);
        out.flush();
    }

    private void sendParametersForPlayersTable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String userName = SessionUtils.getUsername(request);
        Game gameManager = gamesManager.getGameByTitle(gamesManager.getGameTitleByUser(userName));
        boolean isBasicGame = gameManager.isBasicGame();
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        ArrayList<String> jsonObject = new ArrayList<>();
        ArrayList<Player> playersManager = gameManager.getPlayersList();
        for (Player o : playersManager) {
            String playerClass = getPlayerClass(o, gameManager);
            boolean isComputer = o.GetIsComputer();
            int score = o.getScore();
            if (o.GetPlayerName().equals(userName)) {
                if (!isComputer) {
                    if (playerClass.equals("currentPlayer")) {
                        if (isBasicGame){
                            jsonObject.add("<tr bgcolor='greenyellow'><td>(Me) " + o.GetPlayerName() + "</td><td> Human </td><td>" + o.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            jsonObject.add("<tr bgcolor='greenyellow'><td>(Me) " + o.GetPlayerName() + "</td><td> Human </td><td>" + o.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    } else {
                        if(isBasicGame){
                            jsonObject.add("<tr ><td>(Me) " + o.GetPlayerName() + "</td><td> Human </td><td>" + o.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            jsonObject.add("<tr ><td>(Me) " + o.GetPlayerName() + "</td><td> Human </td><td>" + o.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                } else {
                    if (playerClass.equals("currentPlayer")) {
                        if(isBasicGame){
                            jsonObject.add("<tr bgcolor='greenyellow'><td>(Me) " + o.GetPlayerName() + "</td><td> Computer </td><td>" + o.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            jsonObject.add("<tr bgcolor='greenyellow'><td>(Me) " + o.GetPlayerName() + "</td><td> Computer </td><td>" + o.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                    else{
                        if (isBasicGame){
                            jsonObject.add("<tr><td>(Me) " + o.GetPlayerName() + "</td><td> Computer </td><td>" + o.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            jsonObject.add("<tr><td>(Me) " + o.GetPlayerName() + "</td><td> Computer </td><td>" + o.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                }
            }
            else {
                if (!isComputer) {
                    if (playerClass.equals("currentPlayer")) {
                        if (isBasicGame){
                            jsonObject.add("<tr bgcolor='greenyellow'><td>" + o.GetPlayerName() + "</td><td> Human </td><td>" + o.RowColPlayer()+ "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            jsonObject.add("<tr bgcolor='greenyellow'><td>" + o.GetPlayerName() + "</td><td> Human </td><td>" + o.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                    else {
                        if(isBasicGame){
                            jsonObject.add("<tr><td>" + o.GetPlayerName() + "</td><td> Human </td><td>" + o.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            jsonObject.add("<tr><td>" + o.GetPlayerName() + "</td><td> Human </td><td>" + o.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                }
                else {
                    if (playerClass.equals("currentPlayer")) {
                        if (isBasicGame){
                            jsonObject.add("<tr bgcolor='greenyellow'><td>" + o.GetPlayerName() + "</td><td> Computer </td><td>" + o.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            jsonObject.add("<tr bgcolor='greenyellow'><td>" + o.GetPlayerName() + "</td><td> Computer </td><td>" + o.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                    else {
                        if(isBasicGame){
                            jsonObject.add("<tr><td>" + o.GetPlayerName() + "</td><td> Computer </td><td>" + o.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            jsonObject.add("<tr><td>" + o.GetPlayerName() + "</td><td> Computer </td><td>" + o.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                }
            }
        }
        String json = gson.toJson(jsonObject);
        out.println(json);
        out.flush();
    }

    private String getPlayerClass(Player player, Game gameManager) {
        if ((player.GetPlayerName().equals(gameManager.getCurrentPlayerManager().GetPlayerName())) && gameManager.isGameStarted()){
            return "currentPlayer";
        }
        else{
            return "player";
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String userName = SessionUtils.getUsername(request);
        if (request.getParameter("removeUserFromShowBoard") != null) {
            gamesManager.removeUserFromShowBoard(userName);
        } else {
            String gameTitle = gamesManager.getGameTitleByUser(userName);
            String nameOfCurrentPlayer = gamesManager.getGameByTitle(gameTitle).getCurrentPlayerManager().GetPlayerName();
            Game currentGame = gamesManager.getGameByTitle(gameTitle);
            PrintWriter out = response.getWriter();

            if (request.getParameter("exitReplay") != null) {
                //currentGame.removeReplay(userName);
            } else if (request.getParameter("leaveGame") != null) {
                if(!currentGame.isBasicGame()){
                    currentGame.leaveGameRemoveCells();
                    currentGame.leaveTheGame(userName);
                }
                else {
                    currentGame.leaveTheGameBasic(userName);
                }


                gamesManager.getGamePropertyByName(gameTitle).setPlayersCount(gamesManager.setPlayerCount(currentGame));

            } else {
                System.out.println("Found userName = " + userName);
                Integer totalMoves = currentGame.getNumOfMoves();
                response.setContentType("text/html;charset=UTF-8");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>" + gameTitle + "</title>");
                out.println("<link rel='stylesheet' type='text/css' href='style/singleGame.css' />");
                out.println("<link rel='stylesheet' href='style/bootstrap.min.css'>");
                out.println("<script src='script/jquery-2.0.3.min.js'></script>");
                out.println("<script src='script/chatRoom.js'></script>");
                out.println("<script src='script/singleGame.js' type='text/javascript'></script>");
                out.println("<script src='script/bootstrap.min.js'></script>");

                out.println("</head>");
                out.println("<body>");
                out.println("<div class='container' id='container'>");


                out.println("<div class='topTitles' id='topTitles'>");
                printGameTitles(out, gamesManager, gameTitle);
                printCurrentPlayer(out, currentGame, nameOfCurrentPlayer);
                printLogoutButton(out);
                out.println("</div>");

                out.println("<div class='playersArea' id='playersArea'>");
                printPlayersList(out, currentGame, userName);

                out.println("<div class='movesTurns' id='movesTurns'>");
                printMovesArea(out, totalMoves);
                out.println("</div>");
                out.println("</div>");

                out.println("<div class='game' id='game'>");
                printBoard(out, currentGame, userName);
                printCommandsButtons(out, currentGame, totalMoves);
                out.println("<form id='clickBoardCell' method='post' action='singlegame'>");
                out.println("<input id='form_col' type='hidden' name='" + COL_PARAMETER + "'/>");
                out.println("<input id='form_row' type='hidden' name='" + ROW_PARAMETER + "'/>");
                out.println("<input id='form_rowcol_value' type='hidden' name='" + "cellValue" + "'/>");
                out.println("<input id='button' type='hidden' name='button'/>");
                out.println("</form>");
                out.println("</div>");

                out.println("</div>");

            }
        }
    }

    private void printCurrentPlayer(PrintWriter out, Game game, String nameOfCurrentPlayer) {
        out.println("<div class='currentPlayerArea' id='currentPlayerArea'>");
        if (game.isGameStarted()) {
            out.println("<h5 class='currPlayer'> Current player turn: " + nameOfCurrentPlayer + "</h5>");
        } else {
            out.println("<h5 class='currPlayer'> Note: Game didn't start yet, waiting for more players</h5>");
        }
        out.println("</div>");
    }

    private void printCommandsButtons(PrintWriter out, Game gameManager, Integer totalMoves) {
        out.println("<div class='commandButtons' id='game'>");
        printPerformMoveButton(out);
        printChatArea(out);
        out.println("</div>");
    }

    private void printChatArea(PrintWriter out) {
        out.println("<div class='chatwindow' id='chatwindow'>");
        out.println("</br>");
        out.println("</br>");
        out.println("</br>");
        out.println("<h4> Chat </h4>");
        out.println("<div id='chatarea' class='span6'>");
        out.println("</div>");
        out.println("<input type='text' id='userstring' name='userstring'/>");
        out.println("<button id='sendMsg' value='sendMsg' onclick='userSendMsg()'>Send</button> <br>");
    }

    private void printLogoutButton(PrintWriter out) {
        out.println("<div class='logout'>");
        out.println("<form action='backtogamesroom' enctype='multipart/form-data' method='POST' id='BackToGamesRoom'>");
        out.println("<button id='logout' class='logout' value='logout' onclick='leaveGame()'>Leave game</button> <br>");
        out.println("</form>");
        out.println("</div>");
    }


    private void printMovesArea(PrintWriter out, Integer totalMoves) {
        out.println("<div class='movesArea' id='movesArea'>");
        out.println("<h3>" + "Moves: " + totalMoves + "</h3>");
        out.println("</div>");

    }

    private void printPerformMoveButton(PrintWriter out) {
        out.println("<div class='preformMove' id='movesHuman'>");
        out.println("<button id='preformMove' value='performMove' onclick='userPerformMove()'>Perform move</button> <br>");
        out.println("</div>");
    }

    private void printGameTitles(PrintWriter out, GamesManager gamesManager, String gameTitle) {
        String organizer = gamesManager.getGamePropertyByName(gameTitle).getOrgenizer();
        out.println("<div class='gameTitle'>");
        out.println("<h2> Game: " + gameTitle + "</h2>");
        out.println("<h4> Organizer: " + organizer + "</h4>");
        out.println("</div>");
    }

    private void printPlayersList(PrintWriter out, Game game, String userName) {
        out.println("<div id ='sidebar' class='sidebar'>");
        out.println("<h4>Players</h4>");
        out.println("<div class='input'>");
        out.println("<table class='table table-hover' id='userstable'>");
        boolean isBasicGame = game.isBasicGame();
        if(isBasicGame){
            out.println("<thead><th>Name</th><th>Type</th><th>Row/Column</th><th>Score</th></thead>");
        }
        else {
            out.println("<thead><th>Name</th><th>Type</th><th>Color</th><th>Score</th></thead>");

        }

        out.println("<tbody id='userslist'>");
        ArrayList<Player> players = game.getPlayersList();
        for (Player player : players) {
            String playerClass = getPlayerClass(player,game);
            boolean isComputer = player.GetIsComputer();
            int score = player.getScore();
            if (player.GetPlayerName().equals(userName)) {
                if (!isComputer) {
                    if (playerClass.equals("currentPlayer")) {
                        if (isBasicGame){
                            out.println("<tr bgcolor='greenyellow'><td>(Me) " + player.GetPlayerName() + "</td><td> Human </td><td>" + player.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            out.println("<tr bgcolor='greenyellow'><td>(Me) " + player.GetPlayerName() + "</td><td> Human </td><td>" + player.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    } else {
                        if (isBasicGame){
                            out.println("<tr ><td>(Me) " + player.GetPlayerName() + "</td><td> Human </td><td>" + player.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            out.println("<tr ><td>(Me) " + player.GetPlayerName() + "</td><td> Human </td><td>" + player.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                } else {
                    if (playerClass.equals("currentPlayer")) {
                        if (isBasicGame){
                            out.println("<tr bgcolor='greenyellow'><td>(Me) " + player.GetPlayerName() + "</td><td> Computer </td><td>" + player.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            out.println("<tr bgcolor='greenyellow'><td>(Me) " + player.GetPlayerName() + "</td><td> Computer </td><td>" + player.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                    else{
                        if (isBasicGame){
                            out.println("<tr><td>(Me) " + player.GetPlayerName() + "</td><td> Computer </td><td>" + player.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            out.println("<tr><td>(Me) " + player.GetPlayerName() + "</td><td> Computer </td><td>" + player.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                }
            }
            else {
                if (!isComputer) {
                    if (playerClass.equals("currentPlayer")) {
                        if (isBasicGame){
                            out.println("<tr bgcolor='greenyellow'><td>" + player.GetPlayerName() + "</td><td> Human </td><td>" + player.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            out.println("<tr bgcolor='greenyellow'><td>" + player.GetPlayerName() + "</td><td> Human </td><td>" + player.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                    else {
                        if(isBasicGame){
                            out.println("<tr><td>" + player.GetPlayerName() + "</td><td> Human </td><td>" + player.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            out.println("<tr><td>" + player.GetPlayerName() + "</td><td> Human </td><td>" + player.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                }
                else {
                    if (playerClass.equals("currentPlayer")) {
                        if (isBasicGame){
                            out.println("<tr bgcolor='greenyellow'><td>" + player.GetPlayerName() + "</td><td> Computer </td><td>" + player.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            out.println("<tr bgcolor='greenyellow'><td>" + player.GetPlayerName() + "</td><td> Computer </td><td>" + player.getColorName() + "</td><td>" + score + "</td></tr>");

                        }
                    }
                    else {
                        if(isBasicGame){
                            out.println("<tr><td>" + player.GetPlayerName() + "</td><td> Computer </td><td>" + player.RowColPlayer() + "</td><td>" + score + "</td></tr>");

                        }
                        else {
                            out.println("<tr><td>" + player.GetPlayerName() + "</td><td> Computer </td><td>" + player.getColorName() + "</td><td>" + score + "</td></tr>");
                        }
                    }
                }
            }
        }
        out.println("</tbody>");
        out.println("</table>");
        out.println("</div>");
        out.println("</div>");
    }


    private void printBoard(PrintWriter out, Game game, String userName) {

        Cell checkedCell = game.getCheckedCell();
        int checkedRow = -1;
        int checkedCol = -1;

        if(checkedCell != null){
            checkedRow = checkedCell.GetNumOfRow();
            checkedCol = checkedCell.GetNumOfCol();
        }

        Cell[][] arrayOfCells = game.GetBoard().getCellBoard();
        Integer numOfRows = game.GetBoard().NumOfRows();
        Integer numOfCols = game.GetBoard().NumOfCols();
        out.println("<div class='boardArea' id='boardArea'>");
        out.println("<table id='boardCells'>");

        out.println("<tr>");
        out.println("<td id='colIndex' class='colIndex'>" + "</td>");
        for (int i = 0; i < numOfCols; i++) {
            out.println("<td id='colIndex' class='colIndex'>" + (i + 1) + "</td>");
        }

        for (int i = 0; i < numOfRows; i++) {
            out.println("<tr>");
            out.println("<td id='rowIndex' class='rowIndex'>" + (i + 1) + "</td>");

            for (int j = 0; j < numOfCols; j++) {

                if( i== checkedRow && j == checkedCol){
                    out.println("<td class='boardCell' data-selected='true' row='" + i + "' col='" + j + "'" + "cellValue='" + arrayOfCells[i][j].GetNumInCell() + "' + color='" + arrayOfCells[i][j].getCellStringColor() +"'>" + arrayOfCells[i][j].GetNumInCell() + "</td>");
                }
                else {
                    out.println("<td class='boardCell' data-selected='false' row='" + i + "' col='" + j + "'" + "cellValue='" + arrayOfCells[i][j].GetNumInCell() + "' + color='" + arrayOfCells[i][j].getCellStringColor() +"'>" + arrayOfCells[i][j].GetNumInCell() + "</td>");

                }
            }
            out.println("</tr>");

        }
        out.println("</table>");
        out.println("</div>");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}