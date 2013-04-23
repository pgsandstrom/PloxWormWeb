package se.persandstrom.ploxworm.web;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * User: pesandst
 * Date: 2013-03-15
 * Time: 12:25
 */
public class SimpleWebSocket extends MessageInbound {

    static Logger log = Logger.getLogger(SimpleWebSocket.class.getName());

    private WsOutbound outbound;

    private HumanPlayer owner;

    public SimpleWebSocket() {
    }

    @Override
    protected void onBinaryMessage(ByteBuffer byteBuffer) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected void onTextMessage(CharBuffer charBuffer) throws IOException {
        owner.received(String.valueOf(charBuffer));
    }

    public void send(String message) throws IOException {
        outbound.writeTextMessage(CharBuffer.wrap(message));
    }

    @Override
    public void onOpen(WsOutbound outbound) {
        log.debug("onOpen");
        this.outbound = outbound;
        owner.open();
    }

    @Override
    protected void onClose(int status) {
        log.debug("onClose");
        owner.disconnected();
    }

    public void setOwner(HumanPlayer owner) {
        this.owner = owner;
    }
}
