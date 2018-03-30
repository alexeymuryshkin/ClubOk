package dc.clubok;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket
public class FeedWebSocketHandler {

    private String sender, msg;
    private static Logger logger = LoggerFactory.getLogger(FeedWebSocketHandler.class.getCanonicalName());

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + ClubOKService.nextUserNumber++;
        logger.debug(username + " connected");
        ClubOKService.userUsernameMap.put(user, username);
        ClubOKService.sendPosts(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = ClubOKService.userUsernameMap.get(user);
        ClubOKService.userUsernameMap.remove(user);
        ClubOKService.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
        logger.debug(username + " disconnected");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        ClubOKService.broadcastMessage(sender = ClubOKService.userUsernameMap.get(user), msg = message);
    }
}
