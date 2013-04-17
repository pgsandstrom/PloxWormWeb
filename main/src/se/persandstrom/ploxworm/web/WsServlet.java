package se.persandstrom.ploxworm.web;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;


public class WsServlet extends org.apache.catalina.websocket.WebSocketServlet {
    //this class seems to act as a singleton

    @Inject
    InitHolder initHolder;

    public WsServlet() {
        System.out.println("WsServlet");
    }

    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest httpServletRequest) {
        System.out.println("createWebSocketInbound");
        SimpleWebSocket webSocket = new SimpleWebSocket();
        HumanPlayer player = new HumanPlayer(webSocket);
        initHolder.addPlayer(player);
        return webSocket;
    }


}
