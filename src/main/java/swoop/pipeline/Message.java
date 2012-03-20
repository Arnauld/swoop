package swoop.pipeline;

import java.io.UnsupportedEncodingException;

import swoop.SwoopException;

public class Message {
    private byte[] binary;
    private String text;
    
    public Message() {
    }
    
    public Message(byte[] binary) {
        this.binary = binary;
    }
    
    public Message(String text) {
        this.text = text;
    }

    public boolean isDefined() {
        return binary != null || text!=null;
    }
    
    public boolean isText() {
        return text!=null;
    }

    public boolean isBinary() {
        return binary!=null;
    }

    public String text() {
        return text;
    }
    
    public byte[] binary() {
        return binary;
    }
    
    public String textOrBinaryAsText(String encoding) {
        if(isText())
            return text();
        else if(isBinary()) {
            try {
                return new String(binary(), encoding);
            } catch (UnsupportedEncodingException e) {
                throw new SwoopException("UnsupportedEncoding <" + encoding + ">", e);
            }
        }
        return null;
    }

    public Message text(String text) {
        this.text = text;
        this.binary = null;
        return this;
    }
    

    public Message binary(byte[] bytes) {
        this.binary = bytes;
        this.text = null;
        return this;
    }
}
