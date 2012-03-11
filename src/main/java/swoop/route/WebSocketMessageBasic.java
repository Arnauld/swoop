package swoop.route;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import swoop.WebSocketMessage;

public class WebSocketMessageBasic implements WebSocketMessage {
    
    private final RouteParameters routeParameters;
    private byte[] bytes;
    private String text;
    
    public WebSocketMessageBasic(RouteParameters routeParameters) {
        super();
        this.routeParameters = routeParameters;
    }

    @Override
    public String routeParam(String param) {
        return routeParameters.routeParam(param);
    }

    @Override
    public List<String> routeParams(String param) {
        return routeParameters.routeParams(param);
    }

    @Override
    public Set<String> routeParamKeys() {
        return routeParameters.routeParamKeys();
    }

    @Override
    public boolean isText() {
        return (text!=null);
    }

    @Override
    public byte[] binary() {
        return bytes;
    }

    @Override
    public void binary(byte[] bytes) {
        this.text = null;
        this.bytes = bytes;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public void text(String text) {
        this.text = text;
        this.bytes = null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "WebSocketMessageBasic [bytes=" + Arrays.toString(bytes)
                + ", text=" + text + "]";
    }

    
}
