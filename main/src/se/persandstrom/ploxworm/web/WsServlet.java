package se.persandstrom.ploxworm.web;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

//this class seems to act as a singleton
public class WsServlet extends org.apache.catalina.websocket.WebSocketServlet {

    static Logger log = Logger.getLogger(WsServlet.class.getName());

    @Inject
    InitHolder initHolder;

    public WsServlet() {
    }

    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest httpServletRequest) {
        log.debug("createWebSocketInbound");
        SimpleWebSocket webSocket = new SimpleWebSocket();
        HumanPlayer player = new HumanPlayer(webSocket);
        initHolder.addPlayer(player);
        return webSocket;
    }


}
