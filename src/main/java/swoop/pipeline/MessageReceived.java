package swoop.pipeline;

public class MessageReceived extends Message {

    public MessageReceived() {
        super();
    }

    public MessageReceived(byte[] binary) {
        super(binary);
    }

    public MessageReceived(String text) {
        super(text);
    }

    public MessageReceived text(String text) {
        return (MessageReceived) super.text(text);
    }

    public MessageReceived binary(byte[] bytes) {
        return (MessageReceived) super.binary(bytes);
    }

}
