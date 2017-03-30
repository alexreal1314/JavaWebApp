package Logic;
import Generated.GameDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.util.*;

import Generated.GameDescriptor.Board.Structure.Squares.Square;
/**
 * Created by alex on 20/11/2016.
 */
public class Game {
    public final int k_PlayerOne = 0;
    public final int k_PlayerTwo = 1;
    private Board m_Board;
    private int numOfMoves = 0;
    private long startTime;
    private XMLReader xmlReader = new XMLReader();
    private GameDescriptor generatedGame;
    private ArrayList<Player> players = new ArrayList<Player>();
    private List<Cell> movesList = new ArrayList<>(); // ADD
    private List<Cell> markerList = new ArrayList<>(); // ADD

    HashMap<Integer, String> playersID;
    HashMap<Integer, String> playersColor;

    private SimpleStringProperty numOfMovesString;
    private Boolean basicGame = false;

    private String m_title;
    private Integer m_numOfPlayersInGame;
    private String m_organizer;
    private String m_playersCount;
    private String m_boardSize;
    private int m_numOfNecessaryPlayersForGame;
    private Boolean m_gameStarted;
    private int m_ID = 1;

    private Player m_currentPlayerManager;
    private int currentPlayerInd = 0;
    private Cell checkedCell = null;
    private boolean gameFinished = false;

    private HashMap<Integer, Boolean> availableColors;

    private HashMap<Integer, Boolean> basicPlayers;


    private ChatManager myChat;


    public ChatManager getChat() {
        return myChat;
    }

    public void initChatManager() {
        myChat = new ChatManager();
    }

    public void initAvailableColors(){
        availableColors = new HashMap<Integer, Boolean>();
        for (int i = 1; i <= 6; i++){
            availableColors.put(i, true);
        }

    }

    public void initAvailableBasicPlayers(){
        basicPlayers = new HashMap<Integer, Boolean>();
        for (int i = 1; i <= 2; i++){
            basicPlayers.put(i, true);
        }

    }


    public ArrayList<Player> getPlayersList() { return players;}

    public int getCurrentPlayerIndex() { return currentPlayerInd; }

    public void NextPlayerIndex() { currentPlayerInd = (currentPlayerInd + 1)% m_numOfPlayersInGame; }

    public void clearPlayersScore() {

        for (Player p : players) {
            p.setScore(0);
        }

    }

    public Player getPlayerByColor(javafx.scene.paint.Color i_color) {
        if (i_color == players.get(0).getActualColor())
            return players.get(0);
        else if (i_color == players.get(1).getActualColor())
            return players.get(1);
        else if (i_color == players.get(2).getActualColor())
            return players.get(2);
        else if (i_color == players.get(3).getActualColor())
            return players.get(3);
        else if (i_color == players.get(4).getActualColor())
            return players.get(4);
        else if (i_color == players.get(5).getActualColor())
            return players.get(5);
        else
            return null;
    }

    public List<Player> GetPlayersList() {
        return players;
    }

    // ADD
    public void addMoveToList(Cell myMove) {
        movesList.add(myMove);
    }

    // ADD
    public void addMarkerToList(Cell myMarker) {
        markerList.add(myMarker);
    }

    // ADD
    public List<Cell> getMarkerList() {
        return markerList;
    }

    public Cell getCellInBorad(int row, int col) {
        return m_Board.getCellBoard()[row][col];
    }

    // ADD
    public List<Cell> getMovesList() {
        return movesList;
    }

    public boolean isBasicGame() {
        return basicGame;
    }

    public Board GetBoard() {
        return m_Board;
    }

    public long calcTimeFromStart() {
        long currentTime = System.currentTimeMillis() - startTime;
        return currentTime / 1000;
    }

    public SimpleStringProperty AddMove() {
        numOfMoves += 1;
        numOfMovesString.setValue(String.valueOf(numOfMoves));
        return numOfMovesString;
    }

    public SimpleStringProperty SubtractMove() {
        numOfMoves -= 1;
        numOfMovesString.setValue(String.valueOf(numOfMoves));
        return numOfMovesString;
    }

    public int getNumOfMoves() {
        return numOfMoves;
    }

    public Game() {
        numOfMoves = 0;
        numOfMovesString = new SimpleStringProperty();
    }

    public void ClearPlayers(String userName) {

        int i=0;
        for (Player p : players){
            if (p.GetPlayerName().equals((userName))){
                players.remove(i);
                m_numOfPlayersInGame--;
            }
            i++;
        }

        //players.clear();
    }

    public void ClearPlayersTech() {
        players.clear();
    }

    public void ResetMoves() {
        numOfMoves = 0;
        numOfMovesString.setValue(String.valueOf(numOfMoves));
    }

    public void ResetPlayersCount() {
        m_numOfPlayersInGame = 0;
        //m_playersCount = null;
    }

    public void resetGameStatus() {
        m_gameStarted = false;
    }

    public void resetGameFinish() { gameFinished = false; }

    public void InitTime() {
        startTime = System.currentTimeMillis();
    }

    public Player GetPlayerX(int i_NumOfPlayer) {
        return players.get(i_NumOfPlayer);
    }

    public String GetNameOfPlayerX(int i_NumOfPlayer) {
        return players.get(i_NumOfPlayer).GetPlayerName();
    }

    public boolean GetIsPlayerComp(int i_NumOfPlayer) {
        return players.get(i_NumOfPlayer).GetIsComputer();
    }

    public void LoadXML(String source) throws JAXBException, SAXException, InvalidXMLException, InvalidExtension, XmlValueException {
        xmlReader.LoadXML(source);
    }

    public void LoadXML() throws JAXBException, SAXException, InvalidXMLException, InvalidExtension, XmlValueException {

    }

    public void InitFromGeneratedGame(InputStream gameFile, String Organizer, int basicGameNum) throws XmlValueException {
        Channel channel = Channels.newChannel(gameFile);
        generatedGame = xmlReader.GetXMLGame();

        // CHECKS
        if (generatedGame.getGameType().equals("AdvanceDynamic")) {
            basicTypeChecker();
            DynamicAdvancedTypeChecker(Organizer);
            initAvailableColors();
            InitAdvancedBoard();
            basicGame = false;

        } else if (generatedGame.getGameType().equals("Basic")) {
            basicGame = true;
            basicTypeChecker();
            InitBasicBoard();
            initBasicGameProperites(Organizer, basicGameNum);
            initAvailableBasicPlayers();
        } else if (generatedGame.getGameType().equals("Advance")) {
            basicGame = false;
            basicTypeChecker();
            advancedTypeChecker();
            InitAdvancedBoard();
        } else {
            throw new XmlValueException("Invalid game type!");
        }

    }

    private void initBasicGameProperites(String Organizer, int basicGameNum) {
        m_title = "Basic game - " + basicGameNum;
        m_numOfPlayersInGame = 0;
        m_numOfNecessaryPlayersForGame = 2;
        m_organizer = Organizer;
        m_playersCount = String.valueOf(2);
        m_boardSize = String.valueOf(generatedGame.getBoard().getSize());
        m_gameStarted = false;

    }

    private void DynamicAdvancedTypeChecker(String Organizer) throws XmlValueException {
        m_title = generatedGame.getDynamicPlayers().getGameTitle();
        m_numOfPlayersInGame = 0;
        m_numOfNecessaryPlayersForGame = generatedGame.getDynamicPlayers().getTotalPlayers();
        m_organizer = Organizer;
        m_playersCount = String.valueOf(generatedGame.getDynamicPlayers().getTotalPlayers());
        m_boardSize = String.valueOf(generatedGame.getBoard().getSize());
        m_gameStarted = false;

        if(m_title.isEmpty()){
            throw new XmlValueException("You need to insert a title for the game");
        }

        if(generatedGame.getBoard().getStructure().getType().equals("Random")){
            int range = Math.abs(generatedGame.getBoard().getStructure().getRange().getTo() - generatedGame.getBoard().getStructure().getRange().getFrom());
            if (generatedGame.getBoard().getStructure().getRange().getFrom() < 0)
                range++;
            int boardSize = generatedGame.getBoard().getSize().intValue();
            if(((boardSize*boardSize-1)/range/m_numOfNecessaryPlayersForGame < 1)){
                players.clear();
                throw new XmlValueException("Invalid board size according to number of players and range");
            }
        }

        if (generatedGame.getDynamicPlayers().getTotalPlayers() < 3 || generatedGame.getDynamicPlayers().getTotalPlayers() > 6){
            throw new XmlValueException("Invalid number of players, number of players needs to be 3-6");
        }
    }

    private void advancedTypeChecker() throws XmlValueException {

        int numOfPlayers = generatedGame.getPlayers().getPlayer().size();
        players = new ArrayList<>(numOfPlayers);
        playersID = new HashMap<>(numOfPlayers);
        String playerName;
        String playerType;
        playersColor = new HashMap<>(numOfPlayers);


        for (int i = 0; i < numOfPlayers; i++) {

            int playerId = generatedGame.getPlayers().getPlayer().get(i).getId().intValue();
            playerName = generatedGame.getPlayers().getPlayer().get(i).getName();
            playerType = generatedGame.getPlayers().getPlayer().get(i).getType();
            int playerColor = generatedGame.getPlayers().getPlayer().get(i).getColor();

            if (!playersID.isEmpty() && playersID.containsKey(playerId)) {

                players.clear();
                throw new XmlValueException("There are 2 players with the same ID value!");
            }
            else if (!playersColor.isEmpty() && playersColor.containsKey(playerColor)){
                players.clear();
                throw new XmlValueException("There are 2 players with the same Color!");
            }
            else {
                //Cell.Color color = Cell.Color.getStatus(playerColor);
                players.add(new Player(playerId, playerName, playerType, playerColor));
                playersID.put(playerId, playerName);
                playersColor.put(playerColor, playerName);
            }
        }

        if(generatedGame.getBoard().getStructure().getType().equals("Random")){
            int range = Math.abs(generatedGame.getBoard().getStructure().getRange().getTo() - generatedGame.getBoard().getStructure().getRange().getFrom());
            if (generatedGame.getBoard().getStructure().getRange().getFrom() < 0)
                range++;
            int boardSize = generatedGame.getBoard().getSize().intValue();
            if(((boardSize*boardSize-1)/range/numOfPlayers < 1)){
                players.clear();
                throw new XmlValueException("Invalid board size according to number of players and range");
            }
        }

        if (generatedGame.getPlayers().getPlayer().size() < 3 || generatedGame.getPlayers().getPlayer().size() > 6){
            throw new XmlValueException("Invalid number of players, number of players needs to be 3-6");
        }


    }

        private void basicTypeChecker() throws  XmlValueException{
        if (generatedGame.getBoard().getStructure().getType().equals("Random")) {
            if (generatedGame.getBoard().getSize().intValue() < 5 || generatedGame.getBoard().getSize().intValue() > 50) {
                throw new XmlValueException("Invalid Board Size, Required: 5 - 50");
            }
            if (generatedGame.getBoard().getStructure().getRange().getFrom() > generatedGame.getBoard().getStructure().getRange().getTo()) {
                throw new XmlValueException("Invalid Range Of Values, 'from' attribute needs to be smaller than 'to'");
            }
            int numbers = Math.abs(generatedGame.getBoard().getStructure().getRange().getTo() - generatedGame.getBoard().getStructure().getRange().getFrom());

            if (generatedGame.getBoard().getStructure().getRange().getFrom() < 0) {
                numbers++;
            }

            if (numbers > (generatedGame.getBoard().getSize().intValue() * generatedGame.getBoard().getSize().intValue()) - 1) {
                throw new XmlValueException("Invalid Board Size To Range Ratio, Required Condition: (Size^2 - 1)/|Range| >= 1");
            }
            if (generatedGame.getBoard().getStructure().getRange().getFrom() < -99 || generatedGame.getBoard().getStructure().getRange().getTo() > 99) {
                throw new XmlValueException("Invalid Range, Range needs to be [-99, 99]");
            }
        } else if (generatedGame.getBoard().getStructure().getType().equals("Explicit")) {
            if (generatedGame.getBoard().getSize().intValue() < 5 || generatedGame.getBoard().getSize().intValue() > 50) {
                throw new XmlValueException("Invalid Board Size, Required: 5 - 50");
            }


            for (Square sqr : generatedGame.getBoard().getStructure().getSquares().getSquare()) {
                if (sqr.getColumn().intValue() > generatedGame.getBoard().getSize().intValue() || sqr.getRow().intValue() > generatedGame.getBoard().getSize().intValue()) {
                    throw new XmlValueException("Invalid Square Coordinates, Each Cell's Coordinates Needs To Be Within Board Bounds");
                }

                if (sqr.getValue().intValue() < -99 || sqr.getValue().intValue() > 99) {
                    throw new XmlValueException("Invalid Value, Numbers Need To Be In Range Of [-99, 99]");
                }
            }
            for (Square sqr1 : generatedGame.getBoard().getStructure().getSquares().getSquare()) {
                for (Square sqr2 : generatedGame.getBoard().getStructure().getSquares().getSquare()) {
                    if (sqr1 != sqr2) {
                        if ((sqr1.getRow().intValue() == sqr2.getRow().intValue()) && (sqr1.getColumn().intValue() == sqr2.getColumn().intValue())) {
                            throw new XmlValueException("Duplicate Cells Found, Each Cell Must Have Unique Coordinates");
                        }
                    }
                }
            }

            if(generatedGame.getBoard().getStructure().getSquares().getMarker().getRow().intValue() > generatedGame.getBoard().getSize().intValue() || generatedGame.getBoard().getStructure().getSquares().getMarker().getColumn().intValue() > generatedGame.getBoard().getSize().intValue()){
                throw new XmlValueException("Invalid Row or Column Coordinates of the Marker!");
            }
        }

    }

    public void InitBasicBoard(){
        m_Board = new Board(generatedGame.getBoard().getSize(),generatedGame.getBoard().getSize(), this);
        if (generatedGame.getBoard().getStructure().getType().equals("Random")) {
            m_Board.initBasicRandomBoard(generatedGame.getBoard().getStructure().getRange().getFrom(), generatedGame.getBoard().getStructure().getRange().getTo());
        }
        else if (generatedGame.getBoard().getStructure().getType().equals("Explicit")){
            m_Board.initExplicitBoard(generatedGame.getBoard().getStructure().getSquares().getSquare(), generatedGame.getBoard().getStructure().getSquares().getMarker());
        }
    }

    private void InitAdvancedBoard(){
        m_Board = new Board(generatedGame.getBoard().getSize(),generatedGame.getBoard().getSize(), this);
        if (generatedGame.getBoard().getStructure().getType().equals("Random")) {
            m_Board.initAdvancedRandomBoard(generatedGame.getBoard().getStructure().getRange().getFrom(), generatedGame.getBoard().getStructure().getRange().getTo());
        }
        else if (generatedGame.getBoard().getStructure().getType().equals("Explicit")){
            m_Board.initAdvancedExplicitBoard(generatedGame.getBoard().getStructure().getSquares().getSquare(), generatedGame.getBoard().getStructure().getSquares().getMarker());
        }

    }

    public void InitBasicPlayers(List<String> i_basicPlayers) {

        Player player1 = new Player(1, i_basicPlayers.get(0),i_basicPlayers.get(1), 5/*Cell.Color.BLACK*/);
        Player player2 = new Player(2, i_basicPlayers.get(2),i_basicPlayers.get(3), 5/*Cell.Color.BLACK*/);
        players.add(player1);
        players.add(player2);
    }

    public boolean isPlayerExist(String usernameFromSession) {
        boolean isExist = false;
        for (Player player : players ) {
            if(player.GetPlayerName().equals(usernameFromSession)){
                isExist = true;
            }
        }
        return isExist;
    }

    public String getGameTitle() {
        //return generatedGame.getDynamicPlayers().getGameTitle();
        return m_title;
    }

    public Integer getNumOfPlayersInTheGame(){
        return m_numOfPlayersInGame;
    }

    public void DeleteUserFromGame(String username) {
        for (Player player: players ) {
            if(player.GetPlayerName().equals(username)){
                players.remove(player);
                m_numOfPlayersInGame--;
                System.out.println("DELETE user: " + username + " from game: "  + m_title);
            }
            else{
                System.out.println("not-----DELETE user: " + username + " from game: "  + m_title);

            }

        }
        System.out.println(" finish all games");
    }

    public List <Player> getListOfPlayersManagers() {
        return players;
    }

    public String getTitle() {
        return m_title;
    }

    public String getNumOfPlayers(){
        return m_playersCount;
    }

    public void readFromInput(InputStream gameFile) throws SAXException, JAXBException, InvalidXMLException, InvalidExtension {

        Channel channel = Channels.newChannel(gameFile);
        m_numOfPlayersInGame = 0;
        xmlReader.deserializeFrom(gameFile);
    }

    public void setMaximumNumOfPlayers(int maximumNumOfPlayers) {
        m_numOfNecessaryPlayersForGame = maximumNumOfPlayers;
    }

    public boolean isFullOfPlayers() {
        return m_numOfPlayersInGame == m_numOfNecessaryPlayersForGame;
    }

    public boolean isGameStarted() {
        return m_gameStarted;
    }

    public void addUserToTheGame(String name, boolean isHuman) throws Exception {

        if(!basicGame) {
            Player player;
            try {
                String type;
                if (isHuman) {
                    type = "Human";
                } else {
                    type = "Computer";
                }


                int newPlayerID = 0;

                for (int i = 1; i <= 6; i++) {
                    boolean isColorFree = availableColors.get(i);
                    if (isColorFree) {
                        newPlayerID = i;
                        availableColors.put(i, false);
                        break;
                    }
                }

                player = new Player(newPlayerID, name, type, newPlayerID);
                if (!FindUserInTheGame(name)) {
                    if (m_numOfPlayersInGame == 0) {
                        m_currentPlayerManager = player;
                    }
                    players.add(player);

                    m_ID++;
                    m_numOfPlayersInGame++;
                    if (m_numOfPlayersInGame == m_numOfNecessaryPlayersForGame) {
                        m_gameStarted = true;
                        m_currentPlayerManager = players.get(0);
                        //startNewGames();
                    }
                    //m_winner = null;
                } else {
                    System.out.println("throw EX 2 users with same name");
                    throw new Exception("Found 2 players with same name!");
                }
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        else {
            Player player;

            try {
                String type;
                if (isHuman) {
                    type = "Human";
                } else {
                    type = "Computer";
                }


                int newPlayerID = 5;


                for (int i = 1; i <= 2; i++) {
                    boolean isPlayerFree = basicPlayers.get(i);
                    if (isPlayerFree) {
                        newPlayerID = i;
                        basicPlayers.put(i, false);
                        break;
                    }
                }

                player = new Player(newPlayerID, name, type, 5);
                if (!FindUserInTheGame(name)) {
                    if (m_numOfPlayersInGame == 0) {
                        m_currentPlayerManager = player;
                    }
                    players.add(player);

                    m_ID++;
                    m_numOfPlayersInGame++;
                    if (m_numOfPlayersInGame == m_numOfNecessaryPlayersForGame) {
                        m_gameStarted = true;
                        m_currentPlayerManager = players.get(0);
                        //startNewGames();
                    }
                    //m_winner = null;
                } else {
                    System.out.println("throw EX 2 users with same name");
                    throw new Exception("Found 2 players with same name!");
                }
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }

        }
    }

    public GameDescriptor getGameDescriptor() {
        return generatedGame;
    }

    private boolean FindUserInTheGame(String name){
        for (Player player:players ) {
            if(player.GetPlayerName().equals(name)){
                return true;
            }
        }

        return false;
    }

    public void setPlayersCount(String playersCount){
        m_playersCount = playersCount;
    }

    public int getCurrentPlayerID() { return m_ID;}

    public void increaseCurrentPlayerID() { m_ID = ((m_ID + 1) % m_numOfNecessaryPlayersForGame);}

    public Player getPlayerManagerByName(String userName) {
        Player res = null;
        for (Player player : players){
            if (player.GetPlayerName().equals(userName)){
                res = player;
            }
        }
        return res;
    }

    public Player getCurrentPlayerManager() {
        return m_currentPlayerManager;
    }

    public void leaveTheGame(String userName) {

        Player playerToRemove = null;
        int index = 0;
        int lastIndex = players.size() - 1;


        for (Player player: players ) {
            if(player.GetPlayerName().equals(userName)) {
                if (m_gameStarted) {
                    if (m_currentPlayerManager.GetPlayerName().equals(userName)) {
                        if (m_numOfPlayersInGame == 1) {
                            m_currentPlayerManager = players.get(0);
                            m_numOfPlayersInGame--;
                        } else if (index < lastIndex) {

                            players.remove(index);
                            m_currentPlayerManager = players.get(currentPlayerInd);
                            //NextPlayerIndex();
                            m_numOfPlayersInGame--;

                        } else if (index == lastIndex) {
                            players.remove(index);
                            currentPlayerInd = 0;
                            m_currentPlayerManager = players.get(currentPlayerInd);
                            m_numOfPlayersInGame--;
                        }
                    }

                    int newPlayerID = player.getID();
                    availableColors.put(newPlayerID, true);
                    break;
                }
                else {

                    if (m_numOfPlayersInGame == 1) {
                        players.remove(index);
                        m_currentPlayerManager = null;
                        m_numOfPlayersInGame--;
                    }
                    else if (index == 0) {
                        players.remove(index);
                        m_currentPlayerManager = players.get(index);
                        //NextPlayerIndex();
                        m_numOfPlayersInGame--;
                    }
                    else {
                        players.remove(index);
                        m_numOfPlayersInGame--;
                    }

                    int newPlayerID = player.getID();
                    availableColors.put(newPlayerID, true);
                    break;

                }
            }
            index++;
        }
    }

    public void leaveTheGameBasic(String userName){


        if (m_gameStarted){
            gameFinished = true;
            if(players.get(0).GetPlayerName().equals(userName)){
                players.remove(0);
            }
            else {
                players.remove(1);
            }
            m_numOfPlayersInGame--;
        }
        else {
            if(players.get(0).GetPlayerName().equals(userName)){
                int newPlayerID = players.get(0).getID();
                players.remove(0);
                basicPlayers.put(1, true);
            }
            else {
                int newPlayerID = players.get(1).getID();
                players.remove(1);
                basicPlayers.put(2, true);
            }

            m_numOfPlayersInGame--;
        }
    }

    public int getNumOfNecessaryPlayers() { return m_numOfNecessaryPlayersForGame; }

    public int HowManyPlayersLeftInTheGame() {
        //return players.size();
        return m_numOfPlayersInGame;
    }

    private void switchCurrentPlayer() {
        if (players.size() == 1){
            //m_currentPlayerManager = null;
            m_currentPlayerManager = null;

            m_numOfPlayersInGame = 0;
            players.clear();
        }
        else{
            int index = 0;
            int lastIndex = players.size() - 1;
            for (Player o : players){
                if (o == m_currentPlayerManager){
                    if (index == lastIndex){
                        m_currentPlayerManager = players.get(0);
                        break;
                    }
                    else{
                        //m_currentPlayerManager = players.get(index + 1);
                        //NextPlayerIndex();
                        Player player = o;
                        int newPlayerID = player.getID();
                        availableColors.put(newPlayerID, true);
                        players.remove(player);
                        m_currentPlayerManager = players.get(currentPlayerInd);
                        m_numOfPlayersInGame--;
                        break;
                    }
                }
                index++;
            }

        }
        //remove this annotation
        if (m_currentPlayerManager != null) {
            System.out.println("currnrPlayerManager is now on:" + m_currentPlayerManager.GetPlayerName());
        }
        else{
            System.out.println("currnrPlayerManager is now NULLL");
        }
    }

    public void saveCheckedCell(String row, String col, String value, boolean checked) {

        checkedCell = m_Board.getCellBoard()[Integer.parseInt(row)][Integer.parseInt(col)];

    }

    public Cell getCheckedCell() {
        return checkedCell;
    }

    public void resetCheckedCell() {
        checkedCell = null;
    }

    public boolean CheckCanMakeMove(){

        boolean res = true;
        if(!basicGame){
            res = m_Board.CanMakeMove(currentPlayerInd);
        }
        else {
            res = m_Board.BasicCheckColRow(currentPlayerInd);
        }

        if(res == false){
            NextPlayerIndex();
            m_currentPlayerManager = players.get(currentPlayerInd);
        }

        return res;
    }

    public boolean performHumanAdvanceMove() {
        //if(m_Board.CanMakeMove(currentPlayerInd)) {
            addMarkerToList(new Cell(m_Board.getRowX() - 1, m_Board.getColX() - 1, "Orange", "X"));
            Cell temp = new Cell(getCellInBorad(checkedCell.GetNumOfRow(), checkedCell.GetNumOfCol()).GetNumOfRow(), getCellInBorad(checkedCell.GetNumOfRow(), checkedCell.GetNumOfCol()).GetNumOfCol(), getCellInBorad(checkedCell.GetNumOfRow(), checkedCell.GetNumOfCol()).getCellStringColor(), getCellInBorad(checkedCell.GetNumOfRow(), checkedCell.GetNumOfCol()).GetNumInCell());
            addMoveToList(temp);
            m_Board.eraseX();
            //boardButtons[GameLoader.myGame.GetBoard().getRowX()-1][GameLoader.myGame.GetBoard().getColX()-1].setText(" ");

            GetPlayerX(currentPlayerInd).AddPoints(checkedCell.GetNumInCell());
            m_Board.UpdateCell(checkedCell.GetNumOfRow(), checkedCell.GetNumOfCol());
            m_Board.setColX(checkedCell.GetNumOfCol() + 1);
            m_Board.setRowX(checkedCell.GetNumOfRow() + 1);
            AddMove();
            numOfMoves = getNumOfMoves();

            //boardButtons[currentSelectedButton.getRow()][currentSelectedButton.getColumn()].setText("X");
            //boardButtons[currentSelectedButton.getRow()][currentSelectedButton.getColumn()].setTextFill(Color.ORANGE);

            //updatePlayerListView();
            NextPlayerIndex();
            //currentSelectedButton.setSelected(false);
            //currentSelectedButton.setId("boardButton");
            //checkWhosMove();
            m_currentPlayerManager = players.get(currentPlayerInd);
            return true;
        //}
        //return false;

    }


    public boolean compBasicMoveMaker(String userName){

        if(!m_Board.BasicCheckColRow(currentPlayerInd)) {
            NextPlayerIndex();
            m_currentPlayerManager = players.get(currentPlayerInd);
            return false;
        }
        else {

            int selection = m_Board.compMoveBasicGame(currentPlayerInd);
            Cell temp = new Cell(m_Board.getRowX()-1,m_Board.getColX()-1, "Orange","X");
            addMarkerToList(temp);

            m_Board.eraseX();
            Cell move;

            if (currentPlayerInd == 0) {
                GetPlayerX(currentPlayerInd).AddPoints(m_Board.getCellBoard()[m_Board.getRowX() - 1][selection - 1].GetNumInCell());
                move = new Cell(m_Board.getRowX() - 1,selection - 1, m_Board.getCellBoard()[m_Board.getRowX() - 1][selection -1].getCellStringColor(),m_Board.getCellBoard()[m_Board.getRowX() - 1][selection -1].GetNumInCell());
                addMoveToList(move);
                m_Board.setColX(selection);
                m_Board.UpdateCell(m_Board.getRowX() - 1, selection - 1);


            }
            else if (currentPlayerInd == 1){
                GetPlayerX(currentPlayerInd).AddPoints(m_Board.getCellBoard()[selection - 1][m_Board.getColX() - 1].GetNumInCell());
                move = new Cell(selection - 1,m_Board.getColX() - 1,m_Board.getCellBoard()[selection -1][m_Board.getColX() - 1].getCellStringColor(),m_Board.getCellBoard()[selection -1][m_Board.getColX() - 1].GetNumInCell());
                addMoveToList(move);
                m_Board.setRowX(selection);
                m_Board.UpdateCell(selection - 1, m_Board.getColX() - 1);
            }

            NextPlayerIndex();
            m_currentPlayerManager = players.get(currentPlayerInd);
            AddMove();
            return true;
        }
    }

    public /*ArrayList<Boolean>*/boolean compAdvancedMoveMaker(String userName) {
        //ArrayList<Boolean> moveResults = new ArrayList<>();
        //gameFinished = m_Board.AdvancedCheckColRow();
        //if(gameFinished == true){
            //moveResults.add(gameFinished);
            //return moveResults;
        //}
        //else {
            //moveResults.add(gameFinished);
            if(!m_Board.CanMakeMove(currentPlayerInd)) {
                NextPlayerIndex();
                m_currentPlayerManager = players.get(currentPlayerInd);
                return false;
                //moveResults.add(false);
            }
            else {
                //moveResults.add(true);
                List<Integer> rowCol = m_Board.compMoveAdvancedGame(currentPlayerInd);
                int row = rowCol.get(0);
                int col = rowCol.get(1);

                Cell marker = new Cell(m_Board.getRowX()-1,m_Board.getColX()-1, "Orange","X");
                addMarkerToList(marker);
                Cell temp = new Cell(row,col,m_Board.getCellBoard()[row][col].getCellStringColor(),m_Board.getCellBoard()[row][col].GetNumInCell());
                addMoveToList(temp);
                m_Board.eraseX();
                GetPlayerX(currentPlayerInd).AddPoints(m_Board.getCellBoard()[row][col].GetNumInCell());
                m_Board.setRowX(row + 1);
                m_Board.setColX(col + 1);
                m_Board.UpdateCell(row, col);
                AddMove();
                NextPlayerIndex();
                m_currentPlayerManager = players.get(currentPlayerInd);
                //return moveResults;
                return true;

            }
        //}
        //return moveResults;
    }


    public String getWinnersList(){
        List<Player> winners  = new ArrayList<Player>();
        for (Player p : GetPlayersList()){
            Player player = new Player();
            try {
                player = p.clone();
                winners.add(player);
            }
            catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }

        Collections.sort(winners, Player.scoreComparator );
        StringBuilder sb = new StringBuilder();
        int i = 1;
        sb.append(("Game over - Player Standings:\n"));
        for (Player p : winners){
            sb.append(String.valueOf(i)).append(". ").append(p.getScore()).append(" - ").append(p.GetPlayerName()).append("\n");
            i++;
        }
        return sb.toString();
    }

    public boolean gameIsFinished(){
        return gameFinished;
    }

    public boolean isGameFinished() {

        gameFinished = m_Board.AdvancedCheckColRow();
        return gameFinished;
    }

    public void setGameFinished() {
        gameFinished = true;
    }

    public void leaveGameRemoveCells(){
        if(m_gameStarted) {
            for (int i = 0; i < m_Board.NumOfRows(); i++) {
                for (int j = 0; j < m_Board.NumOfCols(); j++) {
                    if (m_Board.getCellBoard()[i][j].GetNumInCell() != " " && m_Board.getCellBoard()[i][j].getCellStringColor().equals(m_currentPlayerManager.getColorName())) {
                        Cell temp = new Cell(i, j, m_Board.getCellBoard()[i][j].getCellStringColor(), m_Board.getCellBoard()[i][j].GetNumInCell());
                        temp.SetQuit();
                        addMoveToList(temp);
                        m_Board.getCellBoard()[i][j].SetNumInCell(" ");
                    }
                }
            }

            m_currentPlayerManager.setIsQuit(true);
        }
    }

    public void performHumanBasicMove() {

        Cell temp1 = new Cell(m_Board.getRowX()-1,m_Board.getColX()-1,"Orange","X");
        //temp1.SetQuit();
        addMarkerToList(temp1);
        Cell temp2 = new Cell(checkedCell.GetNumOfRow(),checkedCell.GetNumOfCol(),m_Board.getCellBoard()[checkedCell.GetNumOfRow()][checkedCell.GetNumOfCol()].getCellStringColor(),checkedCell.GetNumInCell());
        //temp2.SetQuit();
        addMoveToList(temp2);

        m_Board.eraseX();
        GetPlayerX(currentPlayerInd).AddPoints(checkedCell.GetNumInCell());
        m_Board.UpdateCell(checkedCell.GetNumOfRow(), checkedCell.GetNumOfCol());
        m_Board.setColX(checkedCell.GetNumOfCol() + 1);
        m_Board.setRowX(checkedCell.GetNumOfRow() + 1);
        AddMove();

        NextPlayerIndex();
        m_currentPlayerManager = players.get(currentPlayerInd);

    }
}

