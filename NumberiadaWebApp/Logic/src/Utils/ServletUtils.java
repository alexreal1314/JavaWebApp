package Utils;

import Logic.ChatManager;
import Logic.GamesManager;
import Logic.UserManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import static Constants.Constants.INT_PARAMETER_ERROR;


/**
 * Created by alex on 09/02/2017.
 */
public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String GAMES_MANAGER_ATTRIBUTE_NAME = "gamesManager";

    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";


    public static UserManager getUserManager(ServletContext servletContext) {
        if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }


    public static GamesManager getGamesManager(ServletContext servletContext) {
        if (servletContext.getAttribute(GAMES_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(GAMES_MANAGER_ATTRIBUTE_NAME, new GamesManager());
        }
        return (GamesManager) servletContext.getAttribute(GAMES_MANAGER_ATTRIBUTE_NAME);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
        if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }
}
