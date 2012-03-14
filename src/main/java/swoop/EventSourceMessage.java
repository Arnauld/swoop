package swoop;

public interface EventSourceMessage {
    void id(Long id);
    Long id();
    
    void content(String content);
    String content();
}
