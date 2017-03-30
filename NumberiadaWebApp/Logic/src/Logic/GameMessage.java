package Logic;

/**
 * Created by alex on 17/02/2017.
 */
public class GameMessage {
    private String isFinished;
    private String value;

    public void setParams(String msg, String value1){
        isFinished = msg;
        value = value1;
    }
}
