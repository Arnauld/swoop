package swoop.pipeline;

public class Payload {
    private Object content;

    public boolean isDefined() {
        return content != null;
    }

    public Object content() {
        return content;
    }

    public Payload content(Object content) {
        this.content = content;
        return this;
    }

}
