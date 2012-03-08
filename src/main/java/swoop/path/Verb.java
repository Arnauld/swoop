package swoop.path;

public enum Verb {
    Get, Post, Put, Delete, Head, Trace, Connect, Options, //
    Any, WebSocket;

    public static Verb lookup(String what) {
        if(what==null)
            return null;
        String trimmedWhat = what.trim();
        for(Verb verb : values()) {
            if(verb.name().equalsIgnoreCase(trimmedWhat))
                return verb;
        }
        return null;
    }

    public boolean isAny() {
        return (this==Any);
    }
    
    public boolean isWebSocket() {
        return (this==WebSocket);
    }

    public boolean isHttpMethod() {
        return (this!=Any && this!=WebSocket);
    }

    public boolean matches(Verb verb) {
        return isAny() || this==verb;
    }
}
