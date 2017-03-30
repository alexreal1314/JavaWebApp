package Servlets;

import Logic.*;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 15/02/2017.
 */
@WebServlet(name = "SingleGameServlet", urlPatterns = {"/singlegame"})
public class SingleGameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String userName = SessionUtils.getUsername(request);
        String gameTitle = gamesManager.getGameTitleByUser(userName);
        Game gameManager = gamesManager.getGameByTitle(gameTitle);
        PrintWriter out = response.getWriter();
        if (request.getParameter("playerActive") != null){
            checkIfPlayerIsActive(request, response,userName,gameManager);
        }
        else if (request.getParameter("isHuman") != null){
            checkIfPlayerIsHuman(request,response, gameManager, userName);
        }
        else if (request.getParameter("performMove") != null) {
            try {
                performMove(request, response, gameManager, userName, out);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (request.getParameter("nextPlayer") != null) {
            nextPlayer(gameManager);
        }
        else if(request.getParameter("checkForTechWin") != null) {
            String win = null;
            if (gameManager.HowManyPlayersLeftInTheGame() == 1  && gameManager.isGameStarted() && (gameManager.getNumOfNecessaryPlayers() > 1)){
                win = "win";

                gamesManager.getGamePropertyByName(gameTitle).setGameStatus("Finished");
                //gameManager.resetGameStatus();
                gameManager.setGameFinished();
                gameManager.ResetPlayersCount();
                gameManager.clearPlayersScore();
                gameManager.ClearPlayersTech();
                gamesManager.getGamePropertyByName(gameTitle).setPlayersCount(gamesManager.setPlayerCount(gameManager));
            }
            Gson gson = new Gson();
            String json = gson.toJson(win);
            out.println(json);
            out.flush();

        }
        else if(request.getParameter("performComputerMove") != null){
            performComputerMove(out, gameManager, userName);

        }
        else if (request.getParameter("winnerExist") != null){
            checkIfGameOver(out, gameManager, gamesManager, gameTitle);
        }
        else if (request.getParameter("cleanGame") != null){
            gameManager.ResetPlayersCount();
            //gameManager.clearPlayersScore();
            //gameManager.ClearPlayersTech();
            //gamesManager.getGamePropertyByName(gameTitle).setPlayersCount(gamesManager.setPlayerCount(gameManager));
            gamesManager.getGamePropertyByName(gameTitle).setPlayersCount(gamesManager.setPlayerCount(gameManager));
            gamesManager.getGamePropertyByName(gameTitle).setGameStatus("Finished");
            System.out.println("cleanGame");
        }
        else if(request.getParameter("getCellData") != null){
            doPost(request, response);
        }
        else if (request.getParameter("humanCanPlay") != null){
            checkIfHumanCanPlay(request, response, gameManager, userName, out);
        }
        else if (request.getParameter("gameStarted") != null){
            Gson gson = new Gson();
            String json = gson.toJson(gameManager.isGameStarted());
            out.println(json);
            out.flush();
        }
    }

    private void checkIfHumanCanPlay(HttpServletRequest request, HttpServletResponse response, Game gameManager, String userName, PrintWriter out){
        boolean res = gameManager.CheckCanMakeMove();
        Gson gson = new Gson();
        String json;

        if(res == false){

            MoveError errorObject = new MoveError();
            errorObject.setParams("false", "No available moves for you: " + userName);
            json = gson.toJson(errorObject);
        }
        else {

            MoveError errorObject = new MoveError();
            errorObject.setParams("true", "check");
            json = gson.toJson(errorObject);
        }

        out.println(json);
        out.flush();

    }

    private void checkIfGameOver(PrintWriter out, Game gameManager, GamesManager gamesManager, String gameTitle) {
        Gson gson = new Gson();
        GameMessage jsonMsg = new GameMessage();
        String json;

        if (gameManager.isGameFinished()){
            String winners = gameManager.getWinnersList();
            //gameManager.ResetPlayersCount();
            //gameManager.clearPlayersScore();
            //gameManager.ClearPlayers();

            gamesManager.getGamePropertyByName(gameTitle).setPlayersCount(gamesManager.setPlayerCount(gameManager));
            gamesManager.getGamePropertyByName(gameTitle).setGameStatus("Finished");
            jsonMsg.setParams("true", winners);
            json = gson.toJson(jsonMsg);
        }
        else {
            jsonMsg.setParams("false", "check");
            json = gson.toJson(jsonMsg);

        }

        out.println(json);
        out.flush();
    }

    private void nextPlayer(Game gameManager) { }

    private void performComputerMove(PrintWriter out, Game gameManager, String userName) {

        boolean couldMakeMove;

        if (!gameManager.isBasicGame()){
            couldMakeMove = gameManager.compAdvancedMoveMaker(userName);
        }
        else {
            couldMakeMove = gameManager.compBasicMoveMaker(userName);
        }

        Gson gson = new Gson();
        String json = gson.toJson("computer move done!");
        if(couldMakeMove){
            MoveError errorObject = new MoveError();
            errorObject.setParams("true", "Computer did a move!");
            json = gson.toJson(errorObject);
        }
        else {
            MoveError errorObject = new MoveError();
            errorObject.setParams("false", "No available moves for computer player: " + userName);
            json = gson.toJson(errorObject);

        }

        out.println(json);
        out.flush();
    }

    private void performMove(HttpServletRequest request, HttpServletResponse response, Game gameManager, String userName, PrintWriter out) throws Exception {

        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");
        Cell checkedCell = gameManager.getCheckedCell();
        Player currentPlayer = gameManager.getPlayerManagerByName(userName);

        Gson gson = new Gson();
        String json = gson.toJson("check");

        if(!gameManager.isBasicGame()) {


            if (gameManager.getCheckedCell() != null) {
                if (gameManager.GetBoard().getColX() - 1 == checkedCell.GetNumOfCol() || gameManager.GetBoard().getRowX() - 1 == checkedCell.GetNumOfRow()) {
                    if (currentPlayer.getColorName().equals(checkedCell.getCellStringColor())) {
                        gameManager.performHumanAdvanceMove();
                        gameManager.resetCheckedCell();

                    } else {
                        MoveError errorObject = new MoveError();
                        errorObject.setParams("error", "You need to choose your color");
                        json = gson.toJson(errorObject);
                    }
                } else {
                    json = gson.toJson("You need to choose a number in row number: " + gameManager.GetBoard().getRowX() + " or column number: " + gameManager.GetBoard().getColX());
                    MoveError errorObject = new MoveError();
                    errorObject.setParams("error", "You need to choose a number in row number: " + gameManager.GetBoard().getRowX() + " or column number: " + gameManager.GetBoard().getColX());
                    json = gson.toJson(errorObject);
                }
            } else {
                MoveError errorObject = new MoveError();
                errorObject.setParams("error", "You need to select a cell first!");
                json = gson.toJson(errorObject);
            }
        }
        else {

            if (gameManager.getCheckedCell() != null) {
                if (gameManager.getCurrentPlayerIndex() == 0) {
                    if (checkedCell.GetNumInCell() != " " && checkedCell.GetNumInCell() != "X"){
                        if(gameManager.GetBoard().getRowX() - 1 == checkedCell.GetNumOfRow()){
                            gameManager.performHumanBasicMove();

                        }
                        else {
                            MoveError errorObject = new MoveError();
                            errorObject.setParams("error", "You need to choose a number in row number: "+ gameManager.GetBoard().getRowX());
                            json = gson.toJson(errorObject);
                        }
                    }
                    else {
                        MoveError errorObject = new MoveError();
                        errorObject.setParams("error", "You need to choose a number");
                        json = gson.toJson(errorObject);
                    }
                }
                else if (gameManager.getCurrentPlayerIndex() == 1) {
                    if (checkedCell.GetNumInCell() != " " && checkedCell.GetNumInCell() != "X"){
                        if(gameManager.GetBoard().getColX() - 1 == checkedCell.GetNumOfCol()){
                            gameManager.performHumanBasicMove();

                        }
                        else {
                            MoveError errorObject = new MoveError();
                            errorObject.setParams("error", "You need to choose a number in column number: "+ gameManager.GetBoard().getColX());
                            json = gson.toJson(errorObject);
                        }
                    }
                    else {
                        MoveError errorObject = new MoveError();
                        errorObject.setParams("error", "You need to choose a number");
                        json = gson.toJson(errorObject);
                    }
                }
            }
            else {
                MoveError errorObject = new MoveError();
                errorObject.setParams("error", "You need to select a cell first!");
                json = gson.toJson(errorObject);
            }
        }
        out.println(json);
        out.flush();
    }

    private void checkIfPlayerIsHuman(HttpServletRequest request, HttpServletResponse response, Game gameManager, String userName) throws IOException {
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        if (gameManager.getPlayerManagerByName(userName).GetIsComputer()){
            out.println(gson.toJson(false));
        }
        else{
            out.println(gson.toJson(true));
        }
        out.flush();
    }

    private void checkIfPlayerIsActive(HttpServletRequest request, HttpServletResponse response, String userName, Game gameManager) throws IOException {
        String nameOfCurrentPlayer = gameManager.getCurrentPlayerManager().GetPlayerName();
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        if (nameOfCurrentPlayer.equals(userName) && gameManager.isGameStarted()){
            out.println(gson.toJson(true));
        }
        else{
            out.println(gson.toJson(false));
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        GamesManager gamesManager = ServletUtils.getGamesManager(getServletContext());
        String userName = SessionUtils.getUsername(request);
        String gameTitle = gamesManager.getGameTitleByUser(userName);
        Game gameManager = gamesManager.getGameByTitle(gameTitle);

        String str1 = request.getParameter("selected");
        String str2 = request.getParameter("cellRow");

        if (request.getParameter("selected").equals("true")) {
            saveCheckedCellParams(request, response, gameManager);

        }
        else {
            gameManager.resetCheckedCell();
        }
    }

    private void saveCheckedCellParams(HttpServletRequest request, HttpServletResponse response, Game gameManager) {
        String row = request.getParameter("cellRow");
        String col = request.getParameter("cellCol");
        String value = request.getParameter("cellValue");
        boolean checked = true;
        gameManager.saveCheckedCell(row, col, value, checked);

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
