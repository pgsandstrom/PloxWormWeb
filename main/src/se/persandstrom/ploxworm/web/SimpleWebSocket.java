package se.persandstrom.ploxworm.web;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * User: pesandst
 * Date: 2013-03-15
 * Time: 12:25
 */
public class SimpleWebSocket extends MessageInbound {

    private WsOutbound outbound;

    private Player owner;

    public SimpleWebSocket() {
    }

    @Override
    protected void onBinaryMessage(ByteBuffer byteBuffer) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected void onTextMessage(CharBuffer charBuffer) throws IOException {
        System.out.println("onTextMessage");
        owner.received(String.valueOf(charBuffer));

        //TODO temp:
//        outbound.writeTextMessage(charBuffer);
    }

    public void send(String message) throws IOException {
        outbound.writeTextMessage(CharBuffer.wrap(message));
    }

    @Override
    public void onOpen(WsOutbound outbound) {
        System.out.println("onOpen");
        this.outbound = outbound;
        owner.open();
    }

    @Override
    protected void onClose(int status) {
        System.out.println("onClose");
        owner.disconnected();
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
