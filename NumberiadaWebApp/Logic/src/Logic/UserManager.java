package Logic;

import java.util.HashMap;
import static Constants.Constants.*;

/**
 * Created by alex on 09/02/2017.
 */
public class UserManager {

    private final HashMap<String, String> users;

    public UserManager() {
        users = new HashMap<>();
    }

    public void addUser(String username, String userType) {
        users.put(username, userType);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public HashMap<String, String> getUsers() {
        return users;
    }

    public boolean isUserExists(String username) {
        return users.containsKey(username);
    }

    public boolean isUserHuman(String name){

        String value = users.get(name);
        System.out.println("is human?? : " + value.equals(USER_HUMAN)+ "  " + name);
        return value.equals(USER_HUMAN);
    }

    public void deleteUser(String username) {
        users.remove(username);
    }

}
