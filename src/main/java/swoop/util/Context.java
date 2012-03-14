package swoop.util;

public interface Context {
    <T> T adaptTo(Class<T> type);
}
