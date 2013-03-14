package se.persandstrom.ploxworm.web;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import javax.servlet.http.HttpServletRequest;

/**
 * User: pesandst
 * Date: 2013-03-14
 * Time: 15:48
 */
public class WebSocketTest extends WebSocketServlet {

    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest httpServletRequest) {
        System.out.println("OMG OMG IT IS HERE!!!");
        return null;  //NOT IMPLEMENTED
    }
}
