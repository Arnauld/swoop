package swoop.path;

public enum Verb {
    //--- httpmethod
    Get(Category.HttpMethod), //
    Post(Category.HttpMethod), //
    Put(Category.HttpMethod), //
    Delete(Category.HttpMethod), //
    Head(Category.HttpMethod), //
    Trace(Category.HttpMethod), //
    Connect(Category.HttpMethod), //
    Options(Category.HttpMethod), //
    //--- websocket
    WebSocketOpen(Category.WebSocket), //
    WebSocketClose(Category.WebSocket), //
    WebSocketMessage(Category.WebSocket), // 
    WebSocketPing(Category.WebSocket), //
    WebSocketPong(Category.WebSocket), //
    //--- eventsource
    EventSourceOpen(Category.EventSource), //
    EventSourceClose(Category.EventSource) //
    ;//
    
    public enum Category {
        HttpMethod,//
        WebSocket,//
        EventSource;
    }
    
    private final Category category;
    private Verb(Category category) {
        this.category = category;
    }
    
    public Category getCategory() {
        return category;
    }

    public static Verb lookup(String what) {
        if (what == null)
            return null;
        String trimmedWhat = what.trim();
        for (Verb verb : values()) {
            if (verb.name().equalsIgnoreCase(trimmedWhat))
                return verb;
        }
        return null;
    }

    public boolean isWebSocket() {
        return this.category == Category.WebSocket;
    }

    public boolean isEventSource() {
        return this.category == Category.EventSource;

    }

    public boolean isHttpMethod() {
        return this.category == Category.HttpMethod;
    }

    public boolean belongsTo(Category category) {
        return this.category == category;
    }
}
