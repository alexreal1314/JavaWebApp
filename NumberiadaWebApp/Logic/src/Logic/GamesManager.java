package Logic;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alex on 09/02/2017.
 */
public class GamesManager {
    private ArrayList<Game> m_Games;
    private ArrayList<GameProperties>m_GamesProperties = new ArrayList<>();
    private HashMap<String, String> m_showBoardRequest;
    private int basicGameNum = 0;

    public class GameProperties{
        private String m_title;
        private String m_organizer;
        private String m_playersCount;
        private String m_totalMoves;
        private String m_boardSize;

        private String m_gameStatus;

        public GameProperties(String title, String organizer, String playersCount, String totalMoves, String boardSize, String gameStatus) {
            this.m_title = title;
            this.m_organizer = organizer;
            this.m_playersCount = playersCount;
            this.m_totalMoves = totalMoves;
            this.m_boardSize = boardSize;
            this.m_gameStatus = gameStatus;
        }

        public String getGameTitle() {
            return m_title;
        }

        public void setPlayersCount(String playersCount){
            m_playersCount = playersCount;
        }

        public String getPlayersCount() {
            return m_playersCount;
        }

        public String getOrgenizer() {
            return m_organizer;
        }

        public void setGameStatus(String status){
            this.m_gameStatus = status;
        }

    }


    public void removeUserFromShowBoard(String userName) {
        m_showBoardRequest.remove(userName);
        System.out.println("remove user " + userName + "from show board");
    }


    public String userExistInGame(String usernameFromSession) {
        String gameTitle = null;
        for (Game game : m_Games) {
            if (game.isPlayerExist(usernameFromSession)) {
                gameTitle = game.getGameTitle();
            }
        }
        return gameTitle;
    }

    public void addUserToShowBoard(String username, String gameTitle) {
        m_showBoardRequest.put(username,gameTitle);
        System.out.println("user " + username + " want to get board from " + gameTitle);
    }

    public String getGameTitleForShowBoard(String userName) {
        return m_showBoardRequest.get(userName);
    }

    public GamesManager() {
        this.m_Games = new ArrayList<>();
        this.m_showBoardRequest = new HashMap<>();
    }


    public void DeleteUserFromGames(String username) {
        for (Game game: m_Games ) {
            game.DeleteUserFromGame(username);

        }
        System.out.println("after delete user from games");
    }

    public String getGameTitleByUser(String userName) {
        String gameTitle = null;
        for (Game gm : m_Games) {
            for (Player op : gm.getListOfPlayersManagers()) {
                String playerName = op.GetPlayerName();
                if (playerName.equals(userName)) {
                    gameTitle = gm.getGameTitle();
                }
            }
        }
        return gameTitle;
    }

    public void addGame(String organizer, StringBuilder out) throws Exception, SAXException, JAXBException, InvalidXMLException, InvalidExtension, XmlValueException {
        InputStream gameFile = new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));

        try {
            Game gameEntered = new Game();
            gameEntered.readFromInput(gameFile);
            gameEntered.InitFromGeneratedGame(gameFile, organizer, basicGameNum);
            basicGameNum++;
            gameEntered.initChatManager();

            String gameTitle = gameEntered.getGameTitle();
            checkIfGameTitlePerform(gameTitle);
            String playersCount = setPlayerCount(gameEntered);
            gameEntered.setMaximumNumOfPlayers(Integer.parseInt(gameEntered.getNumOfPlayers()));
            String totalMoves = (String.valueOf(gameEntered.getNumOfMoves()));
            String boardSize = (String.valueOf(gameEntered.GetBoard().NumOfCols()));

            String gameStatus = "Ready";
            m_Games.add(gameEntered);
            m_GamesProperties.add(new GameProperties(gameTitle, organizer, playersCount, totalMoves, boardSize, gameStatus));
        }
        catch (JAXBException exc1) {
            throw new JAXBException("XML file not found or not matching the schema");
        }
        catch (SAXException exc2) {
            throw new SAXException("XML schema file not found");
        }
        catch (InvalidXMLException exc3) {
            throw new InvalidXMLException("XML file not valid");
        }
        catch (InvalidExtension exc4) {
            throw new InvalidExtension(exc4.GetMessage());
        }
        catch (XmlValueException exc5) {
            throw new XmlValueException(exc5.GetMessage());
        }
        catch (Exception exc6) {
            throw new Exception(exc6.getMessage());
        }
    }


    private void checkIfGameTitlePerform(String gameTitle) throws Exception {
        for (Game game : m_Games){
            if (game.getGameTitle().equals(gameTitle)){
                throw new Exception("Found another game with the same title!!");
            }
        }
    }

    public String setPlayerCount(Game gameEntered) {
        System.out.println("set Players count to " + gameEntered.getNumOfPlayersInTheGame().toString() +  " / " + gameEntered.getNumOfPlayers());
        return gameEntered.getNumOfPlayersInTheGame().toString() +  " / " + gameEntered.getNumOfPlayers();

    }

    public Game getGameByTitle(String title){
        for (Game game : m_Games) {
            if(game.getTitle().equals(title)){
                return game;
            }
        }

        return null; //we don't get here
    }

    public void addUserToAGame(String gameTitle,String playerName, boolean isHuman) throws Exception {
        Game game = getGameByTitle(gameTitle);
        game.addUserToTheGame(playerName, isHuman);
        if (game.getNumOfNecessaryPlayers() == game.getNumOfPlayersInTheGame()){
            GameProperties gameProperties = getGamePropertyByName(gameTitle);
            gameProperties.setGameStatus("Started");
        }
    }

    public ArrayList<Game> getGames() {
        return m_Games;
    }

    public GameProperties getGamePropertyByName(String gameTitle) {
        for (GameProperties game : m_GamesProperties) {
            if (game.getGameTitle().equals(gameTitle)){
                return game;
            }
        }
        return null;
    }

    public ArrayList<GameProperties> getGamesProperties() {
        return m_GamesProperties;
    }

    public void updateGamesProperties(){

        int i = 0;
        //String playersCount = setPlayerCount(gameEntered);
        for(Game game : m_Games){
            m_GamesProperties.get(i).m_totalMoves = String.valueOf(game.getNumOfMoves());
            i++;
        }

    }

    public boolean removeGame(String gameTitle) {
        int i = 0;
        for(Game game : m_Games) {
            if(game.getGameTitle().equals(gameTitle)){
                if(game.getNumOfPlayersInTheGame() > 0){
                    return false;
                }
                else {
                    m_Games.remove(game);
                    m_GamesProperties.remove(i);
                    return true;
                }
            }
            i++;
        }

        return true;
    }

}
