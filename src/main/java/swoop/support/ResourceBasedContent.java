package swoop.support;

import java.io.IOException;
import java.io.InputStream;

import swoop.Action;
import swoop.Request;
import swoop.Response;
import swoop.SwoopException;
import swoop.util.IO;

public class ResourceBasedContent extends Action {
    
    public static String resourcePath(Class<?> clazz) {
        return clazz.getPackage().getName().replace('.', '/');
    }
    
    private String resourcePath;

    public ResourceBasedContent(String resourcePath) {
        super();
        this.resourcePath = resourcePath;
    }

    public ResourceBasedContent(String path, String resourcePath) {
        super(path);
        this.resourcePath = resourcePath;
    }
    
    @Override
    public void handle(Request request, Response response) {
        response.body(resourceAsString(resourcePath));
    }
    
    private static String resourceAsString(String resourcePath) {
        InputStream input = ResourceBasedContent.class.getClassLoader().getResourceAsStream(resourcePath);
        if(input==null) {
            throw new SwoopException("Missing resource <" + resourcePath + ">");
        }
        try {
            return IO.toString(input, "utf8").toString();
        }
        catch(IOException ioe) {
            throw new SwoopException("Failed to load resource <" + resourcePath + ">", ioe);
        }
        finally {
            IO.closeQuietly(input);
        }
    }

}
