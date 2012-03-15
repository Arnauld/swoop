package swoop.util;

public interface Context {
    <T> T get(Class<T> type);
}
