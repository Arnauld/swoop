package swoop.pipeline;

public interface Handler {
    public enum Mode {
        Downstream,
        Upstream,
        Target
    }
}
