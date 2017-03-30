package Logic;

/**
 * Created by alex on 29/11/2016.
 */

public class InvalidXMLException extends Exception {
    private String msg;

    public InvalidXMLException() {
    }

    public InvalidXMLException(String message){
        super(message);
        msg = message;
    }

    public String GetMessage(){
        return msg;
    }
}
