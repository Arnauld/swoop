package swoop;

public interface Cookie {
    
    Object raw();

    String comment();
    
    void comment(String purpose);

    String domain();
    
    void domain(String pattern);

    long maxAge();
    
    void maxAge(long expiry);

    String name();

    String path();
    
    void path(String uri);

    boolean secure();
    
    void secure(boolean flag);

    String value();
    
    void value(String newValue);
    
    int version();
    
    void version(int ver);

}
