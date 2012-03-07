package swoop.route;

import swoop.SwoopException;

@SuppressWarnings("serial")
public class RedirectException extends SwoopException {
    
    private final String location;
    
    public RedirectException(String location) {
        this.location = location;
    }
    
    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }
}
