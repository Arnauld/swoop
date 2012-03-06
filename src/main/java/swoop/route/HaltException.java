package swoop.route;

import swoop.SwoopException;

@SuppressWarnings("serial")
public class HaltException extends SwoopException {
    
    private int statusCode = 200;
    private String body = null;
    
    public HaltException() {
        super();
    }
    
    public HaltException(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public HaltException(String body) {
        this.body = body;
    }
    
    HaltException(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    
    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
}
